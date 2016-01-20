package com.commonrail.rtplayer;

import android.media.MediaCodec;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import com.commonrail.rtplayer.builder.RendererBuilder;
import com.commonrail.rtplayer.listener.CaptionListener;
import com.commonrail.rtplayer.listener.Id3MetadataListener;
import com.commonrail.rtplayer.listener.InfoListener;
import com.commonrail.rtplayer.listener.InternalErrorListener;
import com.commonrail.rtplayer.listener.RtMuxListener;
import com.commonrail.rtplayer.listener.RtPlayerListener;
import com.google.android.exoplayer.CodecCounters;
import com.google.android.exoplayer.DummyTrackRenderer;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.chunk.BaseChunkSampleSourceEventListener;
import com.google.android.exoplayer.chunk.ChunkSampleSource;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.drm.StreamingDrmSessionManager;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.PlayerControl;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 项目名称：rtplayer
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:34
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:34
 * 修改备注：
 */
public class RtPlayer implements
        ExoPlayer.Listener,
        DefaultBandwidthMeter.EventListener,
        HlsSampleSource.EventListener,
        ChunkSampleSource.EventListener,
        MediaCodecVideoTrackRenderer.EventListener,
        MediaCodecAudioTrackRenderer.EventListener,
        StreamingDrmSessionManager.EventListener,
        DebugTextViewHelper.Provider {

    /**
     * 播放器状态
     * STATE_IDLE : 播放器没有准备好,也没有开始准备
     * <p/>
     * STATE_PREPARING : 播放器开始准备
     * <p/>
     * STATE_BUFFERING : 播放器已经准备好,但是不能从当前位置开始播放
     * 1.由TrackRenderer决定,通常发生在需要更多的数据
     * 2.正在缓冲的时候
     * <p/>
     * STATE_READY : 播放器已经准备好,可以从当前位置开始播放
     * 1.setPlayWhenReady(boolean)返回true,开始播放
     * 2.setPlayWhenReady(boolean)返回false,暂停播放
     * <p/>
     * STATE_ENDED : 播放器已经播放完成
     * <p/>
     * TRACK_DISABLED : 可以做为setSelectedTrack(int, int)的第二个参数来禁用渲染
     * TRACK_DEFAULT : 可以做为setSelectedTrack(int, int)的第二个参数来选择默认的轨道
     */
    public static final int STATE_IDLE = 1;
    public static final int STATE_PREPARING = 2;
    public static final int STATE_BUFFERING = 3;
    public static final int STATE_READY = 4;
    public static final int STATE_ENDED = 5;
    public static final int TRACK_DISABLED = -1;
    public static final int TRACK_DEFAULT = 0;
    //渲染器个数默认4个,1个视频1个音频,2个虚拟渲染器
    public static final int RENDERER_COUNT = 4;
    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_METADATA = 3;
    /**
     * 渲染器状态
     * STATE_IDLE : 还没创建
     */
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private final ExoPlayer player;
    private final PlayerControl playerControl;
    private final Handler mainHandler;
    private final CopyOnWriteArrayList<RtPlayerListener> listeners;
    private InternalErrorListener internalErrorListener;
    private CaptionListener captionListener;
    private Id3MetadataListener id3MetadataListener;
    private InfoListener infoListener;
    private RendererBuilder rendererBuilder;
    private BandwidthMeter bandwidthMeter;
    private Format videoFormat;
    private Surface surface;
    private CodecCounters codecCounters;
    private TrackRenderer videoRenderer;
    private TrackRenderer audioRenderer;
    private int videoTrackToRestore;
    private long position;

    private int rendererBuildingState;
    //ExpPlayer播放器状态
    private int lastReportedPlaybackState;
    private boolean lastReportedPlayWhenReady;
    private boolean backgrounded;
    private boolean prepared = false;

    public RtPlayer(RendererBuilder rendererBuilder) {
        this.rendererBuilder = rendererBuilder;
        player = ExoPlayer.Factory.newInstance(RENDERER_COUNT, 1500, 5000);
        player.addListener(this);
        playerControl = new PlayerControl(player);
        mainHandler = new Handler();
        listeners = new CopyOnWriteArrayList<>();
        //初始化播放器状态
        lastReportedPlaybackState = ExoPlayer.STATE_IDLE;
        //初始化渲染器状态
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        //初始化时禁用字幕渲染器的轨道
        player.setSelectedTrack(TYPE_TEXT, TRACK_DISABLED);
    }

    public void addListener(RtPlayerListener listener) {
        listeners.add(listener);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void removeListener(ExoPlayer.Listener listener) {
        listeners.remove(listener);
    }

    public PlayerControl getPlayerControl() {
        return playerControl;
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    public Surface getSurface() {
        return surface;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
        pushSurface(false);
    }

    public void pushSurface(boolean blockForSurfacePush) {
        if (videoRenderer == null) {
            return;
        }
        //发送surface到渲染器
        if (blockForSurfacePush) {
            //阻塞线程,直到消息发送到为止(发送消息的目标,用于识别消息的类型,消息内容)
            //surfaceView被销毁
            player.blockingSendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
        } else {
            //发送消息到指定的组件上,该消息会被传递到播放线程.
            //如果组件抛出ExoPlaybackException,这将作为一个error传递到播放器
            player.sendMessage(
                    videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
        }
    }

    public long getDuration() {
        return player.getDuration();
    }

    public long getBufferedPosition() {
        return player.getBufferedPosition();
    }

    public int getBufferedPercentage() {
        return player.getBufferedPercentage();
    }

    public Looper getPlaybackLooper() {
        return player.getPlaybackLooper();
    }

    public int getTrackCount(int rendererIndex) {
        return player.getTrackCount(rendererIndex);
    }

    public MediaFormat getTrackFormat(int rendererIndex, int trackIndex) {
        return player.getTrackFormat(rendererIndex, trackIndex);
    }

    @Override
    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    /**
     * {@link DebugTextViewHelper.Provider}
     */
    @Override
    public Format getFormat() {
        return videoFormat;
    }

    @Override
    public BandwidthMeter getBandwidthMeter() {
        return bandwidthMeter;
    }

    @Override
    public CodecCounters getCodecCounters() {
        return codecCounters;
    }

    /**
     * 自定义view时使用
     */
    public void replaceRenderBuilder(RendererBuilder rendererBuilder) {
        this.rendererBuilder = rendererBuilder;
    }

    public boolean getPlayWhenReady() {
        return player.getPlayWhenReady();
    }

    /**
     * 如果播放器状态是STATE_READY,准备好,那么此方法可以用于暂停和恢复
     *
     * @param playWhenReady playWhenReady
     */
    public void setPlayWhenReady(boolean playWhenReady) {
        player.setPlayWhenReady(playWhenReady);
    }

    public boolean getBackgrounded() {
        return backgrounded;
    }

    /**
     * backgrounded=true 播放器暂停视频渲染器
     * backgrounded=false 播放器界面从后台重新进入,恢复视频播放(渲染器)
     */
    public void setBackgrounded(boolean backgrounded) {
        if (this.backgrounded == backgrounded) {
            return;
        }
        this.backgrounded = backgrounded;
        if (backgrounded) {
            //返回对应渲染器的计数
            videoTrackToRestore = getSelectedTrack(TYPE_VIDEO);
            //TRACK_DISABLED = -1
            setSelectedTrack(TYPE_VIDEO, TRACK_DISABLED);
//            setSelectedTrack(TYPE_AUDIO, TRACK_DISABLED);
//            position = player.getCurrentPosition();
            blockingClearSurface();
        } else {
            //参数2,如果是一个负数或大于或等于渲染器对应的计数,将禁用对应渲染器
            //TRACK_DEFAULT = 0
            setSelectedTrack(TYPE_VIDEO, TRACK_DEFAULT);
//            setSelectedTrack(TYPE_AUDIO, TRACK_DEFAULT);
//            player.seekTo(position);
        }
    }

    public int getSelectedTrack(int type) {
        return player.getSelectedTrack(type);
    }

    public void setSelectedTrack(int type, int index) {
        player.setSelectedTrack(type, index);
        if (type == TYPE_TEXT && index < 0 && captionListener != null) {
            captionListener.onCues(Collections.<Cue>emptyList());
        }
    }

    /**
     * surfaceView销毁时(从另外一个页面有请求进入),清空surface
     */
    public void blockingClearSurface() {
        surface = null;
        pushSurface(true);
    }

    public InternalErrorListener getInternalErrorListener() {
        return internalErrorListener;
    }

    public void setInternalErrorListener(InternalErrorListener internalErrorListener) {
        this.internalErrorListener = internalErrorListener;
    }

    public CaptionListener getCaptionListener() {
        return captionListener;
    }

    public void setCaptionListener(CaptionListener captionListener) {
        this.captionListener = captionListener;
    }

    public Id3MetadataListener getId3MetadataListener() {
        return id3MetadataListener;
    }

    public void setId3MetadataListener(Id3MetadataListener id3MetadataListener) {
        this.id3MetadataListener = id3MetadataListener;
    }

    public InfoListener getInfoListener() {
        return infoListener;
    }

    public void setInfoListener(InfoListener infoListener) {
        this.infoListener = infoListener;
    }

    /**
     * 播放准备工作,新建渲染器
     */
    public void prepare() {
        if (prepared || rendererBuilder == null) {
            return;
        }

        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILT) {
            player.stop();
        }
        rendererBuilder.cancel();

        videoFormat = null;
        videoRenderer = null;
        rendererBuildingState = RENDERER_BUILDING_STATE_BUILDING;
        maybeReportPlayerState();

        rendererBuilder.buildRenderers(this);
        prepared = true;
    }

    /**
     * {@link  }
     * <p/>
     * 报告播放器状态
     */
    private void maybeReportPlayerState() {
        boolean playWhenReady = player.getPlayWhenReady();
        int playbackState = getPlaybackState();

        if (lastReportedPlayWhenReady != playWhenReady || lastReportedPlaybackState != playbackState) {
            for (RtPlayerListener listener : listeners) {
                listener.onStateChanged(playWhenReady, playbackState);
            }

            lastReportedPlayWhenReady = playWhenReady;
            lastReportedPlaybackState = playbackState;
        }
    }

    /**
     * 获取播放状态
     */
    public int getPlaybackState() {
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILDING) {
            return STATE_PREPARING;
        }
        int playerState = player.getPlaybackState();
        //当渲染器准备好了而且播放器状态还没开始准备,返回播放播放器开始准备
        if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILT && playerState == STATE_IDLE) {
            // This is an edge case where the renderers are built, but are still being passed to the
            // player's playback thread.
            //当渲染器创建好,但是任然会被传递到播放器的播放线程
            return STATE_PREPARING;
        }
        return playerState;
    }

    public void seekTo(long positionMs) {
        player.seekTo(positionMs);
    }

    /**
     * 释放播放器
     */
    public void release() {
        if (rendererBuilder != null) {
            rendererBuilder.cancel();
        }
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        surface = null;
        listeners.clear();
        player.release();
    }

    public void onRenderers(TrackRenderer[] renderers, BandwidthMeter bandwidthMeter) {
        for (int i = 0; i < RENDERER_COUNT; i++) {
            if (renderers[i] == null) {
                //如果没有注入视频音频渲染器,则放入虚拟的渲染器(无作用)
                renderers[i] = new DummyTrackRenderer();
            }
        }
        // 播放器准备完成
        this.videoRenderer = renderers[TYPE_VIDEO];
        this.audioRenderer = renderers[TYPE_AUDIO];

        pushSurface(false);
        //注入渲染器
        player.prepare(renderers);
        //改变渲染器状态
        rendererBuildingState = RENDERER_BUILDING_STATE_BUILT;
    }

    /**
     * {@link RendererBuilder} 创建渲染器遇到错误时调用
     */
    public void onRenderersError(Exception e) {
        if (internalErrorListener != null) {
            internalErrorListener.onRendererInitializationError(e);
        }
        for (RtPlayerListener listener : listeners) {
            listener.onError(e);
        }
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        maybeReportPlayerState();
    }

    /**
     * {@link ExoPlayer.Listener}
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        maybeReportPlayerState();
        for (RtPlayerListener listener : listeners) {
            listener.onStateChanged(playWhenReady, playbackState);
        }
    }

    @Override
    public void onPlayWhenReadyCommitted() {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        for (RtPlayerListener listener : listeners) {
            listener.onError(error);
        }
    }

    /**
     * {@link MediaCodecTrackRenderer.EventListener}
     */
    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onDecoderInitializationError(e);
        }
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onCryptoError(e);
        }
    }

    /**
     * @param decoderName              解码器名字 OMX.qcom.audio.decoder.aac
     * @param elapsedRealtimeMs        经过的实时毫秒
     * @param initializationDurationMs 初始化持续时间
     */
    @Override
    public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs, long initializationDurationMs) {

    }

    /**
     * {@link MediaCodecVideoTrackRenderer.EventListener}
     *
     * @param count 掉帧的块
     */
    @Override
    public void onDroppedFrames(int count, long elapsed) {
        if (infoListener != null) {
            infoListener.onDroppedFrames(count, elapsed);
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        for (RtPlayerListener listener : listeners) {
            listener.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
        }
    }

    /**
     * 绘制到surface上
     *
     * @param surface surface
     */
    @Override
    public void onDrawnToSurface(Surface surface) {

    }

    /**
     * {@link  MediaCodecAudioTrackRenderer.EventListener }
     */
    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onAudioTrackInitializationError(e);
        }
    }

    @Override
    public void onAudioTrackWriteError(AudioTrack.WriteException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onAudioTrackWriteError(e);
        }
    }

    /**
     * 在后台播放音频
     *
     * @param bufferSize             44100
     * @param bufferSizeMs           250
     * @param elapsedSinceLastFeedMs 自从上次注入的时间
     */
    @Override
    public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

    }

    /**
     * {@link BaseChunkSampleSourceEventListener}
     * {@link HlsSampleSource.EventListener}
     * {@link ChunkSampleSource.EventListener}
     */
    @Override
    public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {

    }

    @Override
    public void onLoadCanceled(int sourceId, long bytesLoaded) {

    }

    @Override
    public void onLoadError(int sourceId, IOException e) {
        if (internalErrorListener != null) {
            internalErrorListener.onLoadError(sourceId, e);
        }
    }

    @Override
    public void onUpstreamDiscarded(int sourceId, long mediaStartTimeMs, long mediaEndTimeMs) {

    }

    @Override
    public void onDownstreamFormatChanged(int sourceId, Format format, int trigger, long mediaTimeMs) {
        if (infoListener == null) {
            return;
        }
        if (sourceId == TYPE_VIDEO) {
            videoFormat = format;
            infoListener.onVideoFormatEnabled(format, trigger, mediaTimeMs);
        } else if (sourceId == TYPE_AUDIO) {
            infoListener.onAudioFormatEnabled(format, trigger, mediaTimeMs);
        }
    }

    /**
     * {@link BandwidthMeter.EventListener}
     *
     * @param elapsedMs 加载到设定缓冲值,使用的时间
     * @param bytes     加载的数据
     * @param bitrate   比特率
     */
    @Override
    public void onBandwidthSample(int elapsedMs, long bytes, long bitrate) {
        if (infoListener != null) {
            infoListener.onBandwidthSample(elapsedMs, bytes, bitrate);
        }

        for (RtPlayerListener okPlayerListener : listeners) {
            if (okPlayerListener instanceof RtMuxListener) {
                ((RtMuxListener) okPlayerListener).onBandwidthSample(elapsedMs, bytes, bitrate);
            }
        }
    }

    /**
     * {@link StreamingDrmSessionManager.EventListener}
     */
    @Override
    public void onDrmKeysLoaded() {

    }

    @Override
    public void onDrmSessionManagerError(Exception e) {
        if (internalErrorListener != null) {
            internalErrorListener.onDrmSessionManagerError(e);
        }
    }
}

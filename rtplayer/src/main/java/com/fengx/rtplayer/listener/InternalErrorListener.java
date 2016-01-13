package com.fengx.rtplayer.listener;

import android.media.MediaCodec;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.chunk.BaseChunkSampleSourceEventListener;
import com.google.android.exoplayer.chunk.ChunkSampleSource;
import com.google.android.exoplayer.drm.StreamingDrmSessionManager;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.fengx.rtplayer.builder.RendererBuilder;

import java.io.IOException;

/**
 * 内部错误的监听
 * <p/>
 * 内部错误对用户是不可见的,所以提供这个监听
 * 错误仅供参考,但是请注意内部错误有可能是致命的
 * <p/>
 * 如果播放器未能恢复内部错误,就会调用{@link ExoPlayer.Listener#onPlayerError(ExoPlaybackException)}
 * <p/>
 * Created by wengyiming on 15/11/29.
 */
public interface InternalErrorListener {

    /**
     * {@link RendererBuilder} 创建渲染器遇到错误时调用
     */
    void onRendererInitializationError(Exception e);

    /**
     * {@link  MediaCodecAudioTrackRenderer.EventListener }
     */
    void onAudioTrackInitializationError(AudioTrack.InitializationException e);

    void onAudioTrackWriteError(AudioTrack.WriteException e);

    /**
     * {@link MediaCodecTrackRenderer.EventListener}
     */
    void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e);

    void onCryptoError(MediaCodec.CryptoException e);

    /**
     * {@link BaseChunkSampleSourceEventListener}
     * {@link HlsSampleSource.EventListener}
     * {@link ChunkSampleSource.EventListener}
     * 流媒体播放
     */
    void onLoadError(int sourceId, IOException e);

    /**
     * {@link StreamingDrmSessionManager.EventListener}
     */
    void onDrmSessionManagerError(Exception e);
}

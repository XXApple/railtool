/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.commonrail.common.rtplayer.builder;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Handler;

import com.commonrail.common.rtplayer.RtPlayer;
import com.google.android.exoplayer.DefaultLoadControl;
import com.google.android.exoplayer.LoadControl;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.chunk.VideoFormatSelectorUtil;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsMasterPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.ManifestFetcher.ManifestCallback;

import java.io.IOException;

/**
 * A {@link RendererBuilder} for HLS.
 */
public class HlsRendererBuilder implements RendererBuilder {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENTS = 256;

    private final Context context;
    private final String userAgent;
    private final String url;

    private AsyncRendererBuilder currentAsyncBuilder;

    public HlsRendererBuilder(Context context, String userAgent, String url) {
        this.context = context;
        this.userAgent = userAgent;
        this.url = url;
    }

    @Override
    public void buildRenderers(RtPlayer player) {
        currentAsyncBuilder = new AsyncRendererBuilder(context, userAgent, url, player);
        currentAsyncBuilder.init();
    }

    @Override
    public void cancel() {
        if (currentAsyncBuilder != null) {
            currentAsyncBuilder.cancel();
            currentAsyncBuilder = null;
        }
    }

    private static final class AsyncRendererBuilder implements ManifestCallback<HlsPlaylist> {

        private final Context context;
        private final String userAgent;
        private final String url;
        private final RtPlayer player;
        private final ManifestFetcher<HlsPlaylist> playlistFetcher;

        private boolean canceled;

        public AsyncRendererBuilder(Context context, String userAgent, String url, RtPlayer player) {
            this.context = context;
            this.userAgent = userAgent;
            this.url = url;
            this.player = player;
            HlsPlaylistParser parser = new HlsPlaylistParser();
            playlistFetcher = new ManifestFetcher<>(url, new DefaultUriDataSource(context, userAgent),
                    parser);
        }

        public void init() {
            playlistFetcher.singleLoad(player.getMainHandler().getLooper(), this);
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onSingleManifest(HlsPlaylist manifest) {
            if (canceled) {
                return;
            }

            Handler mainHandler = player.getMainHandler();
            //Allocator资源的分配,设置默认的资源分配(初始化一个空的资源池来装)
            //数据加载控制
            LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(BUFFER_SEGMENT_SIZE));
            //如果数据传输是开放的,那创建一个带宽采样并更新的时候,可以记录传输的字节数
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            //对于主播放列表,以最大的可能去发现设备能够播放的类型
            int[] variantIndices = null;
            if (manifest instanceof HlsMasterPlaylist) {
                HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) manifest;
                try {
                    variantIndices = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(
                            context, masterPlaylist.variants, null, false);
                } catch (DecoderQueryException e) {
                    //解码器查询错误
                    player.onRenderersError(e);
                    return;
                }
                if (variantIndices.length == 0) {
                    //不支持播放,没有可以选择的变量
                    player.onRenderersError(new IllegalStateException("No variants selected."));
                    return;
                }
            }

            //创建采样源用于渲染
            DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
            HlsChunkSource chunkSource = new HlsChunkSource(dataSource, url, manifest, bandwidthMeter,
                    variantIndices, HlsChunkSource.ADAPTIVE_MODE_SPLICE);
            HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, loadControl,
                    BUFFER_SEGMENTS * BUFFER_SEGMENT_SIZE, mainHandler, player, RtPlayer.TYPE_VIDEO);

            //创建渲染器
            //5000是最大加载事件
            MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(context,
                    sampleSource, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000, mainHandler, player, 50);
            MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                    null, true, player.getMainHandler(), player, AudioCapabilities.getCapabilities(context));
//            MetadataTrackRenderer<Map<String, Object>> id3Renderer = new MetadataTrackRenderer<>(
//                    sampleSource, new Id3Parser(), player, mainHandler.getLooper());
//            Eia608TrackRenderer closedCaptionRenderer = new Eia608TrackRenderer(sampleSource, player,
//                    mainHandler.getLooper());

            //填充渲染器列表并回调
            TrackRenderer[] renderers = new TrackRenderer[RtPlayer.RENDERER_COUNT];
            renderers[RtPlayer.TYPE_VIDEO] = videoRenderer;
            renderers[RtPlayer.TYPE_AUDIO] = audioRenderer;
//            renderers[OkPlayer.TYPE_METADATA] = id3Renderer;
//            renderers[OkPlayer.TYPE_TEXT] = closedCaptionRenderer;
            player.onRenderers(renderers, bandwidthMeter);
        }

        @Override
        public void onSingleManifestError(IOException e) {
            if (canceled) {
                return;
            }

            player.onRenderersError(e);
        }
    }
}

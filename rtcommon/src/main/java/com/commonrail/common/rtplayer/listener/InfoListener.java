package com.commonrail.common.rtplayer.listener;

import com.google.android.exoplayer.TimeRange;
import com.google.android.exoplayer.chunk.Format;

/**
 * A listener for debugging information.
 */
public interface InfoListener {

    void onVideoFormatEnabled(Format format, int trigger, long mediaTimeMs);

    void onAudioFormatEnabled(Format format, int trigger, long mediaTimeMs);

    void onDroppedFrames(int count, long elapsed);

    void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate);

    void onLoadStarted(int sourceId, long length, int type, int trigger, Format format,
                       long mediaStartTimeMs, long mediaEndTimeMs);

    void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format,
                         long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs);

    void onDecoderInitialized(String decoderName, long elapsedRealtimeMs,
                              long initializationDurationMs);

    void onAvailableRangeChanged(TimeRange availableRange);
}
package com.fengx.rtplayer.listener;

/**
 * Created by succlz123 on 15/11/29.
 */
public interface RtPlayerListener {

    void onStateChanged(boolean playWhenReady, int playbackState);

    void onError(Exception e);

    void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio);
}
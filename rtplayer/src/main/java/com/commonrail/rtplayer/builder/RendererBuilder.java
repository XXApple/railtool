package com.commonrail.rtplayer.builder;

import com.commonrail.rtplayer.RtPlayer;

/**
 * Builds renderers for the player.
 * <p/>
 * Created by succlz123 on 15/12/1.
 */
public interface RendererBuilder {

    void buildRenderers(RtPlayer player);

    void cancel();
}
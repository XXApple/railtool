package com.commonrail.common.rtplayer.listener;

import java.util.Map;

/**
 * A listener for receiving ID3 metadata parsed from the media stream.
 */
public interface Id3MetadataListener {

    void onId3Metadata(Map<String, Object> metadata);
}
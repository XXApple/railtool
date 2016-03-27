package com.commonrail.rtplayer.listener;

import com.google.android.exoplayer.text.Cue;

import java.util.List;

/**
 * A listener for receiving notifications of timed text.
 */
public interface CaptionListener {

    void onCues(List<Cue> cues);
}

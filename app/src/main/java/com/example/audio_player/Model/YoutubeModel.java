package com.example.audio_player.Model;

import android.content.om.OverlayIdentifier;

import java.io.Serializable;

public class YoutubeModel implements Serializable {

    private final String videoTitle;
    private final String videoImageUrl;
    private final String videoUrl;

    public YoutubeModel(String videoTitle, String videoImageUrl, String videoUrl)
    {
        this.videoTitle = videoTitle;
        this.videoImageUrl = videoImageUrl;
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle()
    {
        return videoTitle;
    }

    public String getVideoImageUrl()
    {
        return videoImageUrl;
    }
    public String getVideoUrl() { return videoUrl;
    }
}

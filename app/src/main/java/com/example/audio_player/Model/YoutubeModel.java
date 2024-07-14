package com.example.audio_player.Model;

import com.google.api.services.youtube.model.SearchResult;

import java.io.Serializable;

public class YoutubeModel implements Serializable
{
    private final String videoTitle;
    private final String videoImageUrl;
    private final String videoUrl;
    private final String duration;

    public YoutubeModel(String videoTitle, String videoImageUrl, String videoUrl, String duration)
    {
        this.videoTitle = videoTitle;
        this.videoImageUrl = videoImageUrl;
        this.videoUrl = videoUrl;
        this.duration = duration;
    }

    public String getVideoTitle()
    {
        return videoTitle;
    }

    public String getVideoImageUrl()
    {
        return videoImageUrl;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }

    public String getDuration()
    {
        return duration;
    }
}

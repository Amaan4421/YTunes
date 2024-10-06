package com.example.audio_player.Model;

public class SongHistoryModel
{
    private int id;
    private String songTitle;
    private String songImageUrl;
    private String songVideoId;
    private String songDuration;
    private String videoUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongImageUrl() {
        return songImageUrl;
    }

    public void setSongImageUrl(String songImageUrl) {
        this.songImageUrl = songImageUrl;
    }

    public String getSongVideoId() {
        return songVideoId;
    }

    public void setSongVideoId(String songVideoId) {
        this.songVideoId = songVideoId;
    }
}

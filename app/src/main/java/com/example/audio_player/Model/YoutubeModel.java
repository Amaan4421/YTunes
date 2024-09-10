package com.example.audio_player.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;


public class YoutubeModel implements Parcelable
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

    protected YoutubeModel(Parcel in) {
        videoTitle = in.readString();
        videoImageUrl = in.readString();
        videoUrl = in.readString();
        duration = in.readString();
    }

    public static final Creator<YoutubeModel> CREATOR = new Creator<YoutubeModel>() {
        @Override
        public YoutubeModel createFromParcel(Parcel in) {
            return new YoutubeModel(in);
        }

        @Override
        public YoutubeModel[] newArray(int size) {
            return new YoutubeModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(videoTitle);
        parcel.writeString(videoImageUrl);
        parcel.writeString(videoUrl);
        parcel.writeString(duration);
    }
}

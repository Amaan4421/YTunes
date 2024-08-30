package com.example.audio_player.Utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.example.audio_player.Model.YoutubeModel;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FetchTrendingMusic {

    private YouTube youTube;
    private FetchTrendingMusicCallback callback;
    private Handler handler;
    private Runnable fetchTrendingRunnable;


    //interface
    public interface FetchTrendingMusicCallback {
        void onFetchTrendingMusic(List<YoutubeModel> youtubeModels);
        void onError(String error);
    }


    //fetching trending music
    public FetchTrendingMusic(YouTube youTube, FetchTrendingMusicCallback callback) {
        this.youTube = youTube;
        this.callback = callback;
        this.handler = new Handler(Looper.getMainLooper());
        this.fetchTrendingRunnable = new Runnable() {
            @Override
            public void run() {
                fetchTrendingSongs();

                //next update
                handler.postDelayed(this, 20000); // 20 seconds
            }
        };
        startFetchingTrendingMusic();
    }

    public void startFetchingTrendingMusic() {
        handler.post(fetchTrendingRunnable);
    }

    public void stopFetchingTrendingMusic() {
        handler.removeCallbacks(fetchTrendingRunnable);
    }

    public void fetchTrendingSongs() {
        new FetchTrendingMusicTask().execute();
    }


    //aysnc method to get trending videos
    private class FetchTrendingMusicTask extends AsyncTask<Void, Void, List<YoutubeModel>> {

        @Override
        protected List<YoutubeModel> doInBackground(Void... voids) {
            try {
                YouTube.Videos.List videoDetailsList = youTube.videos().list("snippet, contentDetails");
                videoDetailsList.setChart("mostPopular");
                videoDetailsList.setVideoCategoryId("10");
                videoDetailsList.setMaxResults(100L);
                videoDetailsList.setFields("items(id,snippet/title," +
                        "snippet/thumbnails/default/url,snippet/thumbnails/medium/url," +
                        "snippet/thumbnails/high/url,snippet/thumbnails/standard/url," +
                        "snippet/thumbnails/maxres/url,contentDetails/duration,snippet/categoryId)");

                VideoListResponse response = videoDetailsList.execute();

                List<YoutubeModel> youtubeModels = new ArrayList<>();
                for (Video video : response.getItems()) {
                    String categoryId = video.getSnippet().getCategoryId();

                    if ("10".equals(categoryId)) { // Check for music category
                        String videoId = video.getId();
                        String audioTitle = video.getSnippet().getTitle();
                        String audioImageUrl = getHighestResolutionThumbnail(video);
                        String audioDuration = video.getContentDetails().getDuration();
                        String formattedAudioDuration = formatDuration(audioDuration);
                        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                        youtubeModels.add(new YoutubeModel(audioTitle, audioImageUrl, videoUrl, formattedAudioDuration));
                    }
                }
                return youtubeModels;
            } catch (IOException e) {
                e.printStackTrace();
                callback.onError("Failed to retrieve trending videos");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<YoutubeModel> youtubeModels) {
            if (youtubeModels != null) {
                callback.onFetchTrendingMusic(youtubeModels);
            }
        }

        private String getHighestResolutionThumbnail(Video video) {
            if (video.getSnippet().getThumbnails().getMaxres() != null) {
                return video.getSnippet().getThumbnails().getMaxres().getUrl();
            } else if (video.getSnippet().getThumbnails().getStandard() != null) {
                return video.getSnippet().getThumbnails().getStandard().getUrl();
            } else if (video.getSnippet().getThumbnails().getHigh() != null) {
                return video.getSnippet().getThumbnails().getHigh().getUrl();
            } else if (video.getSnippet().getThumbnails().getMedium() != null) {
                return video.getSnippet().getThumbnails().getMedium().getUrl();
            } else {
                return video.getSnippet().getThumbnails().getDefault().getUrl();
            }
        }

        private String formatDuration(String audioDuration) {
            String formattedAudioDuration = "";
            audioDuration = audioDuration.replace("PT", "");
            int hours = 0, minutes = 0, seconds = 0;

            if (audioDuration.contains("H")) {
                String[] parts = audioDuration.split("H");
                hours = Integer.parseInt(parts[0]);
                audioDuration = parts.length > 1 ? parts[1] : "";
            }

            if (audioDuration.contains("M")) {
                String[] parts = audioDuration.split("M");
                minutes = Integer.parseInt(parts[0]);
                audioDuration = parts.length > 1 ? parts[1] : "";
            }

            if (audioDuration.contains("S")) {
                seconds = Integer.parseInt(audioDuration.split("S")[0]);
            }

            if (hours > 0) {
                formattedAudioDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                formattedAudioDuration = String.format("%02d:%02d", minutes, seconds);
            }

            return formattedAudioDuration;
        }
    }
}

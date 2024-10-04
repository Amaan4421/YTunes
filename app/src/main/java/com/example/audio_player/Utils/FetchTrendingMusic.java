package com.example.audio_player.Utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.audio_player.Fragment.SearchResultFragment;
import com.example.audio_player.Model.YoutubeModel;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
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
    public interface FetchTrendingMusicCallback
    {
        void onFetchTrendingMusic(List<YoutubeModel> trendingSongs, List<YoutubeModel> hindiSongs);
        void onError(String error);
    }


    //fetching trending music
    public FetchTrendingMusic(YouTube youTube, FetchTrendingMusicCallback callback) {
        this.youTube = youTube;
        this.callback = callback;
        this.handler = new Handler(Looper.getMainLooper());
        this.fetchTrendingRunnable = new Runnable() {
            @Override
            public void run()
            {
                fetchTrendingSongs();
                fetchTrendingHindiSongs();

                //next update
                handler.postDelayed(this, 60000);
            }
        };
        startFetchingTrendingMusic();
    }//end of method

    public void startFetchingTrendingMusic() {
        handler.post(fetchTrendingRunnable);
    }

    public void stopFetchingTrendingMusic() {
        handler.removeCallbacks(fetchTrendingRunnable);
    }

    public void fetchTrendingSongs() {
        new FetchTrendingMusicTask().execute();
    }

    public void fetchTrendingHindiSongs() { new FetchTrendingHindiMusicTask().execute(); }



    //async method to get trending songs
    private class FetchTrendingMusicTask extends AsyncTask<Void, Void, List<YoutubeModel>>
    {
        @Override
        protected List<YoutubeModel> doInBackground(Void... voids) {
            try
            {
                //get worldwide trending songs
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

                    //check music category
                    if ("10".equals(categoryId))
                    {
                        String videoId = video.getId();
                        String audioTitle = video.getSnippet().getTitle();
                        String audioImageUrl = getHighestResolutionThumbnail(video);
                        String audioDuration = video.getContentDetails().getDuration();
                        String formattedAudioDuration = formatDuration(audioDuration);
                        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                        youtubeModels.add(new YoutubeModel(videoId, audioTitle, audioImageUrl, videoUrl, formattedAudioDuration));
                    }//end of if
                }//end of for
                return youtubeModels;
            }//end of try
            catch (IOException e)
            {
                e.printStackTrace();
                callback.onError("Failed to retrieve trending videos");
                return null;
            }//end of catch
        }

        @Override
        protected void onPostExecute(List<YoutubeModel> youtubeModels)
        {
            if (youtubeModels != null)
            {
                callback.onFetchTrendingMusic(youtubeModels, null);
            }//end of if
        }
    }//end of async method



    //async method to get hindi trending songs
    private class FetchTrendingHindiMusicTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids)
        {
            try
            {
                //first search hindi audio latest songs
                YouTube.Search.List searchList = youTube.search().list("snippet");
                searchList.setQ("Hindi latest songs");
                searchList.setType("video");
                searchList.setMaxResults(50L);
                searchList.setVideoCategoryId("10");
                searchList.setRegionCode("IN");
                searchList.setRelevanceLanguage("hi");

                //store the response and pass it to get details method
                SearchListResponse response = searchList.execute();

                List<String> videoIds = new ArrayList<>();
                if (response.getItems() != null)
                {
                    response.getItems().forEach(searchResult -> videoIds.add(searchResult.getId().getVideoId()));
                }//end of if
                return videoIds;
            }//end of try
            catch (IOException e)
            {
                e.printStackTrace();
                callback.onError("Failed to retrieve trending videos!!!");
                return null;
            }//end of catch
        }

        @Override
        protected void onPostExecute(List<String> videoIds) {

            if (videoIds != null && !videoIds.isEmpty())
            {
                // Once search results are fetched, get the video metadata
                getSongDetails(videoIds);
            }//end of if
            else
            {
                callback.onError("Failed to retrieve search results!!!");
            }//end of else
        }
    }//end of async method



    //fetch song details based on the list of IDs
    public void getSongDetails(List<String> videoIds)
    {
        new FetchSongDetailsTask(videoIds).execute();
    }

    //asyncTask to fetch song metadata
    private class FetchSongDetailsTask extends AsyncTask<Void, Void, List<YoutubeModel>>
    {
        private List<String> videoIds;

        //constructor to pass the video IDs to fetch details
        public FetchSongDetailsTask(List<String> videoIds)
        {
            this.videoIds = videoIds;
        }

        @Override
        protected List<YoutubeModel> doInBackground(Void... voids) {
            try
            {
                //get meta data of particular songs
                YouTube.Videos.List videoDetailsList = youTube.videos().list("snippet, contentDetails");
                videoDetailsList.setId(String.join(",", videoIds)); // Pass the video IDs
                videoDetailsList.setFields("items(id,snippet/title,snippet/thumbnails,contentDetails/duration)");

                //store the data and pass
                VideoListResponse response = videoDetailsList.execute();

                List<YoutubeModel> youtubeModels = new ArrayList<>();

                //store all data into model
                for (Video video : response.getItems())
                {
                    String videoId = video.getId();
                    String audioTitle = video.getSnippet().getTitle();
                    String audioImageUrl = getHighestResolutionThumbnail(video);
                    String audioDuration = formatDuration(video.getContentDetails().getDuration());
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                    // Add the video details to the list
                    youtubeModels.add(new YoutubeModel(videoId, audioTitle, audioImageUrl, videoUrl, audioDuration));
                }//end of for
                return youtubeModels;
            }//end of try
            catch (IOException e)
            {
                e.printStackTrace();
                callback.onError("Failed to retrieve video details");
                return null;
            }//end of catch
        }

        @Override
        protected void onPostExecute(List<YoutubeModel> youtubeModels)
        {
            if (youtubeModels != null)
            {
                // Return the list of Hindi songs via the callback
                callback.onFetchTrendingMusic(null, youtubeModels);
            }//end of if
        }//end of method
    }//end of async method



    //get standard song image if available otherwise lower resolution image
    public String getHighestResolutionThumbnail(Video video)
    {
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
    }//end of method


    //set time standard format such as (MM:SS)
    public String formatDuration(String audioDuration) {
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
    }//end of method
}//end of class

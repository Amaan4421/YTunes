package com.example.audio_player.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.example.audio_player.Activity.MainActivity;
import com.example.audio_player.Activity.PlayAudio;
import com.example.audio_player.Adapter.ListAdapter;
import com.example.audio_player.BuildConfig;
import com.example.audio_player.Model.YoutubeModel;
import com.example.audio_player.R;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment
{
    //final global variables
    private static final String ARG_QUERY = "query";
    private String query;
    private ProgressBar progressBar;
    private YouTube youTube;
    private RecyclerView searchResultList;
    private ArrayList<YoutubeModel> searchResults;
    private ListAdapter adapter;


    //constructor
    public static SearchResultFragment newInstance(String query)
    {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUERY, query);
        fragment.setArguments(args);
        return fragment;
    }//end of method



    //onCreate method
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //check query is null or not
        if (getArguments() != null)
        {
            query = getArguments().getString(ARG_QUERY);
        }//end of if
    }//end of onCreate method


    //on create view method to load UI
    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //get xml file reference
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        //get ids from xml file
        searchResultList = view.findViewById(R.id.searchResultList);
        progressBar = view.findViewById(R.id.showLoading);



        //create blank list to store search result and pass it to adapter
        searchResults = new ArrayList<>();

        //when user clicks any item in list, get the video url of that item from model
        //also pass the image and title to next page
        adapter = new ListAdapter(getContext(), searchResults, new ListAdapter.ClickEvent() {
            @Override
            public void onItemClick(YoutubeModel youtubeModel)
            {
                Intent i = new Intent(getContext(), PlayAudio.class);
                i.putExtra("title", youtubeModel.getVideoTitle());
                i.putExtra("image", youtubeModel.getVideoImageUrl());
                getAudioFileUrl(youtubeModel.getVideoUrl(), i);
            }
        });//end of onClick method
        searchResultList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //set result in list
        searchResultList.setAdapter(adapter);



        //get the api key from gradle file
        String api_key = BuildConfig.API_KEY;


        //create object of youtube to make http request and pass the api key
        youTube = new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                new com.google.api.client.json.jackson2.JacksonFactory(),
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {
                    }
                }
        ).setYouTubeRequestInitializer(new YouTubeRequestInitializer(api_key))
                .setApplicationName(getString(R.string.app_name)).build();


        //call method which will be responsible to search data in yt database and get the result from database
        searchInYouTube(query);

        return view;
    }//end of onCreateView method




    //search query in youtube using API
    private void searchInYouTube(String query)
    {

        //call async method to avoid crashing
        new YouTubeSearchTask().execute(query);

    }//end of search method



    //search query in youtube database and give the search result...async method
    private class YouTubeSearchTask extends AsyncTask<String, Void, List<SearchResult>>
    {
        protected void onPreExecute()
        {
            super.onPreExecute();

            //change the visibility of loading bar
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<SearchResult> doInBackground(String... params)
        {
            try
            {
                //search in youtube and get snippet data from video metadata
                YouTube.Search.List searchList = youTube.search().list("snippet");
                searchList.setQ(params[0]);
                searchList.setType("video");
                searchList.setVideoCategoryId("10");
                searchList.setMaxResults(100L);

                //add response in one list
                SearchListResponse response = searchList.execute();
                return response.getItems();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<SearchResult> searchResults)
        {
            if (searchResults != null)
            {
                progressBar.setVisibility(View.GONE);

                //fetch all videos id from youtube database and store it in one list to fetch their details
                //such details are title, duration, image, etc...
                List<String> resultVideoIds = new ArrayList<>();

                //store ids by using for loop
                for(SearchResult result: searchResults)
                {
                    resultVideoIds.add(result.getId().getVideoId());
                }

                //call one more async method to fetch the video details
                new FetchVideoDetails().execute(resultVideoIds.toArray(new String[0]));
            }//end of if
            else
            {
                Toast.makeText(getContext(), "Failed to retrieve search results!!!", Toast.LENGTH_SHORT).show();
            }//end of else
        }
    }//end of method



    //fetch video details from metadata
    private class FetchVideoDetails extends AsyncTask<String, Void, List<YoutubeModel>>
    {
        @Override
        protected List<YoutubeModel> doInBackground(String... videoIds)
        {
            try
            {
                //get the videos list and metadata of snippet and content details part
                YouTube.Videos.List videoDetailsList = youTube.videos().list("snippet, contentDetails");
                videoDetailsList.setId(String.join(",", videoIds));
                videoDetailsList.setMaxResults(100L);
                videoDetailsList.setFields("items(id,snippet/title," +
                        "snippet/thumbnails/default/url,snippet/thumbnails/medium/url," +
                        "snippet/thumbnails/high/url,snippet/thumbnails/standard/url," +
                        "snippet/thumbnails/maxres/url,contentDetails/duration,snippet/categoryId)");

                //add response in one list
                VideoListResponse response = videoDetailsList.execute();

                //make array list of all those videos
                List<YoutubeModel> youtubeModels = new ArrayList<>();

                //for every video from list, fetch the particular details and store it in youtube model
                for (Video video : response.getItems())
                {
                    //category id to check it is music or not
                    String categoryId = video.getSnippet().getCategoryId();

                    //id 10 is for music category, if found any then and only then add details of that video in model
                    if ("10".equals(categoryId))
                    {
                        //fetch and store other details
                        String videoId = video.getId();       //for url
                        String audioTitle = video.getSnippet().getTitle();

                        //check the higher level of image resolution is available or not
                        String audioImageUrl = null;
                        if(video.getSnippet().getThumbnails().getMaxres() != null)   //highest resolution
                        {
                            audioImageUrl = video.getSnippet().getThumbnails().getMaxres().getUrl();
                        }
                        else if (video.getSnippet().getThumbnails().getStandard() != null)   //standard resolution
                        {
                            audioImageUrl = video.getSnippet().getThumbnails().getStandard().getUrl();
                        }
                        else if (video.getSnippet().getThumbnails().getHigh() != null)   //high resolution
                        {
                            audioImageUrl = video.getSnippet().getThumbnails().getHigh().getUrl();
                        }
                        else if (video.getSnippet().getThumbnails().getMedium() != null)    //medium resolution
                        {
                            audioImageUrl = video.getSnippet().getThumbnails().getMedium().getUrl();
                        }
                        else    //default resolution(lowest)
                        {
                            audioImageUrl = video.getSnippet().getThumbnails().getDefault().getUrl();
                        }

                        String audioDuration = video.getContentDetails().getDuration();
                        String formattedAudioDuration = formatDuration(audioDuration);
                        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;   //make url and store it for audio extracting

                        //add all details in model
                        youtubeModels.add(new YoutubeModel(audioTitle, audioImageUrl, videoUrl, formattedAudioDuration));
                    }//end of if
                }//end of for
                return youtubeModels;
            }//end of try block
            catch (IOException e)
            {
                e.printStackTrace();
            }//end of catch block
            return null;
        }



        //set all the search result in listview on home page
        @Override
        protected void onPostExecute(List<YoutubeModel> youtubeModels)
        {
            progressBar.setVisibility(View.GONE);

            //if list is not null then set all the values of list on home page in manner
            if (youtubeModels != null)
            {
                searchResults.clear();
                searchResults.addAll(youtubeModels);
                adapter.notifyDataSetChanged();
            }//end of if
            else
            {
                Toast.makeText(getContext(), "Failed to retrieve search results", Toast.LENGTH_SHORT).show();
            }//end of else
        }//end of method
    }//end of class




    //change the video duration format to normal format
    //eg. youtube store the duration in ISO format(eg. PT3M40S), we want to show as (03:47 or 3:47)
    private String formatDuration(String audioDuration)
    {
        //declare null string
        String formattedAudioDuration = "";

        //replace first two char in duration value
        audioDuration = audioDuration.replace("PT", "");

        //decalre variables to store time
        int hours = 0, minutes = 0, seconds = 0;


        //if hours symbol found
        if (audioDuration.contains("H"))
        {
            //split value upto char H and store all value which are before the char H
            String[] parts = audioDuration.split("H");
            hours = Integer.parseInt(parts[0]);
            audioDuration = parts.length > 1 ? parts[1] : "";
        }

        //if minute symbol found
        if (audioDuration.contains("M"))
        {
            String[] parts = audioDuration.split("M");
            minutes = Integer.parseInt(parts[0]);
            audioDuration = parts.length > 1 ? parts[1] : "";
        }

        //if second symbol found
        if (audioDuration.contains("S"))
        {
            seconds = Integer.parseInt(audioDuration.split("S")[0]);
        }


        //now check whether video is more than 1 hour long or not
        //if yes, then show the duration in following manner -> 1:03:20
        if (hours > 0)
        {
            formattedAudioDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        //if not found then show as -> 3:20
        else
        {
            formattedAudioDuration = String.format("%02d:%02d", minutes, seconds);
        }

        //return the new formatted duration
        return formattedAudioDuration;
    }//end of format audio method




    //to play audio file from list
    private void getAudioFileUrl(String videoUrl, Intent i)
    {

        //run this method async to avoid app crashing
        new ExtractAudioTask(i).execute(videoUrl);

    }//end of method



    //async method to extract audio from video in background
    private class ExtractAudioTask extends AsyncTask<String, Void, String>
    {

        //constructor to declare intent
        private Intent i;
        public ExtractAudioTask(Intent i) {
            this.i = i;
        }

        //when user clicks the list item it will show loading bar
        protected void onPreExecute()
        {
            super.onPreExecute();

            //change the visibility of loading bar
            progressBar.setVisibility(View.VISIBLE);
        }//end of method async



        //this method will download the audio file by using python script
        @Override
        protected String doInBackground(String... urls)
        {
            //get the url from model
            String videoUrl = urls[0];


            //calling python function with it's object to extract audio
            Python py = Python.getInstance();
            PyObject pyObject = py.getModule("extract_audio");

            //now again calling the function to get the audio file url
            PyObject result = pyObject.callAttr("extract_audio", videoUrl);
            return result.toString();
        }//end of method async



        //after downloading file, main page navigate to play audio page where user can listen music
        @Override
        protected void onPostExecute(String audioUrl)
        {
            //change the visibility of loading bar again
            progressBar.setVisibility(View.GONE);

            i.putExtra("audioUrl", audioUrl);
            startActivity(i);
        }//end of method async
    }//end of method
}//end of class

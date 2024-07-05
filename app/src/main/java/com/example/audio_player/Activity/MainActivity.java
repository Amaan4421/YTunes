package com.example.audio_player.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.audio_player.Adapter.ListAdapter;
import com.example.audio_player.BuildConfig;
import com.example.audio_player.Model.YoutubeModel;
import com.example.audio_player.R;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private EditText searchEditText;
    private ListAdapter adapter;
    private ArrayList<YoutubeModel> searchResults;
    private YouTube youTube;
    private ProgressBar progressBar;


    //on create method to show screen UI
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start the python
        if(!Python.isStarted())
        {
            Python.start(new AndroidPlatform(this ));
        }

        //get instance of python first
        Python py = Python.getInstance();

        //get ids from xml file
        searchEditText = findViewById(R.id.searchText);
        progressBar = findViewById(R.id.showLoading);
        Button searchButton = findViewById(R.id.searchButton);
        ListView searchListView = findViewById(R.id.searchList);



        //set values to arraylist and adapter
        searchResults = new ArrayList<>();
        adapter = new ListAdapter(this, searchResults);
        searchListView.setAdapter(adapter);



        //when user clicks search button
        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //calling method
                searchInYouTube();
            }
        }); //end of search button



        //when user clicks any item in list, get the video url of that item from model
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                getAudioFileUrl(searchResults.get(position).getVideoUrl());
            }
        }); //end of listview

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
    }//end of on create method




    //search query in youtube using API
    private void searchInYouTube()
    {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty())
        {
            //calling asynchronous method to search data in youtube
            new YouTubeSearchTask().execute(query);
        }
        else
        {
            Toast.makeText(this, "Please enter query to search!!!", Toast.LENGTH_SHORT).show();
        }
    }//end of search method




    //search query in youtube database
    private class YouTubeSearchTask extends AsyncTask<String, Void, List<SearchResult>>
    {
        @Override
        protected List<SearchResult> doInBackground(String... params)
        {
            try
            {
                YouTube.Search.List searchList = youTube.search().list("snippet");
                searchList.setQ(params[0]);
                searchList.setType("audio");
                searchList.setMaxResults(100L);
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
        protected void onPostExecute(List<SearchResult> searchResults) {
            if (searchResults != null)
            {
                updateSearchResultsList(searchResults);
            }
            else
            {
                Toast.makeText(MainActivity.this, "Failed to retrieve search results", Toast.LENGTH_SHORT).show();
            }
        }
    }//end of method




    //to show output related to query in listview
    private void updateSearchResultsList(List<SearchResult> results)
    {
//        searchResults.clear();
        for (SearchResult result : results)
        {
            String videoTitle = result.getSnippet().getTitle();
            String videoImageUrl = result.getSnippet().getThumbnails().getDefault().getUrl();
            String videoId = result.getId().getVideoId();

            //for testing
            // Log.d("Video ID", "Video ID: " + videoId);

            String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

            //store this video file details in model
            YoutubeModel youtubeModel = new YoutubeModel(videoTitle, videoImageUrl, videoUrl);
            searchResults.add(youtubeModel);
        }
        adapter.notifyDataSetChanged();
    }//end of method




    //to play audio file from list
    private void getAudioFileUrl(String videoUrl)
    {

        //run this method async to avoid app crashing
        new ExtractAudioTask().execute(videoUrl);

    }//end of method




    //async method to extract audio from video in background
    private class ExtractAudioTask extends AsyncTask<String, Void, String> {

        //when user clicks the list item it will show loading bar
        protected void onPreExecute()
        {
            super.onPreExecute();

            //change the visibility of loading bar
            progressBar.setVisibility(View.VISIBLE);
        }//end of method async



        //this method will download the audio file by using python script
        //file will be stored in cache memory
        @Override
        protected String doInBackground(String... urls)
        {
            //get the url from model
            String videoUrl = urls[0];

            //for testing
//            Log.d("ExtractAudioTask", "Downloading audio from URL: " + videoUrl);


            //calling python function with it's object to extract audio
            Python py = Python.getInstance();
            PyObject pyObject = py.getModule("extract_audio");

            //now again calling the function to get the audio file url
            PyObject result = pyObject.callAttr("extract_audio", videoUrl);
            return result.toString();
        }//end of method async



        //after downloading file, main page navigate to play audio page where user can listen audio
        @Override
        protected void onPostExecute(String audioUrl)
        {
            //change the visibility of loading bar again
            progressBar.setVisibility(View.GONE);

            //for testing
//            Toast.makeText(MainActivity.this, "Audio extracted successfully", Toast.LENGTH_SHORT).show();
//            Log.d("ExtractAudioTask", "Audio URL: " + audioUrl);

            //pass the audio url to play audio java file
            Intent intent = new Intent(MainActivity.this, PlayAudio.class);
            intent.putExtra("audioUrl", audioUrl);
            startActivity(intent);
        }//end of method async
    }//end of method
}//end of class

package com.example.audio_player.Activity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.audio_player.Adapter.HindiListAdapter;
import com.example.audio_player.Adapter.ListAdapter;
import com.example.audio_player.BuildConfig;
import com.example.audio_player.Fragment.SearchResultFragment;
import com.example.audio_player.Model.YoutubeModel;
import com.example.audio_player.R;
import com.example.audio_player.Utils.FetchTrendingMusic;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class MainActivity extends AppCompatActivity implements FetchTrendingMusic.FetchTrendingMusicCallback
{
    //final global variables
    private ListAdapter adapter;
    private ArrayList<YoutubeModel> searchResults;
    private YouTube youTube;
    private ProgressBar progressBar;
    private List<YoutubeModel> hindiSongs;
    private HindiListAdapter hindiListAdapter;
    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;


    //on create method to show screen UI
    @SuppressLint("MissingInflatedId")
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
        progressBar = findViewById(R.id.showLoading);
        searchButton = findViewById(R.id.searchButton);
        ListView searchListView = findViewById(R.id.searchList);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        RecyclerView hindiListView = findViewById(R.id.hindiRecyclerView);


        //when user clicks search button from toolbar, open search page
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });//end of on click method



        //set values to all arraylist and adapter
        searchResults = new ArrayList<>();
        adapter = new ListAdapter(this, searchResults);
        searchListView.setAdapter(adapter);



        //on click event for hindi songs list to play that song
        hindiSongs = new ArrayList<>();
        hindiListAdapter = new HindiListAdapter(this, hindiSongs, new HindiListAdapter.ClickEvent() {
            @Override
            public void onItemClick(YoutubeModel youtubeModel) {
                Intent i = new Intent(MainActivity.this, PlayAudio.class);
                i.putExtra("title", youtubeModel.getVideoTitle());
                i.putExtra("image", youtubeModel.getVideoImageUrl());
                getAudioFileUrl(youtubeModel.getVideoUrl(), i);
            }
        });

        //to set songs list horizontally
        hindiListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hindiListView.setAdapter(hindiListAdapter);





        //when user clicks any item in list, get the video url of that item from model and extract audio file and pass to next activity
        //also pass the image and title to next page
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                YoutubeModel item = searchResults.get(position);
                Intent i = new Intent(MainActivity.this, PlayAudio.class);
                i.putExtra("title", item.getVideoTitle());
                i.putExtra("image", item.getVideoImageUrl());
                getAudioFileUrl(item.getVideoUrl(), i);
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


        //load trending songs on home page when user opens app
        FetchTrendingMusic fetchTrendingMusic = new FetchTrendingMusic(youTube, (FetchTrendingMusic.FetchTrendingMusicCallback) this);
        fetchTrendingMusic.fetchTrendingSongs();
        fetchTrendingMusic.fetchTrendingHindiSongs();



        //set bottom navigation view and pass the references to change the page
        //eg. when user click search page then opens the SearchActivity
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {

                int id = item.getItemId();

                //when user clicks home button
                if(id == R.id.nav_home)
                {
                    Intent searchIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(searchIntent);
                    return true;
                }//end of if
                //when user clicks search button
                else if(id == R.id.nav_search)
                {
                    Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(searchIntent);
                    return true;
                }//end of else if
                return true;
            }//end of on click navigation
        });//end of on click method


    }//end of onCreate method





// fetching music methods to show popular songs on home screen..........


    //calling callback function to fetch trending music
    @SuppressLint("NotifyDataSetChanged")
    public void onFetchTrendingMusic(List<YoutubeModel> youtubeModels, List<YoutubeModel> hindiSongs)
    {
        //for worldwide trending songs
        if(youtubeModels != null)
        {
            this.searchResults.clear();
            this.searchResults.addAll(youtubeModels);
            adapter.notifyDataSetChanged();
        }//end of if

        //for hindi trending songs
        if (hindiSongs != null)
        {
            this.hindiSongs.clear();
            this.hindiSongs.addAll(hindiSongs);
            hindiListAdapter.notifyDataSetChanged();
        }//end of if
    }//end of method

    //show error
    public void onError(String error)
    {
        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
    }//end of method

    //when app closed, fetching should be stopped
    protected void onDestroy()
    {
        super.onDestroy();
        FetchTrendingMusic fetchTrendingMusic = new FetchTrendingMusic(youTube, this);
        fetchTrendingMusic.stopFetchingTrendingMusic();
    }


//fetching music methods to show songs on home screen........methods ends here



    //To play song from list, need audio extracted url from video url
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

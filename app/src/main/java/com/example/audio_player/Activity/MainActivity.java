package com.example.audio_player.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.audio_player.Adapter.HindiListAdapter;
import com.example.audio_player.Adapter.ListAdapter;
import com.example.audio_player.Adapter.RecentSongsListAdapter;
import com.example.audio_player.BuildConfig;
import com.example.audio_player.Model.SongHistoryModel;
import com.example.audio_player.Model.YoutubeModel;
import com.example.audio_player.R;
import com.example.audio_player.Utils.AudioExtractor;
import com.example.audio_player.Utils.FetchTrendingMusic;
import com.example.audio_player.Utils.HistoryDatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FetchTrendingMusic.FetchTrendingMusicCallback
{
    //final global variables
    private ListAdapter adapter;
    private ArrayList<YoutubeModel> searchResults;
    private YouTube youTube;
    private ProgressBar progressBar;
    private List<YoutubeModel> hindiSongs;
    private List<SongHistoryModel> recentSongs;
    private HindiListAdapter hindiListAdapter;
    private RecentSongsListAdapter recentSongsListAdapter;
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

        //get ids from xml file
        progressBar = findViewById(R.id.showLoading);
        searchButton = findViewById(R.id.searchButton);
        RecyclerView searchListView = findViewById(R.id.searchList);
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




//when user clicks any item in list, get the video url of that item from model and extract audio file and pass to next activity
// also pass the image and title to next page

        //on click event for worldwide trending songs list to play that song
        searchResults = new ArrayList<>();
        adapter = new ListAdapter(this, searchResults, new ListAdapter.ClickEvent() {
            @Override
            public void onItemClick(YoutubeModel youtubeModel)
            {
                Intent i = new Intent(MainActivity.this, PlayAudio.class);
                i.putExtra("title", youtubeModel.getVideoTitle());
                i.putExtra("image", youtubeModel.getVideoImageUrl());


                //call audio extracting class to get audio url to play song
                AudioExtractor audioExtractor = new AudioExtractor(MainActivity.this);
                audioExtractor.getAudioFileUrl(youtubeModel.getVideoUrl(), i, progressBar);


                //add that song into history
                HistoryDatabaseHelper db = new HistoryDatabaseHelper(MainActivity.this);
                SongHistoryModel songHistory = new SongHistoryModel();
                songHistory.setSongTitle(youtubeModel.getVideoTitle());
                songHistory.setSongImageUrl(youtubeModel.getVideoImageUrl());
                songHistory.setSongVideoId(youtubeModel.getVideoId());
                songHistory.setSongDuration(youtubeModel.getDuration());
                songHistory.setVideoUrl(youtubeModel.getVideoUrl());
                db.addSongToHistory(songHistory);
            }
        });
        searchListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        searchListView.setAdapter(adapter);          //end of worldwide songs adapter method



        //on click event for hindi songs list to play that song
        hindiSongs = new ArrayList<>();
        hindiListAdapter = new HindiListAdapter(this, hindiSongs, new HindiListAdapter.ClickEvent() {
            @Override
            public void onItemClick(YoutubeModel youtubeModel)
            {

                Intent i = new Intent(MainActivity.this, PlayAudio.class);
                i.putExtra("title", youtubeModel.getVideoTitle());
                i.putExtra("image", youtubeModel.getVideoImageUrl());

                //call audio extracting class to get audio url to play song
                AudioExtractor audioExtractor = new AudioExtractor(MainActivity.this);
                audioExtractor.getAudioFileUrl(youtubeModel.getVideoUrl(), i, progressBar);


                //add that song into history
                HistoryDatabaseHelper db = new HistoryDatabaseHelper(MainActivity.this);
                SongHistoryModel songHistory = new SongHistoryModel();
                songHistory.setSongTitle(youtubeModel.getVideoTitle());
                songHistory.setSongImageUrl(youtubeModel.getVideoImageUrl());
                songHistory.setSongVideoId(youtubeModel.getVideoId());
                songHistory.setSongDuration(youtubeModel.getDuration());
                songHistory.setVideoUrl(youtubeModel.getVideoUrl());
                db.addSongToHistory(songHistory);
            }
        });

        //to set songs list horizontally
        hindiListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hindiListView.setAdapter(hindiListAdapter);          //end of hindi songs adapter method


        //show recently played songs
        loadRecentSongs();

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
                    finish();
                    startActivity(getIntent());
                    return true;
                }//end of if

                //when user clicks search button
                else if(id == R.id.nav_search)
                {
                    Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(searchIntent);
                    return true;
                }//end of else if

                //when user clicks library button
                else if (id == R.id.nav_library)
                {
                    Intent searchIntent = new Intent(MainActivity.this, LibraryActivity.class);
                    startActivity(searchIntent);
                    return true;
                }
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
    }//end of method

    //when app closed, fetching should be stopped
    protected void onDestroy()
    {
        super.onDestroy();
        FetchTrendingMusic fetchTrendingMusic = new FetchTrendingMusic(youTube, this);
        fetchTrendingMusic.stopFetchingTrendingMusic();
    }//end of method




    //get the data from database and show by using adapter
    @SuppressLint("NotifyDataSetChanged")
    private void loadRecentSongs()
    {
        //call helper class
        HistoryDatabaseHelper db = new HistoryDatabaseHelper(this);
        recentSongs = db.getSongHistory();

        Collections.reverse(recentSongs);

        TextView recentTitle = findViewById(R.id.tv_recent);
        RecyclerView recentSongsListView = findViewById(R.id.recentRecyclerView);

        //if data found in database
        if (recentSongs.isEmpty())
        {
            recentTitle.setVisibility(View.GONE);
            recentSongsListView.setVisibility(View.GONE);
        }//end of if
        else
        {
            recentSongsListAdapter = new RecentSongsListAdapter(this, recentSongs, new RecentSongsListAdapter.ClickEvent() {
                @Override
                public void onItemClick(SongHistoryModel songHistoryModel)
                {
                    Intent i = new Intent(MainActivity.this, PlayAudio.class);
                    i.putExtra("title", songHistoryModel.getSongTitle());
                    i.putExtra("image", songHistoryModel.getSongImageUrl());

                    //call extract audio class for url
                    AudioExtractor audioExtractor = new AudioExtractor(MainActivity.this);
                    audioExtractor.getAudioFileUrl(songHistoryModel.getVideoUrl(), i, progressBar);
                }//end of on item click
            });

            //set the recycler view for showing recently played songs
            recentSongsListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recentSongsListView.setAdapter(recentSongsListAdapter);
            recentSongsListAdapter.notifyDataSetChanged();
        }//end of else
    }//end of method
}//end of class


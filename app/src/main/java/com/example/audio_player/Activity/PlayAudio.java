package com.example.audio_player.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audio_player.Adapter.RelatedSongAdapter;
import com.example.audio_player.BuildConfig;
import com.example.audio_player.Model.RelatedSongsModel;
import com.example.audio_player.R;
import com.example.audio_player.Services.BackgroundPlayService;
import com.example.audio_player.Utils.FetchRelatedSongs;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.squareup.picasso.Picasso;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import java.io.IOException;
import java.util.List;

public class PlayAudio extends AppCompatActivity {

    private ExoPlayer player;
    YouTube youTube;
    private ImageButton playPauseButton, nextButton, prevButton, shuffleButton, repeatButton;
    private ImageView favoriteButton;
    private SeekBar seekBar;
    private TextView currentTime, totalTime, songTitle;
    private Handler handler = new Handler();
    private boolean isShuffleOn = false;
    private boolean isRepeatOn = false;
    private boolean isFavorite = false;
    private RecyclerView relatedSongsList;
    private RelatedSongAdapter relatedSongAdapter;

    @OptIn(markerClass = UnstableApi.class)
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playaudio);

        //get intent to play that song
        Intent intent = getIntent();
        String audioUrl = intent.getStringExtra("audioUrl");
        String title = intent.getStringExtra("title");
        String image = intent.getStringExtra("image");


        //get variable reference from xml file
        playPauseButton = findViewById(R.id.playPauseButton);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        repeatButton = findViewById(R.id.repeatButton);
        favoriteButton = findViewById(R.id.favoriteButton);
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        ImageView songImage = findViewById(R.id.songImage);
        songTitle = findViewById(R.id.songTitle);
        relatedSongsList = findViewById(R.id.queueList);



        //set the song title and image in audio playing screen
        if(title != null)
        {
            songTitle.setText(title);
        }//end of if
        if(image != null && !image.isEmpty())
        {
            Picasso.get().load(image).into(songImage);
        }//end of if



        //handling favourite button click
        favoriteButton.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            favoriteButton.setImageResource(isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
        });//end of click listener


        //handling shuffle button click
        shuffleButton.setOnClickListener(v -> {
            isShuffleOn = !isShuffleOn;
            player.setShuffleModeEnabled(isShuffleOn);
            shuffleButton.setImageResource(isShuffleOn ? androidx.media3.session.R.drawable.media3_icon_shuffle_on : androidx.media3.session.R.drawable.media3_icon_shuffle_off);
        });//end of click listener


        //handling repeat button click
        repeatButton.setOnClickListener(v -> {
            isRepeatOn = !isRepeatOn;
            player.setRepeatMode(isRepeatOn ? ExoPlayer.REPEAT_MODE_ONE : ExoPlayer.REPEAT_MODE_OFF);
            repeatButton.setImageResource(isRepeatOn ? androidx.media3.session.R.drawable.media3_icon_repeat_one : androidx.media3.session.R.drawable.media3_icon_repeat_off);
        });//end of click listener


        //handling play/pause button click and also change the icon
        playPauseButton.setOnClickListener(v -> {
            //if song is playing then stop the song
            if (player.isPlaying())
            {
                player.pause();
                playPauseButton.setImageResource(R.drawable.baseline_play_arrow_24);
            }
            //otherwise start the song
            else
            {
                player.play();
                playPauseButton.setImageResource(R.drawable.baseline_pause_24);
            }

            //to ensure seek bar keep updating, calling handler method here
            handler.post(updateSeekBar);
        });//end of click listener


        //handling seek bar movement
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    player.seekTo(progress);
                }
                //call the method that handles updating current time
                updateCurrentTime();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });//end of click listener


        //start the song from url
        initializePlayer(audioUrl);

        //pass audio url to background service to play audio in background as well
        Intent serviceIntent = new Intent(PlayAudio.this, BackgroundPlayService.class);
        serviceIntent.setAction("ACTION_PLAY");   //set action string to pass
        serviceIntent.putExtra("audioUrl", audioUrl);   //set url string to pass
        startService(serviceIntent);

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

//        fetchRelatedVideos(videoId);
    }//end of onCreate method


//    private void fetchRelatedVideos(String videoId) {
//        new FetchRelatedSongs(youTube, videoId, new FetchRelatedSongs.FetchRelatedVideosCallback() {
//            @Override
//            public void onFetchRelatedVideos(List<RelatedSongsModel> relatedSongs) {
//                // Set up the RecyclerView with the related songs
//                relatedSongsList.setLayoutManager(new LinearLayoutManager(PlayAudio.this, RecyclerView.VERTICAL, false));
//                relatedSongAdapter = new RelatedSongAdapter(relatedSongs);
//                relatedSongsList.setAdapter(relatedSongAdapter);
//                relatedSongsList.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onError(String error) {}
//        });
//    }

    //set the exo player to play song from url
    private void initializePlayer(String audioUrl)
    {
        //build the exoplayer
        player = new ExoPlayer.Builder(this).build();

        //pass url in media and set the player
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(audioUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        playPauseButton.setImageResource(R.drawable.baseline_pause_24);


        //set total time of song
        player.addListener(new Player.Listener()
        {
            @Override
            public void onPlaybackStateChanged(int state)
            {
                if (state == Player.STATE_READY)
                {
                    totalTime.setText(getFormattedTime((int) player.getDuration()));
                    seekBar.setMax((int) player.getDuration());

                    //start seek bar progress and timing as well
                    handler.post(updateSeekBar);
                }
            }
        });//end of time listener

        //update seekbar every second to show song progress
        handler.post(updateSeekBar);
    }//end of method




    //helper method to update current time
    private void updateCurrentTime()
    {
        currentTime.setText(getFormattedTime((int) player.getCurrentPosition()));
    }//end of method



    //seek bar update
    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run()
        {
            if (player != null)
            {
                //update seek bar progress and current time
                seekBar.setProgress((int) player.getCurrentPosition());
                updateCurrentTime();

                //set timing of updating seek bar which is 1 sec here
                handler.postDelayed(this, 1000);
            }//end of if
        }
    };//end of method



    //method to format time in (mm:ss)
    private String getFormattedTime(int timeInMillis)
    {
        int minutes = (timeInMillis / 1000) / 60;
        int seconds = (timeInMillis / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }//end of method



    //when user stops the song or close the activity then release the player
    @Override
    protected void onStop()
    {
        super.onStop();
        if (player != null)
        {
            player.release();
            player = null;
        }//end of if

        //to stop updating seek bar when song is not playing
        handler.removeCallbacks(updateSeekBar);
    }//end of method



    //when user clicks the default back button provided by phone system
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }//end of method
}//end of class
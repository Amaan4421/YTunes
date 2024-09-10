package com.example.audio_player.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;


import com.example.audio_player.R;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.audio_player.Services.BackgroundPlayService;
import com.squareup.picasso.Picasso;

public class PlayAudio extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playaudio);


        //take id reference from xml file
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView back = findViewById(R.id.backButton);
        TextView audioTitle = findViewById(R.id.audio_title);
        ImageView songImage = findViewById(R.id.audio_image);
        playerView = findViewById(R.id.player_view);

        //when user clicks the back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PlayAudio.this, MainActivity.class);
                startActivity(i);
            }
        });



        //get audio file intent from previous activity(here previous activity is MainActivity)
        Intent i = getIntent();

        //check it is null or not
        if (i != null)
        {
            //get audio file title
            String title = i.getStringExtra("title");
            if(title != null)
            {
                audioTitle.setText(title);
            }//end of if


            //get audio file image
            String image = i.getStringExtra("image");
            if(image != null && !image.isEmpty())
            {
                Picasso.get().load(image).into(songImage);
            }//end of if


            //get audio file url to play
            String audioUrl = i.getStringExtra("audioUrl");
            if (audioUrl != null && !audioUrl.isEmpty())
            {
                //call method to play audio file
                initializePlayer(audioUrl);

                //pass audio url to background service to play audio in background as well
                Intent serviceIntent = new Intent(PlayAudio.this, BackgroundPlayService.class);
                serviceIntent.setAction("ACTION_PLAY");   //set action string to pass
                serviceIntent.putExtra("audioUrl", audioUrl);   //set url string to pass
                startService(serviceIntent);   //start service
            }//end of if
        }
    }//end of method



    //method to play audio file
    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer(String audioUrl)
    {
        //using exoplayer to play mp3 file
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer((Player) player);
        player.clearVideoSurface();
        playerView.setControllerShowTimeoutMs(0);

        //passing audio file url to media item
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(audioUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }//end of method



    //stop audio method
    @Override
    protected void onStop()
    {
        super.onStop();
        if (player != null)
        {
            player.release();
            player = null;
        }//end of if
    }//end of method



    //when user clicks the default back button provided by phone system
    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        //go back to home screen
//        Intent i = new Intent(PlayAudio.this, MainActivity.class);
//        startActivity(i);
//        finish();
    }//end of method
}//end of class

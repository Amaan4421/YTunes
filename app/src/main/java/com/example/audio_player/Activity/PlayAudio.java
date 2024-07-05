package com.example.audio_player.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.audio_player.Model.YoutubeModel;
import com.example.audio_player.R;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import com.squareup.picasso.Picasso;

public class PlayAudio extends AppCompatActivity {

    private Button playButton;
    private ExoPlayer player;
    private PlayerView playerView;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playaudio);


        //take id reference from xml file
        TextView audioTitle = findViewById(R.id.audio_title);
        ImageView songImage = findViewById(R.id.audio_image);
        playButton = findViewById(R.id.play_btn);
        playerView = findViewById(R.id.player_view);

//        audioTitle.setText(youtubeModel.getVideoTitle());
//        Picasso.get().load(youtubeModel.getVideoImageUrl()).into(songImage);


        //get audio file intent from previous activity(here it is MainActivity)
        Intent i = getIntent();

        //check it is null or not
        if (i != null)
        {
            String audioUrl = i.getStringExtra("audioUrl");
            if (audioUrl != null && !audioUrl.isEmpty())
            {
                //call method to play audio file
                initializePlayer(audioUrl);
            }
        }

        //on button click event, start audio file
        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //check boolean value first
                if (isPlaying)
                {
                    player.pause();
                    playButton.setText("Play");
                }//end of if
                else
                {
                    player.play();
                    playButton.setText("Pause");
                }//end of else
                isPlaying = !isPlaying;
            }//end of on click event
        });//end of button click event
    }//end of method



    //method to play audio file
    private void initializePlayer(String audioUrl)
    {
        //using exoplayer to play mp3 file
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer((Player) player);

        //passing audio file url to media item
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(audioUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        playButton.setText("Pause");
        isPlaying = true;
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



    //when user clicks the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //go back to home screen
        Intent i = new Intent(PlayAudio.this, MainActivity.class);
        startActivity(i);
        finish();
    }//end of method
}//end of class

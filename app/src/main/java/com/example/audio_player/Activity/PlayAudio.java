package com.example.audio_player.Activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.audio_player.R;

import java.io.IOException;

public class PlayAudio extends AppCompatActivity {

    private Button previousButton, playButton, nextButton;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playaudio);

        previousButton = findViewById(R.id.previous_btn);
        playButton = findViewById(R.id.play_btn);
        nextButton = findViewById(R.id.next_btn);

        String audioUrl = getIntent().getStringExtra("audioUrl");
        if (audioUrl == null || audioUrl.isEmpty()) {
            Toast.makeText(this, "Audio URL is not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load audio", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playButton.setEnabled(true);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlayback();
            }
        });

        playButton.setOnClickListener(v -> {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });

    }

    private void stopPlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //when user clicks the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = new Intent(PlayAudio.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}

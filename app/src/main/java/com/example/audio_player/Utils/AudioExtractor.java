package com.example.audio_player.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class AudioExtractor {

    private Context context;

    //constructor
    public AudioExtractor(Context context) {
        this.context = context;
    }

    //public method to extract audio URL
    public void getAudioFileUrl(String videoUrl, Intent intent, ProgressBar progressBar) {
        new ExtractAudioTask(intent, progressBar).execute(videoUrl);
    }

    //asyncTask for extracting audio from video URL
    private class ExtractAudioTask extends AsyncTask<String, Void, String> {

        private Intent intent;
        private ProgressBar progressBar;

        public ExtractAudioTask(Intent intent, ProgressBar progressBar) {
            this.intent = intent;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            //show progress bar before starting the background task
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String videoUrl = urls[0];

            //call Python script to extract the audio URL
            Python py = Python.getInstance();
            PyObject pyObject = py.getModule("extract_audio");
            PyObject result = pyObject.callAttr("extract_audio", videoUrl);

            return result.toString();
        }

        @Override
        protected void onPostExecute(String audioUrl) {
            //hide progress bar after task completion
            progressBar.setVisibility(View.GONE);

            //pass the extracted audio URL and start the PlayAudio activity
            intent.putExtra("audioUrl", audioUrl);
            context.startActivity(intent);
        }
    }
}

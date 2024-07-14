package com.example.audio_player.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.audio_player.Model.YoutubeModel;
import com.example.audio_player.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<YoutubeModel>
{

    //global variables
    Context context;
    private List<YoutubeModel> youtubeModels;


    //set adapter file
    public ListAdapter(Context context, ArrayList<YoutubeModel> youtubeModels)
    {
        super(context, R.layout.raw_list, youtubeModels);
        this.context = context;
        this.youtubeModels = youtubeModels;
    }


    //get data and set in view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        //set view
        View view = convertView;
        if (view ==  null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.raw_list, null);
        }

        //take references from xml file
        TextView audioDuration = view.findViewById(R.id.audio_duration);
        TextView audiotitle = view.findViewById(R.id.audio_title);
        ImageView audioImage = view.findViewById(R.id.videoImage);

        YoutubeModel youtubeModel = getItem(position);
        if (youtubeModel != null)
        {
            audiotitle.setText(youtubeModel.getVideoTitle());
            Picasso.get().load(youtubeModel.getVideoImageUrl()).into(audioImage);
            audioDuration.setText(youtubeModel.getDuration());
        }
        else
        {
            audiotitle.setText("Loading...");
            audioImage.setImageDrawable(null);
            audioDuration.setText("0:00");
        }
        return view;
    }
}

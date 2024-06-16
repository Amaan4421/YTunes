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

public class ListAdapter extends ArrayAdapter<YoutubeModel> {

    Context context;
    private List<YoutubeModel> youtubeModels;

    public ListAdapter(Context context, ArrayList<YoutubeModel> youtubeModels) {
        super(context, R.layout.raw_list, youtubeModels);
        this.context = context;
        this.youtubeModels = youtubeModels;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view ==  null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.raw_list, null);
        }

        TextView videotitle = view.findViewById(R.id.audio_title);
        ImageView videoImage = view.findViewById(R.id.videoImage);

        YoutubeModel youtubeModel = getItem(position);
        if (youtubeModel != null)
        {
            videotitle.setText(youtubeModel.getVideoTitle());
            Picasso.get().load(youtubeModel.getVideoImageUrl()).into(videoImage);
        }
        else
        {
            videotitle.setText("Loading...");
            videoImage.setImageDrawable(null);
        }
        return view;
    }
}

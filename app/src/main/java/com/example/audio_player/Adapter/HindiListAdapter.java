package com.example.audio_player.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audio_player.Fragment.SongDetailsFragment;
import com.example.audio_player.Model.YoutubeModel;
import com.example.audio_player.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HindiListAdapter extends RecyclerView.Adapter<HindiListAdapter.ViewHolder> {

    private final Context context;
    private final ClickEvent onItemClickListener;
    private final List<YoutubeModel> youtubeModels;

    public HindiListAdapter(Context context, List<YoutubeModel> youtubeModels, ClickEvent onItemClickListener) {
        this.context = context;
        this.youtubeModels = youtubeModels;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_hindi_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        YoutubeModel youtubeModel = youtubeModels.get(position);
        holder.audioTitle.setText(youtubeModel.getVideoTitle());
        holder.audioDuration.setText(youtubeModel.getDuration());
        Picasso.get().load(youtubeModel.getVideoImageUrl()).into(holder.audioImage);


        //pass the onClick event to main activity for playing that song
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.onItemClick(youtubeModel);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the bottom sheet with options
                SongDetailsFragment bottomSheet = SongDetailsFragment.newInstance();

                Bundle args = new Bundle();
                args.putString("videoTitle", youtubeModel.getVideoTitle());
                args.putString("duration", youtubeModel.getDuration());
                args.putString("imageUrl", youtubeModel.getVideoImageUrl());
                bottomSheet.setArguments(args);

                bottomSheet.setSongOptionsListener(new SongDetailsFragment.SongOptionsListener() {
                    @Override
                    public void onPlayNowClicked() {
                        // Handle "Play Now" action
                    }

                    @Override
                    public void onPlayNextClicked() {
                        // Handle "Play Next" action
                    }

                    @Override
                    public void onAddToPlaylistClicked() {
                        // Handle "Add to Playlist" action
                    }
                });

                // Show the bottom sheet dialog
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "SongOptionsBottomSheet");
            }
        });
    }

    @Override
    public int getItemCount() {
        return youtubeModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView audioTitle, audioDuration;
        ImageView audioImage, menuButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioTitle = itemView.findViewById(R.id.audio_title);
            audioDuration = itemView.findViewById(R.id.audio_duration);
            audioImage = itemView.findViewById(R.id.videoImage);
            menuButton = itemView.findViewById(R.id.optionButton);
        }
    }

    public interface ClickEvent {
        void onItemClick(YoutubeModel youtubeModel);
    }
}

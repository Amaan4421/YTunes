package com.example.audio_player.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.audio_player.Activity.PlayAudio;
import com.example.audio_player.Fragment.SongDetailsFragment;
import com.example.audio_player.Model.SongHistoryModel;
import com.example.audio_player.R;
import com.example.audio_player.Utils.AudioExtractor;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecentSongsListAdapter extends RecyclerView.Adapter<RecentSongsListAdapter.ViewHolder> {

    private final Context context;
    private final ClickEvent onItemClickListener;
    private final List<SongHistoryModel> songHistoryModels;

    public RecentSongsListAdapter(Context context, List<SongHistoryModel> songHistoryModels, ClickEvent onItemClickListener) {
        this.context = context;
        this.songHistoryModels = songHistoryModels;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_recent_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        SongHistoryModel songHistoryModel = songHistoryModels.get(position);
        holder.audioTitle.setText(songHistoryModel.getSongTitle());
        holder.audioDuration.setText(songHistoryModel.getSongDuration());
        Picasso.get().load(songHistoryModel.getSongImageUrl()).into(holder.audioImage);


        //pass the onClick event to main activity for playing that song
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.onItemClick(songHistoryModel);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //show the bottom sheet with options
                SongDetailsFragment bottomSheet = SongDetailsFragment.newInstance();

                Bundle args = new Bundle();
                args.putString("videoTitle", songHistoryModel.getSongTitle());
                args.putString("duration", songHistoryModel.getSongDuration());
                args.putString("imageUrl", songHistoryModel.getSongImageUrl());
                bottomSheet.setArguments(args);

                bottomSheet.setSongOptionsListener(new SongDetailsFragment.SongOptionsListener() {
                    @Override
                    public void onPlayNowClicked() {
                        holder.progressBar.setVisibility(View.VISIBLE);

                        Intent i = new Intent(context, PlayAudio.class);
                        i.putExtra("title", songHistoryModel.getSongTitle());
                        i.putExtra("image", songHistoryModel.getSongImageUrl());

                        AudioExtractor audioExtractor = new AudioExtractor(context);
                        audioExtractor.getAudioFileUrl(songHistoryModel.getVideoUrl(), i, holder.progressBar);
                    }

                    @Override
                    public void onPlayNextClicked() {
                    }

                    @Override
                    public void onAddToPlaylistClicked() {
                    }

                    @Override
                    public void onAddToFavouritesClicked() {
                    }
                });

                //show the bottom sheet dialog
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "SongOptionsBottomSheet");
            }
        });
    }

    @Override
    public int getItemCount() {
        return songHistoryModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView audioTitle, audioDuration;
        ImageView audioImage, menuButton;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audioTitle = itemView.findViewById(R.id.audio_title);
            audioDuration = itemView.findViewById(R.id.audio_duration);
            audioImage = itemView.findViewById(R.id.videoImage);
            menuButton = itemView.findViewById(R.id.optionButton);
            progressBar = itemView.findViewById(R.id.showLoading);
        }
    }

    public interface ClickEvent {
        void onItemClick(SongHistoryModel songHistoryModel);
    }
}

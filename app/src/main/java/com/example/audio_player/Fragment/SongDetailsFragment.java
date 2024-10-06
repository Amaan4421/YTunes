package com.example.audio_player.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.audio_player.R;
import com.squareup.picasso.Picasso;

public class SongDetailsFragment extends BottomSheetDialogFragment {

    private SongOptionsListener optionsListener;

    public static SongDetailsFragment newInstance() {
        return new SongDetailsFragment();
    }

    public void setSongOptionsListener(SongOptionsListener listener) {
        this.optionsListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_details_list, container, false);

        Bundle args = getArguments();
        String videoTitle = args != null ? args.getString("videoTitle") : "Unknown Title";
        String duration = args != null ? args.getString("duration") : "Unknown Duration";
        String imageUrl = args != null ? args.getString("imageUrl") : "";


        TextView songTitle = view.findViewById(R.id.audio_title);
        TextView songDuration = view.findViewById(R.id.audio_duration);
        ImageView songImage = view.findViewById(R.id.songImage);
        TextView playNow = view.findViewById(R.id.play_now);
        TextView playNext = view.findViewById(R.id.play_next);
        TextView addToPlaylist = view.findViewById(R.id.add_to_playlist);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView addToFavourites = view.findViewById(R.id.add_to_favourites);

        songTitle.setText(videoTitle);
        songDuration.setText(duration);
        Picasso.get().load(imageUrl).into(songImage);

        //set click listeners for the options
        playNow.setOnClickListener(v -> {
            if (optionsListener != null) {
                optionsListener.onPlayNowClicked();
            }
            dismiss();
        });

        playNext.setOnClickListener(v -> {
            if (optionsListener != null) {
                optionsListener.onPlayNextClicked();
            }
            dismiss();
        });

        addToPlaylist.setOnClickListener(v -> {
            if (optionsListener != null) {
                optionsListener.onAddToPlaylistClicked();
            }
            dismiss();
        });

        addToFavourites.setOnClickListener(v -> {
            if(optionsListener != null){
                optionsListener.onAddToFavouritesClicked();
            }
        });

        return view;
    }

    public interface SongOptionsListener {
        void onPlayNowClicked();
        void onPlayNextClicked();
        void onAddToPlaylistClicked();
        void onAddToFavouritesClicked();
    }
}

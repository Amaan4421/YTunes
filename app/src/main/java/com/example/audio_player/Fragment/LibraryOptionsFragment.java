package com.example.audio_player.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.audio_player.R;

@SuppressLint({"MissingInflatedId", "LocalSuppress"})
public class LibraryOptionsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        //get the buttons from xml file by their ids
        Button historyButton = view.findViewById(R.id.history);
        Button playlistButton = view.findViewById(R.id.playlist);
        Button favoritesButton = view.findViewById(R.id.favorites);
        Button settingsButton = view.findViewById(R.id.settings);

        //set click listeners for buttons
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "History Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Playlist Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Favorites Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Settings Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

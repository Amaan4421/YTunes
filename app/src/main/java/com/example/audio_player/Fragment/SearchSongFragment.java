package com.example.audio_player.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.audio_player.Activity.SearchActivity;
import com.example.audio_player.R;

public class SearchSongFragment extends Fragment {

    private EditText searchText;
    private Button searchButton;
    private ImageButton backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_song, container, false);

        searchText = view.findViewById(R.id.searchText);
        searchButton = view.findViewById(R.id.searchButton);
        backButton = view.findViewById(R.id.backButton);

        //set listener to take query from user and pass it to SearchActivity for further process
        searchButton.setOnClickListener(v -> {
            String query = searchText.getText().toString().trim();
            if (!query.isEmpty())
            {
                //passing query to the activity to load SearchResultFragment
                ((SearchActivity) getActivity()).loadSearchResults(query);
            }//end of if
            else
            {
                Toast.makeText(getContext(), "Search can't be empty!!!", Toast.LENGTH_SHORT).show();
            }//end of else
        });
        return view;
    }//end of onCreateView method
}//end of class

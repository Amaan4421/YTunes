package com.example.audio_player.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.audio_player.Fragment.SearchResultFragment;
import com.example.audio_player.Fragment.SearchSongFragment;
import com.example.audio_player.R;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Load the SearchSongFragment first where user can search songs
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new SearchSongFragment())
                    .commit();
        }
    }


    //pass the search query to result page to show songs related to query
    public void loadSearchResults(String query)
    {
        //call fragment class
        SearchResultFragment searchResultFragment = SearchResultFragment.newInstance(query);

        //Replace SearchSongFragment with SearchResultFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, searchResultFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

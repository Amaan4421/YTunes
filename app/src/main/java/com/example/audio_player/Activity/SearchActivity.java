package com.example.audio_player.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.audio_player.Fragment.SearchResultFragment;
import com.example.audio_player.Fragment.SearchSongFragment;
import com.example.audio_player.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //load the SearchSongFragment first where user can search songs
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new SearchSongFragment())
                    .commit();
        }

        //set bottom navigation view and pass the references to change the page
        //eg. when user click search page then opens the SearchActivity
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {

                int id = item.getItemId();

                //when user clicks home button
                if(id == R.id.nav_home)
                {
                    Intent searchIntent = new Intent(SearchActivity.this, MainActivity.class);
                    startActivity(searchIntent);
                    return true;
                }//end of if

                //when user clicks search button
                else if(id == R.id.nav_search)
                {
                    finish();
                    startActivity(getIntent());
                    return true;
                }//end of else if

                //when user clicks library button
                else if (id == R.id.nav_library)
                {
                    Intent searchIntent = new Intent(SearchActivity.this, LibraryActivity.class);
                    startActivity(searchIntent);
                    return true;
                }
                return true;
            }//end of on click navigation
        });//end of on click method
    }


    //pass the search query to result page to show songs related to query
    public void loadSearchResults(String query)
    {
        //call fragment class
        SearchResultFragment searchResultFragment = SearchResultFragment.newInstance(query);

        //replace SearchSongFragment with SearchResultFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, searchResultFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

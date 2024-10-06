package com.example.audio_player.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.audio_player.Fragment.LibraryOptionsFragment;
import com.example.audio_player.Fragment.SearchResultFragment;
import com.example.audio_player.Fragment.SearchSongFragment;
import com.example.audio_player.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LibraryActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //to load fragment in screen
        loadFragment(new LibraryOptionsFragment());


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
                    Intent searchIntent = new Intent(LibraryActivity.this, MainActivity.class);
                    startActivity(searchIntent);
                    return true;
                }//end of if

                //when user clicks search button
                else if(id == R.id.nav_search)
                {
                    Intent searchIntent = new Intent(LibraryActivity.this, SearchActivity.class);
                    startActivity(searchIntent);
                    return true;
                }//end of else if

                //when user clicks library button
                else if (id == R.id.nav_library)
                {
                    finish();
                    startActivity(getIntent());
                    return true;
                }
                return true;
            }//end of on click navigation
        });//end of on click method
    }

    //method to load the fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);  // 'frame' is the id of FrameLayout in the activity_main.xml
        transaction.commit();
    }
}

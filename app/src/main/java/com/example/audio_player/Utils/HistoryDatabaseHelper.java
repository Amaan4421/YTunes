package com.example.audio_player.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.audio_player.Model.SongHistoryModel;

import java.util.ArrayList;

public class HistoryDatabaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "audio_player.db";

    //version to keep track of database tables
    private static final int DATABASE_VERSION = 1;

    //table name
    private static final String TABLE_HISTORY = "history";

    //history table column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "songTitle";
    private static final String COLUMN_IMAGE = "songImageUrl";
    private static final String COLUMN_VIDEO_ID = "songVideoId";
    private static final String COLUMN_VIDEO_DURATION = "songDuration";
    private static final String COLUMN_VIDEO_URL = "songVideoUrl";


    //constructor
    public HistoryDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //create table query
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_VIDEO_ID + " TEXT, "
                + COLUMN_VIDEO_DURATION + " TEXT, "
                + COLUMN_VIDEO_URL + " TEXT)";
        db.execSQL(CREATE_HISTORY_TABLE);
        Log.d("DB_CREATE", "History table created");
    }//end of method


    //call if any update required in table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }//end of method


    //method to add a song to the history table
    public void addSongToHistory(SongHistoryModel songHistory)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, songHistory.getSongTitle());
        values.put(COLUMN_IMAGE, songHistory.getSongImageUrl());
        values.put(COLUMN_VIDEO_ID, songHistory.getSongVideoId());
        values.put(COLUMN_VIDEO_DURATION, songHistory.getSongDuration());
        values.put(COLUMN_VIDEO_URL, songHistory.getVideoUrl());
        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }//end of method


    //method to get data from history table
    @SuppressLint("Range")
    public ArrayList<SongHistoryModel> getSongHistory()
    {
        //create arraylist to store data from table
        ArrayList<SongHistoryModel> songHistoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //SELECT query to get all the columns from the history table
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY;
        Cursor cursor = db.rawQuery(selectQuery, null);

        //check if the cursor has results before attempting to read it
        if (cursor != null && cursor.moveToFirst())
        {
            //get all data from one single row
            do
            {
                SongHistoryModel songHistory = new SongHistoryModel();

                //correctly retrieve column values using their proper index
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int imageUrlIndex = cursor.getColumnIndex(COLUMN_IMAGE);
                int videoIdIndex = cursor.getColumnIndex(COLUMN_VIDEO_ID);
                int durationIndex = cursor.getColumnIndex(COLUMN_VIDEO_DURATION);
                int videoUrlIndex = cursor.getColumnIndex(COLUMN_VIDEO_URL);

                //checking if the column exists before accessing the value
                if (idIndex != -1)
                {
                    songHistory.setId(cursor.getInt(idIndex));
                }//end of if
                if (titleIndex != -1)
                {
                    songHistory.setSongTitle(cursor.getString(titleIndex));
                }//end of if
                if (imageUrlIndex != -1)
                {
                    songHistory.setSongImageUrl(cursor.getString(imageUrlIndex));
                }//end of if
                if (videoIdIndex != -1)
                {
                    songHistory.setSongVideoId(cursor.getString(videoIdIndex));
                }//end of if
                if (durationIndex != -1)
                {
                    songHistory.setSongDuration(cursor.getString(durationIndex));
                }//end of if
                if (videoUrlIndex != -1)
                {
                    songHistory.setVideoUrl(cursor.getString(videoUrlIndex));
                }//end of if
                songHistoryList.add(songHistory);
            }//end of do
            while (cursor.moveToNext());
        }//end of method

        //close the cursor after reading
        if (cursor != null)
        {
            cursor.close();
        }//end of method

        //close database
        db.close();

        //return all data in list
        return songHistoryList;
    }//end of method
}//end of class


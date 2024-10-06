package com.example.audio_player.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.ExoPlayer.Builder;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.ui.PlayerNotificationManager;

import com.example.audio_player.Activity.MainActivity;

@UnstableApi
public class BackgroundPlayService extends MediaSessionService
{

    //global variables
    private ExoPlayer exoPlayer;
    private PlayerNotificationManager playerNotificationManager;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;


    //on create method
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate()
    {
        super.onCreate();

        //create exo player reference
        exoPlayer = new Builder(this).build();

        //first check sdk version, if satisfied then create notification in user's phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createNotification();
        }//end of if


        //create notification player to manage audio playback from notification
        playerNotificationManager = new PlayerNotificationManager.Builder(
                this, NOTIFICATION_ID, CHANNEL_ID, new PlayerNotificationManager.MediaDescriptionAdapter()
        {

            //set title
            @Override
            public CharSequence getCurrentContentTitle(Player player)
            {
                return "YTunes";
            }//end of method



            //play audio by getting audio url from intent
            @Nullable
            @Override
            public PendingIntent createCurrentContentIntent(Player player)
            {
                //get intent from previous activity(here previous activity is PlayAudio)
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                return PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            }//end of method



            //for artist name
            @Nullable
            @Override
            public CharSequence getCurrentContentText(Player player)
            {
                return null;
            }//end of method



            //for icon in notification
            @Nullable
            @Override
            public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback)
            {
                return null;
            }//end of method
        }).setNotificationListener(new PlayerNotificationManager.NotificationListener()     //now set the notification
        {
            //when audio is running or until user clears the notification, show notification
            @Override
            public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing)
            {
                startForeground(notificationId, notification);
            }//end of method

            //when user clears the notification
            @Override
            public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                stopSelf();
            }//end of method
        }).build();

        //set player in notification
        playerNotificationManager.setPlayer(exoPlayer);
    }//end of on create method



    //in-built method
    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo)
    {
        return null;
    }//end of method



    //to get Intent here and bind
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        super.onBind(intent);
        return null;
    }//end of method



    //to start audio in notification, start command used and it will take data such us audio url from pending intent method
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        //check intent got from other activity is not null
        if (intent != null && intent.getAction() != null)
        {
            String action = intent.getAction();

            //check action string is not null
            if (action != null && action.equals("ACTION_PLAY"))
            {
                String audioUrl = intent.getStringExtra("audioUrl");

                //check audio url is valid and not null
                if (audioUrl != null && !audioUrl.isEmpty())
                {
                    //if all condition satisfied, play audio file
                    initializePlayer(audioUrl);
                }//end of inner if
            }//end of inner if
            else if (action.equals("ACTION_PAUSE")) {
                if (exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                    updateNotification();
                }
            }
        }//end of if
        else
        {
            //show error msg
            Toast.makeText(this, "Error Playing Your File!!!", Toast.LENGTH_SHORT).show();
        }//end of else
        return START_STICKY;
    }//end of method



    //play audio from url
    private void initializePlayer(String audioUrl)
    {
        //set url in media item to play
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(audioUrl));
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        exoPlayer.play();
    }//end of method

    private void updateNotification() {
        // Update your notification based on whether the player is playing or paused
        playerNotificationManager.setPlayer(exoPlayer);
    }



    //if user removes the notification, stop the player and release
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        //release player
        if (exoPlayer != null)
        {
            exoPlayer.release();
            exoPlayer = null;
        }//end of if

        //remove from the notification as well
        if (playerNotificationManager != null)
        {
            playerNotificationManager.setPlayer(null);
        }//end of if
    }//end of method



    //when audio completed
    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        //stop playing
        stopSelf();
    }//end of method



    //create notification and set properties such as name, logo, image, etc...
    private void createNotification()
    {
        //null variable
        NotificationChannel channel = null;

        //instance of notification manager
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        //check sdk version, if satisfied then create notification in user's phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            //set the notification channel to null variable
            channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Notification",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music notification channel for foreground service!!!");  //set description of notification
            notificationManager.createNotificationChannel(channel);  //set channel into notification section
        }//end of if
    }//end of method
}//end of class
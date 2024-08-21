package com.safe_keep.app;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseUser;

public class Notification
{

    public static Notification Instance;
    private final FirebaseUser user;
    private final String CHANNEL_ID = "CHANNEL_ID_NOTIFICATION";

    public Notification(FirebaseUser user, Context cn) throws Exception
    {
        if(Instance == null)
            Instance = this;
        else
            throw new Exception("Notification is meant to be created once");

        this.user = user;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            //requestPermission(cn);
        }
    }
    /*
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestPermission(Context cn)
    {
        if(ContextCompat.checkSelfPermission(cn, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.class, new String[] {Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }
    */
    public void sendNotification(Context cn)
    {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(cn, CHANNEL_ID);

        builder.setSmallIcon(R.drawable.safekeep);
        builder.setContentTitle("SafeKeep alert!");
        builder.setContentText("Help ...");
        builder.setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        // Change this later to different class
        Intent intent = new Intent(cn, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(cn, 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) cn.getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager != null)
        {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is not in the Support Library.
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SafeKeep", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Some des");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(0, builder.build());
        }
    }


}

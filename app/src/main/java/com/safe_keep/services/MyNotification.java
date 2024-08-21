package com.safe_keep.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseUser;
import com.safe_keep.app.MapsActivity;
import com.safe_keep.app.R;

public class MyNotification
{
    public static MyNotification Instance;
    private final FirebaseUser user;
    private final String CHANNEL_ID = "CHANNEL_ID_NOTIFICATION";
    public NotificationCompat.Builder builder;
    public MyNotification(FirebaseUser user, Context cn, String[] userList) throws Exception
    {
        if(Instance == null)
            Instance = this;
        else
            throw new Exception("Notification is meant to be created once");

        this.user = user;
        createFrame(cn, userList);
    }

    private void createFrame(Context cn, String[] userList)
    {
        builder =
                new NotificationCompat.Builder(cn, CHANNEL_ID);

        builder.setSmallIcon(R.drawable.safekeep);
        builder.setContentTitle("SafeKeep alert!");
        builder.setContentText("Help ...");
        builder.setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        // Change this later to different class
        Intent intent = new Intent(cn, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("guards", userList);

        PendingIntent pendingIntent = PendingIntent.getActivity(cn, 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
    }

    public void updateContactList(Context cn, String[] userList)
    {
        // Change this later to different class
        Intent intent = new Intent(cn, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("guards", userList);

        PendingIntent pendingIntent = PendingIntent.getActivity(cn, 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
    }

    public void sendNotification(Context cn, String conText)
    {
        NotificationManager notificationManager = (NotificationManager) cn.getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager != null)
        {
            builder.setContentText(conText);

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

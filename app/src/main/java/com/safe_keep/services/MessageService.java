package com.safe_keep.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageService extends Service
{
    private DatabaseReference messagesRef;
    private ValueEventListener messagesListener;
    private FirebaseUser user;

    @Override
    public void onCreate()
    {
        super.onCreate();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://safekeep-36fd7-default-rtdb.europe-west1.firebasedatabase.app/");
        messagesRef = database.getReference("messages");
        user = FirebaseAuth.getInstance().getCurrentUser();
        listenForMessages();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    // Not bound
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (messagesRef != null && messagesListener != null)
            messagesRef.removeEventListener(messagesListener);
    }

    private void listenForMessages()
    {
        messagesListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null && message.getReceiverId().equals(user.getEmail()))
                    {
                        // Handle the received message
                        //Log.d("ReceiveMessage", "Received message from " + message.getSenderId() + ": " + message.getMessage());
                        MyNotification.Instance.sendNotification(getApplicationContext(), message.getMessage());
                        // Delete later the message from the server
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                //Add later
                //Log.e("ReceiveMessage", "Failed to read messages", error.toException());
            }
        };
        messagesRef.addValueEventListener(messagesListener);
    }

}
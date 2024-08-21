package com.safe_keep.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageController
{
    public static void sendMessage(String senderId, String receiverId, String messageText)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://safekeep-36fd7-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference messagesRef = database.getReference("messages");

        String messageId = messagesRef.push().getKey();
        if (messageId != null)
        {
            Message message = new Message(senderId, receiverId, messageText);
            messagesRef.child(messageId).setValue(message);
        }
    }
}

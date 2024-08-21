package com.safe_keep.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TempActivity extends Activity {

    FirebaseAuth auth;
    Button not;
    Button newEventButton;
    Button mapButton;
    Button contactListButton;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        auth = FirebaseAuth.getInstance();
        not = findViewById(R.id.not);
        newEventButton = findViewById(R.id.newEvent);
        contactListButton = findViewById(R.id.contactListButton);
        mapButton = findViewById(R.id.mapButton);
        user = auth.getCurrentUser();

        // Set OnClickListener for the 'not' button
        not.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your notification functionality here
            }
        });

        // Set OnClickListener for the 'newEventButton'
        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNewEvent();
            }
        });

        // Set OnClickListener for the 'contactListButton'
        contactListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContactList.class);
                startActivity(intent);
                finish();
            }
        });

        // Set OnClickListener for the 'mapButton'
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void callNewEvent() {
        // Function for creating a new watch event, user will choose which contacts will
        // guard them, where and for how long
        Intent intent = new Intent(getApplicationContext(), ContactList.class);
        startActivity(intent);
        finish();
    }
}

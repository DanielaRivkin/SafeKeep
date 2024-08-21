package com.safe_keep.app;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContactList extends AppCompatActivity {

    /*
    This class implements a contact  list, the user can see every contact that
    has the app installed on their phone
    TODO: function getContactList
    TODO: check if contact is in user-database
    TODO: add to guards function
    */
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private ListView contactListView;
    private ContactAdapter contactAdapter;
    Button addAsKeeperButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        // Initialize ListView and set adapter
        contactListView = findViewById(R.id.contactList);
        contactAdapter = new ContactAdapter(this, new ArrayList<>());
        contactListView.setAdapter(contactAdapter);
        addAsKeeperButton = findViewById(R.id.add_as_keeper);

        // Check for contact permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            loadContacts();
        }


        addAsKeeperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<Contact> highlightedContacts = contactAdapter.getHighlightedContacts();
                List<Contact> highlightedContactsList = new ArrayList<>(highlightedContacts);
                contactAdapter = new ContactAdapter(ContactList.this, highlightedContactsList);
                contactListView.setAdapter(contactAdapter);
            }
        });
    }

    private void loadContacts() {
        List<Contact> contacts = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            List<Task<Void>> tasks = new ArrayList<>();

            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                tasks.add(validateContact(name, phoneNumber, contacts));
            }
            cursor.close();

            // Wait for all tasks to complete
            Tasks.whenAll(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // Update adapter with loaded contacts
                    contactAdapter = new ContactAdapter(ContactList.this, contacts);
                    contactListView.setAdapter(contactAdapter);
                }
            });
        }
    }

    private Task<Void> validateContact(String name, String phoneNumber, List<Contact> contactList) {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        db.collection("users").whereEqualTo("phone", phoneNumber)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Phone number exists in database, add to contact list
                            contactList.add(new Contact(name, phoneNumber));
                        }
                        tcs.setResult(null);
                    }
                });

        return tcs.getTask();
    }


    @Override
    public void onBackPressed() {
        // returns to last screen when pressing the back button
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

}
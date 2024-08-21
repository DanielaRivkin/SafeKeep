package com.safe_keep.app;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Custom adapter to bind contact data to ListView
public class ContactAdapter extends BaseAdapter {
    private Context context;
    private List<Contact> contactList;
    private Set<Contact> highlightedContacts = new HashSet<>();


    // Constructor to initialize context and contact list
    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
        }

        // Get the current contact
        Contact contact = contactList.get(position);

        // Set the contact name and phone number to the respective TextViews
        TextView nameTextView = convertView.findViewById(R.id.contact_name);
        TextView numberTextView = convertView.findViewById(R.id.contact_number);

        nameTextView.setText(contact.getName());
        numberTextView.setText(contact.getPhoneNumber());

        // Highlight the item if it's in the highlighted list
        if (highlightedContacts.contains(contact)) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.lavender));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        // Set an OnClickListener for the convertView to toggle highlighting
        View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (highlightedContacts.contains(contact)) {
                    highlightedContacts.remove(contact);
                    finalConvertView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                } else {
                    highlightedContacts.add(contact);
                    finalConvertView.setBackgroundColor(ContextCompat.getColor(context, R.color.lavender));
                }
            }
        });

        return convertView;
    }


    public Set<Contact> getHighlightedContacts() {
        return highlightedContacts;
    }
}
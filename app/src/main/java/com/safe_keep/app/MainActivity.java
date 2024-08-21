package com.safe_keep.app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.safe_keep.services.MessageService;
import com.safe_keep.services.MyNotification;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    // Firebase authentication instance
    private FirebaseAuth auth;

    // UI elements
    private TextView textView;
    private FirebaseUser user;
    private Button buttonLogout;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private Map<Integer, Class<?>> activityMap = new HashMap<>();
    private TextView dateView;
    private Button buttonTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Find views by their IDs
        textView = findViewById(R.id.monthYearTV);
        buttonLogout = findViewById(R.id.logout);
        dateView = findViewById(R.id.date);
        buttonTemp = findViewById(R.id.TempActivity);


        // Check if a user is already logged in
        user = auth.getCurrentUser();
        if (user == null) {
            // If no user is logged in, redirect to the login activity


            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        } else {
            // If user is logged in, set the email in the textView
            textView.setText(user.getEmail());
        }

        try {
            // Asks user for permission to send notifications, read contacts, location
            // for now we can assume that the user grants permission for everything

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            {requestPermissions(new String[] {android.Manifest.permission.POST_NOTIFICATIONS,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, 101);
            }

            // Add user_list
            MyNotification notification = new MyNotification(user, getApplicationContext(), new String[] {"netane54544@gmail.com"});
        } catch (Exception e) {
            // Does nothing
        }

        // Initialize widgets and set initial month view
        initWidgets();
        selectedDate = LocalDate.now();
        setMonthView();

        // Set click listener for logout button
        buttonLogout.setOnClickListener(view -> {
            // Sign out the user
            auth.signOut();

            // Redirect to the login activity
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

        buttonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TempActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the activityMap with menu item IDs and activity classes
        activityMap.put(R.id.bottom_home, MainActivity.class);
        activityMap.put(R.id.bottom_search, SearchActivity.class);
        activityMap.put(R.id.bottom_settings, SettingsActivity.class);
        activityMap.put(R.id.bottom_profile, ProfileActivity.class);

        // Bottom navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Class<?> activityClass = activityMap.get(item.getItemId());
            if (activityClass != null) {
                startActivity(new Intent(getApplicationContext(), activityClass));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        // Set click listener for open date options
        dateView.setOnClickListener(view -> {
            // Display new_date.xml layout as a dialog
            showNewDateDialog();
        });

        startService(new Intent(this, MessageService.class));
    }

    // Display new_date.xml layout as a dialog
    private void showNewDateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_date, null);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when OK button is clicked
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Initialize RecyclerView and TextView
    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    // Set up the month view
    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    // Create an ArrayList of strings representing the days in the selected month
    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    // Format the date to display month and year
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    // Action when previous month button is clicked
    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    // Action when next month button is clicked
    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    // Handle item click in the RecyclerView
    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}

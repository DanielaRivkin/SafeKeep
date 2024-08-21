package com.safe_keep.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashMap;
import java.util.Map;

/**
 * The ProfileActivity class represents the profile screen of the application.
 * It displays user profile information and handles bottom navigation.
 */
public class ProfileActivity extends AppCompatActivity {

    // Map to map menu item IDs to activity classes
    private Map<Integer, Class<?>> activityMap = new HashMap<Integer, Class<?>>() {{
        put(R.id.bottom_home, MainActivity.class);
        put(R.id.bottom_search, SearchActivity.class);
        put(R.id.bottom_settings, SettingsActivity.class);
        put(R.id.bottom_profile, ProfileActivity.class);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

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
    }
}

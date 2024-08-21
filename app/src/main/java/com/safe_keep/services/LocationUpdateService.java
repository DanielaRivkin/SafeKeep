package com.safe_keep.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.maps.model.LatLng;
import com.safe_keep.app.R;
import com.safe_keep.services.MessageController;

public class LocationUpdateService extends Service
{
    private static final String CHANNEL_ID = "LocationUpdateServiceChannel";

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private LatLng geofenceCenter;
    private float geofenceRadius;

    @Override
    public void onCreate() {
        super.onCreate();
        createLocationRequest();
        createLocationCallback();
        startLocationUpdates();
        createNotificationChannel();
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    checkGeofence(location); // Check geofence for the new location
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates()
    {
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,
                locationCallback,
                null /* Looper */);
    }

    private void checkGeofence(Location location) {
        if (geofenceCenter != null) {
            float[] distance = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    geofenceCenter.latitude, geofenceCenter.longitude, distance);
            Log.d("LocationUpdateService", "Distance to geofence center: " + distance[0]);
            Log.d("LocationUpdateService", "Geofence radius: " + geofenceRadius);
            boolean isInsideGeofence = distance[0] < geofenceRadius;
            if (isInsideGeofence)
            {
                Log.d("LocationUpdateService", "Inside geofence");
            } else {
                Log.d("LocationUpdateService", "Outside geofence");
                notifyGuards();
            }
        }
    }


    private void notifyGuards() {
        String[] userList = {"netane54544@gmail.com"};

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        for (String g : userList)
        {
            String dM = String.format("%s is in Danger! Your help is needed", user.getDisplayName());
            MessageController.sendMessage(user.getEmail(), g, dM);
        }
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Location Update Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        geofenceCenter = intent.getParcelableExtra("geofenceCenter");
        geofenceRadius = intent.getFloatExtra("geofenceRadius", 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Update Service")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.safekeep)
                .build();

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
    }

}

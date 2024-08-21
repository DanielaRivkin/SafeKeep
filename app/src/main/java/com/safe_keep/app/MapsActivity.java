package com.safe_keep.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.safe_keep.services.LocationUpdateService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private LatLng geofenceCenter;
    private float geofenceRadius;
    private LocationCallback locationCallback;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    private void createLocationRequest()
    {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback()
    {
        locationCallback = new LocationCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    // Update the map center based on user location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();
        createLocationCallback();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Move the map to the updated location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null)
                        {
                            LatLng gLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gLocation, 18));
                            mMap.addMarker(new MarkerOptions().position(gLocation).title("Your position"));
                        }
                    }
                });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                showRadiusInputDialog(latLng);
            }
        });
    }

    private void startLocationUpdateService()
    {
        Intent serviceIntent = new Intent(this, LocationUpdateService.class);
        serviceIntent.putExtra("geofenceCenter", geofenceCenter);
        serviceIntent.putExtra("geofenceRadius", geofenceRadius);

        ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
    }

    private void showRadiusInputDialog(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Radius");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String radiusStr = input.getText().toString();
                if (!radiusStr.isEmpty()) {
                    double radius = Double.parseDouble(radiusStr);
                    drawCircle(latLng, radius);
                    addGeofence(latLng, radius);
                    startLocationUpdateService();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addGeofence(LatLng latLng, double radius) {
        geofenceCenter = latLng;
        geofenceRadius = (float) radius;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Start requesting location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Stop requesting location updates
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void drawCircle(final LatLng latLng, final double radius) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(radius)
                        .strokeColor(Color.BLUE)
                        .fillColor(0x30ff0000)
                        .strokeWidth(2);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addCircle(circleOptions);
                    }
                });
            }
        });
    }
}

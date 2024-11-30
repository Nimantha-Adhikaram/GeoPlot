package com.example.splashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.PolylineOptions;

import com.google.android.gms.tasks.OnSuccessListener;

public class Shortest extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private Button showLocationButton;
    private Spinner unitSpinner;
    private TextView distanceTextView;
    private CardView distanceCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortest);

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the SupportMapFragment and set the callback
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize the button to show the current location
        showLocationButton = findViewById(R.id.showLocationButton);
        showLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });

        // Spinner for selecting distance unit
        unitSpinner = findViewById(R.id.unitSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.distance_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        // TextView and CardView to display distance
        distanceTextView = findViewById(R.id.distanceTextView);
        distanceCardView = findViewById(R.id.distanceCardView);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this::onMapLongClick);
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        }
                    }
                });
    }

    private void onMapLongClick(LatLng latLng) {
        if (currentLocation != null) {
            LatLng destination = latLng;
            mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

            // Draw a line to the destination
            drawShortestPath(currentLocation, destination);
        } else {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawShortestPath(Location currentLocation, LatLng destination) {
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addPolyline(new PolylineOptions().add(currentLatLng, destination));

        // Calculate distance
        float[] results = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                destination.latitude, destination.longitude, results);
        float distanceInMeters = results[0];

        // Convert and display distance based on selected unit
        String selectedUnit = unitSpinner.getSelectedItem().toString();
        displayDistance(distanceInMeters, selectedUnit);
    }

    private void displayDistance(float distanceInMeters, String unit) {
        float convertedDistance;
        String unitLabel;

        if (unit.equals("Kilometers")) {
            convertedDistance = distanceInMeters / 1000; // Convert meters to kilometers
            unitLabel = " km";
        } else {
            convertedDistance = distanceInMeters; // Keep distance in meters
            unitLabel = " meters";
        }

        distanceTextView.setText(String.format("Distance: %.2f%s", convertedDistance, unitLabel));
        distanceCardView.setVisibility(View.VISIBLE); // Make sure the CardView is visible
    }
}

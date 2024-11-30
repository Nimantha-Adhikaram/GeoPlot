package com.example.splashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class AreaNew extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "AreaNew";
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private List<LatLng> pathPoints = new ArrayList<>();
    private Polygon polygon;
    private TextView areaTextView;
    private Button startButton, addMarkerButton, stopButton, removeMarkerButton;
    private List<Marker> markers = new ArrayList<>(); // List to store markers
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_new);

        try {
            // Initialize the map fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            } else {
                Log.e(TAG, "Map fragment is null");
            }

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            areaTextView = findViewById(R.id.areaTextView);
            startButton = findViewById(R.id.startButton);
            addMarkerButton = findViewById(R.id.addMarkerButton);
            stopButton = findViewById(R.id.stopButton);
            removeMarkerButton = findViewById(R.id.removeMarkerButton);

            // Set up location callback
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    for (Location location : locationResult.getLocations()) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                }
            };

            startButton.setOnClickListener(view -> startTracking());
            addMarkerButton.setOnClickListener(view -> addMarker());
            stopButton.setOnClickListener(view -> stopTracking());
            removeMarkerButton.setOnClickListener(view -> removeLastMarker()); // Add remove marker functionality

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Error initializing the map", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableUserLocation();
        }
    }

    private void enableUserLocation() {
        if (mMap != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fetchCurrentLocation();
        }
    }

    private void fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            pathPoints.add(currentLocation); // Add first point (start location)
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Start Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            startButton.setVisibility(Button.VISIBLE);
                            addMarkerButton.setVisibility(Button.VISIBLE);
                            stopButton.setVisibility(Button.VISIBLE);
                            removeMarkerButton.setVisibility(Button.VISIBLE);
                        } else {
                            Log.e(TAG, "Couldn't get current location");
                            Toast.makeText(AreaNew.this, "Couldn't get current location", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching current location: ", e);
                        Toast.makeText(AreaNew.this, "Error fetching current location", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void startTracking() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            fetchCurrentLocation();
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMarker() {
        if (currentLocation != null) {
            pathPoints.add(currentLocation);
            Marker marker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker " + pathPoints.size()));
            markers.add(marker); // Add marker to the list

            if (pathPoints.size() >= 3) {
                drawPolygon();
            }
        } else {
            Toast.makeText(this, "Current location is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeLastMarker() {
        if (!markers.isEmpty()) {
            // Remove last marker
            Marker lastMarker = markers.remove(markers.size() - 1);
            lastMarker.remove();

            // Remove last point from pathPoints
            pathPoints.remove(pathPoints.size() - 1);

            // Redraw the polygon with remaining points
            if (polygon != null) {
                polygon.remove();
            }
            if (pathPoints.size() >= 3) {
                drawPolygon();
            } else {
                areaTextView.setText("Area: ");
            }
        } else {
            Toast.makeText(this, "No markers to remove", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback);

        if (currentLocation != null) {
            pathPoints.add(currentLocation); // Add stop point
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("Stop Location"));
            drawPolygon();

            if (polygon != null) {
                double area = SphericalUtil.computeArea(polygon.getPoints());
                areaTextView.setText("Area: " + area + " square meters");
            } else {
                Toast.makeText(this, "Polygon not complete", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void drawPolygon() {
        if (polygon != null) {
            polygon.remove();
        }
        PolygonOptions polygonOptions = new PolygonOptions().addAll(pathPoints).fillColor(0x3300FF00).strokeColor(0xFF00FF00).strokeWidth(5);
        polygon = mMap.addPolygon(polygonOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

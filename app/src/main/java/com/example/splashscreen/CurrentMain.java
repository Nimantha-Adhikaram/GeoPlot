package com.example.splashscreen;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CurrentMain extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private TextView textLatLong, textHeight, textEasting, textNorthing, textAccuracy, textSatellites;
    private ProgressBar progressBar;
    private Spinner spinnerCoordinateSystem;
    private LocationCallback locationCallback;
    private LocationManager locationManager;
    private int visibleSatellitesCount = 0;
    private int usedSatellitesCount = 0;

    // Firebase Firestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        textLatLong = findViewById(R.id.textLatLong);
        textHeight = findViewById(R.id.textHeight);
        progressBar = findViewById(R.id.progressBar);
        textEasting = findViewById(R.id.textEasting);
        textNorthing = findViewById(R.id.textNorthing);
        textAccuracy = findViewById(R.id.textAccuracy);
        textSatellites = findViewById(R.id.textSatellites);
        spinnerCoordinateSystem = findViewById(R.id.spinnerCoordinateSystem);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Set up the coordinate system spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.coordinate_system_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCoordinateSystem.setAdapter(adapter);

        findViewById(R.id.buttonGetCurrentLocation).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CurrentMain.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION_PERMISSION);
            } else {
                getCurrentLocation();
            }
        });

        // Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    updateUIWithLocation(location);
                }
                progressBar.setVisibility(View.GONE);
            }
        };

        // Handle Save to Database button clicks
        findViewById(R.id.buttonSaveToDatabase).setOnClickListener(v -> saveDataToFirestore());

        // Handle View Last Saved Data button click
        findViewById(R.id.buttonViewLastSavedData).setOnClickListener(v -> viewLastSavedData());

        // Handle Saved Data button click (previous feature)
        findViewById(R.id.buttonSavedData).setOnClickListener(v -> {
            Intent intent = new Intent(CurrentMain.this, SavedDataActivity.class);
            startActivity(intent);
        });
    }

    private void getCurrentLocation() {
        progressBar.setVisibility(View.VISIBLE);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setMaxWaitTime(3000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        initializeGnssStatusListener();
    }

    private void initializeGnssStatusListener() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            GnssStatus.Callback gnssStatusCallback = new GnssStatus.Callback() {
                @Override
                public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    visibleSatellitesCount = status.getSatelliteCount();
                    usedSatellitesCount = 0;
                    for (int i = 0; i < status.getSatelliteCount(); i++) {
                        if (status.usedInFix(i)) {
                            usedSatellitesCount++;
                        }
                    }
                    runOnUiThread(() -> textSatellites.setText(
                            "Satellites Visible: " + visibleSatellitesCount +
                                    "\nSatellites Used in Fix: " + usedSatellitesCount
                    ));
                }
            };

            locationManager.registerGnssStatusCallback(gnssStatusCallback, new Handler(Looper.getMainLooper()));
        }
    }

    private void updateUIWithLocation(Location location) {
        String coordinates = "Latitude: " + location.getLatitude() + "\n" + "Longitude: " + location.getLongitude();
        String height = "Height: " + location.getAltitude();

        String[] utmCoordinates = UTMConverter.latLonToUTM(location.getLatitude(), location.getLongitude());

        textLatLong.setText(coordinates);
        textHeight.setText(height);
        textEasting.setText(utmCoordinates[0]);
        textNorthing.setText(utmCoordinates[1]);
        textAccuracy.setText("Accuracy: " + location.getAccuracy() + " meters");
    }

    private void saveDataToFirestore() {
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", textLatLong.getText().toString());
        locationData.put("height", textHeight.getText().toString());
        locationData.put("easting", textEasting.getText().toString());
        locationData.put("northing", textNorthing.getText().toString());
        locationData.put("accuracy", textAccuracy.getText().toString());
        locationData.put("satellites", textSatellites.getText().toString());
        locationData.put("timestamp", System.currentTimeMillis()); // Add a timestamp

        db.collection("SplashScreen")
                .add(locationData)
                .addOnSuccessListener(documentReference -> Toast.makeText(CurrentMain.this, "Data saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(CurrentMain.this, "Failed to save data", Toast.LENGTH_SHORT).show());
    }

    private void viewLastSavedData() {
        db.collection("SplashScreen")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String lastData = "Latitude/Longitude: " + document.getString("latitude") + "\n"
                                    + "Height: " + document.getString("height") + "\n"
                                    + "Easting: " + document.getString("easting") + "\n"
                                    + "Northing: " + document.getString("northing") + "\n"
                                    + "Accuracy: " + document.getString("accuracy") + "\n"
                                    + "Satellites: " + document.getString("satellites");

                            new AlertDialog.Builder(CurrentMain.this)
                                    .setTitle("Saved Data")
                                    .setMessage(lastData)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();
                        } else {
                            Toast.makeText(CurrentMain.this, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CurrentMain.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

package com.example.splashscreen;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Compass extends AppCompatActivity implements SensorEventListener, LocationListener {
    private ImageView compassImageView;
    private TextView headingTV, directionTV, latitudeTV, longitudeTV, trueHeadingTV, magneticStrengthTV, presureTV, altitudeTV;
    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer, pressureSensor;
    private float[] gravity, geomagnetic;
    private float azimuth;
    private LocationManager locationManager;
    private double latitude, longitude, altitude;
    private float pressure, magneticStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass);

        // Initialize UI elements
        compassImageView = findViewById(R.id.compassImageView);
        headingTV = findViewById(R.id.headingTV);
        directionTV = findViewById(R.id.directionTV);
        latitudeTV = findViewById(R.id.latitudeTV);
        longitudeTV = findViewById(R.id.longitudeTV);
        trueHeadingTV = findViewById(R.id.trueHeadingTV);
        magneticStrengthTV = findViewById(R.id.magneticStrengthTV);
        presureTV = findViewById(R.id.presureTV);
        altitudeTV = findViewById(R.id.altitudeTV);

        // Initialize sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        // Initialize location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_UI);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
        sensorManager.unregisterListener(this, pressureSensor);

        locationManager.removeUpdates(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
            magneticStrength = event.values[0]; // Example to get magnetic field strength
            magneticStrengthTV.setText(String.format("Magnetic Strength: %.2f µT", magneticStrength));
        }
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            pressure = event.values[0];
            presureTV.setText(String.format("Pressure: %.2f hPa", pressure));
            // Estimate altitude from pressure
            altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
            altitudeTV.setText(String.format("Altitude: %.2f meters", altitude));
        }
        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                if (azimuth < 0) {
                    azimuth += 360;
                }
                updateCompass(azimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude(); // Update altitude based on GPS data

        latitudeTV.setText(String.format("Lat: %.4f", latitude));
        longitudeTV.setText(String.format("Lon: %.4f", longitude));
        altitudeTV.setText(String.format("Altitude: %.2f meters", altitude)); // Update with GPS altitude
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    private void updateCompass(float azimuth) {
        headingTV.setText(String.format("%03d", (int) azimuth));
        String direction = getDirection((int) azimuth);
        directionTV.setText(direction);
        compassImageView.setRotation(-azimuth);
        trueHeadingTV.setText(String.format("True Heading: %03d°", (int) azimuth));
    }

    private String getDirection(int azimuth) {
        if (azimuth >= 350 || azimuth <= 10) {
            return "N";
        } else if (azimuth < 80) {
            return "NE";
        } else if (azimuth < 100) {
            return "E";
        } else if (azimuth < 170) {
            return "SE";
        } else if (azimuth < 190) {
            return "S";
        } else if (azimuth < 260) {
            return "SW";
        } else if (azimuth < 280) {
            return "W";
        } else {
            return "NW";
        }
    }
}
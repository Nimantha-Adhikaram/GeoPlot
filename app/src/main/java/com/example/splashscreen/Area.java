package com.example.splashscreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.SphericalUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Area extends AppCompatActivity implements OnMapReadyCallback {

    static final int PERMISSION_LOCATION = 1001; // Request code for location permission
    static final int CHECK_GPS_ON_SETTING = 1002; // Request code for GPS settings

    private GoogleMap mMap;
    private TextView areaTextView;
    private Spinner mapTypeSpinner;
    private List<LatLng> points = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area);

        areaTextView = findViewById(R.id.tv_area);
        mapTypeSpinner = findViewById(R.id.map_type_spinner);

        // Initialize the spinner with map type options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.map_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypeSpinner.setAdapter(adapter);

        mapTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMap != null) {
                    switch (position) {
                        case 0: // Normal view
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        case 1: // Satellite view
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing here
            }
        });

        FloatingActionButton fabUndo = findViewById(R.id.fab_undo);
        fabUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points.size() > 0) {
                    Marker marker = markerList.get(markerList.size() - 1);
                    marker.remove();
                    markerList.remove(marker);
                    points.remove(points.size() - 1);
                    if (points.size() > 0) {
                        drawPolygon();
                        setAreaLength(points);
                    } else {
                        areaTextView.setText("0 m²");
                    }
                }
            }
        });

        // Button to navigate to AreaNew activity
        Button buttonOther = findViewById(R.id.button_other);
        buttonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Area.this, AreaNew.class);
                startActivity(intent);
            }
        });

        // Load the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                markerList.add(marker);
                points.add(latLng);
                drawPolygon();
                setAreaLength(points);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                updateMarkerLocation(marker, false);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                updateMarkerLocation(marker, true);
            }
        });
    }

    private void updateMarkerLocation(Marker marker, boolean calculate) {
        LatLng latLng = (LatLng) marker.getTag();
        int position = points.indexOf(latLng);
        points.set(position, marker.getPosition());
        marker.setTag(marker.getPosition());
        drawPolygon();
        if (calculate)
            setAreaLength(points);
    }

    private void drawPolygon() {
        if (mMap != null) {
            mMap.clear();
            if (points.size() > 2) {
                PolygonOptions polygonOptions = new PolygonOptions().addAll(points);
                mMap.addPolygon(polygonOptions);
            }
        }
    }

    private void setAreaLength(List<LatLng> points) {
        if (points.size() > 2) {
            double area = SphericalUtil.computeArea(points);
            areaTextView.setText(String.format("%.2f m²", area));
        } else {
            areaTextView.setText("0 m²");
        }
    }
}

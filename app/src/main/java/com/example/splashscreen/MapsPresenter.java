package com.example.splashscreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsPresenter implements MapsContract.Presenter, LifecycleObserver {

    private MapsContract.View mView;
    private GoogleMap mMap;

    private List<LatLng> points = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private Polygon polygon;

    MapsPresenter(MapsContract.View view) {
        mView = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {

        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mView.getViewActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (mView.getViewActivity().shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(mView.getViewActivity(), "Please grant location permission", Toast.LENGTH_SHORT).show();
                    mView.getViewActivity().requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Area.PERMISSION_LOCATION);
                } else {
                    mView.getViewActivity().requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Area.PERMISSION_LOCATION);
                }
            } else {
                mView.loadMap();
            }
        } else {
            mView.loadMap();
        }
    }

    @Override
    public void locationPermissionGranted() {

        mView.loadMap();
    }

    @Override
    public void locationPermissionRefused() {

        mView.showLocationPermissionNeeded();
    }

    @Override
    public void requestGps() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(mView.getViewActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(mView.getViewActivity(), Area.CHECK_GPS_ON_SETTING);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Handle the exception
                                Toast.makeText(mView.getViewActivity(), "Failed to enable GPS", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // GPS not available, handle accordingly
                            Toast.makeText(mView.getViewActivity(), "GPS is not available on this device", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void addMarkerToMap(LatLng latLng) {
        Activity activity = mView.getViewActivity();
        @SuppressLint("ResourceType") @IdRes int icon = R.drawable.ic_add_location_light_green_500_36dp;
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(activity, icon));
        MarkerOptions options = new MarkerOptions().position(latLng).icon(bitmap).draggable(true);
        mView.addMarkerToMap(options, latLng);
    }

    private static Bitmap getBitmapFromDrawable(Context context, int icon) {
        Drawable drawable = ContextCompat.getDrawable(context, icon);
        Bitmap obm = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(obm);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return obm;
    }

    public void setMap(GoogleMap googleMap) {

        this.mMap = googleMap;
    }

    private void drawPolygon() {
        if (polygon != null) {
            polygon.remove();
        }
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.fillColor(Color.argb(0, 0, 0, 0));
        polygonOptions.strokeColor(Color.argb(255, 0, 0, 0));
        polygonOptions.strokeWidth(10);
        polygonOptions.addAll(points);
        polygon = mMap.addPolygon(polygonOptions);
    }
}
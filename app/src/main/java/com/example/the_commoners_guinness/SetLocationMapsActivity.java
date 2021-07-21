package com.example.the_commoners_guinness;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.the_commoners_guinness.databinding.ActivityMapsBinding;
import com.parse.ParseGeoPoint;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetLocationMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Button btnSaveLocation;
    private LatLng finalPosition;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnSaveLocation = findViewById(R.id.btnSaveLocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        finalPosition = new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude());
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                ParseGeoPoint finalParseGeoPoint = new ParseGeoPoint(finalPosition.latitude, finalPosition.longitude);
                i.putExtra("Location", finalParseGeoPoint);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng currentPost = new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude());
        googleMap.addMarker(new MarkerOptions().position(currentPost).title("Post location").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setDraggable(true);
        // zoom the map to the currentUserLocation
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPost, 10));
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMarkerDragListener(this);
    }

    private ParseGeoPoint getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(SetLocationMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SetLocationMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SetLocationMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = getLastKnownLocation();
            if (location != null) {
                ParseGeoPoint currentPostLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                return currentPostLocation;
            }
        }
        Log.e("MapsActivity", "No location returned");
        return null;
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SetLocationMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onInfoWindowClick(@NonNull @NotNull Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(@NonNull @NotNull Marker marker) {
        LatLng position0 = marker.getPosition();

        Log.d(getClass().getSimpleName(), String.format("Drag from %f:%f",
                position0.latitude,
                position0.longitude));
    }

    @Override
    public void onMarkerDrag(@NonNull @NotNull Marker marker) {
        LatLng position0 = marker.getPosition();

        Log.d(getClass().getSimpleName(),
                String.format("Dragging to %f:%f", position0.latitude,
                        position0.longitude));
    }

    @Override
    public void onMarkerDragEnd(@NonNull @NotNull Marker marker) {
        finalPosition = marker.getPosition();

        Log.d(getClass().getSimpleName(), String.format("Dragged to %f:%f",
                finalPosition.latitude,
                finalPosition.longitude));
    }


}
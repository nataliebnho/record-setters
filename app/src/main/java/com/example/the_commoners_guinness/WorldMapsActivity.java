package com.example.the_commoners_guinness;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.models.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.the_commoners_guinness.databinding.ActivityWorldMapsBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityWorldMapsBinding binding;
    private HashMap<ParseGeoPoint, String[]> locations = new HashMap<ParseGeoPoint, String[] >();
    public static final String TAG = "WorldMapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWorldMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        queryPostLocations();
    }

    private void queryPostLocations() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts for locations", e);
                }
                for (Post post: posts) {
                    if (post.getLocation() != null) {
                        String username = null;
                        Category category = null;
                        String categoryName = null;
                        String isWinner = null;
                        try {
                            username = post.getUser().fetchIfNeeded().getUsername();
                            category = post.getCategory().fetchIfNeeded();
                            categoryName = category.getName();
                            if (((ParseUser)category.getWinnerUser()).fetchIfNeeded().getUsername().equals(username)) {
                                isWinner = "true";
                            } else {
                                isWinner = "false";
                            }
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }

                        String[] names = new String[] {username, categoryName, isWinner};
                        locations.put(post.getLocation(), names);
                    }
                }
                addMarkers(locations);
            }
        });
    }

    private void addMarkers(HashMap<ParseGeoPoint, String[]> locations) {
        for (Map.Entry<ParseGeoPoint, String[]> mapElement : locations.entrySet()) {
            Log.i("SetMarker", "Here");
            LatLng location = new LatLng(mapElement.getKey().getLatitude(), mapElement.getKey().getLongitude());
            String title = mapElement.getValue()[0] + ": " +  mapElement.getValue()[1];
            Boolean isWinner = Boolean.valueOf(mapElement.getValue()[2]);

            if (isWinner) {
                mMap.addMarker(new MarkerOptions().position(location).title(title).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            } else {
                mMap.addMarker(new MarkerOptions().position(location).title(title));
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 3));
        }
    }


}
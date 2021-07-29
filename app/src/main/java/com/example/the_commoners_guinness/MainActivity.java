package com.example.the_commoners_guinness;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.the_commoners_guinness.ui.challenges.DiscoverFragment;
import com.example.the_commoners_guinness.ui.create.CreateFragment;
import com.example.the_commoners_guinness.ui.home.HomeFragment;
import com.example.the_commoners_guinness.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.the_commoners_guinness.databinding.ActivityMainBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ImageView ivWorld;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setNavView();

        setWorldMaps();
    }

    private void setWorldMaps() {
        ivWorld = findViewById(R.id.ivWorld);
        ivWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WorldMapsActivity.class);
                startActivity(i);
            }
        });
    }

    private void setNavView() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        BottomNavigationView navViewChallenge = findViewById(R.id.nav_view);
        navViewChallenge.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_create:
                        fragment = new CreateFragment();
                        break;
                    case R.id.navigation_profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.navigation_viewChallenges:
                        fragment = new DiscoverFragment();
                        break;
                    case R.id.navigation_home:
                    default:
                        fragment = new HomeFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, fragment).commit();
                return true;
            }
        });
        navViewChallenge.setSelectedItemId(R.id.navigation_home);
    }
}

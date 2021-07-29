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
    private List<String> categories = new ArrayList<>();
    private List<ParseUser> sampleUsers = new ArrayList<>();
    private List<ParseUser> allUsers = new ArrayList<>();

    private boolean isRunning = true;
    private List<String> finalRecommended;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_create, R.id.navigation_viewChallenges, R.id.navigation_profile)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupWithNavController(binding.navView, navController);
        setNavView();

        setWorldMaps();
        //setRecommended();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setRecommended() {
        ParseUser currUser = ParseUser.getCurrentUser();
        // 1. Generate hashmap of normalized scores per category for current user
        ArrayList<String> userLikes = (ArrayList<String>) currUser.get("likes");
        HashMap<String, Double> currHM = generateNormHM(userLikes);
        // 2. Sample 10 random candidate users to do recommendations
        allUsers = queryUsers();
        Collections.shuffle(allUsers);
        for (int i = 0; i < 10; i++) {
            sampleUsers.add(allUsers.get(i));
        }
        HashMap<String, Double> recommended = new HashMap<>();
        // 3. Loop through each candidate user
        for (ParseUser candidateUser : sampleUsers) {
            // 3.1 Retrieve categories liked by each user
            ArrayList<String> candLikes = (ArrayList<String>) candidateUser.get("likes");
            // 3.2 Generate hashmap of normalized scores per category for candidate user
            HashMap<String, Double> candHM = generateNormHM(candLikes);
            // 3.3 Calculate set intersection score by looping through
            //     each category liked by the current user, adding
            //     up percentages to generate final overlap score
            double overlapScore = 0;
            for (String categoryName : userLikes) {
                if (candHM.containsKey(categoryName)) {
                    overlapScore += candHM.get(categoryName) + currHM.get(categoryName);
                }
            }
            // 3.4 Update rank of categories by looping through each category liked by the
            //     candidate user and checking if the category is not liked by the current user.
            //     If it isn't, then we add the current category to the return list adding the
            //     overlap score to the current category's score
            for (String categoryName : candLikes) {
                if (!currHM.containsKey(categoryName)) {
                    if (recommended.containsKey(categoryName)) {
                        recommended.put(categoryName, recommended.get(categoryName) + overlapScore);
                    } else {
                        recommended.put(categoryName, overlapScore);
                    }
                }
            }
        }
        // 4. Take keys in category score hashmap and sort them. Return keys in a list based on
        //    category score
        finalRecommended = sortByValue(recommended);
    }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public static <K, V extends Comparable<? super V>> List<K> sortByValue(Map<K, V> map) {
            List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
            list.sort(Map.Entry.comparingByValue());
            List<K> retLst = new ArrayList<>();
            for (Map.Entry<K, V> pair : list ) {
                retLst.add(pair.getKey());
            }

            return retLst;
//            Map<K, V> result = new LinkedHashMap<>();
//            for (Map.Entry<K, V> entry : list) {
//                result.put(entry.getKey(), entry.getValue());
//            }
//
//            return result;

    }

    private HashMap<String, Double> generateNormHM(List<String> list) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        for (String categoryName : list) {
            if (map.containsKey(categoryName)) {
                map.put(categoryName, map.get(categoryName) + 1);
            } else {
                Double d = new Double(1);
                map.put(categoryName, d);
            }
        }
        for (String categoryName : map.keySet()) {
            map.put(categoryName,  map.get(categoryName) / list.size());
        }
        return map;
    }

    private List<ParseUser> queryUsers() {
        List<ParseUser> users = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }

                users.addAll(users);
                isRunning = false;
            }
        });
        while (isRunning) {
            continue;
        }
        return users;
    }

}

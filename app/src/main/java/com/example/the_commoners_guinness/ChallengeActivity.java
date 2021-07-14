package com.example.the_commoners_guinness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.the_commoners_guinness.databinding.ActivityChallengeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class ChallengeActivity extends AppCompatActivity {

    private ActivityChallengeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChallengeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final FragmentManager fragmentManager = getSupportFragmentManager();

        BottomNavigationView navViewChallenge = findViewById(R.id.nav_view_challenge);

        navViewChallenge.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                //avController navController = Navigation.findNavController(ChallengeActivity.this, R.id.nav_host_fragment_activity_challenge);
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_leaderboard:
                        fragment = new LeaderboardFragment();
                        break;
                    case R.id.navigation_challenge:
                    default:
                        fragment = new ChallengeFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_challenge, fragment).commit();
                return true;
            }
        });
        navViewChallenge.setSelectedItemId(R.id.navigation_challenge);


        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_challenge, R.id.navigation_leaderboard)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_challenge);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navViewChallenge, navController);


    }


}
package com.example.the_commoners_guinness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.the_commoners_guinness.databinding.ActivityChallengeBinding;
import com.example.the_commoners_guinness.models.Category;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

public class ChallengeActivity extends AppCompatActivity {

    private static final String TAG = "ChallengeActivity";
    private ActivityChallengeBinding binding;
    private Category category;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChallengeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        category = Parcels.unwrap(getIntent().getParcelableExtra("Category"));
        bundle = new Bundle();
        bundle.putParcelable("category", Parcels.wrap(category));

        setNavView();

    }

    private void setNavView() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        BottomNavigationView navViewChallenge = findViewById(R.id.nav_view_challenge);
        navViewChallenge.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_leaderboard:
                        fragment = new LeaderboardFragment();
                        fragment.setArguments(bundle);
                        break;
                    case R.id.navigation_challenge:
                    default:
                        fragment = new ChallengeFragment();
                        fragment.setArguments(bundle);
                }
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_challenge, fragment).commit();
                return true;
            }
        });
        navViewChallenge.setSelectedItemId(R.id.navigation_challenge);
    }


}
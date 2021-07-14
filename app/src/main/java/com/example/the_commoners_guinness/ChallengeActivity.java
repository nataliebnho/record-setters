package com.example.the_commoners_guinness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.MenuItem;

import com.example.the_commoners_guinness.databinding.ActivityChallengeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

public class ChallengeActivity extends AppCompatActivity {

    private static final String TAG = "ChallengeActivity";
    private ActivityChallengeBinding binding;
    private String categoryName;
    private String categoryID;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChallengeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        category = Parcels.unwrap(getIntent().getParcelableExtra("Category"));
//        categoryName = category.getName();
//        categoryID = category.getObjectId();
//        Log.i(TAG, "Category Name: " + categoryName);
//        Log.i(TAG, "Category Name: " + categoryID);

        Bundle bundle = new Bundle();
//        bundle.putString("categoryName", categoryName);
//        bundle.putString("categoryID", categoryID);
        bundle.putParcelable("category", Parcels.wrap(category));

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
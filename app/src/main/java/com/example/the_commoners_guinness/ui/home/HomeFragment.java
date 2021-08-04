package com.example.the_commoners_guinness.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.the_commoners_guinness.ViewPagerAdapter;
import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.LoginActivity;
import com.example.the_commoners_guinness.models.Post;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.ui.profile.UserBadgesFragment;
import com.example.the_commoners_guinness.ui.profile.UserPostsFragment;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    Button btnLogout;

    TabLayout tabLayout;
    ViewPager viewPager;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setChildrenFragments(view);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }

    private void setChildrenFragments(View view) {
        tabLayout = view.findViewById(R.id.tabLayoutHome);
        viewPager = view.findViewById(R.id.viewPagerHome);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        Fragment allPostsFragment = new HomeAllTimelineFragment();
        Fragment followingPostsFragment = new HomeFollowingFragment();

        adapter.addFragment(allPostsFragment, "Timeline");
        adapter.addFragment(followingPostsFragment, "Following");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

}
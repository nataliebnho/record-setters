package com.example.the_commoners_guinness.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OtherUserProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 40;

    private ImageView ivProfilePicture;
    private TextView tvNumBadges;
    private TextView tvUsername;
    private ParseUser user;
    private Button btnFollow;
    private boolean currentUserFollows;

    TabLayout tabLayout;
    ViewPager viewPager;
    Fragment userBadgesFragment;

    private ArrayList<Category> categoryWins = new ArrayList<Category>();

    public OtherUserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = getArguments().getParcelable("user");
        findViews(view);
        queryIfCurrentUserFollows(user);
        try {
            queryFetchUserWins(view);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if current user is not following anyone (null) or not following profile's user
                if (!currentUserFollows) {
                    Log.i("Follow status: ", "current user does not follow, so add follow");
                    ParseUser.getCurrentUser().getRelation("following").add(user);
                    ParseUser.getCurrentUser().saveInBackground();
                    btnFollow.setBackgroundColor(getResources().getColor(R.color.green));
                    btnFollow.setText("Following");
                } else {
                    Log.i("Follow status: ", "user already follows, so delete follow");
                    ParseUser.getCurrentUser().getRelation("following").remove(user);
                    ParseUser.getCurrentUser().saveInBackground();
                    btnFollow.setBackgroundColor(getResources().getColor(R.color.teal_200));
                    btnFollow.setText("Follow");
                }
            }
        });

    }

    private void queryIfCurrentUserFollows(ParseUser otherUser) {
        ParseRelation relation = ParseUser.getCurrentUser().getRelation("following");
        ParseQuery<ParseUser> query = relation.getQuery();
        currentUserFollows = false;
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                for (ParseUser user : users) {
                    if (user.getObjectId().equals(otherUser.getObjectId())) {
                        Log.i("Found user: ", "true");
                        currentUserFollows = true;
                        btnFollow.setBackgroundColor(getResources().getColor(R.color.green));
                        btnFollow.setText("Following");
                    }
                }
            }
        });

    }

    private void findViews(View view) {
        tvUsername = view.findViewById(R.id.tvProfileNameOther);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicOther);
        tvNumBadges = view.findViewById(R.id.tvNumBadgesOther);
        tvUsername.setText(user.getUsername());
        btnFollow = view.findViewById(R.id.btnFollow);
        if (user.get("profilePicture") != null) {
            String profilePicture = user.getParseFile("profilePicture").getUrl();
            Glide.with(getContext()).load(profilePicture).into(ivProfilePicture);
        }
    }

    private void queryFetchUserWins(View view) throws ParseException {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.whereEqualTo(Category.KEY_WINNERUSER, user);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }
                for (Category category: categories) {
                    categoryWins.add(category);
                    Log.i("Category wins: ", category.getName());
                }
                setBadgesText();
                setChildrenFragments(view);
            }
        });

    }

    private void setBadgesText() {
        tvNumBadges.setText(String.valueOf(categoryWins.size()));
    }

    private void setChildrenFragments(View view) {
        tabLayout = view.findViewById(R.id.tabLayoutOther);
        viewPager = view.findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        userBadgesFragment = new UserBadgesFragment();
        Fragment userPostsFragment = new UserPostsFragment();

        Bundle badgesBundle = new Bundle();
        badgesBundle.putParcelableArrayList("Categories", categoryWins);
        userBadgesFragment.setArguments(badgesBundle);

        adapter.addFragment(userPostsFragment, "Posts");
        adapter.addFragment(userBadgesFragment, "Badges");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
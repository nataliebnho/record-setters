package com.example.the_commoners_guinness;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.the_commoners_guinness.ui.home.PostsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private static final String TAG = "LeaderboardFragment";
    RecyclerView rvCategoryPosts;
    List<Post> categoryPosts;
    protected PostsAdapter adapter;
    Category category;
    TextView tvLeaderboardCategory;
    Post[] leaderboard = new Post[3];
    TextView tvUsernameFirst;
    TextView tvUsernameSecond;
    TextView tvUsernameThird;
    int numLeaderboard;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCategoryPosts = view.findViewById(R.id.rvCategoryPosts);
        categoryPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), categoryPosts);
        tvLeaderboardCategory = view.findViewById(R.id.tvLeaderboardCategory);
        tvUsernameFirst = view.findViewById(R.id.tvUsernameFirst);
        tvUsernameSecond = view.findViewById(R.id.tvUsernameSecond);
        tvUsernameThird = view.findViewById(R.id.tvUsernameThird);


        rvCategoryPosts.setAdapter(adapter);
        rvCategoryPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        category = Parcels.unwrap(getArguments().getParcelable("category"));
        tvLeaderboardCategory.setText(category.getName());

        queryCategoryPosts(tvUsernameFirst, tvUsernameSecond, tvUsernameThird);
    }

    private void queryCategoryPosts(TextView one, TextView two, TextView three) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder("voteCount");
        query.whereEqualTo(Post.KEY_CATEGORY, category);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }
                numLeaderboard = Math.min(3, posts.size());
                for(int i = 0; i < numLeaderboard; i++) {
                    leaderboard[i] = posts.get(i);
                    Log.i("Leaderboard", i + " : " + posts.get(i).getObjectId());
                }
                categoryPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                setLeaderboardUsernames(one, two, three);
            }
        });
    }

    public void setLeaderboardUsernames(TextView one, TextView two, TextView three) {
        Log.i(TAG, String.valueOf(numLeaderboard));
        //Will clean this logic later
        if (numLeaderboard >= 1) {
            tvUsernameFirst.setText(leaderboard[0].getUser().getUsername());
        }
        if (numLeaderboard >= 2) {
            tvUsernameSecond.setText(leaderboard[0].getUser().getUsername());
        }
        if (numLeaderboard == 3) {
            tvUsernameThird.setText(leaderboard[0].getUser().getUsername());
        }
    }

}
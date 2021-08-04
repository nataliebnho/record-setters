package com.example.the_commoners_guinness.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFollowingFragment extends Fragment {

    private static final String TAG = "HOMEFOLLOWINGTIMELINE";
    RecyclerView rvPostsFollowing;
    SwipeRefreshLayout swipeContainerFollowing;

    protected PostsAdapter adapter;
    protected List<Post> followingPosts;

    public HomeFollowingFragment() {
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
        return inflater.inflate(R.layout.fragment_home_following, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configureRecyclerView(view);
        configureSwipeContainer(view);
        queryFollowing();
    }

    private void configureRecyclerView(View view) {
        rvPostsFollowing = view.findViewById(R.id.rvPostsFollowing);
        followingPosts = new ArrayList<>();
        adapter = new PostsAdapter(getActivity(), followingPosts);
        rvPostsFollowing.setAdapter(adapter);
        rvPostsFollowing.setLayoutManager(new LinearLayoutManager(getContext()));
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        getActivity().finish();
//    }

    private void configureSwipeContainer(View view) {
        swipeContainerFollowing = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerFollowing);
        swipeContainerFollowing.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
        swipeContainerFollowing.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void fetchTimelineAsync (int i) {
        adapter.clear();
        queryFollowing();
        swipeContainerFollowing.setRefreshing(false);
    }

    private void queryFollowing() {
        ParseQuery query = ParseUser.getCurrentUser().getRelation("following").getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    for (ParseUser user: users) {
                        queryPosts(user);
                    }
                }
            }
        });
    }

    private void queryPosts(ParseUser user) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.whereEqualTo("user", user);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }

                followingPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
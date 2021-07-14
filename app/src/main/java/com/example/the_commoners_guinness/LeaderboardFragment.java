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

        rvCategoryPosts.setAdapter(adapter);
        rvCategoryPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        category = Parcels.unwrap(getArguments().getParcelable("category"));

        queryCategoryPosts();
    }

    private void queryCategoryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.whereEqualTo(Post.KEY_CATEGORY, category);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }
                for (Post post: posts) {
                    Log.i(TAG, "Post: " + post.getCaption() + ", username: " + post.getUser().getUsername());
                }
                categoryPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
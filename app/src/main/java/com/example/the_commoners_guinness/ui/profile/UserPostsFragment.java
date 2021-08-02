package com.example.the_commoners_guinness.ui.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.bekawestberg.loopinglayout.library.LoopingLayoutManager;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Post;
import com.mig35.carousellayoutmanager.CarouselLayoutManager;
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.mig35.carousellayoutmanager.CenterScrollListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserPostsFragment extends Fragment {

    protected ProfileAdapter adapter;
    protected List<Post> userPosts;
    RecyclerView rvUserPosts;
    protected ParseUser user;

    public static final String TAG = "UserPostsFragment";


    public UserPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = getArguments().getParcelable("User");

        rvUserPosts = view.findViewById(R.id.rvUserPosts);
        userPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), userPosts, rvUserPosts);


        queryUserPosts(user);
        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.VERTICAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        rvUserPosts.setLayoutManager(layoutManager);
        rvUserPosts.setHasFixedSize(true);
        rvUserPosts.setAdapter(adapter);
        rvUserPosts.addOnScrollListener(new CenterScrollListener());
    }

    private void queryUserPosts(ParseUser user) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, user);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue w getting posts", e);
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getCaption() + ", username: " + post.getUser().getUsername());
                }
                userPosts.clear();
                userPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

}

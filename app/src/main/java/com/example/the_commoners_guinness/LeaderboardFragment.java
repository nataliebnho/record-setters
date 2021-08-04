package com.example.the_commoners_guinness;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.models.Post;
import com.example.the_commoners_guinness.ui.home.PostsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

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
    TextView tvNumVotesFirst;
    TextView tvNumVotesSecond;
    TextView tvNumVotesThird;
    CardView cvWatchList;
    ImageView ivWatchList;
    TextView tvAddToWatchList;
    ArrayList<Category> watchList = new ArrayList<>();

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
        findViews(view);
        setRecyclerView();

        category = Parcels.unwrap(getArguments().getParcelable("category"));
        tvLeaderboardCategory.setText(category.getName());
        queryCategoryPosts();

        findCategoryInWatchList();

        cvWatchList.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (tvAddToWatchList.getText().equals("Added to watch list")) { // already added
                    removeFromSubscribeArray();
                    ivWatchList.setImageResource(R.drawable.not_watched);
                    tvAddToWatchList.setText("Add to watch list");
                } else {
                    // add category to Parse backend
                    ParseUser.getCurrentUser().addUnique("subscriptions", category);
                    ParseUser.getCurrentUser().saveInBackground();
                    // change cardview
                    ivWatchList.setImageResource(R.drawable.watched);
                    tvAddToWatchList.setText("Added to watch list");

                }

            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void findCategoryInWatchList() {
        watchList = (ArrayList<Category>) ParseUser.getCurrentUser().get("subscriptions");
        for (int i = 0; i < watchList.size(); i++) {
            if (category.getObjectId().equals(watchList.get(i).getObjectId())) {
                ivWatchList.setImageResource(R.drawable.watched);
                tvAddToWatchList.setText("Added to watch list");
            }
        }
    }

    public void removeFromSubscribeArray() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<Category> userSubscriptions = (ArrayList<Category>) currentUser.get("subscriptions");

        if (userSubscriptions == null) {
            userSubscriptions = new ArrayList<>();
        }

        for (int i  = 0; i < userSubscriptions.size(); i++) {
            String categoryID = userSubscriptions.get(i).getObjectId();
            if (category.getObjectId().equals(categoryID)){
                userSubscriptions.remove(i);
            }
        }
        currentUser.put("subscriptions", userSubscriptions);
        currentUser.saveInBackground();
    }

    private void findViews(View view) {
        rvCategoryPosts = view.findViewById(R.id.rvCategoryPosts);
        tvLeaderboardCategory = view.findViewById(R.id.tvLeaderboardCategory);
        tvUsernameFirst = view.findViewById(R.id.tvUsernameFirstCV);
        tvUsernameSecond = view.findViewById(R.id.tvUsernameSecondCV);
        tvUsernameThird = view.findViewById(R.id.tvUsernameThirdCV);
        tvNumVotesFirst = view.findViewById(R.id.tvNumVotesFirstD);
        tvNumVotesSecond = view.findViewById(R.id.tvNumVotesSecondD);
        tvNumVotesThird = view.findViewById(R.id.tvNumVotesThirdD);
        cvWatchList = view.findViewById(R.id.cvWatchList);
        ivWatchList = view.findViewById(R.id.ivWatchList);
        tvAddToWatchList = view.findViewById(R.id.tvAddToWatchList);
    }

    private void setRecyclerView() {
        categoryPosts = new ArrayList<>();
        adapter = new PostsAdapter(getActivity(), categoryPosts);
        rvCategoryPosts.setAdapter(adapter);
        rvCategoryPosts.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    public void queryCategoryPosts() {
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
                setLeaderboard(posts);
                categoryPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                try {
                    setLeaderboardUsernamesandVotes();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
        });
    }

    public void setLeaderboard(List<Post> posts) {
        numLeaderboard = Math.min(3, posts.size());
        for(int i = 0; i < numLeaderboard; i++) {
            leaderboard[i] = posts.get(i);
            Log.i("Leaderboard", i + " : " + posts.get(i).getObjectId());
        }
    }

    public void setLeaderboardUsernamesandVotes() throws ParseException {
        Log.i(TAG, String.valueOf(numLeaderboard));
        if (numLeaderboard >= 1) {
            tvUsernameFirst.setText(leaderboard[0].getUser().getUsername());
            tvNumVotesFirst.setText(String.valueOf(leaderboard[0].get("voteCount")));
        }

        if (numLeaderboard >= 2) {
            tvUsernameSecond.setText(leaderboard[1].getUser().getUsername());
            tvNumVotesSecond.setText(String.valueOf(leaderboard[1].get("voteCount")));
        }
        if (numLeaderboard == 3) {
            tvUsernameThird.setText(leaderboard[2].getUser().getUsername());
            tvNumVotesThird.setText(String.valueOf(leaderboard[2].get("voteCount")));

        }
    }

}
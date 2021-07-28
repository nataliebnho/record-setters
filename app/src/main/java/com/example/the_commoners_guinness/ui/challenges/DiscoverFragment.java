package com.example.the_commoners_guinness.ui.challenges;

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
import android.widget.SearchView;

import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Category;
import com.mig35.carousellayoutmanager.CarouselLayoutManager;
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.mig35.carousellayoutmanager.CenterScrollListener;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    public static final String TAG = "AllChallengesFragment";
    RecyclerView rvCategories;
    RecyclerView rvUsers;
    List<Category> allCategories;
    List<Category> activeCategories;
    List<ParseUser> allUsers;
    protected ChallengesAdapter adapter;
    protected UserPreviewAdapter userAdapter;
    SearchView actionSearch;
    List<Category> categoriesFull;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategories = view.findViewById(R.id.rvChallengeType);
        rvUsers = view.findViewById(R.id.rvTopUsers);

        allCategories = new ArrayList<>();
        activeCategories = new ArrayList<>();
        categoriesFull = new ArrayList<>();
        allUsers = new ArrayList<>();

        adapter = new ChallengesAdapter(getContext(), activeCategories, categoriesFull);
        userAdapter = new UserPreviewAdapter(getContext(), allUsers);
        actionSearch = view.findViewById(R.id.action_search);

        setRVAdapter();
        setRVUserAdapter();
        queryCategories();
        queryUsers();


        actionSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void setRVAdapter() {

        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener(0.5f));

        rvCategories.setLayoutManager(layoutManager);
        rvCategories.setHasFixedSize(true);
        rvCategories.setAdapter(adapter);
        rvCategories.addOnScrollListener(new CenterScrollListener());

    }

    private void setRVUserAdapter() {

        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener(0.5f));

        rvUsers.setLayoutManager(layoutManager);
        rvUsers.setHasFixedSize(true);
        rvUsers.setAdapter(userAdapter);
        rvUsers.addOnScrollListener(new CenterScrollListener());

    }

    private void queryCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");

        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }

                for (Category category: categories) {
                    if (category.getFirstChallengePost() != null) {
                        activeCategories.add(category);
                    }
                }

                allCategories.addAll(categories);
                categoriesFull.addAll(categories);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void queryUsers() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }
                allUsers.addAll(users);
                userAdapter.notifyDataSetChanged();
            }
        });
    }

}
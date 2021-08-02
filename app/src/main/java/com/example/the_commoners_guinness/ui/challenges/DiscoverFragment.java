package com.example.the_commoners_guinness.ui.challenges;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.example.the_commoners_guinness.models.Post;
import com.example.the_commoners_guinness.ui.home.PostsAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DiscoverFragment extends Fragment {

    public static final String TAG = "AllChallengesFragment";
    RecyclerView rvCategories;
    RecyclerView rvUsers;
    RecyclerView rvRecommended;
    List<Category> allCategories;
    List<Category> categoriesFull;
    List<Category> activeCategories;
    List<ParseUser> topUsers;

    private static LinkedList<String> recommendedCategoryStrings = new LinkedList<>();
    List<Category> recommendedCategoryObjects;
    List<Post> recommendedPosts;
    private List<ParseUser> sampleUsers = new ArrayList<>();

    ArrayList<String> userLikes;
    HashMap<String, Double> currHM;

    protected ChallengesAdapter adapter;
    protected UserPreviewAdapter userAdapter;
    protected ChallengesAdapter recommendedAdapter;
    SearchView actionSearch;

    private List<ParseUser> allUsersRecommended = new ArrayList<>();


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
        rvRecommended = view.findViewById(R.id.rvRecommended);

        //allCategories = new ArrayList<>();
        activeCategories = new ArrayList<>();
        categoriesFull = new ArrayList<>();
        topUsers = new ArrayList<>();
        recommendedCategoryObjects = new ArrayList<>();
        recommendedPosts = new ArrayList<>();

        adapter = new ChallengesAdapter(getContext(), activeCategories, categoriesFull);
        userAdapter = new UserPreviewAdapter(getActivity(), topUsers);
        recommendedAdapter = new ChallengesAdapter(getContext(), recommendedCategoryObjects, categoriesFull);
        actionSearch = view.findViewById(R.id.action_search);

        setAdapter(rvCategories, adapter);
        setAdapter(rvUsers, userAdapter);
        setAdapter(rvRecommended, recommendedAdapter);

//
//        setRVAdapter();
//        setRVUserAdapter();
//        setRVRecommendedAdapter();
        queryCategories();
        queryUsers();
        setRecommended();

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

    private void setAdapter(RecyclerView rv, RecyclerView.Adapter adapter) {
        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener(0.5f));

        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new CenterScrollListener());
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
                        try {
                            Date date = (Date) category.fetchIfNeeded().getParseObject("firstChallengePost").fetchIfNeeded().get("createdAt");
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                        if (System.currentTimeMillis() - category.getFirstChallengePost().getCreatedAt().getTime() < 86400000) {
                            activeCategories.add(category);
                            categoriesFull.add(category);
                        }
                    }
                }
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
                topUsers.addAll(users);
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setRecommended() {
        ParseUser currUser = ParseUser.getCurrentUser();
        // 1. Generate hashmap of normalized scores per category for current user
        userLikes = (ArrayList<String>) currUser.get("likes");
        currHM = generateNormHM(userLikes);
        // 2. Sample 10 random candidate users to do recommendations
        queryUsersRecommended();
    }

    public void advance() {
        Collections.shuffle(allUsersRecommended);
        for (int i = 0; i < 5; i++) {
            sampleUsers.add(allUsersRecommended.get(i));
        }
        HashMap<String, Double> recommended = new HashMap<>();
        // 3. Loop through each candidate user
        for (ParseUser candidateUser : sampleUsers) {
            // 3.1 Retrieve categories liked by each user
            ArrayList<String> candLikes = new ArrayList<>();
            candLikes = (ArrayList<String>) candidateUser.get("likes");
            // 3.2 Generate hashmap of normalized scores per category for candidate user
            if (candLikes == null) {
                continue;
            }

            HashMap<String, Double> candHM = generateNormHM(candLikes);
            // 3.3 Calculate set intersection score by looping through
            //     each category liked by the current user, adding
            //     up percentages to generate final overlap score
            double overlapScore = 0;
            for (String categoryName : userLikes) {
                if (candHM.containsKey(categoryName)) {
                    overlapScore += candHM.get(categoryName) + currHM.get(categoryName);
                }
            }
            // 3.4 Update rank of categories by looping through each category liked by the
            //     candidate user and checking if the category is not liked by the current user.
            //     If it isn't, then we add the current category to the return list adding the
            //     overlap score to the current category's score
            ArrayList<String> seenInThisCandidate = new ArrayList<String>();
            for (String categoryName : candLikes) {
                if (!currHM.containsKey(categoryName)) {
                    if (recommended.containsKey(categoryName) && !seenInThisCandidate.contains(categoryName)) {
                        recommended.put(categoryName, recommended.get(categoryName) + overlapScore);
                    } else {
                        recommended.put(categoryName, overlapScore);
                        seenInThisCandidate.add(categoryName);
                    }
                }
            }
        }

        // 4 sort list by values (descending order) and only take
        sortByValue(recommended);
        Log.i("FINAL", recommended.toString());
        Log.i("FINAL", recommendedCategoryStrings.toString());
        getCategoriesFromName(recommendedCategoryStrings);
    }

    public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm) {
        recommendedCategoryStrings.clear();
        // Create a list from elements of HashMap
        List<Map.Entry<String, Double> > list =
                new LinkedList<Map.Entry<String, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
            if (aa.getValue() != 0) {
                recommendedCategoryStrings.add(aa.getKey());
            }
        }
        return temp;
    }

    public HashMap<String, Double> generateNormHM(List<String> list) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        for (String categoryName : list) {
            if (map.containsKey(categoryName)) {
                map.put(categoryName, map.get(categoryName) + 1);
            } else {
                Double d = new Double(1);
                map.put(categoryName, d);
            }
        }
        for (String categoryName : map.keySet()) {
            map.put(categoryName,  map.get(categoryName) / list.size());
        }
        return map;
    }

    public void queryUsersRecommended() {
        List<ParseUser> users = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }
                allUsersRecommended.addAll(users);
                advance();
            }
        });
    }

    private void getCategoriesFromName(List<String> categories) {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        for (String categoryName : categories) {
            query.whereEqualTo("name", categoryName);
            query.findInBackground(new FindCallback<Category>() {
                @Override
                public void done(List<Category> categories, ParseException e) {
                    recommendedCategoryObjects.add(categories.get(0));
                    Category category = categories.get(0);
                    Post winner = (Post) category.get("winner");
                    recommendedPosts.add(winner);
                    recommendedAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
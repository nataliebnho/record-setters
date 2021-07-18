package com.example.the_commoners_guinness.ChildFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.the_commoners_guinness.BadgesAdapter;
import com.example.the_commoners_guinness.Category;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.ui.home.PostsAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserBadgesFragment extends Fragment {

    //public ArrayList<String> categoryBadges = new ArrayList<>();
    public ArrayList<Category> categoryBadges = new ArrayList<>();
    private RecyclerView rvUserBadges;
    protected BadgesAdapter adapter;

    public UserBadgesFragment() {
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
        return inflater.inflate(R.layout.fragment_user_badges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //categoryBadges = getArguments().getStringArrayList("Categories");
        categoryBadges = getArguments().getParcelableArrayList("Categories");
        rvUserBadges = view.findViewById(R.id.rvUserBadges);

        adapter = new BadgesAdapter(getContext(), categoryBadges);
        rvUserBadges.setAdapter(adapter);
        rvUserBadges.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
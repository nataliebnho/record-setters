package com.example.the_commoners_guinness.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.the_commoners_guinness.ui.profile.BadgesAdapter;
import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserBadgesFragment extends Fragment {

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
        categoryBadges = getArguments().getParcelableArrayList("Categories");
        rvUserBadges = view.findViewById(R.id.rvUserBadges);

        adapter = new BadgesAdapter(getContext(), categoryBadges);
        rvUserBadges.setAdapter(adapter);
        rvUserBadges.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
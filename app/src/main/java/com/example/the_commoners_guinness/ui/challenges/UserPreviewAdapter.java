package com.example.the_commoners_guinness.ui.challenges;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.ui.profile.BadgesAdapter;
import com.example.the_commoners_guinness.ui.profile.OtherUserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserPreviewAdapter extends RecyclerView.Adapter<UserPreviewAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "UserPreviewAdapter";
    private FragmentActivity c;
    private List<ParseUser> users;
    private List<ParseUser> usersFull;
    private List<Category> userBadges = new ArrayList<>();

    public UserPreviewAdapter(FragmentActivity c, List<ParseUser> users) {
        this.c = c;
        this.users = users;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @NotNull
    @Override
    public UserPreviewAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.user_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserPreviewAdapter.ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        try {
            holder.bind(user);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        Log.i(TAG, String.valueOf(users.size()));
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserPreviewPic;
        TextView tvUsernamePreview;
        TextView tvMemberSince;
        RecyclerView rvUserPreviewBadges;
        BadgesPreviewAdapter adapter;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            findViews();
        }

        private void findViews() {
            ivUserPreviewPic = itemView.findViewById(R.id.ivUserPreviewPic);
            tvUsernamePreview = itemView.findViewById(R.id.tvUsernamePreview);
            tvMemberSince = itemView.findViewById(R.id.tvMembersince);
            //rvUserPreviewBadges = itemView.findViewById(R.id.rvUserPreviewBadges);
        }

        public void bind(ParseUser user) throws ParseException, com.parse.ParseException {
            //queryFetchUserWins(user);
            tvUsernamePreview.setText(user.getUsername());

            tvUsernamePreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = c.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (!user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                        Fragment fragment = new OtherUserProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", user);
                        fragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else {
                        ((BottomNavigationView)c.findViewById(R.id.nav_view)).setSelectedItemId(R.id.navigation_profile);
                    }
                }
            });

            if (user.getParseFile("profilePicture") != null) {
                String profilePicture = user.getParseFile("profilePicture").getUrl();
                Glide.with(c).load(profilePicture).into(ivUserPreviewPic);
            }
            Date date = user.getCreatedAt();
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            String reportDate = df.format(date);
            tvMemberSince.setText(reportDate);
        }

        private void queryFetchUserWins(ParseUser user) throws com.parse.ParseException {
            ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
            query.whereEqualTo(Category.KEY_WINNERUSER, user);
            query.findInBackground(new FindCallback<Category>() {
                @Override
                public void done(List<Category> categories, com.parse.ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue with retrieving posts", e);
                    }
                    userBadges.clear();
                    userBadges.addAll(categories);
                    adapter = new BadgesPreviewAdapter(c, userBadges);
                    rvUserPreviewBadges.setAdapter(adapter);
                    rvUserPreviewBadges.setLayoutManager(new LinearLayoutManager(c));
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }


}

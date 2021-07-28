package com.example.the_commoners_guinness.ui.challenges;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.ui.profile.BadgesAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class UserPreviewAdapter extends RecyclerView.Adapter<UserPreviewAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "UserPreviewAdapter";
    private Context context;
    private List<ParseUser> users;
    private List<ParseUser> usersFull;
    private List<Category> userBadges = new ArrayList<>();

    public UserPreviewAdapter(Context context, List<ParseUser> users) {
        this.context = context;
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
        View view = LayoutInflater.from(context).inflate(R.layout.user_preview, parent, false);
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
        RecyclerView rvUserPreviewBadges;
        BadgesPreviewAdapter adapter;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            findViews();
        }

        private void findViews() {
            ivUserPreviewPic = itemView.findViewById(R.id.ivUserPreviewPic);
            tvUsernamePreview = itemView.findViewById(R.id.tvUsernamePreview);
            rvUserPreviewBadges = itemView.findViewById(R.id.rvUserPreviewBadges);
        }

        public void bind(ParseUser user) throws ParseException, com.parse.ParseException {
            queryFetchUserWins(user);
            tvUsernamePreview.setText(user.getUsername());

            if (user.getParseFile("profilePicture") != null) {
                String profilePicture = ParseUser.getCurrentUser().getParseFile("profilePicture").getUrl();
                Glide.with(context).load(profilePicture).into(ivUserPreviewPic);
            }

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
                    adapter = new BadgesPreviewAdapter(context, userBadges);
                    rvUserPreviewBadges.setAdapter(adapter);
                    rvUserPreviewBadges.setLayoutManager(new LinearLayoutManager(context));
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }


}

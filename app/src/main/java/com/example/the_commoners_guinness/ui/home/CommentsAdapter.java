package com.example.the_commoners_guinness.ui.home;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Category;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<com.example.the_commoners_guinness.ui.home.CommentsAdapter.ViewHolder>{

    private static final String TAG = "COMMENTSADAPTER";
    private Context context;
    private List<String> comments;

    public CommentsAdapter(Context context, List<String> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @NotNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_list_item, parent, false);
        return new com.example.the_commoners_guinness.ui.home.CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentsAdapter.ViewHolder holder, int position) {
        String comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCommentUsername;
        private TextView tvCommentItem;
        private ImageView ivCommentImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvCommentUsername = itemView.findViewById(R.id.tvCommentUsername);
            tvCommentItem = itemView.findViewById(R.id.tvCommentItem);
            ivCommentImage = itemView.findViewById(R.id.ivCommentImage);
        }
        public void bind(String comment) {
            String[] username_comment = comment.split(":");
            tvCommentUsername.setText(username_comment[0]);
            tvCommentItem.setText(username_comment[1]);
            fetchProfilePicFromUsername(username_comment[0]);
        }

        public void fetchProfilePicFromUsername(String username) {
            ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
            query.whereEqualTo("username", username);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (users.get(0).getParseFile("profilePicture")!= null) {
                        String profilePicture = ParseUser.getCurrentUser().getParseFile("profilePicture").getUrl();
                        Glide.with(context).load(profilePicture).into(ivCommentImage);
                    }
                }
            });
        }
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<String> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }






}

package com.example.the_commoners_guinness.ui.home;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_commoners_guinness.Category;
import com.example.the_commoners_guinness.ChallengeActivity;
import com.example.the_commoners_guinness.Post;
import com.example.the_commoners_guinness.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.Challenge;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    private static final String TAG = "POSTSADAPTER";
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PostsAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        try {
            holder.bind(post);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCategory;
        private TextView tvUsername;
        private TextView tvCaption;
        private VideoView vvPostVideo;
        private ImageView ivChallenge;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            vvPostVideo = itemView.findViewById(R.id.vvPostVideo);
            ivChallenge = itemView.findViewById(R.id.ivChallenge);
        }

        public void bind(Post post) throws ParseException {
            tvUsername.setText(post.getUser().getUsername());
            tvCaption.setText(post.getCaption());
            Category category = post.getCategory();
            if (category.getPosts() == null || !category.getPosts().contains(post)) {
                category.setPosts(post);
            }
            tvCategory.setText(category.fetchIfNeeded().getString("name"));

            ParseFile file = post.getParseFile("video");
            vvPostVideo.setVideoURI(Uri.parse(file.getUrl()));
            vvPostVideo.requestFocus();
            vvPostVideo.start();
            vvPostVideo.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    vvPostVideo.start();
                }
            });

            ivChallenge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), ChallengeActivity.class);
                    context.startActivity(i);
                }
            });
        }
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }
}

package com.example.the_commoners_guinness.ui.profile;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;
    public static final String TAG = "ProfileAdapter";
    private RecyclerView rvPosts;

    public ProfileAdapter(Context context, List<Post> posts, RecyclerView rvPosts) {
        this.context = context;
        this.posts = posts;
        this.rvPosts = rvPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_post, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProfileAdapter.ViewHolder holder, int position) {
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivGridPost;
        private VideoView vvUserVideo;
        private ImageView ivBtnPlay;
        private TextView tvCategoryProfile;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGridPost = itemView.findViewById(R.id.ivGridPost);
            itemView.setOnClickListener(this);
            vvUserVideo = itemView.findViewById(R.id.vvUserVideo);
            ivBtnPlay = itemView.findViewById(R.id.ivBtnPlay);
            tvCategoryProfile = itemView.findViewById(R.id.tvCategoryProfile);
        }

        public void bind(Post post) throws ParseException {
            tvCategoryProfile.setText((String) post.getCategory().fetchIfNeeded().get("name"));

            ParseFile videoFile = post.getVideo();
            vvUserVideo.setVisibility(View.INVISIBLE);
            vvUserVideo.setVideoURI(Uri.parse(videoFile.getUrl()));

            if (videoFile != null) {
                Log.i(TAG, videoFile.getUrl());
                long thumb = getLayoutPosition() * 1000;
                RequestOptions thumbnail = new RequestOptions().frame(thumb);
                Glide.with(itemView.getContext()).load(videoFile.getUrl()).apply(thumbnail).into(ivGridPost);
            }

            ivBtnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vvUserVideo.setVisibility(View.VISIBLE);
                    ivGridPost.setVisibility(View.INVISIBLE);
                    ivBtnPlay.setVisibility(View.INVISIBLE);
                    vvUserVideo.requestFocus();
                    vvUserVideo.start();

                    vvUserVideo.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            vvUserVideo.setVisibility(View.INVISIBLE);
                            ivGridPost.setVisibility(View.VISIBLE);
                            ivBtnPlay.setVisibility(View.VISIBLE);

                        }
                    });
                }
            });

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Post post = posts.get(position);
            Intent i = new Intent(context, PostDetailActivity.class);
            i.putExtra("post", Parcels.wrap(post));
            i.putExtra("position", position);
            context.startActivity(i);
        }
    }
}


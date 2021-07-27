package com.example.the_commoners_guinness.ui.profile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Post;
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
//
//        int width = rvPosts.getWidth();
//        ViewGroup.LayoutParams params = view.getLayoutParams();
//        params.width = (int)(width * 0.75);
//        view.setLayoutParams(params);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProfileAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView ivGridPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGridPost = itemView.findViewById(R.id.ivGridPost);
            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            ParseFile video = post.getVideo(); // TODO - get video thumbnail
            if (video != null) {
                Log.i(TAG, video.getUrl());
                long thumb = getLayoutPosition()*1000;
                RequestOptions options = new RequestOptions().frame(thumb);
                Glide.with(itemView.getContext()).load(video.getUrl()).apply(options).into(ivGridPost);
            }

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


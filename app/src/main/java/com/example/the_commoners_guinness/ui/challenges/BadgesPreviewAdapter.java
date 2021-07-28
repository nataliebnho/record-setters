package com.example.the_commoners_guinness.ui.challenges;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.models.Post;
import com.example.the_commoners_guinness.ui.profile.BadgesAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BadgesPreviewAdapter extends RecyclerView.Adapter<com.example.the_commoners_guinness.ui.challenges.BadgesPreviewAdapter.ViewHolder>{

        private static final String TAG = "BADGESADAPTER";
        private Context context;
        private List<Category> categories;

        public BadgesPreviewAdapter(Context context, List<Category> categories) {
            this.context = context;
            this.categories = categories;
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.badges_for_discover, parent, false);
            return new com.example.the_commoners_guinness.ui.challenges.BadgesPreviewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.bind(category);
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, String.valueOf(categories.size()));
            return categories.size();
        }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategory;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvBadgeCategory);
        }

        public void bind(Category category) {
            tvCategory.setText(category.getName());
        }
    }

    public void clear() {
        categories.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Category> list) {
        categories.addAll(list);
        notifyDataSetChanged();
    }




}

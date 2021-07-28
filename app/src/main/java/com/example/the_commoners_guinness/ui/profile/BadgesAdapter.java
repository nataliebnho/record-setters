package com.example.the_commoners_guinness.ui.profile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.ViewHolder>{

    private static final String TAG = "BADGESADAPTER";
    private Context context;
    private List<Category> categories;

    public BadgesAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.badges_view, parent, false);
        return new BadgesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
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
}

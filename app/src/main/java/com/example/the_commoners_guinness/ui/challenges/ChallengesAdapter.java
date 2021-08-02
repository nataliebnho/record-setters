package com.example.the_commoners_guinness.ui.challenges;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_commoners_guinness.ChallengeActivity;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.models.Post;
import com.parse.FindCallback;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ViewHolder> implements Filterable {


    private static final String TAG = "ChallengesAdapter";
    private Context context;
    private List<Category> categories;
    private List<Category> categoryFull;

    public ChallengesAdapter(Context context, List<Category> categories, List<Category> categoryFull) {
        this.context = context;
        this.categories = categories;
        this.categoryFull = categoryFull;
    }

    @NonNull
    @NotNull
    @Override
    public ChallengesAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expandable_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChallengesAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.setIsRecyclable(false);
        try {
            holder.bind(category);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Category> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                Log.i(TAG, "constraint is null");
                filteredList.addAll(categoryFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Category category : categoryFull) {
                    if (category.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(category);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            categories.clear();
            categories.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        Post[] leaderboard = new Post[3];
        TextView tvCategoryNameCV;
        TextView tvCountdownView;
        TextView tvUsernameFirstCV;
        TextView tvUsernameSecondCV;
        TextView tvUsernameThirdCV;
        TextView tvNumVotesFirst;
        TextView tvNumVotesSecond;
        TextView tvNumVotesThird;
        Category categoryObj;
        ImageView ivChallengeFromView;
        int numLeaderboard;
        RelativeLayout rlExpandable;
        private CountDownTimer countDownTimer;
        private long timeLeftInMillis;
        private long votingPeriodMillis = 86400000; // There are 86400000 millis in one day
        private Long timeSinceFirstChallengeMillis;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            findViews();
        }

        public void bind(Category category) throws ParseException, com.parse.ParseException {
            Log.i(TAG, String.valueOf(categoryFull.isEmpty()));
            categoryObj = category;
            tvCategoryNameCV.setText(category.getName());
            queryCategoryPosts();
            setCountDownTimer(category);

            ivChallengeFromView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), ChallengeActivity.class);
                    Bundle bundle = new Bundle();
                    i.putExtra("Category", Parcels.wrap(category));
                    context.startActivity(i);
                }
            });
        }

        private void setCountDownTimer(Category category) throws com.parse.ParseException {
            if (category.fetchIfNeeded().getParseObject("firstChallengePost") != null) {
                Date date = (Date) category.fetchIfNeeded().getParseObject("firstChallengePost").fetchIfNeeded().get("createdAt");
                timeSinceFirstChallengeMillis = System.currentTimeMillis() - (category.getFirstChallengePost().getCreatedAt()).getTime();
                timeLeftInMillis = votingPeriodMillis - timeSinceFirstChallengeMillis;

                countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeftInMillis = millisUntilFinished;
                        updateCountDownText();
                    }

                    @Override
                    public void onFinish() {
                        tvCountdownView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                        tvCountdownView.setText("The voting period for this category is closed");
                    }
                }.start();
            } else {
                tvCountdownView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                tvCountdownView.setText("The voting period for this category is closed");
            }
        }

        private void updateCountDownText() {
            int hours   = (int) ((timeLeftInMillis / (1000*60*60)) % 24);
            int minutes = (int) ((timeLeftInMillis / (1000*60)) % 60);
            int seconds = (int) (timeLeftInMillis / 1000) % 60 ;

            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
            tvCountdownView.setText(timeLeftFormatted);
        }




        public void queryCategoryPosts() {
            ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
            query.include(Post.KEY_USER);
            query.setLimit(20);
            query.addDescendingOrder("voteCount");
            query.whereEqualTo(Post.KEY_CATEGORY, categoryObj);
            query.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> posts, com.parse.ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue with retrieving posts", e);
                    }
                    setLeaderboard(posts);
                    try {
                        setLeaderboardUsernamesandVotes();
                    } catch (com.parse.ParseException parseException) {
                        parseException.printStackTrace();
                    }
                }
            });
        }

        public void setLeaderboard(List<Post> posts) {
            numLeaderboard = Math.min(3, posts.size());
            for(int i = 0; i < numLeaderboard; i++) {
                leaderboard[i] = posts.get(i);
            }
        }

        public void setLeaderboardUsernamesandVotes() throws com.parse.ParseException {
            if (numLeaderboard >= 1) {
                tvUsernameFirstCV.setText(leaderboard[0].getUser().getUsername());
                tvNumVotesFirst.setText(String.valueOf(leaderboard[0].get("voteCount")));
            }

            if (numLeaderboard >= 2) {
                tvUsernameSecondCV.setText(leaderboard[1].getUser().getUsername());
                tvNumVotesSecond.setText(String.valueOf(leaderboard[1].get("voteCount")));
            }
            if (numLeaderboard == 3) {
                tvUsernameThirdCV.setText(leaderboard[2].getUser().getUsername());
                tvNumVotesThird.setText(String.valueOf(leaderboard[2].get("voteCount")));

            }
        }

        private void findViews() {
            tvCategoryNameCV = itemView.findViewById(R.id.tvCategoryNameCV);
            tvCountdownView = itemView.findViewById(R.id.tvCountdownView);
            tvUsernameFirstCV = itemView.findViewById(R.id.tvUsernameFirstCV);
            tvUsernameSecondCV = itemView.findViewById(R.id.tvUsernameSecondCV);
            tvUsernameThirdCV = itemView.findViewById(R.id.tvUsernameThirdCV);
            rlExpandable = itemView.findViewById(R.id.rlExpandable);
            ivChallengeFromView = itemView.findViewById(R.id.ivChallengeFromView);
            tvNumVotesFirst = itemView.findViewById(R.id.tvNumVotesFirstD);
            tvNumVotesSecond = itemView.findViewById(R.id.tvNumVotesSecondD);
            tvNumVotesThird = itemView.findViewById(R.id.tvNumVotesThirdD);
        }

    }
}

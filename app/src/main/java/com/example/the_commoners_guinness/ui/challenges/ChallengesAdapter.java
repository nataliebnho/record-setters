package com.example.the_commoners_guinness.ui.challenges;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.models.Post;
import com.parse.FindCallback;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.List;

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ViewHolder>{


    private static final String TAG = "ChallengesAdapter";
    private Context context;
    private List<Category> categories;

    public ChallengesAdapter(Context context,List<Category> categories) {
        this.context = context;
        this.categories = categories;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        Post[] leaderboard = new Post[3];
        TextView tvCategoryNameCV;
        TextView tvCountdownView;
        TextView tvUsernameFirstCV;
        TextView tvUsernameSecondCV;
        TextView tvUsernameThirdCV;
        Category categoryT;
        int numLeaderboard;
        RelativeLayout rlExpandable;
        private boolean expanded = false;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            findViews();
        }

        public void bind(Category category) throws ParseException, com.parse.ParseException {
            categoryT = category;
            tvCategoryNameCV.setText(category.getName());
            queryCategoryPosts();
            tvCategoryNameCV.setClickable(true);
            tvCategoryNameCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rlExpandable.getVisibility() == (View.GONE)) {
                        rlExpandable.setVisibility(View.VISIBLE);
                    } else {
                        rlExpandable.setVisibility(View.GONE);
                    }
                }
            });
        }

        public void queryCategoryPosts() {
            ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
            query.include(Post.KEY_USER);
            query.setLimit(20);
            query.addDescendingOrder("voteCount");
            query.whereEqualTo(Post.KEY_CATEGORY, categoryT);
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
                Log.i("Leaderboard", i + " : " + posts.get(i).getObjectId());
            }
        }

        public void setLeaderboardUsernamesandVotes() throws com.parse.ParseException {
            Log.i(TAG, String.valueOf(numLeaderboard));
            if (numLeaderboard >= 1) {
                tvUsernameFirstCV.setText(leaderboard[0].getUser().getUsername());
              //  tvNumVotesFirstCV.setText(String.valueOf(leaderboard[0].get("voteCount")));
            }

            if (numLeaderboard >= 2) {
                tvUsernameSecondCV.setText(leaderboard[1].getUser().getUsername());
                //tvNumVotesSecond.setText(String.valueOf(leaderboard[1].get("voteCount")));
            }
            if (numLeaderboard == 3) {
                tvUsernameThirdCV.setText(leaderboard[2].getUser().getUsername());
                //tvNumVotesThird.setText(String.valueOf(leaderboard[2].get("voteCount")));

            }
        }

        private void findViews() {
            tvCategoryNameCV = itemView.findViewById(R.id.tvCategoryNameCV);
            tvCountdownView = itemView.findViewById(R.id.tvCountdownView);
            tvUsernameFirstCV = itemView.findViewById(R.id.tvUsernameFirstCV);
            tvUsernameSecondCV = itemView.findViewById(R.id.tvUsernameSecondCV);
            tvUsernameThirdCV = itemView.findViewById(R.id.tvUsernameThirdCV);
            rlExpandable = itemView.findViewById(R.id.rlExpandable);
            rlExpandable.setVisibility(View.GONE);

        }

    }
}

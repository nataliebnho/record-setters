package com.example.the_commoners_guinness.ui.home;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_commoners_guinness.Category;
import com.example.the_commoners_guinness.ChallengeActivity;
import com.example.the_commoners_guinness.Post;
import com.example.the_commoners_guinness.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.parceler.Parcels;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


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
        holder.setIsRecyclable(false);
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
        private ImageView ivVote;
        private TextView tvNumVotes;
        private int currentSize;
        private Category category;
        private TextView tvVotingTimeStatus;

        private TextView tvCountdown;
        private CountDownTimer countDownTimer;
        private boolean timerRunning;
        private long timeLeftInMillis;
        private long timeSincePostMillis;
        private long votingPeriodMillis = 86400000; // There are 86400000 millis in one day
        private long categoryTimeLeftInMillis;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            vvPostVideo = itemView.findViewById(R.id.vvPostVideo);
            ivChallenge = itemView.findViewById(R.id.ivChallenge);
            ivVote = itemView.findViewById(R.id.ivVote);
            tvNumVotes = itemView.findViewById(R.id.tvNumVotes);
            tvCountdown = itemView.findViewById(R.id.tvCountdown);
            tvVotingTimeStatus = itemView.findViewById(R.id.tvVotingTimeLeft);


        }

        public void bind(Post post) throws ParseException {
            tvUsername.setText(post.getUser().getUsername());
            tvCaption.setText(post.getCaption());
            category = post.getCategory();
            tvCategory.setText(category.fetchIfNeeded().getString("name"));

            ParseFile file = post.getVideo();
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
                    i.putExtra("Category", Parcels.wrap(category));
                    context.startActivity(i);
                }
            });

            queryLikesForVoteImage(post, ivVote);
            queryVotesForNumVotes(post);
            setCountDownTimer(post);
        }

        private void setCountDownTimer(Post post) throws ParseException {
            timeSincePostMillis = System.currentTimeMillis() - post.getCreatedAt().getTime();
            setCategoryVoteStatus(category);

//            if (timeSincePostMillis > votingPeriodMillis) {
            if (!category.getVotingPeriod()) {
                tvCountdown.setText("");
                tvVotingTimeStatus.setText("The voting period for this category has closed");
                ivVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context.getApplicationContext(), "The voting period for this category has closed!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                changeLikeButtons(post);
                countDownTimer = new CountDownTimer(category.getVotingPeriodTime(), 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        //timeLeftInMillis = millisUntilFinished;
                        updateCountDownText();
                    }
                    @Override
                    public void onFinish() {
                        tvCountdown.setText("");
                    }
                }.start();
            }
        }

        private void setCategoryVoteStatus(Category category) throws ParseException {
            if (timeSincePostMillis < votingPeriodMillis) {
                category.setVotingPeriod(true);
                category.setVotingPeriodTime(votingPeriodMillis - timeSincePostMillis);
                category.save();
            }
        }

        private void updateCountDownText() {
            int hours   = (int) ((category.getVotingPeriodTime() / (1000*60*60)) % 24);
            int minutes = (int) ((category.getVotingPeriodTime() / (1000*60)) % 60);
            int seconds = (int) (category.getVotingPeriodTime() / 1000) % 60 ;

            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
            tvCountdown.setText(timeLeftFormatted);
        }

        private void changeLikeButtons(Post post) {
            ivVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (ivVote.isSelected()) {
                                deleteVote(post);
                                ivVote.setImageResource(R.drawable.vote_empty);
                                ivVote.setSelected(false);
                                tvNumVotes.setText("" + (currentSize - 1));
                                currentSize -= 1;
                                queryForUpdateWinner(category);
                            } else {
                                postVote(post);
                                ivVote.setImageResource(R.drawable.vote);
                                ivVote.setSelected(true);
                                tvNumVotes.setText("" + (currentSize + 1));
                                currentSize += 1;
                                queryForUpdateWinner(category);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.e(TAG, "post was not liked");
                        }
                    }
                });

        }

        private void queryVotesForNumVotes(Post post) {
            ParseQuery simpleQuery = post.getRelation("vote").getQuery();
            simpleQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        tvNumVotes.setText(String.valueOf(objects.size()));
                        currentSize = objects.size();
                    }
                }
            });
        }

    }



    private void queryLikesForVoteImage(Post post, ImageView ivVotes) {
        ParseQuery query = post.getRelation("vote").getQuery().whereContains("objectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        //do not like
                        ivVotes.setImageResource(R.drawable.vote_empty);
                        ivVotes.setSelected(false);
                        Log.d(TAG, "current user not liked it");
                    } else {
                        // display that it is liked
                        ivVotes.setImageResource(R.drawable.vote);
                        ivVotes.setSelected(true);
                        Log.d(TAG, "current user did already liked it");
                    }
                }
            }
        });
    }

    private void queryForUpdateWinner(Category category) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.setLimit(1);
        query.addDescendingOrder("voteCount");
        query.whereEqualTo(Post.KEY_CATEGORY, category);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }
                try {
                    Log.i("Winning post: ", posts.get(0).getObjectId());
                    category.setWinner(posts.get(0));
                    category.setWinnerUser(posts.get(0).getUser());
                    category.save();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
        });
    }

    private void postVote(Post post) throws ParseException {
        ParseRelation<ParseObject> relation = post.getRelation("vote");
        relation.add(ParseUser.getCurrentUser());
        post.setVoteCount(post.getVoteCount() + 1);
        post.save();
    }

    private void deleteVote(Post post) throws ParseException {
        ParseRelation<ParseObject> relation = post.getRelation("vote");
        relation.remove(ParseUser.getCurrentUser());
        post.setVoteCount(post.getVoteCount() - 1);
        post.save();
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

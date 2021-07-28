package com.example.the_commoners_guinness.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.ChallengeActivity;
import com.example.the_commoners_guinness.models.Post;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.ui.profile.OtherUserProfileFragment;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    private static final String TAG = "POSTSADAPTER";
    private FragmentActivity c;
    private List<Post> posts;
    private Activity activity;
    SimpleExoPlayer simpleExoPlayer;
    PlayerView playerView;


    public PostsAdapter(FragmentActivity c, List<Post> posts) {
        this.c = c;
        this.posts = posts;

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.item_view, parent, false);
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
        private ImageView ivLike;
        private int currentLikeSize;
        private int currentVoteSize;
        private Category category;
        private TextView tvVotingTimeStatus;
        private TextView tvNumLikes;
        private ImageView ivComment;
        private TextView tvCountdown;
        private CountDownTimer countDownTimer;
        private long timeLeftInMillis;
        private long votingPeriodMillis = 86400000; // There are 86400000 millis in one day
        private Long timeSinceFirstChallengeMillis;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            findViews();
        }

        public void bind(Post post) throws ParseException {
            tvUsername.setText(post.getUser().getUsername());
            tvCaption.setText(post.getCaption());
            category = post.getCategory();
            tvCategory.setText(category.fetchIfNeeded().getString("name"));

            ivChallenge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), ChallengeActivity.class);
                    i.putExtra("Category", Parcels.wrap(category));
                    c.startActivity(i);
                }
            });

            ivComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        onShowPopup(v, post);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            setUsernameOnClickListener(post);
            setCountDownTimer(post);
            configureVideoView(post);
            queryVotesForVoteImage(post, ivVote);
            queryVotesForNumVotes(post);
            setOnDoubleTap(post);

            queryLikesForNumLikes(post);
            queryLikesForHeartImage(post, ivLike);
            changeLikeButtons(post);
        }

        private void setUsernameOnClickListener(Post post) {
            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager fragmentManager = c.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (!post.getUser().getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                        Fragment fragment = new OtherUserProfileFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", post.getUser());
                        fragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else {
                        ((BottomNavigationView)c.findViewById(R.id.nav_view)).setSelectedItemId(R.id.navigation_profile);
                    }

                }
            });
        }

        private void configureVideoView(Post post) {
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


        }

        private void setCountDownTimer(Post post) throws ParseException {
            if (category.fetchIfNeeded().getParseObject("firstChallengePost") != null) {
                Date date = (Date) category.fetchIfNeeded().getParseObject("firstChallengePost").fetchIfNeeded().get("createdAt");
                timeSinceFirstChallengeMillis = System.currentTimeMillis() - (category.getFirstChallengePost().getCreatedAt()).getTime();
                timeLeftInMillis = votingPeriodMillis - timeSinceFirstChallengeMillis;
            }

            if (timeSinceFirstChallengeMillis == null || timeSinceFirstChallengeMillis > votingPeriodMillis) {
                votingPeriodClosedLogic();
            } else {
                votingPeriodOpenLogic(post);
            }
        }

        private void votingPeriodOpenLogic(Post post) {
            changeVoteButtons(post);
            countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftInMillis = millisUntilFinished;
                    updateCountDownText();
                }
                @Override
                public void onFinish() {
                    tvCountdown.setText("The voting period for this category is closed");
                }
            }.start();
        }

        private void votingPeriodClosedLogic() {
            category.remove("firstChallengePost");
            category.saveInBackground();
            tvCountdown.setText("");
            tvVotingTimeStatus.setText("The voting period for this category is closed");
            ivVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(c.getApplicationContext(), "The voting period for this category is closed!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updateCountDownText() {
            int hours   = (int) ((timeLeftInMillis / (1000*60*60)) % 24);
            int minutes = (int) ((timeLeftInMillis / (1000*60)) % 60);
            int seconds = (int) (timeLeftInMillis / 1000) % 60 ;

            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
            tvCountdown.setText(timeLeftFormatted);
        }


        private void setOnDoubleTap(Post post) {
            itemView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (timeSinceFirstChallengeMillis != null) {
                            try {
                                setVoteButtons(post);
                            } catch (ParseException | JSONException parseException) {
                                parseException.printStackTrace();
                            }
                        } else {
                            Toast.makeText(c.getApplicationContext(), "The voting period for this category has closed!", Toast.LENGTH_SHORT).show();
                        }
                        return super.onDoubleTap(e);
                    }
                });
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        private void changeVoteButtons(Post post) {
            ivVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Category currentCategory = post.getCategory();
                            ParseUser user = ParseUser.getCurrentUser();
                            if (currentCategory.hasVoted(user)) { // user has voted in this category
                                if (ivVote.isSelected()) { // user has voted in this category, and is unliking that post
                                    setVoteButtons(post);
                                } else { // user has voted in this category, and is trying to like another post
                                    Toast.makeText(c.getApplicationContext(),
                                            "You can only vote for one post per category", Toast.LENGTH_SHORT).show();
                                }
                            } else { // user has not voted in this category yet
                                setVoteButtons(post);
                            }
                        } catch (ParseException | JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "post was not liked");
                        }
                    }
                });
        }

        private void setVoteButtons(Post post) throws ParseException, JSONException {
            if (ivVote.isSelected()) {
                deleteVote(post);
                ivVote.setImageResource(R.drawable.vote_empty);
                ivVote.setSelected(false);
                Log.d("voted", "setVoteButtons: this is false now");
                tvNumVotes.setText("" + (currentVoteSize - 1));
                currentVoteSize -= 1;
                queryForUpdateWinner(category);
            } else {
                postVote(post);
                ivVote.setImageResource(R.drawable.vote);
                ivVote.setSelected(true);
                Log.d("voted", "setVoteButtons: this is true now");
                tvNumVotes.setText("" + (currentVoteSize + 1));
                currentVoteSize += 1;
                queryForUpdateWinner(category);
            }
        }

        private void deleteVote(Post post) throws ParseException, JSONException {
            ParseRelation<ParseObject> relation = post.getRelation("vote");
            relation.remove(ParseUser.getCurrentUser());
            post.setVoteCount(post.getVoteCount() - 1);

            Category currentCategory = post.getCategory();
            ParseUser user = ParseUser.getCurrentUser();

            if (currentCategory.hasVoted(user)){
                currentCategory.removeVote(user);
                currentCategory.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("voted", "done: saving category");
                        Log.d("voted1", "deleteVote: saving postin backgoriunddfadsf");
                        post.saveInBackground();
                    }
                });
            } else {
                Log.d("voted2", "deleteVote: saving postin backgoriunddfadsf");
                post.saveInBackground();
            }
        }

        private void queryVotesForNumVotes(Post post) {
            ParseQuery simpleQuery = post.getRelation("vote").getQuery();
            simpleQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        tvNumVotes.setText(String.valueOf(objects.size()));
                        currentVoteSize = objects.size();
                    }
                }
            });
        }

        private void changeLikeButtons(Post post) {
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (ivLike.isSelected()) {
                            deleteLikes(post);
                            ivLike.setImageResource(R.drawable.ufi_heart);
                            ivLike.setSelected(false);
                            tvNumLikes.setText("" + (currentLikeSize - 1));
                            currentLikeSize -= 1;
                        } else {
                            postLikes(post);
                            ivLike.setImageResource(R.drawable.ufi_heart_active);
                            ivLike.setSelected(true);
                            tvNumLikes.setText("" + (currentLikeSize + 1));
                            currentLikeSize += 1;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e(TAG, "post was not liked");
                    }
                }
            });
        }

        private void queryLikesForNumLikes(Post post) {
            ParseQuery simpleQuery = post.getRelation("like").getQuery();
            simpleQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        tvNumLikes.setText("" + objects.size());
                        currentLikeSize = objects.size();
                    }
                }
            });
        }

        // call this method when required to show popup
        public void onShowPopup(View v, Post post) throws JSONException {
            LayoutInflater layoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View inflatedView = layoutInflater.inflate(R.layout.comment_popup_layout, null, false);
            ListView listView = inflatedView.findViewById(R.id.commentsListView);
            LinearLayout headerView = (LinearLayout)inflatedView.findViewById(R.id.headerLayout);
            Display display = c.getWindowManager().getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);
            DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            PopupWindow popWindow = new PopupWindow(inflatedView, width, height - 200, true);
            popWindow.setBackgroundDrawable(c.getResources().getDrawable(R.drawable.plainwhitebackground));

            popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

            ArrayList<String> commentsList = new ArrayList<String>();
            ImageView postComment = inflatedView.findViewById(R.id.postComment);
            EditText etWriteComment = inflatedView.findViewById(R.id.writeComment);

            postComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comment = etWriteComment.getText().toString();
                    try {
                        postComment(post, comment);
                        Log.i("Comment post success: ", comment);
                        etWriteComment.setText("");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            setSimpleList(listView, post, commentsList);
            popWindow.setAnimationStyle(R.style.PopupAnimation);
            popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
        }

        private void setSimpleList(ListView listView, Post post, ArrayList<String> commentsList) throws JSONException {
            JSONArray comments = post.getComments();

            if (comments == null || comments.length() == 0) {
                commentsList.add("Be the first to comment!");
            } else {
                for (int i = 0; i < comments.length(); i ++) {
                    commentsList.add((String) comments.get(i));
                }
            }

            listView.setAdapter(new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, commentsList));
        }

        private void findViews() {
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            vvPostVideo = itemView.findViewById(R.id.vvUserVideo);
            ivChallenge = itemView.findViewById(R.id.ivChallenge);
            ivVote = itemView.findViewById(R.id.ivVote);
            tvNumVotes = itemView.findViewById(R.id.tvNumVotes);
            tvCountdown = itemView.findViewById(R.id.tvCountdown);
            tvVotingTimeStatus = itemView.findViewById(R.id.tvVotingTimeLeft);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            ivComment = itemView.findViewById(R.id.ivComment);
        }

    }

    private void queryVotesForVoteImage(Post post, ImageView ivVotes) {
        ParseQuery query = post.getRelation("vote").getQuery().
                whereContains("objectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        //do not like
                        ivVotes.setImageResource(R.drawable.vote_empty);
                        ivVotes.setSelected(false);
                    } else {
                        // display that it is liked
                        ivVotes.setImageResource(R.drawable.vote);
                        ivVotes.setSelected(true);
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
        post.getCategory().setUsersVoted(ParseUser.getCurrentUser().getObjectId());
        post.saveInBackground();
        post.getCategory().saveInBackground();

    }



    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    private void queryLikesForHeartImage(Post post, ImageView ivLikes) {
        ParseQuery query = post.getRelation("like").getQuery().whereContains("objectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        //do not like
                        ivLikes.setImageResource(R.drawable.ufi_heart);
                        ivLikes.setSelected(false);
                    } else {
                        // display that it is liked
                        ivLikes.setImageResource(R.drawable.ufi_heart_active);
                        ivLikes.setSelected(true);
                    }
                }
            }
        });
    }

    private void postLikes(Post post) throws ParseException {
        ParseRelation<ParseObject> relation = post.getRelation("like");
        relation.add(ParseUser.getCurrentUser());
        post.save();
    }

    private void deleteLikes(Post post) throws ParseException {
        ParseRelation<ParseObject> relation = post.getRelation("like");
        relation.remove(ParseUser.getCurrentUser());
        post.save();
    }

    private void postComment(Post post, String comment) throws ParseException {
        post.setComments(comment);
        post.save();
    }


    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS; // 60000
        int HOUR_MILLIS = 60 * MINUTE_MILLIS; //
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

}

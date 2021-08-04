package com.example.the_commoners_guinness.ui.create;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.SetLocationMapsActivity;
import com.example.the_commoners_guinness.models.Post;
import com.example.the_commoners_guinness.R;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateFragment extends Fragment {

    public static final String CHANNEL = "channel";
    public final String TAG = "CreateFragment";
    ImageButton btnTakeVideo;
    ImageView btnUploadPhoto;
    Button btnShare;
    VideoView vvVideoToPost;
    File mediaFile;
    EditText etCaption;
    Button btnAddLocation;
    ParseGeoPoint location;
    Category category;
    AutoCompleteTextView actvCategory;
    List<String> categoryNames;
    List<Category> categoryObjects = new ArrayList<>();
    long VOTING_PERIOD_MILLIS = 10000;
    boolean isNewCategory = false;
    View placeholder;

    private static final int VIDEO_CAPTURE = 101;
    public static final int VIDEO_UPLOAD = 102;
    private static final int MAPS_REQUEST_CODE = 40;

    public CreateFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable
            Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTakeVideo = view.findViewById(R.id.btnTakeVideoChallenge);
        btnUploadPhoto = view.findViewById(R.id.ivUpload);
        vvVideoToPost = view.findViewById(R.id.vvVideoToPostChallenge);
        placeholder = view.findViewById(R.id.placeholder);
        RelativeLayout video_view_container = view.findViewById(R.id.video_view_container);
        video_view_container.setClipToOutline(true);

        etCaption = view.findViewById(R.id.etCaptionChallenge);
        btnShare = view.findViewById(R.id.btnPostChallenge);
        btnAddLocation = view.findViewById(R.id.btnAddLocation);
        actvCategory = view.findViewById(R.id.autoCompleteTextView);

        categoryNames = queryCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, categoryNames);

        actvCategory.setThreshold(1); //will start working from first character
        actvCategory.setAdapter(adapter);
        actvCategory.setTextColor(Color.BLACK);


        btnTakeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startRecordingVideo(); }
        });

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("video/*");
                startActivityForResult(i, VIDEO_UPLOAD);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { sharePost(); }
        });

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SetLocationMapsActivity.class);
                startActivityForResult(i, MAPS_REQUEST_CODE);
            }
        });
    }

    private void sharePost() {
        String caption = etCaption.getText().toString();
        String categoryName = actvCategory.getText().toString();

        if (categoryNames.contains(categoryName)) {
            int index = categoryNames.indexOf(categoryName);
            savePostChallenge(ParseUser.getCurrentUser(), caption, categoryObjects.get(index));
        } else {
            Category newCategory = new Category();
            newCategory.setName(categoryName);
            isNewCategory = true;
            savePostChallenge(ParseUser.getCurrentUser(), caption, newCategory);
           // savePost(ParseUser.getCurrentUser(), caption, categoryName);
        }
        etCaption.setText("");
        actvCategory.setText("");
        vvVideoToPost.setBackgroundResource(0);
    }

    public void startRecordingVideo() {
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            mediaFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES), "share_image_" + System.currentTimeMillis() + ".mp4");

            Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider.the-commoners-guinness", mediaFile);
            Log.i("CAMERA: ", fileProvider.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
            startActivityForResult(intent, VIDEO_CAPTURE);

        } else {
            Toast.makeText(getContext(), "No camera on device", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        VideoView videoView = getView().findViewById(R.id.vvVideoToPostChallenge);
        if (requestCode == VIDEO_CAPTURE) {
            onActivityResultVideoCapture(requestCode, resultCode, data, videoView);
        }
        if (requestCode == MAPS_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                location = data.getParcelableExtra("Location");
            }
        }
        if (requestCode == VIDEO_UPLOAD) {
            if (resultCode == getActivity().RESULT_OK) {
               if (data != null) {
                   btnTakeVideo.setVisibility(View.GONE);
                   btnUploadPhoto.setVisibility(View.GONE);
                   placeholder.setVisibility(View.GONE);
                   Uri uri = data.getData();
                   createDocumentFileFromFile(uri);
               }
            }
        }
    }

    private void onActivityResultVideoCapture(int requestCode, int resultCode, Intent data, VideoView videoView) {
        if (resultCode == getActivity().RESULT_OK) {
            btnTakeVideo.setVisibility(View.GONE);
            btnUploadPhoto.setVisibility(View.GONE);
            placeholder.setVisibility(View.GONE);
            videoView.setVideoURI(data.getData());
            videoView.setMediaController(new MediaController(getContext()));
            videoView.requestFocus();
            videoView.start();
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getContext(), "Video recording cancelled.",  Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Failed to record video",  Toast.LENGTH_LONG).show();
        }
    }

    private void createDocumentFileFromFile(Uri uri) {
        mediaFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES), "share_image_" + System.currentTimeMillis() + ".mp4");

        Uri outputUri =  FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider.the-commoners-guinness", mediaFile);

        try {
            InputStream in = getContext().getContentResolver().openInputStream(uri);
            OutputStream out = getContext().getContentResolver().openOutputStream(outputUri);
            try {
                int nbOfBytes = 0;
                final int BLOCKSIZE = 4096;
                byte[] bytesRead = new byte[BLOCKSIZE];
                while (true) {
                    nbOfBytes = in.read(bytesRead);
                    if (nbOfBytes == -1) {
                        break;
                    }
                    out.write(bytesRead, 0, nbOfBytes);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePostChallenge(ParseUser currentUser, String caption, Category categoryObj) {
        Post post = new Post();
        post.setCaption(caption);
        post.setCategory(categoryObj);
        if (location != null) {
            post.setLocation(location);
        }
        post.setVideo(new ParseFile(mediaFile));
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                try {
                    Post firstPost = (Post) categoryObj.fetchIfNeeded().getParseObject("firstChallengePost");
                    if (firstPost == null) { // if there is no challenge going on, there are 2 possible cases: one there is an existing category and it is now closed, or it is a new category
                        if (isNewCategory) { // if this is a new category
                            categoryObj.setWinner(post); //set the category's winner and winner user to the current post and user
                            categoryObj.setWinnerUser(ParseUser.getCurrentUser());
                        }
                        categoryObj.setFirstChallengePost(post); // in both cases, set first challenge post to this post
                        categoryObj.saveInBackground();
                        scheduleNotificationAlarm(categoryObj.getName(), VOTING_PERIOD_MILLIS);
                    }
                    else { // else, there is a challenge going on
                        long firstPostTime = categoryObj.fetchIfNeeded().getParseObject("firstChallengePost").fetchIfNeeded().getCreatedAt().getTime(); // health check to see if the first challenge post is still active
                        long timeSinceFirstChallengeMillis = System.currentTimeMillis() - firstPostTime;
                        if (timeSinceFirstChallengeMillis > VOTING_PERIOD_MILLIS) { // this challenge has expired
                            categoryObj.setFirstChallengePost(post); // reset first challenge post
                            categoryObj.saveInBackground();
                            scheduleNotificationAlarm(categoryObj.getName(), VOTING_PERIOD_MILLIS);
                        } else { // this challenge is active
                            scheduleNotificationAlarm(categoryObj.getName(), timeSinceFirstChallengeMillis);
                        }

                    }
//                    // create or subscribe to a push notification channel
                    ParsePush.subscribeInBackground(categoryObj.getName());
//                    ParseUser.getCurrentUser().getJSONArray("subscriptions").put(categoryObj.getName());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
        });
    }

    private List<String> queryCategories() {
        List<String> categoryNames = new ArrayList<>();
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving categories", e);
                }
                for (Category category: categories) {
                    categoryNames.add(category.getName());
                    categoryObjects.add(category);
                }
            }
        });
        return categoryNames;
    }

    public void scheduleNotificationAlarm(String categoryName, long timer) {
        Context context = getContext();
        createNotificationChannel();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        long timeAtCreatePost = System.currentTimeMillis();

        Intent intent = new Intent(getActivity(), ReminderBroadcast.class);
        intent.putExtra("categoryName", categoryName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtCreatePost + timer, pendingIntent);
    }

    private void createNotificationChannel() {
        Context context = getContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TestingChannel";
            String description = "Channel for reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void cloudCodeFunction() {
        final HashMap<String, String> params = new HashMap<>();
        // Calling the cloud code function
        ParseCloud.callFunctionInBackground("pushsample", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object response, ParseException exc) {
                if(exc == null) {
                    // The function was executed, but it's interesting to check its response
                    Toast.makeText(getContext(), "Successful push", Toast.LENGTH_LONG).show();
                }
                else {
                    // Something went wrong
                    Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}


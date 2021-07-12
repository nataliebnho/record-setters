package com.example.the_commoners_guinness.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.the_commoners_guinness.Category;
import com.example.the_commoners_guinness.Post;
import com.example.the_commoners_guinness.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;

public class CreateFragment extends Fragment {


    public final String APP_TAG = "CreateFragment";
    public final String TAG = "CreateFragment";
    private static final int VIDEO_CAPTURE = 101;
    ImageButton btnTakeVideo;
    Button btnShare;
    VideoView vvVideoToPost;
    File mediaFile;
    EditText etCaption;
    EditText etCategory;
    String photoFileName = "video.mp4";
    int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;

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
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTakeVideo = view.findViewById(R.id.btnTakeVideo);
        vvVideoToPost = view.findViewById(R.id.vvVideoToPost);
        etCaption = view.findViewById(R.id.etCaption);
        etCategory = view.findViewById(R.id.etCategory);
        btnShare = view.findViewById(R.id.btnPost);

        btnTakeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecordingVideo();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = etCaption.getText().toString();
                String category = etCategory.getText().toString();

                savePost(ParseUser.getCurrentUser(), caption, category);
            }
        });

    }
    public void startRecordingVideo() {
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(intent, VIDEO_CAPTURE);

        } else {
            Toast.makeText(getContext(), "No camera on device", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getContext(), "Video has been saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                //playbackRecordedVideo();
                VideoView videoView = getView().findViewById(R.id.vvVideoToPost);
                videoView.setVideoURI(data.getData());
                videoView.setMediaController(new MediaController(getContext()));
                videoView.requestFocus();
                videoView.start();
                mediaFile = new File(data.getData().getPath());
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Toast.makeText(getContext(), "Video recording cancelled.",  Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Failed to record video",  Toast.LENGTH_LONG).show();
            }
        }
    }

    private void savePost(ParseUser currentUser, String caption, String categoryName) {
        Post post = new Post();
        post.setCaption(caption);
        Category category = new Category();
        category.setName(categoryName);
        post.setCategory(category);
        //post.setVideo(new ParseFile(mediaFile));
        post.setUser(currentUser);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(APP_TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(APP_TAG, "Post save was successful!");
                //getView().findViewById(R.id.etCaption);
               // ivPhotoToPost.setImageResource(0);
            }
        });
    }


}
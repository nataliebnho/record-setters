package com.example.the_commoners_guinness.ui.dashboard;

import android.content.Intent;
import android.content.pm.PackageManager;

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

public class CreateFragment extends Fragment {

    public final String APP_TAG = "CreateFragment";
    private static final int VIDEO_CAPTURE = 101;
    ImageButton btnTakeVideo;
    Button btnShare;
    VideoView vvVideoToPost;
    File mediaFile;
    EditText etCaption;
    EditText etCategory;

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
        vvVideoToPost = view.findViewById(R.id.vvVideoToPostChallenge);
        etCaption = view.findViewById(R.id.etCaptionChallenge);
        etCategory = view.findViewById(R.id.etCategory);
        btnShare = view.findViewById(R.id.btnPostChallenge);

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
                etCaption.setText("");
                etCategory.setText("");
                vvVideoToPost.setBackgroundResource(0);
            }
        });
    }

    public void startRecordingVideo() {
        if (getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //mediaFile = getPhotoFileUri(photoFileName);

            mediaFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES), "share_image_" + System.currentTimeMillis() + ".mp4");
            Log.i("Test MediaFile:", mediaFile.toString());
            // wrap File object into a content provider. NOTE: authority here should match authority in manifest declaration
            Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider.the-commoners-guinness", mediaFile);
            Log.i("Fileprovider: ", fileProvider.toString());

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
            startActivityForResult(intent, VIDEO_CAPTURE);

        } else {
            Toast.makeText(getContext(), "No camera on device", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getContext(), "Video has been saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                VideoView videoView = getView().findViewById(R.id.vvVideoToPostChallenge);
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
    }

    private void savePost(ParseUser currentUser, String caption, String categoryName) {
        Post post = new Post();

        post.setCaption(caption);

        Category category = new Category();
        category.setName(categoryName);
        post.setCategory(category);

        post.setVideo(new ParseFile(mediaFile));
        post.setUser(currentUser);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(APP_TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                //category.setPosts(post); ** Why isn't this working
                Log.i(APP_TAG, "Post save was successful!");
            }
        });
    }



}
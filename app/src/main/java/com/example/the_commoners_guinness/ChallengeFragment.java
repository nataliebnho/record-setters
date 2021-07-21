package com.example.the_commoners_guinness;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ChallengeFragment extends Fragment {

    public final String TAG = "ChallengeFragment";
    ImageButton btnTakeVideoChallenge;
    ImageView ivUploadChallenge;
    Button btnShareChallenge;
    VideoView vvVideoToPostChallenge;
    File mediaFile;
    EditText etCaptionChallenge;
    TextView tvChallengeCategory;
    String categoryName;
    Category category;
    Button btnAddLocationChallenge;
    ParseGeoPoint location;
    private static final int VIDEO_CAPTURE = 10;
    public static final int VIDEO_UPLOAD = 11;
    public static final int MAPS_REQUEST_CODE = 41;

    public ChallengeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_challenge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnTakeVideoChallenge = view.findViewById(R.id.btnTakeVideoChallenge);
        vvVideoToPostChallenge = view.findViewById(R.id.vvVideoToPostChallenge);
        etCaptionChallenge = view.findViewById(R.id.etCaptionChallenge);
        btnShareChallenge = view.findViewById(R.id.btnPostChallenge);
        tvChallengeCategory = view.findViewById(R.id.tvChallengeCategory);
        btnAddLocationChallenge = view.findViewById(R.id.btnAddLocationChallenge);
        ivUploadChallenge = view.findViewById(R.id.ivUploadChallenge);

        category = Parcels.unwrap(getArguments().getParcelable("category"));
;       tvChallengeCategory.setText(category.getName());

        btnTakeVideoChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecordingVideo();
            }
        });

        btnAddLocationChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), SetLocationMapsActivity.class);
                startActivityForResult(i, MAPS_REQUEST_CODE);
            }
        });

        ivUploadChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("video/*");
                startActivityForResult(i, VIDEO_UPLOAD);
            }
        });

        btnShareChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = etCaptionChallenge.getText().toString();

                savePost(ParseUser.getCurrentUser(), caption);
                etCaptionChallenge.setText("");
                vvVideoToPostChallenge.setBackgroundResource(0);
            }
        });
    }

    private void startRecordingVideo() {
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
        if (requestCode == MAPS_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                location = data.getParcelableExtra("Location");
                Log.i("Location", location.toString());
            }
        }
        if (requestCode == VIDEO_UPLOAD) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    Toast.makeText(getContext(), "Video content URI: " + data.getData(), Toast.LENGTH_SHORT).show();
                    createDocumentFileFromFile(uri);
                }
            }
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

    private void savePost(ParseUser currentUser, String caption) {
        Post post = new Post();

        post.setCaption(caption);

        post.setCategory(category);

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
                //category.setPosts(post); ** Why isn't this working
                Log.i(TAG, "Post save was successful!");
            }
        });
    }

}
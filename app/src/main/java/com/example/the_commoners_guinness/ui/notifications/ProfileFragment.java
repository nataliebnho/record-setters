package com.example.the_commoners_guinness.ui.notifications;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.the_commoners_guinness.Category;
import com.example.the_commoners_guinness.ChildFragment.UserBadgesFragment;
import com.example.the_commoners_guinness.ChildFragment.UserPostsFragment;
import com.example.the_commoners_guinness.Post;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 40;
    private ImageView ivProfilePicture;
    private ImageView ivAddProfilePicture;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    private TextView tvNumBadges;
    private ArrayList<Category> categoryWins = new ArrayList<Category>();
    TabLayout tabLayout;
    ViewPager viewPager;
    private ArrayList<String> categoryWinsName = new ArrayList<String>();
    Fragment userBadgesFragment;
    private TextView tvUsername;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            queryFetchUserWins(view);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvUsername = view.findViewById(R.id.tvProfileName);
        ivProfilePicture = view.findViewById(R.id.ivProfilePic);
        ivAddProfilePicture = view.findViewById(R.id.ivAddProfileImage);
        tvNumBadges = view.findViewById(R.id.tvNumBadges);


        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        if (ParseUser.getCurrentUser().get("profilePicture") != null) {
            String profilePicture = ParseUser.getCurrentUser().getParseFile("profilePicture").getUrl();
            Glide.with(getContext()).load(profilePicture).into(ivProfilePicture);
        }

        ivAddProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });




    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.the-commoners-guinness", photoFile);
        Log.i(TAG, "This is the fileProvider: " + fileProvider.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    private File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        Log.i(TAG, "This is the file: " + file.toString());

        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Log.i("Absolute path", photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivProfilePicture.setImageBitmap(takenImage);
                saveProfilePicture(photoFile);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveProfilePicture(File photoFile) {
        ParseFile parseFile = new ParseFile(photoFile);
        ParseUser.getCurrentUser().put("profilePicture", parseFile);
        ParseUser.getCurrentUser().saveInBackground();
    }

    private void queryFetchUserWins(View view) throws ParseException {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.whereEqualTo(Category.KEY_WINNERUSER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with retrieving posts", e);
                }
                for (Category category: categories) {
                    categoryWins.add(category);
                  //  categoryWinsName.add(category.getName());
                    Log.i("Category wins: ", category.getName());
                }
                setBadgesText();
                setFragments(view);
            }
        });

    }

    private void setBadgesText() {
        tvNumBadges.setText(String.valueOf(categoryWins.size()));
    }

    private void setFragments(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        userBadgesFragment = new UserBadgesFragment();
        Fragment userPostsFragment = new UserPostsFragment();

        Bundle badgesBundle = new Bundle();
        //badgesBundle.putStringArrayList("CategoryNames", categoryWinsName);
        badgesBundle.putParcelableArrayList("Categories", categoryWins);
        userBadgesFragment.setArguments(badgesBundle);

        adapter.addFragment(userPostsFragment, "Posts");
        adapter.addFragment(userBadgesFragment, "Badges");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
package com.example.the_commoners_guinness.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.R;
import com.example.the_commoners_guinness.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 40;

    private ImageView ivProfilePicture;
    private ImageView ivAddProfilePicture;
    public String photoFileName = "photo.jpg";
    private File photoFile;
    private TextView tvNumBadges;
    private TextView tvUsername;
    private ImageView ivEditProfile;
    private TextView tvBioOther;

    TabLayout tabLayout;
    ViewPager viewPager;
    Fragment userBadgesFragment;

    private ArrayList<Category> categoryWins = new ArrayList<Category>();
    private ArrayList<String> categoryWinsName = new ArrayList<String>();

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
        findViews(view);
        ivAddProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getContext(), PopupEdit.class);
//                startActivityForResult(i, 400);
                displayPopUpWindow();
            }
        });
    }

    private void displayPopUpWindow() {
        View layout = getLayoutInflater().inflate(R.layout.activity_popup_edit,null);

        EditText etEditName = layout.findViewById(R.id.etEditName);
        EditText etEditBio = layout.findViewById(R.id.etEditBio);
        EditText etEditEmail = layout.findViewById(R.id.etEditEmail);

        etEditName.setText(ParseUser.getCurrentUser().getUsername());

        if (ParseUser.getCurrentUser().get("bio") != null) {
            etEditBio.setText((String) ParseUser.getCurrentUser().get("bio"));
        }

        final PopupWindow popup = new PopupWindow(getContext());
        popup.setContentView(layout);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        popup.setWidth((int)(width * 0.8));
        popup.setHeight((int) (height * 0.7));
        popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button button = layout.findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View layout) {
                popup.dismiss();
                if (etEditBio.getText() != null) {
                    ParseUser.getCurrentUser().put("bio", etEditBio.getText().toString());
                    ParseUser.getCurrentUser().saveInBackground();
                    tvBioOther.setText(etEditBio.getText().toString());

                }
            }
        });
    }

    private void findViews(View view) {
        tvUsername = view.findViewById(R.id.tvProfileNameOther);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicOther);
        ivAddProfilePicture = view.findViewById(R.id.ivAddProfileImage);
        tvNumBadges = view.findViewById(R.id.tvNumBadgesOther);
        ivEditProfile = view.findViewById(R.id.ivEditProfile);
        tvBioOther = view.findViewById(R.id.tvBioOther);

        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        if (ParseUser.getCurrentUser().get("bio") != null) {
            tvBioOther.setText((String) ParseUser.getCurrentUser().get("bio"));
        }

        if (ParseUser.getCurrentUser().get("profilePicture") != null) {
            String profilePicture = ParseUser.getCurrentUser().getParseFile("profilePicture").getUrl();
            Glide.with(getContext()).load(profilePicture).transform(new CircleCrop()).into(ivProfilePicture);
        }
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.the-commoners-guinness", photoFile);
        Log.i(TAG, "This is the fileProvider: " + fileProvider.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        Log.i(TAG, "This is the file: " + file.toString());

        return new File(mediaStorageDir.getPath() + File.separator + photoFileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Log.i("Absolute path", photoFile.getAbsolutePath());
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
                    Log.i("Category wins: ", category.getName());
                }
                setBadgesText();
                setChildrenFragments(view);
            }
        });

    }

    private void setBadgesText() {
        tvNumBadges.setText(String.valueOf(categoryWins.size()));
    }

    private void setChildrenFragments(View view) {
        tabLayout = view.findViewById(R.id.tabLayoutOther);
        viewPager = view.findViewById(R.id.viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        userBadgesFragment = new UserBadgesFragment();
        Fragment userPostsFragment = new UserPostsFragment();

        Bundle userBundle = new Bundle();
        userBundle.putParcelable("User", ParseUser.getCurrentUser());
        userPostsFragment.setArguments(userBundle);

        Bundle badgesBundle = new Bundle();
        badgesBundle.putParcelableArrayList("Categories", categoryWins);
        userBadgesFragment.setArguments(badgesBundle);

        adapter.addFragment(userPostsFragment, "Posts");
        adapter.addFragment(userBadgesFragment, "Badges");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
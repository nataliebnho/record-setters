package com.example.the_commoners_guinness;

import android.app.Application;

import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.models.Post;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.ArrayList;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {

        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Category.class);


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        ArrayList<String> channels = new ArrayList<>();
        channels.add("News");
        installation.put("GCMSenderId", "594000851142");
        installation.put("channels", channels);
        installation.saveInBackground();
    }
}


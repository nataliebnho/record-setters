package com.example.the_commoners_guinness;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.the_commoners_guinness.models.Category;
import com.example.the_commoners_guinness.models.Post;
import com.onesignal.OneSignal;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

import java.util.ArrayList;

public class ParseApplication extends Application {

    public static final String CHANNEL = "channel";

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

        createNotificationChannel();

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "594000851142");
        installation.saveInBackground();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("460278f6-ad17-4315-92c3-dd2fabca3838");

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TestingChannel";
            String description = "Channel for reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}


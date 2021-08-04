package com.example.the_commoners_guinness.ui.create;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.the_commoners_guinness.R;
import com.parse.ParsePushBroadcastReceiver;

public class ReminderBroadcast extends BroadcastReceiver {

    String categoryName;

    public ReminderBroadcast() {
        // empty constructor
    }

    public ReminderBroadcast(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String categoryName = intent.getStringExtra("categoryName");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CreateFragment.CHANNEL)
                .setSmallIcon(R.drawable.silver)
                .setContentTitle("Record Setters")
                .setContentText("Winner for " + categoryName +  " has been released")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }


}
package com.practise.eatit.helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.practise.eatit.R;

public class NotificationHelper extends ContextWrapper {

    public static final String CANCEL_ID = "com.practise.eatit.ABEERFood";
    public static final String CANCEL_CHANNEL_NAME = "Abeer Food";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CANCEL_ID,
                CANCEL_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getEatItChannelNotification(String title, String body, PendingIntent contentIntent,
                                                            Uri soundUri) {
        return new Notification.Builder(getApplicationContext(), CANCEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}

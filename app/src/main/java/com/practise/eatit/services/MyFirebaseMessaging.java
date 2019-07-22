package com.practise.eatit.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.practise.eatit.R;
import com.practise.eatit.model.Token;
import com.practise.eatit.utils.Common;
import com.practise.eatit.view.MainActivity;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String tokenRefreshed= FirebaseInstanceId.getInstance().getToken();
        updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db= FirebaseDatabase.getInstance();
        DatabaseReference tokens= db.getReference("Tokens");
        Token token=new Token(tokenRefreshed,true); //this is server side.. this needs to be true
        tokens.child(Common.currentUser.getUserPhoneNum()).setValue(token);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String Notification_channel_id= "com.practise.eatit.ABEERFood";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(Notification_channel_id,
                    "Notification", NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("foodApp");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


        RemoteMessage.Notification notification=remoteMessage.getNotification();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,Notification_channel_id);
        builder.setSmallIcon(R.drawable.abeerlogo)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(),builder.build());
    }
}


package com.example.virtualchat.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;

public class OreoAndAboveNotification extends ContextWrapper {

    private static final String CHANGE_ID="some_id";
    private static final String CHANGE_NAME="VirtualChat";
    private NotificationManager notificationManager;

    public OreoAndAboveNotification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel=new NotificationChannel(CHANGE_ID,CHANGE_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
       notificationChannel.enableLights(true);
       notificationChannel.enableVibration(true);
       notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
       getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager(){
        if (notificationManager==null){
            notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title,
                                                String body,
                                                PendingIntent pendingIntent,
                                                Uri soundUri,
                                                String icon){
        return new Notification.Builder(getApplicationContext(),CHANGE_ID)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(Integer.parseInt(icon));
    }

}


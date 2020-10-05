package com.example.firstapplication.service.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firstapplication.R;
import com.example.firstapplication.model.Assistant;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import static com.example.firstapplication.shared.Constant.NOTIFICATION_CHANEL_ID;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");

        Assistant auth = Assistant.getInstance();
        if (auth == null || sented.equals(auth.getInitial()))
            sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String message = remoteMessage.getData().get("message");

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANEL_ID);

        notificationBuilder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.com_facebook_button_icon)
                .setContentTitle("Tester")
                .setContentText(message)
                .setSound(sound)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());

    }
}

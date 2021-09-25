package com.rwn.rwnstudy.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.activities.ChatActivity;
import com.rwn.rwnstudy.activities.FriendRequestActivity;
import com.rwn.rwnstudy.activities.MainActivity;
import com.rwn.rwnstudy.activities.SplashActivity;

import java.io.UnsupportedEncodingException;
import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    String notificationType = "new";
    private static final String ADMIN_CHANNEL_ID = "RWN Study";
    static public NotificationManager notificationManager;
    public static Uri alaramSound;
    private final long[] pattern = {100, 300, 300, 300};


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        notificationType = remoteMessage.getData().get("type");

        Log.d("lfelflef", "onMessageReceived: " + notificationType);
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        switch (notificationType) {
            case "message":
                messageNotificationSend(remoteMessage);
                Log.d("cdvvds", "onMessageReceived:  message notificationsend");
                break;
            case "friendReq":
                friendReq(remoteMessage);

                Log.d("cdvvds", "onMessageReceived: frnd req");
                break;
            case "notification":
                notificationNotification(remoteMessage);

                Log.d("cdvvds", "onMessageReceived: notificationsend");
                break;
            default:
                notificationNotification(remoteMessage);

                Log.d("cdvvds", "onMessageReceived: notificationsend");

        }


        //Setting up Notification channels for android O and above

        //  int notificationId = new Random().nextInt(60000);


//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);


//        TaskStackBuilder taskStackBuilder =  TaskStackBuilder.create(this);
//        taskStackBuilder.addParentStack(FriendRequestActivity.class);
//        taskStackBuilder.addNextIntent(notificationIntent);


//        PendingIntent pendingIntent1 = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);


        //    notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

    }

    private void friendReq(RemoteMessage remoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels("Friend Request Notification", "we delivered Friend Request notification on your notification bar");
        }
        String type = remoteMessage.getData().get("type");
        Intent notificationIntent;
        if (MainActivity.isAppRunning) {
            notificationIntent = new Intent(this, FriendRequestActivity.class);
        } else {
            notificationIntent = new Intent(this, SplashActivity.class);
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra("filter", "notification");
        notificationIntent.putExtra("type", type);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);

       alaramSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)//a resource for your custom small icon
                .setContentTitle(remoteMessage.getNotification().getTitle()) //the "title" value you sent in your notification
                .setContentText(remoteMessage.getNotification().getBody()) //ditto
                .setBadgeIconType(R.drawable.ic_launcher)//dismisses the notification on click
                .setSound(alaramSound)
                .setVibrate(pattern)
                .setChannelId(ADMIN_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY);
        }
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt(60000);
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }



    private void notificationNotification(RemoteMessage remoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels("Offer Notification", "we delivered Offers notification on your notification bar");
        }
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String product_id = remoteMessage.getData().get("product_id");
        String store_name = remoteMessage.getData().get("store_name");
        String type = remoteMessage.getData().get("type");

        Intent notificationIntent;
        if (MainActivity.isAppRunning) {
            notificationIntent = new Intent(this, MainActivity.class);
        } else {
            notificationIntent = new Intent(this, SplashActivity.class);
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra("filter", "notification");
        notificationIntent.putExtra("product_id", product_id);
        notificationIntent.putExtra("store_name", store_name);
        notificationIntent.putExtra("type", type);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);


        alaramSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)//a resource for your custom small icon
                .setContentTitle(remoteMessage.getNotification().getTitle()) //the "title" value you sent in your notification
                .setContentText(remoteMessage.getNotification().getBody()) //ditto
                .setAutoCancel(true)
                .setBadgeIconType(R.drawable.ic_launcher)//dismisses the notification on click
                .setSound(alaramSound)
                .setChannelId(ADMIN_CHANNEL_ID)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(pattern)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY);
        }


        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        int notificationId = new Random().nextInt(60000);

        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());


    }

    private void messageNotificationSend(RemoteMessage remoteMessage) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels("Chatting Notification", "we delivered chat notification on your notification bar");
        }


        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//


        Intent notificationIntent;


        String id = remoteMessage.getData().get("id");
        String name = remoteMessage.getData().get("name");
        String type = remoteMessage.getData().get("type");

        if (MainActivity.isAppRunning) {
            notificationIntent = new Intent(this, ChatActivity.class);
            notificationIntent.putExtra("visit_user_id", id);
            notificationIntent.putExtra("username", name);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        } else {
            notificationIntent = new Intent(this, SplashActivity.class);

            notificationIntent.putExtra("id", id);
            notificationIntent.putExtra("name", name);
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        notificationIntent.putExtra("type", type);

        Log.d("cdvvds", "messageNotificationSend: "+ id+"\n"+ name);

//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);


        alaramSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)//a resource for your custom small icon
                .setContentTitle(remoteMessage.getNotification().getTitle()) //the "title" value you sent in your notification
                .setContentText(remoteMessage.getNotification().getBody()) //ditto

                .setChannelId(ADMIN_CHANNEL_ID)
                .setBadgeIconType(R.drawable.ic_launcher)//dismisses the notification on click
                .setSound(alaramSound)
                .setVibrate(pattern)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY);
        }

        String notificationId = "";
        String text = remoteMessage.getData().get("id");        // translating text String to 7 bit ASCII encoding
        try {
            byte[] bytes = text.getBytes("US-ASCII");
            for (byte a : bytes) {
                notificationId = notificationId + a;
            }
            Log.d("lfelflef", "messageNotificationSend: " + notificationId);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String n = notificationId.substring(50);
        long a = Long.parseLong(n);
        int b = (int) a;

        notificationManager.notify(b /* ID of notification */, notificationBuilder.build());

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(String headline, String desc) {


        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, headline, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(desc);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}

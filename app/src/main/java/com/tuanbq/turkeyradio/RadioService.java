package com.tuanbq.turkeyradio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by User on 5/27/2018.
 */

public class RadioService extends Service {
    Bundle notiBundle;
    NotificationManagerCompat notificationManagerCompat;
    final NotificationCompat.Builder playingChannelNoti = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManagerCompat = NotificationManagerCompat.from(this);
        if (intent != null) {
            notiBundle = intent.getBundleExtra("noti_bundle");
            buildNotification();
            if (intent.getAction().equals(Constants.INTENT_ACTION_SHOW_NOTI_FROM_APP)) {
                notificationManagerCompat.notify(Constants.NOTIFICATION_ID, playingChannelNoti.build());
            }
        }
        return START_STICKY;
    }

    private void buildNotification() {
        createNotificationChannel();


        if (notiBundle.getString("channel_pic") != null && !notiBundle.getString("channel_pic").isEmpty()) {
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    playingChannelNoti.setLargeIcon(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Bitmap defaultImg = BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.app_icon);
                    playingChannelNoti.setLargeIcon(defaultImg);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.with(getBaseContext()).load(notiBundle.getString("channel_pic")).into(target);
        }
        //start count down

        //back to application - do later

        playingChannelNoti.setSmallIcon(R.drawable.radio_icon);
        playingChannelNoti.setContentTitle(notiBundle.getString("channel_name"));
        playingChannelNoti.setContentText("Category: " + notiBundle.getString("channel_cat"));
        playingChannelNoti.setDeleteIntent(createOnDismissedIntent(getBaseContext(),Constants.NOTIFICATION_ID));
        playingChannelNoti.setAutoCancel(true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Constants.NOTIFICATION_NAME;
            String description = Constants.NOTIFICATION_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Constants.INTENT_ACTION_STOP_RADIO_FROM_NOTI);
        intent.putExtra("notification_id", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

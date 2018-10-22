package com.tuanbq.algeriaradio;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by User on 5/27/2018.
 */

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            int notificationId = intent.getExtras().getInt("notification_id");
            if (intent.getAction().equals(Constants.INTENT_ACTION_STOP_RADIO_FROM_NOTI)) {
                if (MainActivity.mPlayerControl == null) {
                    return;
                }
                if (TimerDialog.timer != null) {
                    TimerDialog.timer.cancel();
                    MainActivity.timerCounter.setVisibility(View.GONE);
                }
                if (MainActivity.mPlayerControl.isPlaying()) {
                    MainActivity.mPlayerControl.pause();
                }
                MainActivity.playPauseIcon.setBackgroundResource(R.drawable.play_icon);
                MainActivity.playPauseIcon.setVisibility(View.VISIBLE);
                MainActivity.channelLoadingIcon.setVisibility(View.GONE);
                NotificationManager notificationManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(Constants.NOTIFICATION_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}

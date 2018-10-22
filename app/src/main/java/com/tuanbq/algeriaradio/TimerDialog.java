package com.tuanbq.algeriaradio;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by User on 5/27/2018.
 */

public class TimerDialog {
    Activity mActivity;
    RadioGroup timerGroupOption;
    RadioButton mins30,mins60,mins90;
    EditText timerCustomValue;
    Button setTimerBtn;
    public static CountDownTimer timer;

    public TimerDialog(Activity activity) {
        this.mActivity = activity;
    }

    public void showTimerDialog() {
        try {
            final Dialog timerDialog = new Dialog(mActivity);
            timerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            timerDialog.setCancelable(true);
            timerDialog.setContentView(R.layout.set_timer_dialog);

            timerGroupOption = timerDialog.findViewById(R.id.timer_group);
            mins30 = timerDialog.findViewById(R.id.timer_30_mins);
            mins60 = timerDialog.findViewById(R.id.timer_60_mins);
            mins90 = timerDialog.findViewById(R.id.timer_90_mins);
            timerCustomValue = timerDialog.findViewById(R.id.timer_cutom_text);
            setTimerBtn = timerDialog.findViewById(R.id.set_timer_btn);

            setTimerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (timer != null) {
                            timer.cancel();
                        }
                        if (!MainActivity.mPlayerControl.isPlaying()) {
                            Toast.makeText(mActivity,"No channel is being played!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        MainActivity.timerCounter.setVisibility(View.VISIBLE);
                        long timerValue = 0;
                        String timerValueStr = timerCustomValue.getText().toString();

                        if (timerValueStr != null && !timerValueStr.equals("")) {
                            timerValue = Integer.parseInt(timerValueStr)*60*1000;
                        } else {
                            int radioButtonID = timerGroupOption.getCheckedRadioButtonId();
                            View radioButton = timerGroupOption.findViewById(radioButtonID);
                            int idx = timerGroupOption.indexOfChild(radioButton);

                            if (idx == 0) {
                                timerValue = 30*60*1000;
                            } else if (idx == 1) {
                                timerValue = 60*60*1000;
                            } else {
                                timerValue = 90*60*1000;
                            }
                        }

                        timer = new CountDownTimer(timerValue, 1000) {
                            @Override
                            public void onTick(long l) {
                                String minsStr = (l/1000)/60 + "";
                                String secsStr = (l/1000)%60 == 0 ? "00" : (l/1000)%60 + "";
                                MainActivity.timerCounter.setText(minsStr+ ":" + secsStr);
                            }

                            @Override
                            public void onFinish() {
                                if (MainActivity.mPlayerControl != null) {
                                    MainActivity.mPlayerControl.pause();
                                    NotificationManager notificationManager = (NotificationManager) mActivity
                                            .getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.cancel(Constants.NOTIFICATION_ID);
                                    MainActivity.timerCounter.setVisibility(View.GONE);
                                    MainActivity.playPauseIcon.setBackgroundResource(R.drawable.play_icon);
                                }
                            }
                        };
                        timer.start();
                        Toast.makeText(mActivity, "Timer is set: " + timerValue/1000/60 + " minute(s)!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
            });

            timerDialog.setCanceledOnTouchOutside(true);

            timerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}

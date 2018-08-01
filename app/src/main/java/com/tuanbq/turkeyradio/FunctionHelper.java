package com.tuanbq.turkeyradio;

import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by tuanbq on 2/27/2018.
 */

public class FunctionHelper {

    public static boolean CheckConectNetwork(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        if (nwInfo != null && nwInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public static String ReadStringFromServer(String str) {
        StringBuilder content = new StringBuilder();
        try {
            // create a url object
            URL url = new URL(str);
            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();
            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static ArrayList<ChannelObject> GetJSONData(StringBuilder sb) {
        ArrayList<ChannelObject> listChannels = new ArrayList<>();

        try {
            if (sb.charAt(0) == '\uFEFF') {
                sb.deleteCharAt(0);
            }
            JSONArray mJson = new JSONArray(sb.toString());
            for (int i = 0; i < mJson.length(); i++) {
                JSONObject son = mJson.getJSONObject(i);
                ChannelObject sonRadio = new ChannelObject();
                sonRadio.setId(i);
                sonRadio.setName(!son.has("name") ? "" : son.getString("name"));
                sonRadio.setLink(!son.has("link") ? "" : son.getString("link"));
                sonRadio.setPic(!son.has("pic")? "" : son.getString("pic"));
                sonRadio.setCat(!son.has("category")? "" : son.getString("category"));

                listChannels.add(sonRadio);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listChannels;
    }

    public static ArrayList<ChannelObject> ConvertChannelStrToList(String channelStr) {
        ArrayList<ChannelObject> listCO = new ArrayList<>();
        if (channelStr.isEmpty()) {
            return listCO;
        } else {
            String[] listFavChannelArr = channelStr.split("100ENDCHANNEL001");
            for (int i=0;i<listFavChannelArr.length;i++) {
                ChannelObject co = new ChannelObject();
                String[] favChannelArr = listFavChannelArr[i].split("100ENDCHAR001");
                co.setName(favChannelArr[0]);
                co.setLink(favChannelArr[1]);
                co.setPic(favChannelArr[2]);
                co.setCat(favChannelArr[3]);
                listCO.add(co);
            }
            return listCO;
        }
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static boolean checkElementIsExisted(ArrayList<String> strList, String sample) {
        if (strList.size() == 0 || strList == null) {
            return false;
        }
        for (String cat : strList) {
            if (cat.equals(sample)) {
                return true;
            }
        }
        return false;
    }

    public static void volumeControl(Context context, SeekBar volumeSeekbar, AudioManager audioManager) {
        try {
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            final AudioManager finalAudioManager = audioManager;
            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    finalAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int convertToPixels(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}

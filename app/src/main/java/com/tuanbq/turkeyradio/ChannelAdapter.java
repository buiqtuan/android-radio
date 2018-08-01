package com.tuanbq.turkeyradio;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by User on 5/22/2018.
 */

public class ChannelAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ChannelObject> channels = new ArrayList<>();
    private PlayerControl mPlayerControl;
    private ExoPlayer mExoPlayer;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Vibrator v;

    public ChannelAdapter(Context mContext, ArrayList<ChannelObject> channels, PlayerControl playerControl, ExoPlayer exoPlayer) {
        this.mContext = mContext;
        this.channels = channels;
        this.mPlayerControl = playerControl;
        this.mExoPlayer = exoPlayer;
        prefs = mContext.getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE);
        editor = prefs.edit();

        v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ChannelObject co = channels.get(i);

        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.channel_layout, null);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.mPlayerControl != null && MainActivity.mPlayerControl.isPlaying()) {
                    NotificationManager notificationManager = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(Constants.NOTIFICATION_ID);
                }
                playingRadioChannel(mContext,co,mExoPlayer);
                int adCounter = prefs.getInt("ad_counter", 0);
                adCounter++;
                if (adCounter == 1) {
                    MainActivity.showFullAds();
                } else if (adCounter%3 == 0) {
                    MainActivity.showFullAds();
                }
                if (adCounter >= (Integer.MAX_VALUE - 20)) {
                    adCounter = 0;
                }
                editor.putInt("ad_counter",adCounter);
                editor.apply();
                Toast.makeText(mContext,"Playing channel " + co.getName() + "!", Toast.LENGTH_SHORT).show();
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int adCounter = prefs.getInt("ad_counter", 0);
                if (adCounter%3 == 0) {
                    MainActivity.showFullAds();
                }
                adCounter++;
                if (adCounter >= (Integer.MAX_VALUE - 20)) {
                    adCounter = 0;
                }
                editor.putInt("ad_counter",adCounter);
                editor.apply();
                String listFavChannelsStr = prefs.getString(Constants.PREF_LIST_FAV_CHANNEL,"");
                if (MainActivity.channelGridState == 0 || MainActivity.channelGridState == 2) {
                    if (listFavChannelsStr.toLowerCase().contains(co.getLink().toLowerCase())) {
                        Toast.makeText(mContext,"Channel " + co.getName() + " has already been added to Favorites!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    listFavChannelsStr = listFavChannelsStr + co.getName() + "100ENDCHAR001"
                            + co.getLink() + "100ENDCHAR001"
                            + co.getPic() + "100ENDCHAR001"
                            + co.getCat() + "100ENDCHANNEL001";
                    editor.putString(Constants.PREF_LIST_FAV_CHANNEL, listFavChannelsStr);
                    editor.apply();
                    vibrateDevice(250);
                    if (MainActivity.playingChannelName.getText().equals(co.getName())) {
                        MainActivity.likeBtn.setBackgroundResource(R.drawable.fav_filled_icon);
                    }
                    Toast.makeText(mContext,"Channel " + co.getName() + " has been added to Favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    String removedChannel = co.getName() + "100ENDCHAR001" + co.getLink() + "100ENDCHAR001" + co.getPic() + "100ENDCHAR001" + co.getCat() + "100ENDCHANNEL001";
                    String favChannelAfterRemove = listFavChannelsStr.replace(removedChannel,"");
                    editor.putString(Constants.PREF_LIST_FAV_CHANNEL, favChannelAfterRemove);
                    editor.apply();
                    ArrayList<ChannelObject> listChannels = FunctionHelper.ConvertChannelStrToList(favChannelAfterRemove);
                    ChannelAdapter channelAdapter = new ChannelAdapter(mContext, listChannels, mPlayerControl, mExoPlayer);
                    MainActivity.channelGrid.setAdapter(channelAdapter);
                    vibrateDevice(250);
                    MainActivity.likeBtn.setBackgroundResource(R.drawable.fav_empty_icon);
                    Toast.makeText(mContext,"Channel " + co.getName() + " has been removed to Favorites!", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

        final TextView channelName = view.findViewById(R.id.gridview_item_text);
        final ImageView channelImg = view.findViewById(R.id.gridview_item_img);

        channelName.setText(co.getName());
        if (!co.getPic().isEmpty()) {
            Picasso.with(mContext).load(co.getPic()).into(channelImg, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    channelImg.setImageResource(R.drawable.app_icon);
                }
            });
        } else {
            channelImg.setImageResource(R.drawable.app_icon);
        }
        return view;
    }

    private void vibrateDevice(long mil) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(mil,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            v.vibrate(mil);
        }
    }

    private void playingRadioChannel(final Context context, final ChannelObject co, ExoPlayer exoPlayer) {
        try {
            Uri radioUri = Uri.parse(co.getLink());
            Allocator allocator = new DefaultAllocator(Constants.BUFFER_SEGMENT_SIZE);
            String userAgent = Util.getUserAgent(context, "Radio/1");
            DefaultUriDataSource dataSource = new DefaultUriDataSource(context, null, userAgent);
            ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                    radioUri, dataSource, allocator, Constants.BUFFER_SEGMENT_SIZE * Constants.BUFFER_SEGMENT_COUNT);
            MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
            exoPlayer.prepare(audioRenderer);
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.addListener(new ExoPlayer.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    //change ui
                    MainActivity.changeUIByRadioStates(playbackState,mContext,co);
                    //fire noti
                    if (playbackState == ExoPlayer.STATE_READY) {
                        if (!MainActivity.mPlayerControl.isPlaying()) {
                            return;
                        }
                        Intent fireNoti = new Intent(context, RadioService.class);
                        Bundle notiBundle = new Bundle();
                        notiBundle.putString("channel_pic", co.getPic());
                        notiBundle.putString("channel_name", co.getName());
                        notiBundle.putString("channel_cat",co.getCat());
                        fireNoti.putExtra("noti_bundle", notiBundle);
                        fireNoti.setAction(Constants.INTENT_ACTION_SHOW_NOTI_FROM_APP);
                        context.startService(fireNoti);

                    }
                }

                @Override
                public void onPlayWhenReadyCommitted() {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    MainActivity.playingChannelName.setText("Sorry, this channel is offline for now");
                    MainActivity.changeUIByRadioStates(-1,mContext,co);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

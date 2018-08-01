package com.tuanbq.turkeyradio;

/**
 * Created by User on 5/22/2018.
 */

public class Constants {
    public static String APP_PREF = "NEW_RADIO_PREFS";

    public static String PREF_LIST_ALL_CHANNEL = "PREF_LIST_ALL_CHANNEL";
    public static String PREF_LIST_FAV_CHANNEL = "PREF_LIST_FAV_CHANNEL";
    public static String PREF_LIST_ALL_CAT = "PREF_LIST_ALL_CAT";

    //country radio links
    public static String CHANNELS_LINK = "https://storage.googleapis.com/radioappdata/.turkey.txt";

    public static int OFFSET_X = -120;
    public static int OFFSET_Y = 150;

    public static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    public static final int BUFFER_SEGMENT_COUNT = 256;

    public static String NOTIFICATION_NAME = "RADIO_NOTIFICATION";
    public static String NOTIFICATION_DESCRIPTION = "NOTIFICATION_DESCRIPTION";
    public static String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID_RADIO";
    public static int NOTIFICATION_ID = 29992;

    public static String INTENT_ACTION_STOP_RADIO_FROM_NOTI = "INTENT_ACTION_STOP_RADIO_FROM_NOTI";
    public static String INTENT_ACTION_STOP_RADIO_FROM_TIMER = "INTENT_ACTION_STOP_RADIO_FROM_TIMER";
    public static String INTENT_ACTION_START_COUNTDOWN = "INTENT_ACTION_START_COUNTDOWN";
    public static String INTENT_ACTION_STOP_COUNTDOWN = "INTENT_ACTION_STOP_COUNTDOWN";
    public static String INTENT_ACTION_SHOW_NOTI_FROM_APP = "INTENT_ACTION_SHOW_NOTI_FROM_APP";
    public static String INTENT_ACTION_REMOVE_NOTI_FROM_APP = "INTENT_ACTION_REMOVE_NOTI_FROM_APP";
}

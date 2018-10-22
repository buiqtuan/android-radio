package com.tuanbq.algeriaradio;

/**
 * Created by User on 5/22/2018.
 */

public class Constants {
    public static String APP_PREF = "NEW_RADIO_PREFS";

    public static String PREF_LIST_ALL_CHANNEL = "PREF_LIST_ALL_CHANNEL";
    public static String PREF_LIST_FAV_CHANNEL = "PREF_LIST_FAV_CHANNEL";
    public static String PREF_LIST_ALL_CAT = "PREF_LIST_ALL_CAT";

    //country radio links
    public static String CHANNELS_LINK = "https://storage.googleapis.com/radioappdata/.algeria.txt";

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

    public static String[] COUNTRIES_LIST = {"AFGHANISTAN", "ALBANIA", "ALGERIA", "ARGENTINA", "ARMENIA", "AUSTRALIA", "AUSTRIA", "BANGLADESH", "BELGIUM", "BOLIVIA", "BOSNIA", "BRAZIL", "BULGARIA", "CAMBODIA"
                                            , "CAMEROON", "CANADA", "CHILE", "CHINA", "COLOMBIA", "COSTA RICA", "CROATIA", "CYPRUS", "CZECH", "DENMARK", "DOMINICAN", "ECUADOR", "EGYPT", "ETHIOPIA", "FIJI", "FINLAND"
                                            , "FRANCE", "GAMBIA", "GERMANY", "GHANA", "GREECE", "HAITI", "HAUSA", "HONDURAS", "HUNGARY", "INDIA", "INDONESIA", "IRAN", "IRAQ", "IRELAND", "ITALY", "JAMAICA", "JAPAN"
                                            , "JORDAN", "KENYA", "KHMER", "KUWAIT", "KYRGYZSTAN", "LEBANON", "LIBERIA", "LITHUANIA", "MACEDONIA", "MADAGASCAR", "MALAWI", "MALAYSIA", "MAURITANIA", "MAURITIUS", "MEXICO"
                                            , "MOLDOVA", "MONGOLIA", "MOROCCO", "MYANMAR", "NETHERLANDS", "NEW ZEALAND", "NIGERIA", "NORWAY", "OMAN", "PAKISTAN", "PANAMA", "PARAGUAY", "PERU", "POLAND", "PORTUGAL"
                                            , "PUERTO RICO", "QATAR", "ROMANIA", "RUSSIA", "SENEGAL", "SERBIA", "SINGAPORE", "SLOVAKIA", "SOUTH KOREA", "SPAIN", "SRI LANKA", "SURINAME", "SWEDEN", "SWITZERLAND", "TAIWAN"
                                            , "TAJIKISTAN", "TANZANIA", "THAILAND", "TOGO", "TUNISIA", "TURKEY", "UGANDA", "UK", "UKRAINE", "URUGUAY", "USA", "UZBEKISTAN", "VENEZUELA", "ZAMBIA"};

    public static String UPDATED_NOTE = "- Add Radio Channels from all over the world. From US, UK, China, France, Russia, ... You just need to go to setting and switch to the country you want to listen to. Enjoy it!\n" +
            "- Add New Radio Channels. \n" +
            "- Press and Hold a Channel to add to Favorites list. \n" +
            "- Add List-View mode. \n" +
            "- Add Night and Day theme. \n" +
            "- Improve App Performance. \n" +
            "\n" +
            "If you are satisfied with our application, please give us 5 star to encourage and support us to make this application better. \n" +
            "\n" +
            "Thank you for using our application.";

    public static String BASE_URL = "https://storage.googleapis.com/radioappdata/";
}

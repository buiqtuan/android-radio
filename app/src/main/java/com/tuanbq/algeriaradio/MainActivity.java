package com.tuanbq.algeriaradio;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageButton rateAppBtn, settingBtn;
    ImageButton setTimerBtn, reloadAppBtn, backToHomeBtn, changeCountry;
    TextView allChannel, myFavorites, catChannel, networkWarning;
    public static TextView playingChannelName;
    EditText searchChannel;
    public static GridView channelGrid;
    public static ListView catListView, channelList;
    ProgressBar appLoadingIcon;
    static ImageButton likeBtn, playPauseIcon, changeViewChannelStyle, changeAppTheme;
    static ImageView playingChannelImg;
    static ProgressBar channelLoadingIcon;
    SeekBar volumeController;


    public static AdView adBanner;
    public static AdRequest adRequest;
    public static InterstitialAd mInterstitialAd;
    public static Context mContext;

    static SharedPreferences prefs;
    static SharedPreferences.Editor editor;

    ColorStateList oldTextColors;

    private AudioManager mAudioManager = null;

    public static TextView timerCounter;
    public static ExoPlayer mExoPlayer;
    public static PlayerControl mPlayerControl;

    private PopupWindow popUpSettingPanel;
    private PopupWindow popUpCatPanel;

    public static int displayedChannelState = 0; //all

    private static ArrayList<ChannelObject> listChannels = new ArrayList<>();
    private ArrayList<ChannelObject> listChannelsByCat = new ArrayList<>();

    //app UI state
    public static boolean appTheme = true; // true is night - false is day
    public static boolean channelViewStyle = true; // true is grid - false is list

    private boolean isCountryChanged = false;
    private String[] countriesList;

    //Ad counter
    public static int adCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            mContext = getApplicationContext();
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);

            initAppElements();

            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            FunctionHelper.volumeControl(getBaseContext(), volumeController, mAudioManager);

            if (!FunctionHelper.CheckConectNetwork(getBaseContext())) {
                networkWarning.setVisibility(View.VISIBLE);
                networkWarning.setText("Please check your device's network connection and restart the application!");
                appLoadingIcon.setVisibility(View.GONE);
                return;
            }

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            initDefaultAppPrefs();

            //get app prefs
            appTheme = prefs.getBoolean("app_theme", true);
            channelViewStyle = prefs.getBoolean("channels_view_style", true);


            //save list channel to sharedprefs
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new LoadingDataAsync().execute();
                }
            });

            //set onclick for all, fav and cat
            allChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        displayedChannelState = 0;
                        changeTextColorWhenSwitchingTabs(allChannel, myFavorites, catChannel);
                        catChannel.setText("Type: All");
                        listChannels = FunctionHelper.GetJSONData(new StringBuilder(prefs.getString(Constants.PREF_LIST_ALL_CHANNEL, "")));
                        ChannelAdapter channelAdapter = new ChannelAdapter(getBaseContext(), listChannels, mPlayerControl, mExoPlayer, channelViewStyle, appTheme);
                        if (channelViewStyle) {
                            channelGrid.setAdapter(channelAdapter);
                        } else {
                            channelList.setAdapter(channelAdapter);
                        }
                    } catch (Exception e) {
                        Toast.makeText(mContext, "An Error has occurred, please refresh the App!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

            myFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        displayedChannelState = 1;
                        changeTextColorWhenSwitchingTabs(myFavorites, allChannel, catChannel);
                        String listFavChannelStr = prefs.getString(Constants.PREF_LIST_FAV_CHANNEL, "");
                        listChannels = FunctionHelper.ConvertChannelStrToList(listFavChannelStr);
                        ChannelAdapter channelAdapter = new ChannelAdapter(getBaseContext(), listChannels, mPlayerControl, mExoPlayer, channelViewStyle, appTheme);
                        if (channelViewStyle) {
                            channelGrid.setAdapter(channelAdapter);
                        } else {
                            channelList.setAdapter(channelAdapter);
                        }
                    } catch (Exception e) {
                        return;
                    }
                }
            });

            catChannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (popUpCatPanel.isShowing()) {
                            popUpCatPanel.dismiss();
                        }
                        displayedChannelState = 2;
                        changeTextColorWhenSwitchingTabs(catChannel, allChannel, myFavorites);
                        showCatPanel(MainActivity.this, getPointOfView(catChannel));
                    } catch (Exception e) {
                        return;
                    }
                }
            });

            //search channels
            searchChannel.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (displayedChannelState == 2) {
                            listChannels = listChannelsByCat;
                        }
                        ArrayList<ChannelObject> searchingList = new ArrayList<>();
                        if (listChannels == null || listChannels.size() == 0) {
                            return;
                        }
                        for (ChannelObject co : listChannels) {
                            if (co.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                searchingList.add(co);
                            }
                        }
                        ChannelAdapter channelAdapter = new ChannelAdapter(getBaseContext(), searchingList, mPlayerControl, mExoPlayer, channelViewStyle, appTheme);
                        if (channelViewStyle) {
                            channelGrid.setAdapter(channelAdapter);
                        } else {
                            channelList.setAdapter(channelAdapter);
                        }
                    } catch (Exception e) {
                        return;
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            //open setting panel
            settingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (popUpSettingPanel.isShowing()) {
                            popUpSettingPanel.dismiss();
                        }
                        showSettingPanel(MainActivity.this, getPointOfView(settingBtn));
                    } catch (Exception e) {
                        return;
                    }
                }
            });

            //turn off screen
            rateAppBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
                    } catch (Exception e) {
                        return;
                    }
                }
            });

            changeChannelViewStyleBtImg();
            changeAppThemeBtnImg();
            if (prefs.getBoolean("new_update_dialog", true)) {
                createDialog("Update Note", Constants.UPDATED_NOTE, MainActivity.this);
                editor.putBoolean("new_update_dialog", false);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void createChangeCountryDialogBox() {
        final AlertDialog.Builder builder;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
            }

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.countries_list, null);
            EditText searchCountry = view.findViewById(R.id.search_country);
            final ListView countriesListView = view.findViewById(R.id.country_list_view);
            countriesListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            countriesListView.setSelector(R.color.selectedItem);

            changeCountryListArray(Constants.COUNTRIES_LIST);
            CountryAdapter countryAdapter = new CountryAdapter(mContext, getCountriesArray());
            countriesListView.setAdapter(countryAdapter);


            searchCountry.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        ArrayList<String> listSearchedCountries = new ArrayList<>();
                        for (int k = 0;k<Constants.COUNTRIES_LIST.length;k++) {
                            if (Constants.COUNTRIES_LIST[k].contains(charSequence.toString().toUpperCase())) {
                                listSearchedCountries.add(Constants.COUNTRIES_LIST[k]);
                            }
                        }
                        String[] arraySearchedCountries = new String[listSearchedCountries.size()];
                        for (int k = 0;k<listSearchedCountries.size();k++) {
                            arraySearchedCountries[k] = listSearchedCountries.get(k);
                        }
                        changeCountryListArray(arraySearchedCountries);
                        CountryAdapter countryAdapter = new CountryAdapter(mContext, arraySearchedCountries);
                        countriesListView.setAdapter(countryAdapter);
                    } catch (Exception e) {
                        return;
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            builder.setCustomTitle(view);

            countriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    try {
                        editor.putString("channels_link",Constants.BASE_URL + "." + countriesList[i].toLowerCase().replace(" ", "") + ".txt");
                        editor.putString("changed_country", countriesList[i]);
                        editor.apply();
                        changeCountryState(true);
                    } catch (Exception e) {
                        return;
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        if (getCountryState()) {
                            if (popUpSettingPanel != null && popUpSettingPanel.isShowing()) {
                                popUpSettingPanel.dismiss();
                            }
                            reloadingChannelsList();
                            Toast.makeText(getBaseContext(),"Loading Radio Channels from " + prefs.getString("changed_country", ""), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        return;
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void changeCountryListArray(String[] newArray) {
        this.countriesList = newArray;
    }

    private String[] getCountriesArray() {
        return this.countriesList;
    }

    private void changeCountryState(boolean state) {
        this.isCountryChanged = state;
    }

    private boolean getCountryState() {
        return this.isCountryChanged;
    }

    public static void createDialog(String title, String msg, Context context) {
        AlertDialog.Builder builder;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setTitle(title)
                    .setMessage(msg)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setIcon(R.drawable.app_icon);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void initDefaultAppPrefs() {
        try {
            prefs = getBaseContext().getSharedPreferences(Constants.APP_PREF, Context.MODE_PRIVATE);
            editor = prefs.edit();
            //get App UI state
            if (!prefs.contains("app_theme")) {
                editor.putBoolean("app_theme", true);
                editor.apply();
            }
            if (!prefs.contains("channels_view_style")) {
                editor.putBoolean("channels_view_style", true);
                editor.apply();
            }
            //set ad counter
            if (!prefs.contains("ad_counter")) {
                editor.putInt("ad_counter", adCounter);
                editor.apply();
            }
            //set dialog for new update
            if (!prefs.contains("new_update_dialog")) {
                editor.putBoolean("new_update_dialog", true);
                editor.apply();
            }
            //set up channels link
            if (!prefs.contains("channels_link")) {
                editor.putString("channels_link", Constants.CHANNELS_LINK);
                editor.apply();
            }
            //set changed country name
            if (!prefs.contains("changed_country")) {
                editor.putString("changed_country", "");
                editor.apply();
            }
            //show side note
            if (!prefs.contains("show_side_note")) {
                editor.putBoolean("show_side_note", true);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


    private Point getPointOfView(View view) {
        try {
            int[] location = new int[2];
            view.getLocationInWindow(location);
            return new Point(location[0], location[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void backToHomeScreen() {
        try {
            Intent backToHomeScreen = new Intent(Intent.ACTION_MAIN);
            backToHomeScreen.addCategory(Intent.CATEGORY_HOME);
            backToHomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backToHomeScreen);
        } catch (Exception e) {
            return;
        }
    }

    void changeTextColorWhenSwitchingTabs(TextView picked, TextView tab1, TextView tab2) {
        try {
            picked.setTextColor(getResources().getColor(R.color.pickedText));
            tab1.setTextColor(oldTextColors);
            tab2.setTextColor(oldTextColors);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    void showCatPanel(final Activity context, Point p) {
        try {
            LinearLayout viewGroup = context.findViewById(R.id.linearlayout_channel_cat);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.list_channel_cat, viewGroup);

            catListView = layout.findViewById(R.id.listview_channel_cat);

            final String[] catArrStr = prefs.getString(Constants.PREF_LIST_ALL_CAT,"").split("##");
            CatAdapter catAdapter = new CatAdapter(getBaseContext(), catArrStr);
            catListView.setAdapter(catAdapter);

            catListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        listChannelsByCat.clear();
                        catChannel.setText("Type: " + (catArrStr[position+1].length() > 8 ? catArrStr[position+1].subSequence(0,8) + "..." : catArrStr[position+1]));
                        listChannels = FunctionHelper.GetJSONData(new StringBuilder(prefs.getString(Constants.PREF_LIST_ALL_CHANNEL,"")));
                        for (ChannelObject co : listChannels) {
                            if (co.getCat().contains(catArrStr[position+1])) {
                                listChannelsByCat.add(co);
                            }
                        }
                        ChannelAdapter channelAdapter = new ChannelAdapter(getBaseContext(), listChannelsByCat, mPlayerControl,mExoPlayer, channelViewStyle, appTheme);
                        if (channelViewStyle) {
                            channelGrid.setAdapter(channelAdapter);
                        } else {
                            channelList.setAdapter(channelAdapter);
                        }
                    } catch (Exception e) {
                        Toast.makeText(mContext, "An Error has occurred, please refresh the App!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });

            popUpCatPanel.setContentView(layout);
            popUpCatPanel.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            popUpCatPanel.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popUpCatPanel.setFocusable(true);

            popUpCatPanel.setBackgroundDrawable(new BitmapDrawable());
            popUpCatPanel.setAnimationStyle(R.style.Animation);

            popUpCatPanel.showAtLocation(layout, Gravity.NO_GRAVITY, p.x - FunctionHelper.convertToPixels(getBaseContext(),15), p.y + (catChannel.getHeight()/3)*4);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    void showSettingPanel(final Activity context, Point p) {
        try {
            // Inflate the popup_layout.xml
            LinearLayout viewGroup = context.findViewById(R.id.setting_panel_view);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.setting_panel, viewGroup);

            setTimerBtn = layout.findViewById(R.id.set_timer_btn);
            reloadAppBtn = layout.findViewById(R.id.refresh_app_btn);
            backToHomeBtn = layout.findViewById(R.id.back_to_home);
            changeCountry = layout.findViewById(R.id.change_country_btn);

            setTimerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimerDialog timerDialog = new TimerDialog(MainActivity.this);
                    timerDialog.showTimerDialog();
                }
            });

            reloadAppBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reloadingChannelsList();
                    Toast.makeText(getBaseContext(),"Refreshing channels", Toast.LENGTH_SHORT).show();
                }
            });

            backToHomeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    backToHomeScreen();
                }
            });

            changeCountry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createChangeCountryDialogBox();
                }
            });

            // Creating the PopupWindow
            popUpSettingPanel.setContentView(layout);
            popUpSettingPanel.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            popUpSettingPanel.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popUpSettingPanel.setFocusable(true);

            // Clear the default translucent background
            popUpSettingPanel.setBackgroundDrawable(new BitmapDrawable());
            popUpSettingPanel.setAnimationStyle(R.style.Animation);

            // Displaying the popup at the specified location, + offsets.
            popUpSettingPanel.showAtLocation(layout, Gravity.NO_GRAVITY, p.x - FunctionHelper.convertToPixels(getBaseContext(),5), p.y + (settingBtn.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    void initAppElements() {
        try {
            appLoadingIcon = findViewById(R.id.app_loading_icon);
            channelGrid = findViewById(R.id.channel_grid_view);
            allChannel = findViewById(R.id.all_channel_tv);
            myFavorites = findViewById(R.id.fav_channel_tv);
            catChannel = findViewById(R.id.cat_tv);
            searchChannel = findViewById(R.id.search_channel);
            settingBtn = findViewById(R.id.setting_btn);
            rateAppBtn = findViewById(R.id.rate_app_btn);
            playingChannelImg = findViewById(R.id.playing_channel_img);
            playingChannelName = findViewById(R.id.playing_channel_name);
            playPauseIcon = findViewById(R.id.play_pause_icon);
            channelLoadingIcon = findViewById(R.id.channel_loading_icon);
            volumeController = findViewById(R.id.volume_controller);
            likeBtn = findViewById(R.id.like_btn);
            timerCounter = findViewById(R.id.timer_countdown);
            networkWarning = findViewById(R.id.network_warning);

            //init View
            popUpSettingPanel = new PopupWindow(MainActivity.this);
            popUpCatPanel = new PopupWindow(MainActivity.this);
            oldTextColors = allChannel.getTextColors();

            //init exoplayer
            mExoPlayer = ExoPlayer.Factory.newInstance(1);
            mPlayerControl = new PlayerControl(mExoPlayer);

            //init ad
            adBanner = findViewById(R.id.ad_banner);
            ShowAd();
            adBanner.bringToFront();

            //init listview channels
            channelList = findViewById(R.id.channel_list_view);
            changeViewChannelStyle = findViewById(R.id.change_view_channel_style);
            changeAppTheme = findViewById(R.id.change_app_theme);
            enableChangeAppStateBtns();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void reloadingChannelsList() {
        try {
            appLoadingIcon.setVisibility(View.VISIBLE);
            if (channelViewStyle) {
                channelGrid.setVisibility(View.GONE);
            } else {
                channelList.setVisibility(View.GONE);
            }
            changeTextColorWhenSwitchingTabs(allChannel,myFavorites,catChannel);
            catChannel.setText("Type: All");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new LoadingDataAsync().execute();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void changeAppThemeBtnImg() {
        try {
            if (appTheme) {
                changeAppTheme.setBackgroundResource(0);
                changeAppTheme.setBackgroundResource(R.drawable.day_icon);
            } else {
                changeAppTheme.setBackgroundResource(0);
                changeAppTheme.setBackgroundResource(R.drawable.night_icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void changeChannelViewStyleBtImg() {
        try {
            if (channelViewStyle) {
                changeViewChannelStyle.setBackgroundResource(0);
                changeViewChannelStyle.setBackgroundResource(R.drawable.list_icon);
            } else {
                changeViewChannelStyle.setBackgroundResource(0);
                changeViewChannelStyle.setBackgroundResource(R.drawable.grid_icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void enableChangeAppStateBtns() {
        try {
            changeViewChannelStyle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        channelViewStyle = channelViewStyle ? false : true;
                        editor.putBoolean("channels_view_style", channelViewStyle);
                        editor.apply();
                        if (displayedChannelState == 2) {
                            listChannels = listChannelsByCat;
                        }
                        ChannelAdapter _ca = new ChannelAdapter(getBaseContext(), listChannels, mPlayerControl, mExoPlayer, channelViewStyle, appTheme);
                        channelGrid.setAdapter(_ca);
                        channelList.setAdapter(_ca);
                        if (channelViewStyle) {
                            channelGrid.setVisibility(View.VISIBLE);
                            channelList.setVisibility(View.GONE);
                        } else {
                            channelList.setVisibility(View.VISIBLE);
                            channelGrid.setVisibility(View.GONE);
                        }
                        changeChannelViewStyleBtImg();
                    } catch (Exception e) {
                        channelViewStyle = true;
//                    editor.putBoolean("channels_view_style", channelViewStyle);
//                    editor.apply();
                        return;
                    }
                }
            });

            changeAppTheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        appTheme = appTheme ? false : true;
                        editor.putBoolean("app_theme", appTheme);
                        editor.apply();
                        if (displayedChannelState == 2) {
                            listChannels = listChannelsByCat;
                        }
                        if (displayedChannelState == 1) {
                            listChannels = FunctionHelper.ConvertChannelStrToList(prefs.getString(Constants.PREF_LIST_FAV_CHANNEL,""));
                        }
                        ChannelAdapter _ca = new ChannelAdapter(getBaseContext(), listChannels, mPlayerControl, mExoPlayer, channelViewStyle, appTheme);
                        if (channelViewStyle) {
                            channelGrid.setAdapter(_ca);
                        } else {
                            channelList.setAdapter(_ca);
                        }
                        changeAppThemeBtnImg();
                    } catch (Exception e) {
                        appTheme = true;
//                    editor.putBoolean("app_theme", appTheme);
//                    editor.apply();
                        return;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        backToHomeScreen();
    }

    private void ShowAd() {
        try {
            adRequest = new AdRequest.Builder().build();
            adBanner.loadAd(adRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void showFullAds() {
        try {
            mInterstitialAd = new InterstitialAd(mContext);
            mInterstitialAd.setAdUnitId(mContext.getResources().getString(R.string.ad_inte));
            AdRequest adRequestInter = new AdRequest.Builder().build();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    mInterstitialAd.show();
                }
            });

            mInterstitialAd.loadAd(adRequestInter);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private class LoadingDataAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                String rawData = FunctionHelper.ReadStringFromServer(prefs.getString("channels_link", prefs.getString("channels_link", Constants.CHANNELS_LINK)));
                editor.putString(Constants.PREF_LIST_ALL_CHANNEL,rawData);
                editor.apply();
                listChannels = FunctionHelper.GetJSONData(new StringBuilder(prefs.getString(Constants.PREF_LIST_ALL_CHANNEL,"")));

                ArrayList<String> catList = new ArrayList<>();
                for (int i=0;i<listChannels.size();i++) {
                    if (!FunctionHelper.checkElementIsExisted(catList, listChannels.get(i).getCat())) {
                        catList.add(listChannels.get(i).getCat());
                    }
                }
                String catListStr = "";
                for (String cat : catList) {
                    catListStr = catListStr + "##" + cat;
                }
                editor.putString(Constants.PREF_LIST_ALL_CAT, catListStr);
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "An Error has occurred, please refresh the App!", Toast.LENGTH_SHORT).show();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                catChannel.setText("Type: All");
                allChannel.setText("All (" + listChannels.size() + " channels)");
                appLoadingIcon.setVisibility(View.GONE);
                channelGrid.setAdapter(new ChannelAdapter(getBaseContext(), listChannels, mPlayerControl,mExoPlayer, true, appTheme));
                channelList.setAdapter(new ChannelAdapter(getBaseContext(), listChannels, mPlayerControl,mExoPlayer, false, appTheme));
                if (channelViewStyle) {
                    // display gird view
                    channelGrid.setVisibility(View.VISIBLE);
                    channelList.setVisibility(View.GONE);

                } else {
                    // display list view
                    channelList.setVisibility(View.VISIBLE);
                    channelGrid.setVisibility(View.GONE);
                }
                changeTextColorWhenSwitchingTabs(allChannel,myFavorites,catChannel);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static void changeUIByRadioStates(int state, final Context context, final ChannelObject co) {
        try {
            playingChannelImg.setBackgroundResource(0);
            playPauseIcon.setBackgroundResource(0);
            likeBtn.setBackgroundResource(0);

            playPauseIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (mPlayerControl.isPlaying()) {
                            if (TimerDialog.timer != null) {
                                TimerDialog.timer.cancel();
                                timerCounter.setVisibility(View.GONE);
                            }
                            mPlayerControl.pause();
                            playPauseIcon.setBackgroundResource(0);
                            playPauseIcon.setBackgroundResource(R.drawable.play_icon);
                        } else {
                            if (mPlayerControl != null) {
                                mExoPlayer.setPlayWhenReady(true);
                                playPauseIcon.setBackgroundResource(0);
                                playPauseIcon.setBackgroundResource(R.drawable.pau_icon);
                            }
                        }
                    } catch (Exception e) {
                        return;
                    }
                }
            });
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String favChannelStr = prefs.getString(Constants.PREF_LIST_FAV_CHANNEL,"");
                        boolean isPlayingChannelFav = favChannelStr.contains(co.getName());

                        if (isPlayingChannelFav) {
                            Toast.makeText(mContext,"Channel " + co.getName() + " has been removed to Favorites!", Toast.LENGTH_SHORT).show();
                            String removedChannel = co.getName() + "100ENDCHAR001" + co.getLink() + "100ENDCHAR001" + co.getPic() + "100ENDCHAR001" + co.getCat() + "100ENDCHANNEL001";
                            String favChannelAfterRemove = favChannelStr.replace(removedChannel,"");
                            editor.putString(Constants.PREF_LIST_FAV_CHANNEL, favChannelAfterRemove);
                            editor.apply();
                            listChannels = FunctionHelper.ConvertChannelStrToList(favChannelAfterRemove);
                            likeBtn.setBackgroundResource(R.drawable.fav_empty_icon);

                        } else {
                            Toast.makeText(mContext,"Channel " + co.getName() + " has been added to Favorites!", Toast.LENGTH_SHORT).show();
                            String favChannelAfterAdd = favChannelStr + co.getName() + "100ENDCHAR001"
                                    + co.getLink() + "100ENDCHAR001"
                                    + co.getPic() + "100ENDCHAR001"
                                    + co.getCat() + "100ENDCHANNEL001";
                            editor.putString(Constants.PREF_LIST_FAV_CHANNEL, favChannelAfterAdd);
                            editor.apply();
                            listChannels = FunctionHelper.ConvertChannelStrToList(favChannelAfterAdd);
                            likeBtn.setBackgroundResource(R.drawable.fav_filled_icon);
                        }
                        if (displayedChannelState == 1) {
                            ChannelAdapter channelAdapter = new ChannelAdapter(context, listChannels, mPlayerControl, mExoPlayer, channelViewStyle,appTheme);
                            if (channelViewStyle) {
                                channelGrid.setAdapter(channelAdapter);
                            } else {
                                channelList.setAdapter(channelAdapter);
                            }
                        }
                    } catch (Exception e) {
                        return;
                    }
                }
            });
            if (prefs.getString(Constants.PREF_LIST_FAV_CHANNEL,"").contains(co.getName())) {
                likeBtn.setBackgroundResource(R.drawable.fav_filled_icon);
            } else {
                likeBtn.setBackgroundResource(R.drawable.fav_empty_icon);
            }

            playingChannelName.setText(co.getName());
            if (!co.getPic().isEmpty()) {
                Picasso.with(context).load(co.getPic()).into(playingChannelImg, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        playingChannelImg.setBackgroundResource(R.drawable.app_icon);
                    }
                });
            }
            if (state == ExoPlayer.STATE_READY) {
                playPauseIcon.setVisibility(View.VISIBLE);
                playPauseIcon.setBackgroundResource(R.drawable.pau_icon);
                channelLoadingIcon.setVisibility(View.GONE);
            } else if (state == ExoPlayer.STATE_ENDED || state == -1) {
                playPauseIcon.setVisibility(View.VISIBLE);
                playPauseIcon.setBackgroundResource(R.drawable.play_icon);
                channelLoadingIcon.setVisibility(View.GONE);
            } else {
                playPauseIcon.setVisibility(View.GONE);
                channelLoadingIcon.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //stop timer if it's set before
            NotificationManager notificationManager = (NotificationManager) getApplication()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Constants.NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }
}
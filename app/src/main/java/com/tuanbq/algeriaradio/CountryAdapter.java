package com.tuanbq.algeriaradio;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CountryAdapter extends BaseAdapter {
    private Context mContext;
    private String[] countriesList;

    public CountryAdapter(Context context, String[] countriesList) {
        this.mContext = context;
        this.countriesList = countriesList;
    }

    @Override
    public int getCount() {
        return countriesList.length;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            view = new TextView(mContext);

            ((TextView) view).setText(countriesList[i].charAt(0) + countriesList[i].substring(1, countriesList[i].length()).toLowerCase());
            ((TextView) view).setGravity(Gravity.LEFT);
            ((TextView) view).setGravity(Gravity.CENTER_VERTICAL);
            view.setPadding(FunctionHelper.convertToPixels(mContext, 10), 0,0,0);
            ((TextView) view).setTextSize(FunctionHelper.convertToPixels(mContext, 10));
            ((TextView) view).setHeight(FunctionHelper.convertToPixels(mContext,42));
            ((TextView) view).setTypeface(Typeface.SANS_SERIF);
            ((TextView) view).setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.lightGray));

            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

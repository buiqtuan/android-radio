package com.tuanbq.algeriaradio;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by tuanbq on 5/25/2018.
 */

public class CatAdapter extends BaseAdapter{
    private Context mContext;
    private String[] catList;

    public CatAdapter(Context mContext, String[] catList) {
        this.mContext = mContext;
        this.catList = catList;
    }

    @Override
    public int getCount() {
        return catList.length-1;
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
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView catTextView = new TextView(mContext);
            catTextView.setGravity(Gravity.CENTER_VERTICAL);
            catTextView.setLayoutParams(layoutParams);
            catTextView.setMinHeight(FunctionHelper.convertToPixels(mContext,30));
            catTextView.setEllipsize(TextUtils.TruncateAt.END);
            catTextView.setMaxLines(1);
            catTextView.setTextSize(15);
            catTextView.setTypeface(catTextView.getTypeface(), Typeface.BOLD_ITALIC);
            catTextView.setPadding(FunctionHelper.convertToPixels(mContext,4),0,0,0);
            catTextView.setTextColor(Color.LTGRAY);
            catTextView.setText(catList[i+1]);

            return catTextView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

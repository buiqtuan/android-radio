package com.tuanbq.algeriaradio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.LinearLayout;

public class RoundedCornerLayout extends LinearLayout {
    private final static float CORNER_RADIUS = 8.0f;

    private Bitmap maskBitmap;
    private Paint paint, maskPaint;
    private float cornerRadius;

    public RoundedCornerLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        try {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS, metrics);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            setWillNotDraw(false);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        try {
            Bitmap offscreenBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas offscreenCanvas = new Canvas(offscreenBitmap);

            super.draw(offscreenCanvas);

            if (maskBitmap == null) {
                maskBitmap = createMask(canvas.getWidth(), canvas.getHeight());
            }

            offscreenCanvas.drawBitmap(maskBitmap, 0f, 0f, maskPaint);
            canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private Bitmap createMask(int width, int height) {
        try {
            Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
            Canvas canvas = new Canvas(mask);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);

            canvas.drawRect(0, 0, width, height, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, paint);

            return mask;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
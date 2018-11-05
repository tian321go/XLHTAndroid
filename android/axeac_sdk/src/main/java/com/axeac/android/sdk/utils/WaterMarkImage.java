package com.axeac.android.sdk.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by Administrator on 2018/1/14.
 */

public class WaterMarkImage {
    private static BitmapDrawable drawable;
    public static BitmapDrawable getDrawable(String name,int height,int width,int alpha){
        drawable = null;
        setDrawable(name,height,width,alpha);
        return drawable;

    }
    public static BitmapDrawable getDrawable(String name,int height,int width,int color,int alpha){
        drawable = null;
        setDrawable(name,height,width,color,alpha);
        return drawable;

    }
    private static void setDrawable(String name,int height,int width,int alpha){
        Bitmap bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setAlpha(alpha);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(50);
        Path path = new Path();
        path.moveTo(30, 150);
        path.lineTo(240, 0);
        canvas.drawTextOnPath(name, path, 0, 35, paint);
        drawable = new BitmapDrawable(bitmap);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        drawable.setDither(true);

    }
    private static void setDrawable(String name,int height,int width,int color,int alpha){
        Bitmap bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setAlpha(alpha);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(50);
        Path path = new Path();
        path.moveTo(30, 150);
        path.lineTo(300, 0);
        canvas.drawTextOnPath(name, path, 0, 35, paint);
        drawable = new BitmapDrawable(bitmap);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        drawable.setDither(true);

    }
}

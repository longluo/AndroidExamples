package com.longluo.devlib.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;


/*
 *  The Device Screen Tools
 * @author long.luo
 * @date 2014-11-8 15:14:22
 */
public class ScreenTools {
    public static final int SCREENT_WIDTH_240 = 240;
    public static final int SCREENT_WIDTH_320 = 320;
    public static final int SCREENT_WIDTH_480 = 480;
    public static final int SCREENT_WIDTH_540 = 540;
    public static final int SCREENT_WIDTH_720 = 720;
    public static final int SCREENT_WIDTH_1080 = 1080;

    public static final int SCREENT_HEIGHT_320 = 320;
    public static final int SCREENT_HEIGHT_480 = 480;
    public static final int SCREENT_HEIGHT_800 = 800;
    public static final int SCREENT_HEIGHT_854 = 854;
    public static final int SCREENT_HEIGHT_1280 = 1280;
    public static final int SCREENT_HEIGHT_1920 = 1920;

    public static int getWidth(Activity _activity) {
        DisplayMetrics dm = new DisplayMetrics();
        _activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getHeight(Activity _activity) {
        DisplayMetrics dm = new DisplayMetrics();
        _activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int setHeight(Activity _activity, int persent) {
        int height = (int) (getHeight(_activity) * (persent * 0.01D));
        return height;
    }

    public static int setWidth(Activity _activity, int persent) {
        int width = (int) (getWidth(_activity) * (persent * 0.01D));
        return width;
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
}

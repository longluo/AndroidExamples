package me.longluo.audio.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;


public class ColorUtil {

    static public int getColorBackground(Context context) {
        Resources.Theme theme = context.getTheme();
        TypedValue value = new TypedValue();
        theme.resolveAttribute(android.R.attr.colorBackground, value, true);
        return value.data;
    }

    static public int getColorForeground(Context context) {
        Resources.Theme theme = context.getTheme();
        TypedValue value = new TypedValue();
        theme.resolveAttribute(android.R.attr.colorForeground, value, true);
        return value.data;
    }
}

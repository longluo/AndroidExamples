package me.longluo.audio.util;

import android.app.Activity;
import android.content.res.Configuration;


public class ThemeUtil {

    static public final String LIGHT = "light";
    static public final String DARK = "dark";
    static public final String SYSTEM = "system";

    static private int resIdThemeDark;
    static private int resIdThemeLight;

    static public void setResIdThemeDark(int resId) {
        ThemeUtil.resIdThemeDark = resId;
    }

    static public void setResIdThemeLight(int resId) {
        ThemeUtil.resIdThemeLight = resId;
    }

    static public void set(Activity activity, String theme) {
        switch (theme) {
            case LIGHT:
                activity.setTheme(resIdThemeLight);
                break;

            case DARK:
                activity.setTheme(resIdThemeDark);
                break;

            case SYSTEM:
                int systemUiMode = activity.getResources().getConfiguration().uiMode;
                int nightMode = systemUiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                    activity.setTheme(resIdThemeDark);
                } else {
                    activity.setTheme(resIdThemeLight);
                }
                break;

            default:
                break;
        }
    }
}
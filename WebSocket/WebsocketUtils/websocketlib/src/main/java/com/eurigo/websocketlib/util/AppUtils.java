package com.eurigo.websocketlib.util;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * @author Eurigo
 * Created on 2022/3/31 10:15
 * desc   :
 */
public class AppUtils {

    public AppUtils() {
    }

    public static AppUtils getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private final static AppUtils INSTANCE = new AppUtils();
    }

    private Application sApp;

    public Application getApp() {
        if (sApp != null) {
            return sApp;
        }
        sApp = getApplicationByReflect();
        if (sApp == null) {
            throw new NullPointerException("reflect failed.");
        }
        return sApp;
    }

    private Application getApplicationByReflect() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object thread = getActivityThread();
            if (thread == null) {
                return null;
            }
            Object app = activityThreadClass.getMethod("getApplication").invoke(thread);
            if (app == null) {
                return null;
            }
            return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object getActivityThread() {
        Object activityThread = getActivityThreadInActivityThreadStaticField();
        if (activityThread != null) {
            return activityThread;
        }
        return getActivityThreadInActivityThreadStaticMethod();
    }

    private Object getActivityThreadInActivityThreadStaticField() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            return sCurrentActivityThreadField.get(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticField: " + e.getMessage());
            return null;
        }
    }

    private Object getActivityThreadInActivityThreadStaticMethod() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            return activityThreadClass.getMethod("currentActivityThread").invoke(null);
        } catch (Exception e) {
            Log.e("UtilsActivityLifecycle", "getActivityThreadInActivityThreadStaticMethod: " + e.getMessage());
            return null;
        }
    }

}

package com.longluo.devlib.utils;

import android.content.Context;
import android.content.res.Resources;

public class ResourceUtils {
    static final String DRAWABLE = "drawable";
    static final String STRING = "string";
    static final String STYLE = "style";
    static final String LAYOUT = "layout";
    static final String ID = "id";
    static final String COLOR = "color";
    static final String RAW = "raw";
    static final String ANIM = "anim";
    static final String ATTR = "attr";
    static final String DIMEN = "dimen";
    static String mPackageName;
    static Resources mResources;

    public static void init(Context appContext) {
        mPackageName = appContext.getPackageName();
        mResources = appContext.getResources();
    }

    private static int getResourceId(String sourceName, String sourceType) {
        if (mResources == null) {
            return -1;
        }

        return mResources.getIdentifier(sourceName, sourceType, mPackageName);
    }

    public static int getResourceIdForString(String sourceName) {
        return getResourceId(sourceName, "string");
    }

    public static int getResourceIdForID(String sourceName) {
        return getResourceId(sourceName, "id");
    }

    public static int getResourceIdForLayout(String sourceName) {
        return getResourceId(sourceName, "layout");
    }

    public static int getResourceIdForDrawable(String sourceName) {
        return getResourceId(sourceName, "drawable");
    }

    public static int getResourceIdForStyle(String sourceName) {
        return getResourceId(sourceName, "style");
    }

    public static int getResourceIdForColor(String sourceName) {
        return getResourceId(sourceName, "color");
    }

    public static int getResourceIdForRaw(String sourceName) {
        return getResourceId(sourceName, "raw");
    }

    public static int getResourceForAnim(String sourceName) {
        return getResourceId(sourceName, "anim");
    }

    public static int getResourceForAttr(String sourceName) {
        return getResourceId(sourceName, "attr");
    }

    public static int getResourceForDimen(String sourceName) {
        return getResourceId(sourceName, "dimen");
    }
}

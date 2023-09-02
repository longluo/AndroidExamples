package com.longluo.dragdemo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class ScreenUtil {

    private static int width = 0;
    private static int height = 0;
    private static int showHeight = 0;
    private static int statusHeight = 0;
    private static float density = 0;

    public static int getScreenWidth(Context context) {
        if (width == 0) {
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            width = display.getWidth();
        }
        return width;
    }

    public static int getScreenHeight(Context context) {
        if (height == 0) {
            WindowManager manager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            height = display.getHeight();
        }
        return height;
    }

    public static int getScreenShowHeight(Context context) {
        if (showHeight == 0) {
            showHeight = getScreenHeight(context) - getStatusBarHeight(context);
        }
        return showHeight;
    }

    public static int getStatusBarHeight(Context context) {
        if (statusHeight > 0) {
            return statusHeight;
        }
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusHeight = context.getResources().getDimensionPixelSize(x);
            return statusHeight;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public static float getScreenDensity(Context context) {
        if (density == 0) {
            try {
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager manager = (WindowManager) context
                        .getSystemService(Context.WINDOW_SERVICE);
                manager.getDefaultDisplay().getMetrics(dm);
                density = dm.density;
            } catch (Exception ex) {
                ex.printStackTrace();
                density = 1.0f;
            }
        }
        return density;
    }

    public static float getScreentMinLength(Context context) {
        return getScreenHeight(context) > getScreenWidth(context) ? getScreenWidth(context)
                : getScreenHeight(context);
    }

    /**
     * 根据指定k的系数获取屏幕在max范围内的最大长宽,默认宽比较小
     *
     * @param context
     * @param k
     * @return
     */
    public static DrawWrap getCutWrap(Context context, float k, float max) {
        float tWidth = getScreenWidth(context);
        float tHeight = getScreenHeight(context);

        if (tWidth * max * k > tHeight) {
            return new DrawWrap(tHeight * max / k, tHeight * max);
        } else {
            return new DrawWrap(tWidth * max, tWidth * max * k);
        }
    }

    public static class DrawWrap {
        public float width;
        public float height;

        public DrawWrap(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * getScreenDensity(context) + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕中控件顶部位置的高度--即控件顶部的Y点
     *
     * @return
     */
    public static int getScreenViewTopHeight(View view) {
        return view.getTop();
    }

    /**
     * 获取屏幕中控件底部位置的高度--即控件底部的Y点
     *
     * @return
     */
    public static int getScreenViewBottomHeight(View view) {
        return view.getBottom();
    }

    /**
     * 获取屏幕中控件左侧的位置--即控件左侧的X点
     *
     * @return
     */
    public static int getScreenViewLeftHeight(View view) {
        return view.getLeft();
    }

    /**
     * 获取屏幕中控件右侧的位置--即控件右侧的X点
     *
     * @return
     */
    public static int getScreenViewRightHeight(View view) {
        return view.getRight();
    }

    /*
     * 获取控件宽
     */
    public static int getWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredWidth());
    }

    /*
     * 获取控件高
     */
    public static int getHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredHeight());
    }

}

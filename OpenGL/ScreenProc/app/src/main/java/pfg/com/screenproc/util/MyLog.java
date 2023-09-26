package pfg.com.screenproc.util;

import android.util.Log;

/**
 * Created by FPENG3 on 2018/7/16.
 */

public class MyLog {

    private final static String PRE_TAG = "ScreenProc+";

    public static void logi(String tag, String msg) {
        Log.i(PRE_TAG+tag, msg);
    }

    public static void logd(String tag, String msg) {
        Log.i(PRE_TAG+tag, msg);
    }

    public static void loge(String tag, String msg) {
        Log.e(PRE_TAG+tag, msg);
    }
}

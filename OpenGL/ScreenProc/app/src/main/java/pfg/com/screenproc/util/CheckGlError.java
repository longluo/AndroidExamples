package pfg.com.screenproc.util;

import android.opengl.GLES30;

/**
 * Created by FPENG3 on 2018/7/25.
 */

public class CheckGlError {
    private static String TAG = "CheckGlError";

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            MyLog.loge(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}

package pfg.com.screenproc.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;

import android.content.Context;
import android.opengl.GLUtils;

/**
 * Created by FPENG3 on 2018/7/19.
 */

public class TextureHelper {
    private final static String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resourceId) {
        int [] textureObjectIds = new int[1];
        // 创建了一个纹理对象，id存储在textureObjectIds中
        GLES30.glGenTextures(1, textureObjectIds, 0);
        if(textureObjectIds[0] == 0) {
            MyLog.loge(TAG, "Could not generate texture object!");
            return 0;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // 不缩放
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if(bitmap == null) {
            MyLog.loge(TAG, "Resource could not be decoded!");
            GLES30.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureObjectIds[0]);
        // 缩小
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        // 放大
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        // 生成MIP贴图
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

        // 解除纹理绑定
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        return textureObjectIds[0];
    }
}

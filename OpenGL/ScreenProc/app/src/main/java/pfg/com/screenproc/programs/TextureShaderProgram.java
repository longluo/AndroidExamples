package pfg.com.screenproc.programs;

import android.content.Context;
import android.opengl.GLES30;

import pfg.com.screenproc.util.MyLog;

import static pfg.com.screenproc.util.CheckGlError.checkGlError;

/**
 * Created by FPENG3 on 2018/7/24.
 */

public class TextureShaderProgram extends ShaderProgram {

    private final static String TAG = "TextureShaderProgram";
    // Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;


    public TextureShaderProgram(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
        uMatrixLocation = GLES30.glGetUniformLocation(program, U_MATRIX);
        MyLog.logd(TAG, "uMatrixLocation:"+uMatrixLocation);
        checkGlError("glGetUniformLocation:U_MATRIX");

        aPositionLocation = GLES30.glGetAttribLocation(program, A_POSITION);
        MyLog.logd(TAG, "aPositionLocation:"+aPositionLocation);
        checkGlError("glGetAttribLocation:A_POSITION");
        aTextureCoordinatesLocation = GLES30.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        MyLog.logd(TAG, "aTextureCoordinatesLocation:"+aTextureCoordinatesLocation);
        checkGlError("glGetAttribLocation:A_TEXTURE_COORDINATES");

        uTextureUnitLocation = GLES30.glGetUniformLocation(program, U_TEXTURE_UNIT);
        MyLog.logd(TAG, "uTextureUnitLocation:"+uTextureUnitLocation);
        checkGlError("glGetUniformLocation:U_TEXTURE_UNIT");
    }

    public void setUniform(float[] matrix, int textureId) {
        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        checkGlError("glUniformMatrix4fv");
        // Set the active texture unit to texture unit 0
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        checkGlError("glActiveTexture");

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        checkGlError("glBindTexture");
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}

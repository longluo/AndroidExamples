package pfg.com.screenproc.programs;

import android.content.Context;
import android.opengl.GLES30;

import static pfg.com.screenproc.util.CheckGlError.checkGlError;

/**
 * Created by FPENG3 on 2018/7/25.
 */

public class ColorShaderProgram extends ShaderProgram {

    private final int uMatrixLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;

    public ColorShaderProgram(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
        uMatrixLocation = GLES30.glGetUniformLocation(program, U_MATRIX);
        checkGlError("glGetUniformLocation:U_MATRIX");

        aPositionLocation = GLES30.glGetAttribLocation(program, A_POSITION);
        checkGlError("glGetAttribLocation:A_POSITION");
        aColorLocation = GLES30.glGetAttribLocation(program, A_COLOR);
        checkGlError("glGetAttribLocation:A_COLOR");
    }

    public void setUniform(float[] matrix) {
        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        checkGlError("glUniformMatrix4fv");
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getColorAttributeLocation() {
        return aColorLocation;
    }

}

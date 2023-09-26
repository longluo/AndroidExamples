package pfg.com.screenproc.programs;

import android.content.Context;
import android.opengl.GLES30;

import pfg.com.screenproc.util.ShaderHelper;

import static pfg.com.screenproc.util.CheckGlError.checkGlError;

/**
 * Created by FPENG3 on 2018/7/24.
 */

public class ShaderProgram {

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // Shader program
    protected final int program;

    public ShaderProgram(Context context, String vertexShader, String fragmentShader) {
        program = ShaderHelper.buildProgram(vertexShader, fragmentShader);
    }

    public void useProgram() {
        GLES30.glUseProgram(program);
        checkGlError("glUseProgram");
    }
}

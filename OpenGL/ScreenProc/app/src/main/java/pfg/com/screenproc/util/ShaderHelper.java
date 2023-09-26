package pfg.com.screenproc.util;
import android.opengl.GLES30;

import static pfg.com.screenproc.util.CheckGlError.checkGlError;

/**
 * Created by FPENG3 on 2018/7/17.
 */

public class ShaderHelper {

    private final static String TAG = "ShaderHelper";


    public static int compileVertexShader(String shader) {
        return compileShader(GLES30.GL_VERTEX_SHADER, shader);
    }

    public static int compileFragmentShader(String shader) {
        return compileShader(GLES30.GL_FRAGMENT_SHADER, shader);
    }

    private static int compileShader(int type, String shader) {
        int shaderid = GLES30.glCreateShader(type);
        checkGlError("glCreateShader");
        GLES30.glShaderSource(shaderid, shader);
        checkGlError("glShaderSource");
        GLES30.glCompileShader(shaderid);
        checkGlError("glCompileShader");

        int []compileStatus = new int[1];
        GLES30.glGetShaderiv(shaderid, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

        if(compileStatus[0] == 0) {
            MyLog.loge(TAG, "compileShader failed type:"+type+", error info:"+GLES30.glGetShaderInfoLog(shaderid));
            GLES30.glDeleteShader(shaderid);
        } else {
            MyLog.logd(TAG, "compileShader successful status:"+compileStatus[0]);
        }
        return shaderid;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        MyLog.logd(TAG, "linkProgram vertexShaderId:"+vertexShaderId+" fragmentShaderId:"+fragmentShaderId);
        int programId = GLES30.glCreateProgram();
        checkGlError("glCreateProgram");
        GLES30.glAttachShader(programId, vertexShaderId);
        checkGlError("glAttachShader");
        GLES30.glAttachShader(programId, fragmentShaderId);
        checkGlError("glAttachShader");
        GLES30.glLinkProgram(programId);
        checkGlError("glLinkProgram");
        int []linkStatus = new int[1];
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if(linkStatus[0] == 0) {
            MyLog.loge(TAG, "linkProgram failed, error info:"+GLES30.glGetProgramInfoLog(programId));
        } else {
            MyLog.logd(TAG, "linkProgram(programId=:"+programId+") successful status:"+linkStatus[0]);
        }
        return programId;
    }

    public static boolean validateProgram(int programId) {
        GLES30.glValidateProgram(programId);
        int []validateStatus = new int[1];
        GLES30.glGetProgramiv(programId, GLES30.GL_VALIDATE_STATUS, validateStatus, 0);

        if(validateStatus[0] == 0) {
            MyLog.loge(TAG, "linkProgram failed validateStatus:"+validateStatus[0]+", error info:"+GLES30.glGetProgramInfoLog(programId));
            return false;
        } else {
            return true;
        }
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        int program;

        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        program = linkProgram(vertexShader, fragmentShader);

        validateProgram(program);
        return program;
    }
}

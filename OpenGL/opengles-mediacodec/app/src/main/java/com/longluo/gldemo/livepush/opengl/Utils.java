package com.longluo.gldemo.livepush.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

    private static final String TAG = "Utils";

    /**
     * 获取 glsl 资源
     */
    public static String getGLResource(Context context, int rawId) {
        InputStream inputStream = context.getResources().openRawResource(rawId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer sb = new StringBuffer();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * 加载着色器
     *
     * @param shaderType 着色器的类型
     * @param source     资源源代码
     */
    private static int loadShader(int shaderType, String source) {
        // 创建 Shader
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            // 设置资源
            GLES20.glShaderSource(shader, source);
            // 编译shader,编译着色器对象
            GLES20.glCompileShader(shader);
            // 判断有没有错误
            int[] status = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
            if (status[0] != GLES20.GL_TRUE) {
                Log.d(TAG, "load shader error");
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }
//
//    private static int loadShader(int shaderType, String shaderSource) {
//        // 创建 Shader
//        int shader = GLES20.glCreateShader(shaderType);
//        if(shader != 0){
//            // 设置资源
//            GLES20.glShaderSource(shader,shaderSource);
//            // 编译shader,编译着色器对象
//            GLES20.glCompileShader(shader);
//            // 判断有没有错误
//            int[] status = new int[1];
//            GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,status,0);
//            if(status[0] != GLES20.GL_TRUE){
//                Log.d(TAG, "loadShader: error");
//                GLES20.glDeleteShader(shader);
//                shader=0;
//            }
//        }
//        return shader;
//    }

//    public static int createProgram(String vertexSource,String fragmentSource){
//        // 分别加载创建着色器
//        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
//        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
//
//        if(vertexShader != 0&&fragmentShader != 0){
//            // 创建 Program
//            int program = GLES20.glCreateProgram();
//            // 将着色器程序添加到渲染程序中
//            GLES20.glAttachShader(program,vertexShader);
//            GLES20.glAttachShader(program,fragmentShader);
//            // 链接源程序
//            GLES20.glLinkProgram(program);
//            // 判断有没有错误
//            int[] status = new int[1];
//            GLES20.glGetProgramiv(program,GLES20.GL_LINK_STATUS,status,0);
//            if (status[0] != GLES20.GL_TRUE){
//
//                Log.d(TAG, "createProgram: error");
//                GLES20.glDeleteProgram(program);
//                return 0;
//            }else
//                return program;
//        }
//        return 0;
//    }
    /**
     * 创建一个 Program
     *
     * @param vertexSource   顶点源码资源
     * @param fragmentSource 片元源码资源
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        // 分别加载创建着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

        if (vertexShader != 0 && fragmentShader != 0) {
            // 创建 Program
            int program = GLES20.glCreateProgram();
            // 将着色器程序添加到渲染程序中
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            // 链接源程序
            GLES20.glLinkProgram(program);
            // 判断有没有错误
            int[] status = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
            if (status[0] != GLES20.GL_TRUE) {
                Log.d(TAG, "create program error");
                GLES20.glDeleteProgram(program);
                program = 0;
            }
                return program;
        }
        return 0;
    }



    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}

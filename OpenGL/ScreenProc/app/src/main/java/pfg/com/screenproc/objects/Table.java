package pfg.com.screenproc.objects;

import android.opengl.GLES30;

import pfg.com.screenproc.data.VertexArray;
import pfg.com.screenproc.programs.TextureShaderProgram;

import static pfg.com.screenproc.util.CheckGlError.checkGlError;
import static pfg.com.screenproc.util.Constants.BYTES_PER_FLOAT;

/**
 * Created by FPENG3 on 2018/7/24.
 */

public class Table {

    private final static int POSITION_COMPONENT_COUNT = 2;
    private final static int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private final static int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
      // Order of coordinates:X, Y, S, T
      // Triangle Fan
      0f,    0f, 0.5f, 0.5f,
   -0.5f, -0.8f,   0f, 1f,
    0.5f, -0.8f,   1f, 1f,
    0.5f,  0.8f,   1f, 0f,
   -0.5f,  0.8f,   0f, 0f,
   -0.5f, -0.8f,   0f, 1f
    };

    private VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertexArray.setVertexAttribPointer(0, textureShaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);

        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, textureShaderProgram.getTextureCoordinatesAttributeLocation(), TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6);
        checkGlError("glDrawArrays");
    }
}

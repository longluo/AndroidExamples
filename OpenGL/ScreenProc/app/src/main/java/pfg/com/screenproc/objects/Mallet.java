package pfg.com.screenproc.objects;

import android.opengl.GLES30;

import pfg.com.screenproc.data.VertexArray;
import pfg.com.screenproc.programs.ColorShaderProgram;

import static pfg.com.screenproc.util.CheckGlError.checkGlError;
import static pfg.com.screenproc.util.Constants.BYTES_PER_FLOAT;

/**
 * Created by FPENG3 on 2018/7/25.
 */

public class Mallet {
    private final static int POSITION_COMPONENT_COUNT = 2;
    private final static int COLOR_COORDINATES_COMPONENT_COUNT = 3;
    private final static int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
    // Order of coordinates:X, Y, R, G, B
      0f, -0.4f, 0f, 0f, 1f,
      0f,  0.4f, 1f, 0f, 0f
    };

    private VertexArray vertexArray;

    public Mallet() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttribPointer(0, colorShaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);

        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, colorShaderProgram.getColorAttributeLocation(), COLOR_COORDINATES_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 2);
        checkGlError("glDrawArrays");
    }
}

package pfg.com.screenproc.data;

import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static pfg.com.screenproc.util.CheckGlError.checkGlError;
import static pfg.com.screenproc.util.Constants.BYTES_PER_FLOAT;

/**
 * Created by FPENG3 on 2018/7/24.
 */

public class VertexArray {

    private final FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertexData);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        GLES30.glVertexAttribPointer(attributeLocation, componentCount, GLES30.GL_FLOAT, false, stride, floatBuffer);
        checkGlError("glVertexAttribPointer");
        GLES30.glEnableVertexAttribArray(attributeLocation);
        checkGlError("glEnableVertexAttribArray");
        floatBuffer.position(0);
    }
}

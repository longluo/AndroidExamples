package pfg.com.screenproc.objects;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.FloatMath;

import java.util.ArrayList;
import java.util.List;

import pfg.com.screenproc.util.Geometry;

import static pfg.com.screenproc.util.Geometry.*;

/**
 * Created by FPENG3 on 2018/7/25.
 */

public class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3; // 一个顶点需要多少浮点数(x,y,z)
    private final float[] vertexData;
    private int offset = 0;

    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();

    private ObjectBuilder(int sizeInVertices /*顶点个数*/) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    // 计算圆柱体顶部圆的顶点个数
    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1); // 三角形扇构造的圆，它有一个顶点在圆心，围着圆的每个点有个一个顶点，
                                    // 并且围着圆的第一个顶点(不是圆心)要重复两次才能使圆闭合
    }

    // 计算圆柱体侧面(是一个卷起来的长方形)的顶点个数
    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) *2; // 由一个三角形带构造，围着圆顶部的每个点都需要两个顶点，
                                   // 并且前两个顶点要重复两次才能使这个管闭合
    }

    static GeneratedData createPuck(Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);
        Circle puckTop = new Circle(puck.center.translateY(puck.height / 2f), puck.radius);
        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);
        return builder.build();
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    // 生成冰球的顶部
    private void appendCircle(Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        for(int i = 0; i < numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
            vertexData[offset++] = (float) (circle.center.x + circle.radius * Math.cos(angleInRadians));
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = (float) (circle.center.z + circle.radius * Math.sin(angleInRadians));
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES30.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    // 生成冰球的侧面
    private void appendOpenCylinder(Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);
        for(int i = 0; i < numPoints; i++) {
            float angleInRadians = ((float) i/ (float) numPoints) * ((float) Math.PI * 2f);
            float xPosition = (float) (cylinder.center.x + cylinder.radius * Math.cos(angleInRadians));
            float zPostion = (float) (cylinder.center.x + cylinder.radius * Math.sin(angleInRadians));
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPostion;
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPostion;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES30.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    static interface DrawCommand {
        void draw();
    }

}

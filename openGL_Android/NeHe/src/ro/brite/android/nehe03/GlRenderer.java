package ro.brite.android.nehe03;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GlRenderer implements Renderer {

	private static float[] triangleCoords = new float[] {
		 0,  1, 0,
		-1, -1, 0,
		 1, -1, 0
	};

	private static float[] triangleColors = new float[] {
		1, 0, 0, 1,
		0, 1, 0, 1,
		0, 0, 1, 1
	};
	
	private static float[] quadCoords = new float[] {
		-1, 1, 0,
		-1,-1, 0,
		 1, 1, 0,
		 1,-1, 0
	};

	private static FloatBuffer triangleVertexBfr;
	private static FloatBuffer triangleColorBfr;
	private static FloatBuffer quadVertexBfr;
	
	static
	{
		triangleVertexBfr = FloatBuffer.wrap(triangleCoords);
		triangleColorBfr = FloatBuffer.wrap(triangleColors);
		quadVertexBfr = FloatBuffer.wrap(quadCoords);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0, 0, 0, 0);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// draw triangle
		gl.glTranslatef(-1.5f, 0, -6);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleVertexBfr);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, triangleColorBfr);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
		// draw quad
		gl.glTranslatef(3, 0, 0);
		gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBfr);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// avoid division by zero
		if (height == 0) height = 1;
		// draw on the entire screen
		gl.glViewport(0, 0, width, height);
		// setup projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 1.0f, 100.0f);
	}

}

package ro.brite.android.nehe05;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

public class GlRenderer implements Renderer {

	private static float[] pyramidCoords = new float[] {
		 0,  1,  0,
		 1, -1,  1,
		 1, -1, -1,
		-1, -1, -1,
		-1, -1,  1,
		 1, -1,  1
	};

	private static float[] pyramidColors = new float[] {
		1, 0, 0, 1,
		0, 1, 0, 1,
		0, 0, 1, 1,
		0, 1, 0, 1,
		0, 0, 1, 1,
		0, 1, 0, 1
	};
	
	private static float[][] cubeCoords = new float[][] {
		new float[] { // top
			 1, 1,-1,
			-1, 1,-1,
			-1, 1, 1,
			 1, 1, 1
		},
		new float[] { // bottom
			 1,-1, 1,
			-1,-1, 1,
			-1,-1,-1,
			 1,-1,-1
		},
		new float[] { // front
			 1, 1, 1,
			-1, 1, 1,
			-1,-1, 1,
			 1,-1, 1
		},
		new float[] { // back
			 1,-1,-1,
			-1,-1,-1,
			-1, 1,-1,
			 1, 1,-1
		},
		new float[] { // left
			-1, 1, 1,
			-1, 1,-1,
			-1,-1,-1,
			-1,-1, 1
		},
		new float[] { // right
			 1, 1,-1,
			 1, 1, 1,
			 1,-1, 1,
			 1,-1,-1
		},
	};

	private static float[] cubeColors = new float[] {
		0,1,0,1,
		1,0.5f,0,1,
		1,0,0,1,
		1,1,0,1,
		0,0,1,1,
		1,0,1,1		
	};
	
	private static FloatBuffer pyramidVertexBfr;
	private static FloatBuffer pyramidColorBfr;
	private static FloatBuffer[] cubeVertexBfr;
	
	private static float pyramidRot;
	private static float cubeRot;
	
	static
	{
		pyramidVertexBfr = FloatBuffer.wrap(pyramidCoords);
		pyramidColorBfr = FloatBuffer.wrap(pyramidColors);
		
		cubeVertexBfr = new FloatBuffer[6];
		for (int i = 0; i < 6; i++)
		{
			cubeVertexBfr[i] = FloatBuffer.wrap(cubeCoords[i]);
		}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0, 0, 0, 0);

		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// draw pyramid
		gl.glTranslatef(-1.5f, 0, -6);
		gl.glRotatef(pyramidRot, 0, 1, 0);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, pyramidVertexBfr);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, pyramidColorBfr);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 6);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		
		// draw cube
		gl.glLoadIdentity();
		gl.glTranslatef(1.5f, 0, -6);
		gl.glRotatef(cubeRot, 1, 1, 1);
		for (int i = 0; i < 6; i++)
		{
			gl.glColor4f(cubeColors[4*i+0], cubeColors[4*i+1], cubeColors[4*i+2], cubeColors[4*i+3]);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeVertexBfr[i]);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
		
		// update rotations
		pyramidRot += 0.8f;
		cubeRot -= 0.5f;
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

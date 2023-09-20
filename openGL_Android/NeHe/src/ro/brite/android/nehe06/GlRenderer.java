package ro.brite.android.nehe06;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ro.brite.android.nehe.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;


public class GlRenderer implements Renderer {

	private Context context;
	
	public GlRenderer(Context context)
	{
		this.context = context;
	}
	
	private static float[][] cubeVertexCoords = new float[][] {
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

	private static float[][] cubeTextureCoords = new float[][] {
		new float[] { // top
			1, 0,
			1, 1,
			0, 1,
			0, 0
		},
		new float[] { // bottom
			0, 0,
			1, 0,
			1, 1,
			0, 1
		},
		new float[] { // front
			1, 1,
			0, 1,
			0, 0,
			1, 0
		},
		new float[] { // back
			0, 1,
			0, 0,
			1, 0,
			1, 1
		},
		new float[] { // left
			1, 1,
			0, 1,
			0, 0,
			1, 0
		},
		new float[] { // right
			0, 1,
			0, 0,
			1, 0,
			1, 1
		},
	};
	
	private static FloatBuffer[] cubeVertexBfr;
	private static FloatBuffer[] cubeTextureBfr;
	
	private IntBuffer texturesBuffer;
	
	private static float cubeRotX;
	private static float cubeRotY;
	private static float cubeRotZ;
	
	static
	{
		cubeVertexBfr = new FloatBuffer[6];
		cubeTextureBfr = new FloatBuffer[6];
		for (int i = 0; i < 6; i++)
		{
			cubeVertexBfr[i] = FloatBuffer.wrap(cubeVertexCoords[i]);
			cubeTextureBfr[i] = FloatBuffer.wrap(cubeTextureCoords[i]);
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
		
		// create texture
		gl.glEnable(GL10.GL_TEXTURE_2D);
		texturesBuffer = IntBuffer.allocate(1);
		gl.glGenTextures(1, texturesBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(0));
		
		// setup texture parameters
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		
		// set the texture
		Bitmap texture = Utils.getTextureFromBitmapResource(context, R.drawable.nehe);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);
		texture.recycle();
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// draw cube
		
		gl.glTranslatef(0, 0, -6);
		gl.glRotatef(cubeRotX, 1, 0, 0);
		gl.glRotatef(cubeRotY, 0, 1, 0);
		gl.glRotatef(cubeRotZ, 0, 0, 1);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(0));
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		for (int i = 0; i < 6; i++) // draw each face
		{
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeVertexBfr[i]);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, cubeTextureBfr[i]);
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		// update rotations
		cubeRotX += 1.2f;
		cubeRotY += 0.8f;
		cubeRotZ += 0.6f;
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

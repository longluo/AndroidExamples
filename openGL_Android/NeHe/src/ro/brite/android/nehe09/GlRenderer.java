package ro.brite.android.nehe09;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ro.brite.android.nehe.R;
import ro.brite.android.nehe09.Utils;

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
	
	private static float[] quadVertexCoords = new float[] {
		-1, 1, 0,
		-1,-1, 0,
		 1, 1, 0,
		 1,-1, 0
	};

	private static float[] quadTextureCoords = new float[] {
		0, 1,
		0, 0,
		1, 1,
		1, 0
	};
	
	private static FloatBuffer quadVertexBuffer;
	private static FloatBuffer quadTextureBuffer;
	
	private IntBuffer texturesBuffer;
	
	private static final int nrStars = 30;
	private static Star[] stars;
	
	static float zoom = -20.0f;
	static float tilt = 90.0f;
	private static float spin;
	private static boolean twinkle = true;
	
	static
	{
		quadVertexBuffer = FloatBuffer.wrap(quadVertexCoords);
		quadTextureBuffer = FloatBuffer.wrap(quadTextureCoords);
		
		// setup stars
		stars = new Star[nrStars];
		for (int i = 0; i < nrStars; i++) {
			stars[i] = new Star();
			stars[i].angle = 0;
			stars[i].dist = (((float)i)/nrStars)*5.0f;
			stars[i].r = (float)Math.random();
			stars[i].g = (float)Math.random();
			stars[i].b = (float)Math.random();
		}
	}
	
	private void LoadTextures(GL10 gl) {
		// create textures
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
		Bitmap texture = Utils.getTextureFromBitmapResource(context, R.drawable.star);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);
		
		// free bitmap
		texture.recycle();
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0, 0, 0, 0);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texturesBuffer.get(0));
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		for (int i = 0; i < nrStars; i++)
		{
			gl.glLoadIdentity();
			gl.glTranslatef(0, 0, zoom);					// Zoom Into The Screen (Using The Value In 'zoom')
			gl.glRotatef(tilt, 1, 0, 0);					// Tilt The View (Using The Value In 'tilt')
			
			gl.glRotatef(stars[i].angle, 0, 1, 0);			// Rotate To The Current Stars Angle
			gl.glTranslatef(stars[i].dist, 0, 0);			// Move Forward On The X Plane
			
			gl.glRotatef(-stars[i].angle, 0, 1, 0);			// Cancel The Current Stars Angle
			gl.glRotatef(-tilt, 1, 0, 0);					// Cancel The Screen Tilt

			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quadVertexBuffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, quadTextureBuffer);

			if (twinkle)									// Twinkling Stars Enabled
			{
				gl.glColor4f(stars[(nrStars-i)-1].r, stars[(nrStars-i)-1].g, stars[(nrStars-i)-1].b, 1.0f);
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			}
			gl.glRotatef(spin, 0, 0, 1);					// Rotate The Star On The Z Axis
			gl.glColor4f(stars[i].r, stars[i].g, stars[i].b, 1.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			spin += 0.01f;									// Used To Spin The Stars
			stars[i].angle += ((float)i)/nrStars;			// Changes The Angle Of A Star
			stars[i].dist -= 0.01f;							// Changes The Distance Of A Star

			if (stars[i].dist < 0.0f)						// Is The Star In The Middle Yet
			{
				stars[i].dist += 5.0f;						// Move The Star 5 Units From The Center
				stars[i].r = (float)Math.random();			// Give It A New Red Value
				stars[i].g = (float)Math.random();			// Give It A New Green Value
				stars[i].b = (float)Math.random();			// Give It A New Blue Value
			}
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// reload textures
		LoadTextures(gl);
		// avoid division by zero
		if (height == 0) height = 1;
		// draw on the entire screen
		gl.glViewport(0, 0, width, height);
		// setup projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 1.0f, 100.0f);
	}

	public static void toggleTwinkle() {
		twinkle = !twinkle;
	}
	
}

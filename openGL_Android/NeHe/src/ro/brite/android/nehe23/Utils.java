package ro.brite.android.nehe23;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLUtils;


final class Utils {

	private static Matrix yFlipMatrix;
	
	static {
		yFlipMatrix = new Matrix();
		yFlipMatrix.postScale(1, -1); // flip Y axis
	}
	
	public static Bitmap getTextureFromBitmapResource(Context context, int resourceId) {
		
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
			return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), yFlipMatrix, false);
		}
		finally	{
			if (bitmap != null) {
				bitmap.recycle();
			}
		}
	}	

	public static void generateMipmapsForBoundTexture(Bitmap texture) {
		
		// generate the full texture (mipmap level 0)
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);
		
		Bitmap currentMipmap = texture;
		
		int width = texture.getWidth();
		int height = texture.getHeight();
		int level = 0;
		
		boolean reachedLastLevel;
		do {
			
			// go to next mipmap level
			if (width > 1) width /= 2;
			if (height > 1) height /= 2;
			level++;
			reachedLastLevel = (width == 1 && height == 1);
			
			// generate next mipmap
			Bitmap mipmap = Bitmap.createScaledBitmap(currentMipmap, width, height, true);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, mipmap, 0);
			
			// recycle last mipmap (but don't recycle original texture)
			if (currentMipmap != texture)
			{
				currentMipmap.recycle();
			}
			
			// remember last generated mipmap
			currentMipmap = mipmap;
			
		} while (!reachedLastLevel);
		
		// once again, recycle last mipmap (but don't recycle original texture)
		if (currentMipmap != texture) {
			currentMipmap.recycle();
		}
	}

	public static void setXYZ(float[] vector, int offset, float x, float y, float z) {
		vector[offset] = x;
		vector[offset + 1] = y;
		vector[offset + 2] = z;
	}

	public static void setXYZn(float[] vector, int offset, float x, float y, float z) {
		float r = (float)Math.sqrt(x * x + y * y + z * z);
		setXYZ(vector, offset, x / r, y / r, z / r);
	}
	
	public static void setXY(float[] vector, int offset, float x, float y) {
		vector[offset] = x;
		vector[offset + 1] = y;
	}

	public static void setSphereEnvTexCoords(GlVertex vEye, GlMatrix mInvRot,
			FloatBuffer vertexBuffer, int vertexOffset,
			FloatBuffer normalBuffer, int normalOffset,
			FloatBuffer texCoordBuffer, int texCoordOffset) {
		
		GlVertex vN = getVertex(normalBuffer, normalOffset);
		GlVertex vP = getVertex(vertexBuffer, vertexOffset);
		
		GlVertex vE = new GlVertex(vEye);
		vE.subtract(vP);
		vE.normalize();

		float cos = GlVertex.dotProduct(vE, vN);
		GlVertex vR = new GlVertex(vN);
		vR.scale(2 * cos);
		vR.subtract(vE);
		
		mInvRot.transform(vR);
		
		float p = (float)Math.sqrt(vR.v[0] * vR.v[0] + vR.v[1] * vR.v[1] + (vR.v[2] + 1) * (vR.v[2] + 1));
		float s = (p != 0) ? 0.5f * (vR.v[0] / p + 1) : 0;
		float t = (p != 0) ? 0.5f * (vR.v[1] / p + 1) : 0;
		
		texCoordBuffer.put(texCoordOffset, s);
		texCoordBuffer.put(texCoordOffset + 1, t);
	}
	
	public static GlVertex getVertex(FloatBuffer buffer, int index) {
		float x = buffer.get(index);
		float y = buffer.get(index + 1);
		float z = buffer.get(index + 2);
		return new GlVertex(x, y, z);
	}
	
}

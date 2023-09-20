package ro.brite.android.nehe07;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLUtils;


final class Utils {

	private static Matrix yFlipMatrix;
	
	static
	{
		yFlipMatrix = new Matrix();
		yFlipMatrix.postScale(1, -1); // flip Y axis
	}
	
	public static Bitmap getTextureFromBitmapResource(Context context, int resourceId)
	{
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

	public static void generateMipmapsForBoundTexture(Bitmap texture)
	{
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
		if (currentMipmap != texture)
		{
			currentMipmap.recycle();
		}
	}
	
}

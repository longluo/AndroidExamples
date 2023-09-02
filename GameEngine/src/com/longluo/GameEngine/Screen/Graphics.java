package com.longluo.GameEngine.Screen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Graphics {
	public static void fillRect(Canvas g, Rect rect, Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		g.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
	}

	public static void drawRect(Canvas g, Rect rect, Paint paint) {
		paint.setStyle(Paint.Style.STROKE);
		g.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
	}

	public static void SETAEERECT(Rect rect, int x, int y, int w, int h) {
		rect.left = x;
		rect.top = y;
		rect.right = x + w;
		rect.bottom = y + h;
	}

	public static void fillRect(Canvas g, int x, int y, int w, int h,
			Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		g.drawRect(x, y, x + w, y + h, paint);
	}

	public static void drawRect(Canvas g, int x, int y, int w, int h,
			Paint paint) {
		paint.setStyle(Paint.Style.STROKE);
		g.drawRect(x, y, x + w, y + h, paint);
	}

	/*------------------------------------
	 * 绘制图片
	 *
	 * @param		x 屏幕上的x坐标	
	 * @param		y 屏幕上的y坐标
	 * @param		w 要绘制的图片的宽度	
	 * @param		h 要绘制的图片的高度
	 * @param		bx图片上的x坐标
	 * @param		by图片上的y坐标
	 *
	 * @return		null
	 ------------------------------------*/
	public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,
			int w, int h, int bx, int by) {
		Rect src = new Rect();// 图片
		Rect dst = new Rect();// 屏幕
		src.left = bx;
		src.top = by;
		src.right = bx + w;
		src.bottom = by + h;
		dst.left = x;
		dst.top = y;
		dst.right = x + w;
		dst.bottom = y + h;
		canvas.drawBitmap(blt, src, dst, null);

		src = null;
		dst = null;
	}

	public static void drawImage(Canvas canvas, Bitmap bitmap, int x, int y) {
		canvas.drawBitmap(bitmap, x, y, null);
	}
}

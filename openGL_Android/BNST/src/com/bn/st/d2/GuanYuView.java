package com.bn.st.d2;

import static com.bn.st.xc.Constant.ratio_height;
import static com.bn.st.xc.Constant.ratio_width;
import static com.bn.st.xc.Constant.scaleToFit;
import com.bn.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GuanYuView extends MySFView
{
	MyActivity activity;		//Activity引用
	Canvas c;					//画布的引用	
	SurfaceHolder holder;		//SurfaceView锁的引用
    Bitmap background;			//背景图
	Bitmap back;
	Bitmap back_press;
    private float button_back_x=20f*ratio_width;//back图片按钮的左上角的点的坐标
	private float button_back_y=415f*ratio_height;
	
	boolean back_flag=false;
	boolean flag=true;
    
	public GuanYuView(Context context) 
	{
		this.activity = (MyActivity) context;//初始化activity的引用
		initBitmap();			//初始化图片
	}
	
	//将图片加载
	public void initBitmap()
	{
		background = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.guanyu),ratio_width,ratio_height);//菜单界面背景图片
		back = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.back),ratio_width,ratio_height);//上一页按钮图片
		back_press = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.back_press),ratio_width,ratio_height);//上一页按钮图片
	}
	
	@Override
	public void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		canvas.drawColor(Color.argb(255, 0, 0, 0));//清屏为黑色
		canvas.drawBitmap(background,0,0, null);//画背景   
		if(!back_flag)
		{
			canvas.drawBitmap(back, button_back_x, button_back_y, null);//绘制back按钮 
		}else
		{
			canvas.drawBitmap(back_press, button_back_x, button_back_y, null);//绘制back按钮 
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		int x=(int)e.getX();
		int y=(int)e.getY();
		switch(e.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(x>button_back_x&&x<button_back_x+back.getWidth()&&y>button_back_y&&y<button_back_y+back.getHeight())
			{//返回按钮
				back_flag=true;
			}  
			break;
		case MotionEvent.ACTION_UP:
			back_flag=false;
			if(x>button_back_x&&x<button_back_x+back.getWidth()&&y>button_back_y&&y<button_back_y+back.getHeight())
			{//返回按钮
				activity.hd.sendEmptyMessage(1);
			}  
			break;
		}
		return true;
	}
}

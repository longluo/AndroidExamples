package com.bn.st.d2;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import static com.bn.st.xc.Constant.*;

import com.bn.R;

public class GameModeView extends MySFView
{
	MyActivity activity;		//Activity引用
	Canvas c;					//画布的引用	
	SurfaceHolder holder;		//SurfaceView锁的引用
    Bitmap background;			//背景图
	Bitmap back;				//返回按钮图片
    
    Bitmap timer_mode;			//计时模式图片
    Bitmap speed_mode;			//竞速模式图片
    Bitmap record_select;   	//记录查询图片
    
    Bitmap timer_mode_press;	//计时模式按下图片
    Bitmap speed_mode_press;	//竞速模式按下图片
    Bitmap record_select_press;	//记录查询按下图片
    Bitmap back_press;
    
    private float timer_mode_x;//按钮图片的左上角X坐标
    private float timer_mode_y;//按钮图片的左上角X坐标
    private float speed_mode_x;//按钮图片的左上角Y坐标
    private float speed_mode_y;//按钮图片的左上角Y坐标
    private float record_select_x;//按钮图片的左上角Y坐标
    private float record_select_y;//按钮图片的左上角Y坐标 
    private float button_back_x=20f*ratio_width;//back图片按钮的左上角的点的坐标
	private float button_back_y=415f*ratio_height;
	
	boolean time_flag=false;
	boolean speed_flag=false;
	boolean record_flag=false;
	boolean back_flag=false;
    
    public boolean flag_go=true;
    int move_flag=1;		//0---不移动   -1---向两侧移动    1---往中间移动
    float move_span=MOVE_V;//按钮移动速度

	public GameModeView(Context context) 
	{
		this.activity = (MyActivity) context;//初始化activity的引用
		initBitmap();			//初始化图片
	}
	
	public void initThread()
	{
		time_flag=false;
		speed_flag=false;
		record_flag=false;
		back_flag=false;	    
	    flag_go=true;
	    move_flag=1;		//0---不移动   -1---向两侧移动    1---往中间移动
	    
	    timer_mode_x=-timer_mode.getWidth();//计时模式图片的初始左上角位置
		timer_mode_y=190*ratio_height;
		speed_mode_x=SCREEN_WIDTH;			//竞速模式图片的初始左上角位置
		speed_mode_y=275*ratio_height;
		record_select_x=timer_mode_x;		//记录查询图片的初始左上角位置
		record_select_y=360*ratio_height;
		
		
		new Thread()
		{
			@Override
			public void run()
			{    
				while(flag_go)
				{
					if(move_flag==1)
					{
						timer_mode_x=timer_mode_x+move_span*ratio_width;
						speed_mode_x=speed_mode_x-move_span*ratio_width;
						record_select_x=timer_mode_x;
						if(timer_mode_x>=(SCREEN_WIDTH-timer_mode.getWidth())*0.5f)
						{
							timer_mode_x=(SCREEN_WIDTH-timer_mode.getWidth())*0.5f;
							speed_mode_x=timer_mode_x;
							record_select_x=timer_mode_x;
							move_flag=0;
						}
					}
					else if(move_flag==-1)
					{
						timer_mode_x=timer_mode_x-move_span*ratio_width;
						speed_mode_x=speed_mode_x+move_span*ratio_width;
						record_select_x=timer_mode_x;
						if(timer_mode_x<=-timer_mode.getWidth())
						{
							timer_mode_x=-timer_mode.getWidth();
							speed_mode_x=SCREEN_WIDTH;
							record_select_x=timer_mode_x;
							move_flag=0;
							flag_go=false;
							activity.hd.sendEmptyMessage(1);
						}
					}
					try
					{
						Thread.sleep(MOVE_TIME);//休眠200毫秒
					}catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}				
			}
		}.start();
	}
	   
	//将图片加载
	public void initBitmap()
	{
		background = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.background),ratio_width,ratio_height);//菜单界面背景图片
		back = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.back),ratio_width,ratio_height);//上一页按钮图片
		timer_mode = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.timer_mode),ratio_width,ratio_height);//
		speed_mode = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.speed_mode),ratio_width,ratio_height);//
		record_select = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.record_select),ratio_width,ratio_height);//	
		
		timer_mode_press = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.timer_mode_press),ratio_width,ratio_height);//
		speed_mode_press = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.speed_mode_press),ratio_width,ratio_height);//
		record_select_press = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.record_select_press),ratio_width,ratio_height);//
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
		if(!time_flag)
		{
			canvas.drawBitmap(timer_mode, timer_mode_x, timer_mode_y, null);
		}else
		{
			canvas.drawBitmap(timer_mode_press, timer_mode_x, timer_mode_y, null);
		}
		if(!speed_flag)
		{
			canvas.drawBitmap(speed_mode, speed_mode_x, speed_mode_y, null);
		}else
		{
			canvas.drawBitmap(speed_mode_press, speed_mode_x, speed_mode_y, null);
		}
		if(!record_flag)
		{
			canvas.drawBitmap(record_select, record_select_x, record_select_y, null);
		}else
		{
			canvas.drawBitmap(record_select_press, record_select_x, record_select_y, null);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int x = (int) event.getX();//获取触控点的X坐标
		int y = (int) event.getY();//获取触控点的Y坐标
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN://按下事件
			if(x>timer_mode_x&&x<timer_mode_x+timer_mode.getWidth()&&y>timer_mode_y&&y<timer_mode_y+timer_mode.getHeight())
			{
				time_flag=true;
			}else if(x>speed_mode_x&&x<speed_mode_x+speed_mode.getWidth()&&y>speed_mode_y&&y<speed_mode_y+speed_mode.getHeight())
			{
				speed_flag=true;
			}else if(x>record_select_x&&x<record_select_x+record_select.getWidth()&&y>record_select_y&&y<record_select_y+record_select.getHeight())
			{
				record_flag=true;
			}else if(x>button_back_x&&x<button_back_x+back.getWidth()&&y>button_back_y&&y<button_back_y+back.getHeight())
			{//返回按钮
    			back_flag=true;
			}
			break;
		case MotionEvent.ACTION_UP://抬起事件
			time_flag=false;
			speed_flag=false;
			record_flag=false;
			back_flag=false;  
			if(x>timer_mode_x&&x<timer_mode_x+timer_mode.getWidth()&&y>timer_mode_y&&y<timer_mode_y+timer_mode.getHeight())
			{
				flag_go=false;
				activity.hd.sendEmptyMessage(8);
			}else if(x>speed_mode_x&&x<speed_mode_x+speed_mode.getWidth()&&y>speed_mode_y&&y<speed_mode_y+speed_mode.getHeight())
			{
				flag_go=false;
				activity.hd.sendEmptyMessage(9);
			}else if(x>record_select_x&&x<record_select_x+record_select.getWidth()&&y>record_select_y&&y<record_select_y+record_select.getHeight())
			{
				flag_go=false;
				activity.hd.sendEmptyMessage(10);				
			}else if(x>button_back_x&&x<button_back_x+back.getWidth()&&y>button_back_y&&y<button_back_y+back.getHeight())
			{//返回按钮
    			move_flag=-1;
			}
			break;
		}
		return true;
	}
}

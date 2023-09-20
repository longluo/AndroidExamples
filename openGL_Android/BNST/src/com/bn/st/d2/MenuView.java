package com.bn.st.d2;			//声明包语句

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import static com.bn.st.xc.Constant.*;

import com.bn.R;

public class MenuView extends MySFView
{
	MyActivity activity;		//Activity引用
	Canvas c;					//画布的引用	
	SurfaceHolder holder;		//SurfaceView锁的引用
    Bitmap background;			//背景图
    
    Bitmap button_play;			//进入游戏按钮图片
    Bitmap button_chooseboat;	//选船游戏按钮图片
    Bitmap button_soundset;		//音效设置按钮图片
    Bitmap button_help;			//游戏帮助按钮图片
    Bitmap button_about;		//关于按钮图片
    Bitmap button_exit;			//退出游戏按钮图片
    
    Bitmap button_play_press;			//进入游戏按钮图片
    Bitmap button_chooseboat_press;	//选船游戏按钮图片
    Bitmap button_soundset_press;		//音效设置按钮图片
    Bitmap button_help_press;			//游戏帮助按钮图片
    Bitmap button_about_press;		//关于按钮图片
    Bitmap button_exit_press;			//退出游戏按钮图片
    
    private float button_play_x;//按钮图片的左上角X坐标
    private float button_play_y;//按钮图片的左上角X坐标
    private float button_chooseboat_x;//按钮图片的左上角Y坐标
    private float button_chooseboat_y;//按钮图片的左上角Y坐标
    private float button_soundset_x;//按钮图片的左上角Y坐标
    private float button_soundset_y;//按钮图片的左上角Y坐标
    private float button_help_x;//按钮图片的右上角X坐标
    private float button_help_y;//按钮图片的右上角X坐标
    private float button_about_x;//按钮图片的右上角Y坐标
    private float button_about_y;//按钮图片的右上角Y坐标
    private float button_exit_x;//按钮图片的右上角Y坐标
    private float button_exit_y;//按钮图片的右上角Y坐标
    
    boolean play_flag=false;
    boolean chooseboat_flag=false;
    boolean soundset_flag=false;
    boolean help_flag=false;
    boolean about_flag=false;
    boolean exit_flag=false;
    
    private boolean flag_go=true;
    int move_flag=1;		//0---不移动   -1---向两侧移动    1---往中间移动
    float move_span=MOVE_V;//按钮移动速度
    int curr_menuId=0;//自定义的菜单按钮编号
	public MenuView(MyActivity activity)  
	{		
		this.activity = activity;//初始化activity的引用
		initBitmap();			//初始化图片
		if(Build.VERSION.SDK_INT<Build.VERSION_CODES.FROYO)
		{
			activity.showDialog(2);
		}
		else if(activity.getGLVersion()<2)   
		{
			activity.showDialog(1);
		}
	}
	
	public void initThread()
	{
		move_flag=1;
		button_play_x=-button_play.getWidth();
		button_play_y=190*ratio_height;
		button_chooseboat_x=SCREEN_WIDTH;
		button_chooseboat_y=button_play_y;
		
		button_soundset_x=button_play_x;
		button_soundset_y=275*ratio_height;
		button_help_x=button_chooseboat_x;
		button_help_y=button_soundset_y;
		
		button_about_x=button_play_x;
		button_about_y=360*ratio_height;
		button_exit_x=button_chooseboat_x;
		button_exit_y=button_about_y;
		flag_go=true;
		new Thread()
		{
			{
				this.setName("menuview thread");
			}
			@Override  
			public void run()
			{  
				while(flag_go)
				{
					if(move_flag==1)//移动标志位为真
					{
						button_play_x=button_play_x+move_span*ratio_width;
						button_chooseboat_x=button_chooseboat_x-move_span*ratio_width;
						button_soundset_x=button_play_x;
						button_help_x=button_chooseboat_x;
						button_about_x=button_play_x;
						button_exit_x=button_chooseboat_x;
						if(button_play_x>=200*ratio_width)
						{
							button_play_x=200*ratio_width;
							button_chooseboat_x=485*ratio_width;
							button_soundset_x=button_play_x;
							button_help_x=button_chooseboat_x;
							button_about_x=button_play_x;
							button_exit_x=button_chooseboat_x;
							move_flag=0;
						}
					}
					else if(move_flag==-1)
					{
						button_play_x=button_play_x-move_span*ratio_width;
						button_chooseboat_x=button_chooseboat_x+move_span*ratio_width;
						button_soundset_x=button_play_x;
						button_help_x=button_chooseboat_x;
						button_about_x=button_play_x;
						button_exit_x=button_chooseboat_x;
						if(button_play_x<=-button_play.getWidth())
						{
							button_play_x=-button_play.getWidth();
							button_chooseboat_x=SCREEN_WIDTH;
							button_soundset_x=button_play_x;
							button_help_x=button_chooseboat_x;
							button_about_x=button_play_x;
							button_exit_x=button_chooseboat_x;
							move_flag=0;
							flag_go=false;
							MenuView.this.activity.hd.sendEmptyMessage(curr_menuId);
						}
					}
					
					try
					{
						Thread.sleep(MOVE_TIME);//休眠200毫秒
					}
					catch(InterruptedException e)
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
		
		button_play = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.play),ratio_width,ratio_height);//进入游戏按钮
		button_chooseboat = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.chooseboat),ratio_width,ratio_height);//选船按钮
		button_soundset = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.soundset),ratio_width,ratio_height);//音效设置按钮
		button_help = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.gamehelp),ratio_width,ratio_height);//游戏帮助按钮
		button_about = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.about),ratio_width,ratio_height);//游戏帮助按钮
		button_exit = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.exit),ratio_width,ratio_height);//设置按钮
		
		button_play_press = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.play_press ),ratio_width,ratio_height);//进入游戏按钮
		button_chooseboat_press  = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.chooseboat_press ),ratio_width,ratio_height);//选船按钮
		button_soundset_press  = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.soundset_press ),ratio_width,ratio_height);//音效设置按钮
		button_help_press  = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.gamehelp_press ),ratio_width,ratio_height);//游戏帮助按钮
		button_about_press  = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.about_press ),ratio_width,ratio_height);//游戏帮助按钮
		button_exit_press  = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.exit_press ),ratio_width,ratio_height);//设置按钮
	}
	@Override
	public void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		canvas.drawColor(Color.argb(255, 0, 0, 0));//清屏为黑色
		canvas.drawBitmap(background,0,0, null);//画背景
		
		if(!play_flag)
		{
			canvas.drawBitmap(button_play, button_play_x, button_play_y, null);
		}else
		{
			canvas.drawBitmap(button_play_press, button_play_x, button_play_y, null);
		}
		if(!chooseboat_flag)
		{
			canvas.drawBitmap(button_chooseboat, button_chooseboat_x, button_chooseboat_y, null);
		}else
		{
			canvas.drawBitmap(button_chooseboat_press, button_chooseboat_x, button_chooseboat_y, null);
		}
		if(!soundset_flag)
		{
			canvas.drawBitmap(button_soundset, button_soundset_x, button_soundset_y, null);
		}else
		{
			canvas.drawBitmap(button_soundset_press, button_soundset_x, button_soundset_y, null);
		}
		if(!help_flag)
		{
			canvas.drawBitmap(button_help, button_help_x, button_help_y, null);
		}else
		{
			canvas.drawBitmap(button_help_press, button_help_x, button_help_y, null);
		}
		if(!about_flag)
		{
			canvas.drawBitmap(button_about, button_about_x, button_about_y, null);
		}else
		{
			canvas.drawBitmap(button_about_press, button_about_x, button_about_y, null);
		}
		if(!exit_flag)
		{
			canvas.drawBitmap(button_exit, button_exit_x, button_exit_y, null);
		}
		else
		{
			canvas.drawBitmap(button_exit_press, button_exit_x, button_exit_y, null);
		}
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int x = (int) event.getX();//获取触控点的X坐标
		int y = (int) event.getY();//获取触控点的Y坐标
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN://按下事件，按下换图
			if(move_flag==0&&x>button_play_x&&x<button_play_x+button_play.getWidth()&&y>button_play_y&&y<button_play_y+button_play.getHeight())
			{//进入游戏
				play_flag=true;
				
			}else if(move_flag==0&&x>button_chooseboat_x&&x<button_chooseboat_x+button_chooseboat.getWidth()&&y>button_chooseboat_y&&y<button_chooseboat_y+button_chooseboat.getHeight())
			{//选船
				chooseboat_flag=true;
			}else if(move_flag==0&&x>button_soundset_x&&x<button_soundset_x+button_soundset.getWidth()&&y>button_soundset_y&&y<button_soundset_y+button_soundset.getHeight())
			{//音效设置
				soundset_flag=true;
			}else if(move_flag==0&&x>button_help_x&&x<button_help_x+button_help.getWidth()&&y>button_help_y&&y<button_help_y+button_help.getHeight())
			{//游戏帮助
				help_flag=true;
			}else if(move_flag==0&&x>button_about_x&&x<button_about_x+button_about.getWidth()&&y>button_about_y&&y<button_about_y+button_about.getHeight())
			{//关于
				 about_flag=true;
			}else if(move_flag==0&&x>button_exit_x&&x<button_exit_x+button_exit.getWidth()&&y>button_exit_y&&y<button_exit_y+button_exit.getHeight())
			{//退出
				exit_flag=true;
			}
			break;
		case MotionEvent.ACTION_UP://抬起事件
			play_flag=false;
			chooseboat_flag=false;
			soundset_flag=false;
			help_flag=false;
			about_flag=false;
			exit_flag=false;
			if(move_flag==0&&x>button_play_x&&x<button_play_x+button_play.getWidth()&&y>button_play_y&&y<button_play_y+button_play.getHeight())
			{//进入游戏
				
				curr_menuId=2;
				move_flag=-1;  
			}else if(move_flag==0&&x>button_chooseboat_x&&x<button_chooseboat_x+button_chooseboat.getWidth()&&y>button_chooseboat_y&&y<button_chooseboat_y+button_chooseboat.getHeight())
			{//选船
				
				curr_menuId=3;
				move_flag=-1; 
			}else if(move_flag==0&&x>button_soundset_x&&x<button_soundset_x+button_soundset.getWidth()&&y>button_soundset_y&&y<button_soundset_y+button_soundset.getHeight())
			{//音效设置
				
				curr_menuId=4;
				move_flag=-1; 
			}else if(move_flag==0&&x>button_help_x&&x<button_help_x+button_help.getWidth()&&y>button_help_y&&y<button_help_y+button_help.getHeight())
			{//游戏帮助
				
				curr_menuId=5;
				move_flag=-1; 
			}else if(move_flag==0&&x>button_about_x&&x<button_about_x+button_about.getWidth()&&y>button_about_y&&y<button_about_y+button_about.getHeight())
			{//关于
				   
				curr_menuId=6;
				move_flag=-1; 
			}else if(move_flag==0&&x>button_exit_x&&x<button_exit_x+button_exit.getWidth()&&y>button_exit_y&&y<button_exit_y+button_exit.getHeight())
			{//退出
				Settings.System.putInt(activity.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,activity.flag);
				System.exit(0);
			}
			break;
		}
		return true;
	}
}
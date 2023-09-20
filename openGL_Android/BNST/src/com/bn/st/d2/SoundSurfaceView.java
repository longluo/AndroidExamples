package com.bn.st.d2;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import static com.bn.st.xc.Constant.*;
import com.bn.R;

public class SoundSurfaceView extends MySFView
{
	MyActivity activity;
	Canvas c;
	SurfaceHolder holder;
	Bitmap background;				//背景图
	Bitmap back;
	Bitmap back_press;
	
	Bitmap button_bgsound;			//背景音乐
	Bitmap button_bgsound_open;		//背景音乐开图片按钮
	Bitmap button_bgsound_close;	//背景音乐关图片按钮

	Bitmap button_gameeffect;		//游戏音效
	Bitmap button_gameeffect_open;	//游戏音效开按钮
	Bitmap button_gameeffect_close;	//游戏音效关按钮
	
	private float button_bgsound_x;//背景音乐图片的左上角X坐标
	private float button_bgsound_y;//背景音乐图片的左上角Y坐标
    private float button_bgsound_open_x;//背景音乐开图片的左上角X坐标
    private float button_bgsound_open_y;//背景音乐关图片的左上角Y坐标
    
    private float button_gameeffect_x;//游戏音效图片的左上角X坐标
    private float button_gameeffect_y;//游戏音效图片的左上角Y坐标
    private float button_gameeffect_open_x;//游戏音效开图片的左上角X坐标
    private float button_gameeffect_open_y;//游戏音效开图片的左上角Y坐标
    private float button_back_x=20f*ratio_width;//back图片按钮的左上角的点的坐标
	private float button_back_y=415f*ratio_height;
    
    public boolean flag_go=true;
    int move_flag=1;		//0---不移动   -1---向两侧移动    1---往中间移动
    float move_span=MOVE_V;//按钮移动速度
    
    boolean back_flag=false;
    
	public SoundSurfaceView(MyActivity activity) 
	{
		this.activity = activity;
		initBitmap();					//初始化图片
		
		button_bgsound_x=-button_bgsound.getWidth();
		button_bgsound_y=222f*ratio_height;
		button_bgsound_open_x=SCREEN_WIDTH;
		button_bgsound_open_y=button_bgsound_y;
		
		button_gameeffect_x=button_bgsound_x;
		button_gameeffect_y=307f*ratio_height;
		button_gameeffect_open_x=button_bgsound_open_x;
		button_gameeffect_open_y=button_gameeffect_y;
	}
	
	public void initThread()
	{
		flag_go=true;
	    move_flag=1;		//0---不移动   -1---向两侧移动    1---往中间移动
		
		button_bgsound_x=-button_bgsound.getWidth();
		button_bgsound_y=222f*ratio_height;
		button_bgsound_open_x=SCREEN_WIDTH;
		button_bgsound_open_y=button_bgsound_y;
		
		button_gameeffect_x=button_bgsound_x;
		button_gameeffect_y=307f*ratio_height;
		button_gameeffect_open_x=button_bgsound_open_x;
		button_gameeffect_open_y=button_gameeffect_y;
		
		new Thread()
		{
			@Override
			public void run()
			{  
				while(flag_go)//移动标志位为真
				{
					if(move_flag==1)   
					{  
						//更改两幅按钮图片的左上角点的X坐标
						button_bgsound_x=button_bgsound_x+move_span*ratio_width;
						button_bgsound_open_x=button_bgsound_open_x-move_span*ratio_width;
						button_gameeffect_x=button_bgsound_x;
						button_gameeffect_open_x=button_bgsound_open_x;
						//当坐标值达到临界值时
						if(button_bgsound_x>=200*ratio_width)
						{    
							//得到此时的X坐标
							button_bgsound_x=200*ratio_width;
							button_bgsound_open_x=485*ratio_width;
							button_gameeffect_x=button_bgsound_x;
							button_gameeffect_open_x=button_bgsound_open_x; 
							move_flag=0;//将移动标志位置为0
						}
					}
					else if(move_flag==-1)   
					{
						//更改两幅按钮图片的左上角点的X坐标             
						button_bgsound_x=button_bgsound_x-move_span*ratio_width;
						button_bgsound_open_x=button_bgsound_open_x+move_span*ratio_width;
						button_gameeffect_x=button_bgsound_x;
						button_gameeffect_open_x=button_bgsound_open_x;
						if(button_bgsound_x<=-button_bgsound.getWidth())
						{
							button_bgsound_x=-button_bgsound.getWidth();
							button_bgsound_open_x=SCREEN_WIDTH;
							button_gameeffect_x=button_bgsound_x;
							button_gameeffect_open_x=button_bgsound_open_x;
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
		back_press = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.back_press),ratio_width,ratio_height);//上一页按钮图片
		button_bgsound=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.bg_music),ratio_width,ratio_height);//背景图片
		button_bgsound_open=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.open),ratio_width,ratio_height);//背景音乐开图片
		button_bgsound_close=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.close),ratio_width,ratio_height);//背景音乐关图片
		
		button_gameeffect=scaleToFit(BitmapFactory.decodeResource(activity.getResources(),R.drawable.game_music),ratio_width,ratio_height);//游戏音效图片
		button_gameeffect_open=scaleToFit(BitmapFactory.decodeResource(activity.getResources(),R.drawable.open),ratio_width,ratio_height);//游戏音效开图片
		button_gameeffect_close=scaleToFit(BitmapFactory.decodeResource(activity.getResources(),R.drawable.close),ratio_width,ratio_height);//游戏音效关图片
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
		canvas.drawBitmap(button_bgsound, button_bgsound_x, button_bgsound_y, null);//绘制背景音乐图片
		canvas.drawBitmap(button_gameeffect, button_gameeffect_x, button_gameeffect_y, null);//绘制游戏音效图片
		if(com.bn.clp.Constant.BgSoundFlag)//根据背景音乐的标志位来绘制图片  
		{
			canvas.drawBitmap(button_bgsound_open, button_bgsound_open_x, button_bgsound_open_y, null);
		}
		else if(!com.bn.clp.Constant.BgSoundFlag)//背景音乐关闭
		{
			canvas.drawBitmap(button_bgsound_close, button_bgsound_open_x, button_bgsound_open_y, null);
		}
		if(com.bn.clp.Constant.SoundEffectFlag)//游戏音效打开  
		{
			canvas.drawBitmap(button_gameeffect_open, button_gameeffect_open_x, button_gameeffect_open_y, null);
		}
		else if(!com.bn.clp.Constant.SoundEffectFlag)//游戏关闭
		{
			canvas.drawBitmap(button_gameeffect_close, button_gameeffect_open_x, button_gameeffect_open_y, null);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		int x = (int) event.getX();
		int y = (int) event.getY();				
    	switch(event.getAction())
    	{
    	case MotionEvent.ACTION_DOWN:
    		if(x>button_bgsound_open_x&&x<button_bgsound_open_x+button_bgsound_open.getWidth()&&
    					y>button_bgsound_open_y&&y<button_bgsound_open_y+button_bgsound_open.getHeight())
			{//背景音乐开关图片，点击换图
    			
			}
    		else if(x>button_gameeffect_open_x&&x<button_gameeffect_open_x+button_gameeffect_open.getWidth()&&
    				y>button_gameeffect_open_y&&y<button_gameeffect_open_y+button_gameeffect_open.getHeight())
			{//游戏音效开关图片，点击换图
    			
			}
    		else if(x>button_back_x&&x<button_back_x+back.getWidth()&&y>button_back_y&&y<button_back_y+back.getHeight())
			{//返回按钮
    			back_flag=true;
			}
    		break;
    	case MotionEvent.ACTION_UP:
    		back_flag=false;
    		if(x>button_bgsound_open_x&&x<button_bgsound_open_x+button_bgsound_open.getWidth()&&
    					y>button_bgsound_open_y&&y<button_bgsound_open_y+button_bgsound_open.getHeight())
			{
    	        //向SharedPreferences中写回本次修改信息
    	        SharedPreferences.Editor editor=activity.sp.edit();
    	        editor.putBoolean("bgSoundFlag", !com.bn.clp.Constant.BgSoundFlag);
    	        editor.commit();
    	        
    			com.bn.clp.Constant.BgSoundFlag=!com.bn.clp.Constant.BgSoundFlag;
    			if(com.bn.clp.Constant.BgSoundFlag)
    			{
    				//Toast.makeText(activity, "背景音乐开", Toast.LENGTH_SHORT).show();
    			}
    			else
    			{
    				//Toast.makeText(activity, "背景音乐关", Toast.LENGTH_SHORT).show();
    			}
			}
    		else if(x>button_gameeffect_open_x&&x<button_gameeffect_open_x+button_gameeffect_open.getWidth()&&
    				y>button_gameeffect_open_y&&y<button_gameeffect_open_y+button_gameeffect_open.getHeight())
			{
    			//向SharedPreferences中写回本次修改信息
    	        SharedPreferences.Editor editor=activity.sp.edit();
    	        editor.putBoolean("soundEffectFlag", !com.bn.clp.Constant.SoundEffectFlag);
    	        editor.commit();
    	        
    			com.bn.clp.Constant.SoundEffectFlag=!com.bn.clp.Constant.SoundEffectFlag;
    			if(com.bn.clp.Constant.SoundEffectFlag)
    			{
    				//Toast.makeText(activity, "游戏音效开", Toast.LENGTH_SHORT).show();
    			}  
    			else
    			{
    				//Toast.makeText(activity, "游戏音效关", Toast.LENGTH_SHORT).show();
    			}
			}
    		else if(x>button_back_x&&x<button_back_x+back.getWidth()&&y>button_back_y&&y<button_back_y+back.getHeight())
			{//返回按钮
    			move_flag=-1;
			}
    		break;
    	}
		return true;
	}


	//缩放图片的方法
	public static Bitmap scaleToFit(Bitmap bm,float width_Ratio,float height_Ratio)
	{		
    	int width = bm.getWidth(); 						//图片宽度
    	int height = bm.getHeight();					//图片高度
    	Matrix matrix = new Matrix(); 
    	matrix.postScale((float)width_Ratio, (float)height_Ratio);//图片等比例缩小为原来的fblRatio倍
    	Bitmap bmResult = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);//声明位图        	
    	return bmResult;								//返回被缩放的图片
    }
}

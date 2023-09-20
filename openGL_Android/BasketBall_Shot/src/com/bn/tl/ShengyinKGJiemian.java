package com.bn.tl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import static com.bn.tl.Constant.*;
//绘制声音界面
public class ShengyinKGJiemian extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	Canvas canvas;//画布
	SurfaceHolder holder;
	Paint paint;
	Bitmap beijing;//背景图片
	Bitmap isnoChangjing;//是否播放场景音乐图片
	Bitmap isnoBeijing;//是否播放背景音乐图片
	Bitmap baiseFangfe;//白色方格
	Bitmap honGou;//红色勾
	
	Bitmap fanHui;//返回按钮
	Bitmap queDing;//确定按钮
	boolean isnoFanhui;//是否按下时在返回按钮上
	boolean isnoQueDing;//是否按下时在确定按钮上
	public ShengyinKGJiemian(BasketBall_Shot_Activity activity) 
	{
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint();
		paint.setAntiAlias(true);
		initBitmap();
	}
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawBitmap(beijing, sXtart, sYtart, paint);//背景
		canvas.drawBitmap(isnoBeijing, sXtart+80*ratio_width, sYtart+300*ratio_height, paint);//是否播放背景音乐文字
		canvas.drawBitmap(isnoChangjing, sXtart+80*ratio_width, sYtart+420*ratio_height, paint);//是否播放场景音乐文字
		
		canvas.drawBitmap(baiseFangfe, sXtart+350*ratio_width, sYtart+310*ratio_height, paint);//白色方格
		canvas.drawBitmap(baiseFangfe, sXtart+350*ratio_width, sYtart+430*ratio_height, paint);//白色方格
		if(isBJmiusic){
			canvas.drawBitmap(honGou, sXtart+350*ratio_width, sYtart+310*ratio_height, paint);//红色勾
		}
		if(isCJmiusic){
			canvas.drawBitmap(honGou, sXtart+350*ratio_width, sYtart+430*ratio_height, paint);//红色勾
		}
		
		
		
		
		if(isnoFanhui){
			canvas.drawBitmap(scaleToFit(fanHui,1.2f,1.2f), sXtart+225*ratio_width, sYtart+572*ratio_height, null);//确定按钮
		}else{
			canvas.drawBitmap(fanHui, sXtart+250*ratio_width, sYtart+580*ratio_height, null);//确定按钮
		}
		if(isnoQueDing){
			canvas.drawBitmap(scaleToFit(queDing,1.2f,1.2f), sXtart+55*ratio_width, sYtart+572*ratio_height, null);//返回按钮
		}else{
			canvas.drawBitmap(queDing, sXtart+70*ratio_width, sYtart+580*ratio_height, null);//返回按钮
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		float x=e.getX();
		float y=e.getY();
		switch(e.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(x>sXtart+50*ratio_width&&x<sXtart+70*ratio_width+180*ratio_width&&y>sYtart+580*ratio_height&&y<sYtart+640*ratio_height){//确定按钮
				isnoQueDing=true;
			}else{
				isnoQueDing=false;
			}
			if(x>sXtart+250*ratio_width&&x<sXtart+250*ratio_width+160*ratio_width&&y>sYtart+580*ratio_height&&y<sYtart+640*ratio_height){//确定按钮
				isnoFanhui=true;
			}else{
				isnoFanhui=false;
			}
			onDrawcanvas();
			break;
		case MotionEvent.ACTION_UP:
			
			if(isnoQueDing&&x>sXtart+70*ratio_width&&x<sXtart+70*ratio_width+160*ratio_width&&y>sYtart+580*ratio_height&&y<sYtart+640*ratio_height){//返回按钮
				isnoQueDing=false;//确定
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//返回到菜单界面
			}else{
				isnoQueDing=false;
			}
			if(isnoFanhui&&x>sXtart+250*ratio_width&&x<sXtart+250*ratio_width+160*ratio_width&&y>sYtart+580*ratio_height&&y<sYtart+640*ratio_height){//确定按钮
				isnoFanhui=false;//返回
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//返回到菜单界面
			}else{
				isnoFanhui=false;
			}
			
			if(x>=sXtart+340*ratio_width&&x<=sXtart+400*ratio_width&&y>=sYtart+300*ratio_height&&y<=sYtart+360*ratio_height)
			{
				if(isBJmiusic){
					
					activity.beijingyinyue.stop();
					activity.beijingyinyue=null;
				}
				else{
					if(activity.beijingyinyue==null){
						activity.beijingyinyue=MediaPlayer.create(activity,R.raw.beijingyingyu);
						activity.beijingyinyue.setLooping(true);
						activity.beijingyinyue.setVolume(0.2f, 0.2f);
					}
				}
				isBJmiusic=!isBJmiusic;
			}
			else if(x>=sXtart+340*ratio_width&&x<=sXtart+400*ratio_width&&y>=sYtart+420*ratio_height&&y<=sYtart+480*ratio_height)
			{
				
				isCJmiusic=!isCJmiusic;
			}
			onDrawcanvas();
		}
		return true;		
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3){
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder){
		this.holder=holder;
		onDrawcanvas();
	}
	public void onDrawcanvas()
	{
		canvas=holder.lockCanvas();
		try
		{
			synchronized(holder)
			{
				onDraw(canvas);//绘制
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(canvas!=null)
			{
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder){
	}
	public void initBitmap()
	{
		beijing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.background),ratio_width,ratio_height);//背景图片
		isnoBeijing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.beijingyinyu),ratio_width,ratio_height);//播放背景音乐	
		isnoChangjing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.changjinyinyu),ratio_width,ratio_height);	//播放场景音乐
		baiseFangfe=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.baisefangge),ratio_width,ratio_height);	//白色方格
		honGou=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.honsegou),ratio_width,ratio_height);	//红色勾
		fanHui=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.fanhuianniu),ratio_width,ratio_height);//返回按钮
		queDing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.quedinganniu),ratio_width,ratio_height);//确定按钮
	
		
		
	}
}
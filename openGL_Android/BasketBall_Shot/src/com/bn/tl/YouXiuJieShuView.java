package com.bn.tl;

import static com.bn.tl.Constant.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class YouXiuJieShuView extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	SurfaceHolder holder;
	Canvas canvas;
	Paint paint;
	CaiDanView w;
	
	Bitmap background;
	Bitmap exit;//退出
	Bitmap retry;//再来一次
	Bitmap fanhuicaidan;//返回菜单
	boolean isnoretry;//是否点击了再来一次
	boolean isnofanhuicaidan;//是否点击了返回菜单按钮
	boolean isnoexit;//是否点击了退出按钮
	
	public YouXiuJieShuView(BasketBall_Shot_Activity activity,CaiDanView w) {
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		this.w=w;
		paint=new Paint();
		paint.setAntiAlias(true);
		initBitmap();
	}
	
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);	
		
		canvas.drawBitmap(background, sXtart, sYtart, paint);//背景
		
		if(isnoretry){
			canvas.drawBitmap(scaleToFit(retry,1.2f,1.2f), sXtart+ratio_width*90, sYtart+244*ratio_height, paint);//再玩一次
		}else{
			canvas.drawBitmap(retry, sXtart+ratio_width*115, sYtart+250*ratio_height, paint);//再玩一次
		}
		if(isnofanhuicaidan){
			canvas.drawBitmap(scaleToFit(fanhuicaidan,1.2f,1.2f), sXtart+ratio_width*90, sYtart+364*ratio_height, paint);//返回菜单
		}else{
			canvas.drawBitmap(fanhuicaidan, sXtart+ratio_width*115, sYtart+370*ratio_height, paint);//返回菜单
		}
		
		if(isnoexit){
			canvas.drawBitmap(scaleToFit(exit,1.2f,1.2f), sXtart+ratio_width*90, sYtart+484*ratio_height, paint);//退出
		}else{
			canvas.drawBitmap(exit, sXtart+ratio_width*115, sYtart+490*ratio_height, paint);//退出
		}
		
	}
	
	public boolean onTouchEvent(MotionEvent e)
	{
		float x=e.getX();
		float y=e.getY();
		switch(e.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+250*ratio_height&&y<sYtart+310*ratio_height){
				isnoretry=true;//再来一次
			}else{
				isnoretry=false;
			}
			
			if(x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+370*ratio_height&&y<sYtart+430*ratio_height){
				isnofanhuicaidan=true;//返回菜单
			}else{
				isnofanhuicaidan=false;
			}
			
			if(x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+490*ratio_height&&y<sYtart+550*ratio_height){
				isnoexit=true;//结束
			}else{
				isnoexit=false;
			}
			doDraw();
			break;
		case MotionEvent.ACTION_UP:
			if(isnoretry&&x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+250*ratio_height&&y<sYtart+310*ratio_height){
				//再来一次
				activity.xiaoxichuli.sendEmptyMessage(JIAZAI_JIEMIAN);  //加载界面
			}
			
			if(isnofanhuicaidan&&x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+370*ratio_height&&y<sYtart+430*ratio_height){
				//返回菜单
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);
			}
			
			if(isnoexit&&x>sXtart+ratio_width*115&&x<sXtart+ratio_width*365&&y>sYtart+490*ratio_height&&y<sYtart+550*ratio_height){
			//退出
			System.exit(0);
			}
			isnoretry=false;
			isnofanhuicaidan=false;
			isnoexit=false;
			doDraw();
			break;
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.holder=holder;
		doDraw();
	}
	public void doDraw()
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
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
	
	public void initBitmap()
	{
		background=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.background),ratio_width,ratio_height);
		retry=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.retry),ratio_width,ratio_height);//再来一次
		fanhuicaidan=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.fanhuicaidan),ratio_width,ratio_height);//返回菜单
		exit=scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.shut),ratio_width,ratio_height);//退出按钮
		
	}
	
}
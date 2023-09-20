package com.bn.tl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import static com.bn.tl.Constant.*;

public class CaiDanView extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	Canvas canvas;//画布
	SurfaceHolder holder;
	Paint paint;	
	Bitmap kaishi;
	Bitmap beijingtupian;
	Bitmap tuichu;
	Bitmap guanyu;
	Bitmap bangzhu;
	Bitmap lishijilu;//历史记录
	Bitmap shezhi;//设置按钮图片
	
	boolean isKaishi;//是否点击了开始按钮
	boolean isshezhi;//是否按下设置按钮	
	boolean isguanyu;//是否按下关于按钮
	boolean isbangzhu;//是否按下帮助按钮
	boolean islishijilu;//是否按下历史记录	
	boolean istuichu;//是否按下退出按钮处
	
	float left1=LEFT*ratio_width+sXtart;
	
	public CaiDanView(BasketBall_Shot_Activity activity) 
	{
		
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);//设置生命周期回调接口的实现者	
		paint = new Paint();
		paint.setAntiAlias(true);//打开抗锯齿
		initBitmap();//初始化位图
	}
	public void onDraw(Canvas canvas)
	{
		float xstar=25*ratio_width;
		float ystar=6*ratio_height;
		if(canvas==null) return;
		super.onDraw(canvas);
		canvas.drawBitmap(beijingtupian, sXtart, sYtart, paint);//背景
		if(isKaishi){
			canvas.drawBitmap(scaleToFit(kaishi,1.2f,1.2f), left1-xstar, 160*ratio_height+sYtart-ystar, paint);//开始 
		}else{
			canvas.drawBitmap(kaishi, left1, 160*ratio_height+sYtart, paint);//开始 
		}
		if(isshezhi){
			canvas.drawBitmap(scaleToFit(shezhi,1.2f,1.2f), left1-xstar,
					240*ratio_height+sYtart-ystar, paint);//设置
		}
		else
		{
			canvas.drawBitmap(shezhi, left1,
					240*ratio_height+sYtart, paint);//设置
		}
		if(isguanyu){
			canvas.drawBitmap(scaleToFit(guanyu,1.2f,1.2f), left1-xstar, 320*ratio_height+sYtart-ystar, paint);//关于
		}
		else
		{
			canvas.drawBitmap(guanyu, left1, 320*ratio_height+sYtart, paint);//关于
		}
		if(isbangzhu)
		{
			canvas.drawBitmap(scaleToFit(bangzhu,1.2f,1.2f), left1-xstar, 
					400*ratio_height+sYtart-ystar, paint);//帮助
		}else{
			canvas.drawBitmap(bangzhu, left1, 400*ratio_height+sYtart, paint);//帮助
		}
		if(islishijilu){
			canvas.drawBitmap(scaleToFit(lishijilu,1.2f,1.2f), left1-xstar, 480*ratio_height+sYtart-ystar, paint);//历史记录
		}else{
			canvas.drawBitmap(lishijilu, left1, 480*ratio_height+sYtart, paint);//历史记录
		}
		if(istuichu){
			canvas.drawBitmap(scaleToFit(tuichu,1.2f,1.2f), left1-xstar, 560*ratio_height+sYtart-ystar, paint);//退出
		}
		else{
			canvas.drawBitmap(tuichu, left1, 560*ratio_height+sYtart, paint);//退出
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
			if(x>left1&&x<left1+250*ratio_width&&y>160*ratio_height+sYtart&&y<160*ratio_height+sYtart+60*ratio_height){
				isKaishi=true;//开始按钮
			}else{
				isKaishi=false;
			}
			
			
			if(x>left1&&x<left1+250*ratio_width&&y>240*ratio_height+sYtart&&y<240*ratio_height+sYtart+60*ratio_height){
				isshezhi=true;//设置按钮
			}else{
				isshezhi=false;
			}
			
			if(x>left1&&x<left1+250*ratio_width&&y>320*ratio_height+sYtart&&y<320*ratio_height+sYtart+60*ratio_height){
				isguanyu=true;//关于按钮
			}else{
				isguanyu=false;
			}
			
			if(x>left1&&x<left1+250*ratio_width&&y>400*ratio_height+sYtart&&y<400*ratio_height+sYtart+60*ratio_height){
				isbangzhu=true;//帮助按钮
			}else{
				isbangzhu=false;
			}
			
			if(x>left1&&x<left1+250*ratio_width&&y>480*ratio_height+sYtart&&y<480*ratio_height+sYtart+60*ratio_height){
				islishijilu=true;//历史按钮
			}else{
				islishijilu=false;
			}
			
			if(x>left1&&x<left1+250*ratio_width&&y>560*ratio_height+sYtart&&y<560*ratio_height+sYtart+60*ratio_height){
				istuichu=true;//退出按钮
			}else{
				istuichu=false;
			}
			
			 doDraw();
			break;
		case MotionEvent.ACTION_UP:
			
			if(isKaishi&&x>left1&&x<left1+250*ratio_width&&y>160*ratio_height+sYtart&&y<160*ratio_height+sYtart+60*ratio_height){
				isKaishi=false;
				isnoHelpView=false;
				
				activity.xiaoxichuli.sendEmptyMessage(JIAZAI_JIEMIAN);  //加载界面
			}else{
				isKaishi=false;
			}
			if(isshezhi&&x>left1&&x<left1+250*ratio_width&&y>240*ratio_height+sYtart&&y<240*ratio_height+sYtart+60*ratio_height){
				isshezhi=false;//设置按钮
				//这里跳到声音界面
				activity.xiaoxichuli.sendEmptyMessage(SHENGYING_KG_JIEMIAN);
			}else{
				isshezhi=false;
			}
			
			if(isguanyu&&x>left1&&x<left1+250*ratio_width&&y>320*ratio_height+sYtart&&y<320*ratio_height+sYtart+60*ratio_height){
				isguanyu=false;//关于按钮
				activity.xiaoxichuli.sendEmptyMessage(GUANYU_JIEMIAN);
			}else{
				isguanyu=false;
			}
			
			if(isbangzhu&&x>left1&&x<left1+250*ratio_width&&y>400*ratio_height+sYtart&&y<400*ratio_height+sYtart+60*ratio_height){
				isbangzhu=false;//帮助按钮
				activity.xiaoxichuli.sendEmptyMessage(BANGZHU_JIEMIAN);
			}else{
				isbangzhu=false;
			}
			
			if(islishijilu&&x>left1&&x<left1+250*ratio_width&&y>480*ratio_height+sYtart&&y<480*ratio_height+sYtart+60*ratio_height){
				islishijilu=false;//历史按钮
				activity.xiaoxichuli.sendEmptyMessage(JILU_JIEMIAN);
			}else{
				islishijilu=false;
			}
			
			if(istuichu&&x>left1&&x<left1+250*ratio_width&&y>560*ratio_height+sYtart&&y<560*ratio_height+sYtart+60*ratio_height){
				istuichu=false;//退出按钮
				System.exit(0);
			}else{
				istuichu=false;
			}
			
			doDraw();
		}		
		return true;
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
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {	
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		this.holder=holder;
		doDraw();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
	}
	public void initBitmap()
	{
		beijingtupian=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.background),ratio_width,ratio_height);	
		kaishi=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.begin),ratio_width,ratio_height);
		tuichu=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.shut),ratio_width,ratio_height);
		guanyu=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.about1),ratio_width,ratio_height);
		bangzhu=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.help1),ratio_width,ratio_height);
		lishijilu=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.jilu),ratio_width,ratio_height);//历史记录按钮
		shezhi=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.shezhi),ratio_width,ratio_height);//设置按钮
	}	
}
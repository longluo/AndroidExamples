package com.bn.tl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import static com.bn.tl.Constant.*;
//绘制声音界面
public class GuanYuView extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	Canvas canvas;//画布
	SurfaceHolder holder;
	Paint paint;
	Bitmap beijing;//背景图片
	Bitmap shijianBeijin;//白色框
	Bitmap wenzitupian;//文字图片
	Bitmap gundontiao;//滚动条
	
	Bitmap fanHui;//返回按钮
	Bitmap queDing;//确定按钮
	boolean isnoDianJi=true;//是否点击了
	
	float timeStartX;
	float timeStartY;//滚动背景的起始位置
	
	float timeStartXCaiJian;//裁剪框的起始位置
	float timeStartYCaiJian;//
	
	float caijianWidth;//裁剪框的宽度
	float caijianHeight;//裁剪框的高度
	
	float timegradeStartX;//滚动方格此时的起始位置
	float timeGradeStartY;
	
	
	
	float timegradeinitX;//滚动方格最初的起始位置
	float timegradeinitY;
	
	
	
	float mDounX;
	float mDounY;//上次触摸的位置
	
	float mtimegradeStartX;//按下时其所在位置
	float mtimeGradeStartY;
	
	boolean isnoFanhui;//是否按下时在返回按钮上
	boolean isnoQueDing;//是否按下时在确定按钮上
	public GuanYuView(BasketBall_Shot_Activity activity) 
	{
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint();
		paint.setAntiAlias(true);
		initBitmap();
		timeStartX=sXtart+15*ratio_width;//滚动背景的起始位置
		timeStartY=sYtart+150*ratio_height;
		
		timeStartXCaiJian=timeStartX;//裁剪框的起始位置
		timeStartYCaiJian=sYtart+timeStartY+4*ratio_height;
		
		caijianWidth=450*ratio_width;
		caijianHeight=shijianBeijin.getHeight();//440*ratio_height;//裁剪框的宽高
		
		timegradeinitX=timeStartX;
		timegradeinitY=timeStartY+4*ratio_height;//滚动方格的初始位置
		
		timegradeStartX=timegradeinitX;
		timeGradeStartY=timegradeinitY;//绘制滚动时的起始位置
		
	}
	public void onDraw(Canvas canvas)	{
		super.onDraw(canvas);
		canvas.drawBitmap(beijing, sXtart, sYtart, null);//背景		
		canvas.drawBitmap(shijianBeijin,timeStartX, timeStartY, null);//白色方框
		canvas.save();
		canvas.clipRect(new RectF(timegradeinitX,timegradeinitY,
				timeStartX+caijianWidth,timegradeinitY+caijianHeight));
		canvas.drawBitmap(wenzitupian,timegradeStartX, timeGradeStartY, null);//绘制文字
		float gundontiaoheight=(-timeGradeStartY+timegradeinitY)*(caijianHeight-50*ratio_height)/
		(wenzitupian.getHeight()-caijianHeight)+timegradeinitY;
		if(isnoDianJi){
			canvas.drawBitmap(gundontiao, timeStartX+caijianWidth-12*ratio_width, gundontiaoheight, null);//滚动条
		}
		canvas.restore();		
		if(isnoQueDing){
			canvas.drawBitmap(scaleToFit(queDing,1.2f,1.2f), sXtart+35*ratio_width,sYtart+ 692*ratio_height, null);//返回按钮
		}else{
			canvas.drawBitmap(queDing, sXtart+50*ratio_width, sYtart+700*ratio_height, null);//返回按钮
		}
		if(isnoFanhui){
			canvas.drawBitmap(scaleToFit(fanHui,1.2f,1.2f), sXtart+245*ratio_width,sYtart+ 692*ratio_height, null);//确定按钮
		}else{
			canvas.drawBitmap(fanHui, sXtart+270*ratio_width, sYtart+700*ratio_height, null);//确定按钮
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
			if(x>timeStartX&&x<timeStartX+caijianWidth&&y>timeStartYCaiJian&&y<timeStartYCaiJian+caijianHeight){
				mDounX=x;
				mDounY=y;
				mtimeGradeStartY=timeGradeStartY;
				isnoDianJi=true;
			}else{
				isnoDianJi=false;
			}
			if(x>sXtart+50*ratio_width&&x<sXtart+50*ratio_width+160*ratio_width&&y>sYtart+700*ratio_height&&y<sYtart+780*ratio_height){//返回按钮
				isnoQueDing=true;
			}else{
				isnoQueDing=false;
			}
			if(x>sXtart+270*ratio_width&&x<sXtart+270*ratio_width+160*ratio_width&&y>sYtart+700*ratio_height&&y<sYtart+780*ratio_height){//确定按钮
				isnoFanhui=true;
			}else{
				isnoFanhui=false;
			}
			onDrawcanvas();
			break;
		case MotionEvent.ACTION_MOVE:
			float dy=y-mDounY;
			if(isnoDianJi==true){
				if(mtimeGradeStartY+dy<timegradeinitY+caijianHeight-wenzitupian.getHeight()){
					timeGradeStartY=timegradeinitY+caijianHeight-wenzitupian.getHeight();
				}
				else if(mtimeGradeStartY+dy>timegradeinitY){
					timeGradeStartY=timegradeinitY;  
				}else{
					timeGradeStartY=mtimeGradeStartY+dy;
				}
				 onDrawcanvas();
			}
			
			break;
		case MotionEvent.ACTION_UP:
			if(isnoQueDing&&x>sXtart+50*ratio_width&&x<sXtart+50*ratio_width+160*ratio_width&&y>sYtart+700*ratio_height&&y<sYtart+780*ratio_height){//返回按钮
				isnoQueDing=false;//确定
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//返回到菜单界面
			}else{
				isnoQueDing=false;
			}
			if(isnoFanhui&&x>sXtart+270*ratio_width&&x<sXtart+270*ratio_width+160*ratio_width&&y>sYtart+700*ratio_height&&y<sYtart+780*ratio_height){//确定按钮
				isnoFanhui=false;//返回
				activity.xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//返回到菜单界面
			}else{
				isnoFanhui=false;
			}
			
			if(isnoDianJi){
				isnoDianJi=false;
				 
			}
			onDrawcanvas();
			break;			
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
	@Override
	public void surfaceDestroyed(SurfaceHolder holder){
	}
	public void initBitmap()
	{				
		shijianBeijin=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.baisewaikuang),ratio_width,ratio_height);//时间分数背景
		gundontiao=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.gundontiao),ratio_width,ratio_height);//滚动条		
		beijing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.background),ratio_width,ratio_height);//背景图片
		fanHui=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.fanhuianniu),ratio_width,ratio_height);//返回按钮
		queDing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.quedinganniu),ratio_width,ratio_height);//确定按钮
		
		wenzitupian=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.guanyuwenzitu),ratio_width,ratio_height);//确定按钮
		
		
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
}
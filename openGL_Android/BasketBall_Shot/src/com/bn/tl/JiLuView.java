package com.bn.tl;

import java.util.Vector;




import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import static com.bn.tl.Constant.*;
//绘制声音界面
public class JiLuView extends SurfaceView implements SurfaceHolder.Callback
{
	BasketBall_Shot_Activity activity;
	Canvas canvas;//画布
	SurfaceHolder holder;
	Paint paint;
	Bitmap beijing;//背景图片
	Bitmap shijianBeijin;//显示时间的和分数的背景
	Bitmap shijianBeijin2;//显示时间和分手背景图2
	boolean isnoGradePaixu=true;//是否为安装分数排序
	
	Bitmap[] iscore=new Bitmap[10];//得分图
    Bitmap JianHaotupian;//减号图
    Bitmap hengXian;//横线
	Bitmap maohao;//冒号
	Bitmap gundontiao;//滚动条
	
	Bitmap fanHui;//返回按钮
	Bitmap queDing;//确定按钮
	boolean isnoDianJi=true;//是否点击了
	int color[][]=new int[][]{
			{100,250,205,0},
			{100,250,60,0}
	};
	Vector<Vector<String>> vector;//存放结果集的向量
	
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
	
	int geziHeight=(int)(40*ratio_height);//
	int geziWidth=(int)(450*ratio_width);//格子的尺寸
	
	
	int scoreWidth=(int)(15*ratio_width);//数字宽度
	int scoreHeght=(int)(20*ratio_height);//数字的高度
	
	int fanggeGeshu=30;//结果个数
	
	float mDounX;
	float mDounY;//上次触摸的位置
	
	float mtimegradeStartX;//按下时其所在位置
	float mtimeGradeStartY;
	
	boolean isnoFanhui;//是否按下时在返回按钮上
	boolean isnoQueDing;//是否按下时在确定按钮上
	public JiLuView(BasketBall_Shot_Activity activity) 
	{
		super(activity);
		this.activity=activity;
		this.getHolder().addCallback(this);
		paint=new Paint();
		paint.setAntiAlias(true);				
		initBitmap();
		String sql_select="select grade,time from paihangbang  order by grade desc limit 0,30;";
    	vector=SQLiteUtil.query(sql_select);//从数据库中取出相应的数据
    	fanggeGeshu=vector.size();
    	if(fanggeGeshu<12){
    		fanggeGeshu=12;
    	}
		timeStartX=sXtart+15*ratio_width;//滚动背景的起始位置
		timeStartY=sYtart+150*ratio_height;
		
		timeStartXCaiJian=timeStartX+20*ratio_height;//裁剪框的起始位置
		timeStartYCaiJian=sYtart+timeStartY+100*ratio_height;
		
		caijianWidth=430*ratio_width;
		caijianHeight=400*ratio_height;//裁剪框的宽高
		
		timegradeinitX=timeStartXCaiJian;
		timegradeinitY=timeStartYCaiJian;//timeStartY+73*ratio_height;//滚动方格的初始位置
		
		timegradeStartX=timegradeinitX;
		timeGradeStartY=timegradeinitY;//绘制滚动时的起始位置
		
	}
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawBitmap(beijing, sXtart, sYtart, null);//背景		
		if(isnoGradePaixu){
			canvas.drawBitmap(shijianBeijin, timeStartX, timeStartY, null);//时间和分数滚动背景	
		}else{
			canvas.drawBitmap(shijianBeijin2, timeStartX, timeStartY, null);//时间和分数滚动背景	
		}
		canvas.save();
		canvas.clipRect(new RectF(timegradeinitX,timegradeinitY,
				timeStartX+caijianWidth,timegradeinitY+caijianHeight));
		
		drawRectBeijing(canvas,timegradeStartX,timeGradeStartY);//绘制颜色条	
		for(int i=0;i<vector.size();i++)//循环绘制排行榜的分数和对应时间
    	{
			int j=i;
			if(!isnoGradePaixu){
				j=vector.size()-i-1;
			}
			drawRiQi(canvas,vector.get(j).get(1).toString(),//绘制时间
					(int)(timegradeStartX+15*ratio_width),(int)(timeGradeStartY+i*geziHeight+5*ratio_height));
			drawScoreStr(canvas,vector.get(i).get(0).toString(),
					(int)timegradeStartX+(int)(300*ratio_width),(int)(timeGradeStartY+i*geziHeight+5*ratio_height));
    	}
		float gundontiaoheight=(-timeGradeStartY+timegradeinitY)*(caijianHeight-50*ratio_height)/
		(fanggeGeshu*geziHeight-caijianHeight)+timegradeinitY;
		if(isnoDianJi){
			canvas.drawBitmap(gundontiao, timeStartX+caijianWidth-8*ratio_width, gundontiaoheight, null);//滚动条
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
	public void drawRectBeijing(Canvas canvas,float startX,float startY){
		
		for(int i=0;i<fanggeGeshu;i++){
			paint.setARGB(color[i%2][0], color[i%2][1], color[i%2][2],color[i%2][3]);//设置画笔颜色	
			Rect r=new Rect((int)(startX),(int)(startY+i*geziHeight),
					(int) (startX+geziWidth),(int) (startY+(1+i)*geziHeight));
			canvas.drawRect(r, paint);
		}
	}
	
	public void drawScoreStr(Canvas canvas,String s,int width,int height)//绘制数字字符串方法
	{
    	//绘制得分
    	String scoreStr=s; 
    	for(int i=0;i<scoreStr.length();i++){//循环绘制得分
    		int tempScore=scoreStr.charAt(i)-'0';
    		canvas.drawBitmap(iscore[tempScore], width+i*scoreWidth,height, null);
    		}
	}
	public void drawRiQi(Canvas canvas,String s,int width,int height)//画年月
	{
		String ss[]=s.split("-");//切割得到年月日
		for(int i=1;i<ss.length;i++){
			if(ss[ss.length-i].length()<2){
				ss[ss.length-i]="0"+ss[ss.length-i];
			}
			drawScoreStr(canvas,ss[ss.length-i],width+scoreWidth*((ss.length-i-1)*3+0),height);//画年数数字
			if(i<3){
				canvas.drawBitmap(maohao,width+scoreWidth*((ss.length-i-1)*3-1),height, null);//画冒号
			}
			else if(i==4){
				canvas.drawBitmap(hengXian,width+scoreWidth*((ss.length-i-1)*3-1),height, null);//画横线
			}
			
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
			if(x>timeStartX&&x<timeStartX+caijianWidth/2&&y>timeStartY&&y<timeStartY+74*ratio_height){//如果点击按照时间排序
				Log.w("dsfjdskfj","2222222222");
				String sql_select="select grade,time from paihangbang   desc limit 0,30;";
		    	vector=SQLiteUtil.query(sql_select);//从数据库中取出相应的数据
				isnoGradePaixu=false;
			}else if(x>timeStartX+caijianWidth/2&&x<timeStartX+caijianWidth&&y>timeStartY&&y<timeStartY+74*ratio_height){//点击按照成绩排序
				isnoGradePaixu=true;
				String sql_select="select grade,time from paihangbang  order by grade desc limit 0,30;";
		    	vector=SQLiteUtil.query(sql_select);//从数据库中取出相应的数据
				Log.w("dsfjdskfj","11111111");
			}
			
			if(x>timeStartX&&x<timeStartX+caijianWidth&&y>timeStartYCaiJian+74*ratio_height&&y<timeStartYCaiJian+caijianHeight+74*ratio_height){
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
				if(mtimeGradeStartY+dy<timegradeinitY+caijianHeight-fanggeGeshu*geziHeight){
					timeGradeStartY=timegradeinitY+caijianHeight-fanggeGeshu*geziHeight;
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
		iscore[0] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d0),ratio_width,ratio_height);//数字图
		iscore[1] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d1),ratio_width,ratio_height);
		iscore[2] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d2),ratio_width,ratio_height);
		iscore[3] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d3),ratio_width,ratio_height);
		iscore[4] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d4),ratio_width,ratio_height);
		iscore[5] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d5),ratio_width,ratio_height);
		iscore[6] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d6),ratio_width,ratio_height);
		iscore[7] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d7),ratio_width,ratio_height);
		iscore[8] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d8),ratio_width,ratio_height);
		iscore[9] = scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.d9),ratio_width,ratio_height);
		hengXian=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.hengxian),ratio_width,ratio_height);//横线
		maohao=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.maohao),ratio_width,ratio_height);//冒号
		shijianBeijin=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.timegradebeijing),ratio_width,ratio_height);//时间分数背景
		shijianBeijin2=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.shijianbeijing2),ratio_width,ratio_height);//时间分数背景
		gundontiao=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.gundontiao),ratio_width,ratio_height);//滚动条		
		beijing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.background),ratio_width,ratio_height);//背景图片
		fanHui=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.fanhuianniu),ratio_width,ratio_height);//返回按钮
		queDing=scaleToFit(BitmapFactory.decodeResource(getResources(), R.drawable.quedinganniu),ratio_width,ratio_height);//确定按钮
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
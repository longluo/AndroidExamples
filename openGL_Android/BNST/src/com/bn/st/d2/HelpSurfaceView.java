package com.bn.st.d2;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import static com.bn.st.xc.Constant.*;

import com.bn.R;

public class HelpSurfaceView extends MySFView
{
	MyActivity activity;//Activity的引用 
	Canvas c;//画笔的引用
	Paint paint;//画笔的引用
	SurfaceHolder holder;//锁的引用
	Bitmap[] bg_bitmap;					//背景图
	Bitmap pre_page;					//上一页按钮图片
	Bitmap next_page;					//下一页按钮图片
	Bitmap back;
	
	Bitmap pre_page_press;				//上一页按钮图片
	Bitmap next_page_press;				//下一页按钮图片
	Bitmap back_press;
	
	//帮助界面中的闪烁提示
	Bitmap[] mark_bitmap;
	//每张闪烁提示图片的xy坐标
	private final float[][] mark_xy=
	{
			{246,308},
			{44,170},
			{15,115},
			{50,160},
			{184,242}
	};
	int fullTime=0;//记录从开始到当前的时间
	long startTime;//开始时间
	
	private float button_pre_x=20f*ratio_width;//back图片按钮的左上角的点的坐标
	private float button_pre_y=415f*ratio_height;
	private float button_next_x=710f*ratio_width;//next图片按钮的左上角点的坐标
	private float button_next_y=415f*ratio_height;
	
	private int  next_flag=0;//0表示不移动,-1表示左移,1表示右移

	private float bg_bitmap_curr_x=0;//当前背景图片的左上角点的X坐标
	private float bg_bitmap_curr_y=0;//当前背景图片的左上角点的Y坐标
	private float bg_bitmap_next_x;//下一幅背景图片的左上角点的X坐标
	private float bg_bitmap_next_y=0;//下一幅背景图片的左上角点的Y坐标
	private float move_span=80;//图片移动的速度
	int page_index=0;//当前帮助页面的索引值
	public boolean flag_go=true;
	
	boolean back_flag=false;
	boolean pre_page_flag=false;
	boolean next_page_flag=false;
	
	boolean isHaveNextFlag=true;
	boolean isHavePreFlag=false;
	
	public HelpSurfaceView(MyActivity activity)
	{
		this.activity = activity;
		paint = new Paint();	 //创建画笔
		bg_bitmap=new Bitmap[5];//创建帮助界面背景图片数组对象
		mark_bitmap=new Bitmap[5];//创建帮助界面中标注提醒图片数组对象
		paint.setAntiAlias(true);//打开抗锯齿
		initBitmap();			//初始化用到的图片资源
		startTime=System.currentTimeMillis();
	}
	
	public void initThread()
	{
		next_flag=0;//0表示不移动,-1表示左移,1表示右移

		bg_bitmap_curr_x=0;//当前背景图片的左上角点的X坐标
		bg_bitmap_curr_y=0;//当前背景图片的左上角点的Y坐标
		bg_bitmap_next_x=0;//下一幅背景图片的左上角点的X坐标
		bg_bitmap_next_y=0;//下一幅背景图片的左上角点的Y坐标
		move_span=80;//图片移动的速度
		page_index=0;//当前帮助页面的索引值
		flag_go=true;
		
		back_flag=false;
		pre_page_flag=false;
		next_page_flag=false;
		
		isHaveNextFlag=true;
		isHavePreFlag=false;		
		
		new Thread()//创建一个线程调用doDraw方法
		{
			@Override
			public void run()
			{
				while(flag_go)
				{
					//判断是左移还是右移
					if(next_flag==-1)//左移
					{
						bg_bitmap_curr_x=bg_bitmap_curr_x-move_span;
						bg_bitmap_next_x=bg_bitmap_next_x-move_span;
						if(bg_bitmap_curr_x<=-SCREEN_WIDTH)
						{
							bg_bitmap_curr_x=-SCREEN_WIDTH;
							next_flag=0;
							page_index++;
							bg_bitmap_curr_x=0;
							bg_bitmap_next_x=SCREEN_WIDTH;
							if(page_index==bg_bitmap.length-1)
							{
								isHaveNextFlag=false;
							}
						}
					}
					if(next_flag==1)//右移
					{
						bg_bitmap_curr_x=bg_bitmap_curr_x+move_span;
						bg_bitmap_next_x=bg_bitmap_next_x+move_span;
						if(bg_bitmap_curr_x>=SCREEN_WIDTH)
						{
							bg_bitmap_curr_x=SCREEN_WIDTH;
							page_index--;
							bg_bitmap_curr_x=0;
							bg_bitmap_next_x=-SCREEN_WIDTH;
							if(page_index==0)
							{
								isHavePreFlag=false;
							}
							next_flag=0;
						}
					}
					try
					{  
						Thread.sleep(10);//线程休眠100毫秒
					}
					catch (InterruptedException e)  
					{
					e.printStackTrace();
					}
				}
			}  
		}.start();
	}
	
	//重写onDraw方法
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawColor(Color.argb(255, 0, 0, 0));//清屏为黑色
		canvas.drawBitmap(bg_bitmap[page_index],bg_bitmap_curr_x,bg_bitmap_curr_y, null);//画当前背景
		
		//当界面没有在移动的时候，绘制当前帮助界面的闪烁图标提示
		if(next_flag==0)
		{
			//绘制帮助界面闪动的图标
			long currentTime=System.currentTimeMillis();//记录当前时间
			fullTime=(int) ((currentTime-startTime));//记录总时间	
			//将1秒分成两份，在0.7秒内绘制，0.3秒内不绘制
			if((fullTime/100)%10 < 7) {
				//绘制翻页指示图标
				System.out.println(ratio_width+"  "+ratio_height);
				canvas.drawBitmap(mark_bitmap[page_index], mark_xy[page_index][0]*ratio_width, mark_xy[page_index][1]*ratio_height, paint);			
			}
		}	
				
		if(next_flag==-1)
		{
			canvas.drawBitmap(bg_bitmap[page_index+1],bg_bitmap_next_x,bg_bitmap_next_y, null);//画下一幅背景
		}
		if(next_flag==1)
		{
			canvas.drawBitmap(bg_bitmap[page_index-1],bg_bitmap_next_x,bg_bitmap_next_y, null);//画下一幅背景
		}
		if(isHaveNextFlag==false)
		{
			if(!back_flag)
			{
				canvas.drawBitmap(back, button_next_x, button_next_y, null);//绘制back按钮
			}
			else
			{
				canvas.drawBitmap(back_press, button_next_x, button_next_y, null);//绘制back按钮
			}
		}
		if(page_index>0)//当前的页面索引大于0
		{
			if(!pre_page_flag)
			{
				canvas.drawBitmap(pre_page, button_pre_x, button_pre_y, paint);//绘制上一页按钮
			}else
			{
				canvas.drawBitmap(pre_page_press, button_pre_x, button_pre_y, paint);//绘制上一页按钮
			}
		}
		if(!isHavePreFlag)
		{
			if(!back_flag)
			{
				canvas.drawBitmap(back, button_pre_x, button_pre_y, null);//绘制back按钮
			}
			else
			{
				canvas.drawBitmap(back_press, button_pre_x, button_pre_y, null);//绘制back按钮
			}
		}
		if(page_index<bg_bitmap.length-1)//当前页面索引值小于帮助图片数组-1
		{
			if(!next_page_flag)
			{
				canvas.drawBitmap(next_page, button_next_x, button_next_y, paint);//绘制下一页按钮
			}else
			{
				canvas.drawBitmap(next_page_press, button_next_x, button_next_y, paint);//绘制下一页按钮
			}
		}
	}
	//重写触摸事件方法
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		int x=(int)e.getX();//获取触控点的XY坐标
		int y=(int)e.getY();
		switch(e.getAction())
		{
		case MotionEvent.ACTION_DOWN://抬起事件
			if(next_flag==0&&x>button_pre_x&&x<button_pre_x+pre_page.getWidth()&&y>button_pre_y&&y<button_pre_y+pre_page.getHeight())
			{
				if(!isHavePreFlag)
				{
					back_flag=true;
				}
				else
				{
					pre_page_flag=true;
				}
			}
			else if(next_flag==0&&x>button_next_x&&x<button_next_x+pre_page.getWidth()&&y>button_next_y&&y<button_next_y+pre_page.getHeight())
			{
				if(!isHaveNextFlag)
				{
					back_flag=true;
				}
				else
				{
					next_page_flag=true;
				}
				
			}
//			else if(page_index==0&&x>button_next_x&&x<button_next_x+back.getWidth()&&y>button_next_y&&y<button_next_y+back.getHeight())
//			{//返回按钮
//    			back_flag=true;
//			}else if(page_index==bg_bitmap.length-1&&x>button_pre_x&&x<button_pre_x+back.getWidth()&&y>button_pre_y&&y<button_pre_y+back.getHeight())
//			{//返回按钮
//    			back_flag=true;
//			}
			break;
		case MotionEvent.ACTION_UP://抬起事件 
			pre_page_flag=false;
			next_page_flag=false;
			back_flag=false;
			if(next_flag==0&&x>button_pre_x&&x<button_pre_x+pre_page.getWidth()&&y>button_pre_y&&y<button_pre_y+pre_page.getHeight())
			{
				if(!isHavePreFlag)
				{
					//返回到主菜单
					flag_go=false;
					activity.hd.sendEmptyMessage(1);
				}
				else
				{
					isHavePreFlag=true;
					isHaveNextFlag=true;
					//右移   
					next_flag=1;
					bg_bitmap_next_x=-SCREEN_WIDTH;
				}
			}
			else if(next_flag==0&&x>button_next_x&&x<button_next_x+pre_page.getWidth()&&y>button_next_y&&y<button_next_y+pre_page.getHeight())
			{
				if(!isHaveNextFlag)
				{
					//返回主菜单
					flag_go=false;
					activity.hd.sendEmptyMessage(1);
				}
				else
				{
					isHaveNextFlag=true;
					isHavePreFlag=true;
					//左移
					next_flag=-1;
					bg_bitmap_next_x=SCREEN_WIDTH;
				}
				
			}
			break;
		}
		return true;
	}
	//初始化图片的方法
	public void initBitmap()
	{
		bg_bitmap[0] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.bg_bitmap0),ratio_width,ratio_height);//帮助界面背景图片
		bg_bitmap[1] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.bg_bitmap1),ratio_width,ratio_height);//帮助界面背景图片
		bg_bitmap[2] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.bg_bitmap2),ratio_width,ratio_height);//帮助界面背景图片
		bg_bitmap[3] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.bg_bitmap3),ratio_width,ratio_height);//帮助界面背景图片
		bg_bitmap[4] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.bg_bitmap4),ratio_width,ratio_height);//帮助界面背景图片
		mark_bitmap[0] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.mark0),ratio_width,ratio_height);
		mark_bitmap[1] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.mark1),ratio_width,ratio_height);
		mark_bitmap[2] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.mark2),ratio_width,ratio_height);
		mark_bitmap[3] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.mark3),ratio_width,ratio_height);
		mark_bitmap[4] = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.mark4),ratio_width,ratio_height);
		pre_page = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.pre_page),ratio_width,ratio_height);//上一页按钮图片
		next_page = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.next_page),ratio_width,ratio_height);//下一页按钮图片
		back = scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.back),ratio_width,ratio_height);//上一页按钮图片
		
		pre_page_press= scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.pre_page_press),ratio_width,ratio_height);//上一页按钮图片
		next_page_press= scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.next_page_press),ratio_width,ratio_height);//下一页按钮图片
		back_press= scaleToFit(BitmapFactory.decodeResource(activity.getResources(), R.drawable.back_press),ratio_width,ratio_height);//上一页按钮图片
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

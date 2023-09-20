package com.bn.st.xc;

import com.bn.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class Constant 
{
	//用来控制选船界面中上一个下一个键的自适应的常量  0号为480x800 1号为480x854 2号为540x960 3号为320x480
	public static final float[][] XC_Self_Adapter_Data_TRASLATE=
	{//上一个  下一个  按钮高度的一半
		{0.21f},
		{0.22f},
		{0.22f},
		{0.20f}
	};
	
	//用来控制菜单移动动画的常量
	public static final float MOVE_V=20f;
	public static final long MOVE_TIME=15;
	
	//屏幕的大小
	public static  float SCREEN_WIDTH;
	public static  float SCREEN_HEIGHT;
	//缩放比例
    public static float ratio_width;
    public static float ratio_height;
	//--------设定房间的长宽高
	public static final float HOUSE_CHANG=50;
	public static final float HOUSE_KUAN=50;
	public static final float HOUSE_GAO=30;
	//-------=设定摄像机观察点和目标点的距离
	public static final float XC_DISTANCE=35;
	//三艘船格子的缩放比例
	public static final float RATIO_BOAT=8.0f;
	//展台的半径和高度
	public static final float RADIUS_DISPLAY=14;
	public static final float LENGTH_DISPLAY=6;
	//设定房间的颜色
	public static final float[][] HOUSE_COLOR=new float[][]
	{
		{1f,1f,1f},//不透明地面
		{1f,1f,1f},//透明墙
	};
	//设定展台圆柱颜色
	public static final float[] COLOR_CYLINDER=new float[]{0.9f,0.9f,0.9f,1.0f};
	//设定展台圆面颜色
	public static final float[] COLOR_CIRCLE=new float[]{0.9f,0.9f,0.9f,0.5f};
	//围墙的宽高   这里的宽高均为  实际的一半
	public static final float WALL_WIDHT=20f;
	public static final float WALL_HEIGHT=18f;
	//缩放纹理
	public static Bitmap scaleToFit(Bitmap bm,float width_Ratio,float height_Ratio)
	{		
    	int width = bm.getWidth(); 							//图片宽度
    	int height = bm.getHeight();							//图片高度
    	Matrix matrix = new Matrix(); 
    	matrix.postScale((float)width_Ratio, (float)height_Ratio);				//图片等比例缩小为原来的fblRatio倍
    	Bitmap bmResult = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);//声明位图        	
    	return bmResult;									//返回被缩放的图片
    }
	
	//新添加的=================================================================================
		
	//需要的图片的id
	public static int[] picId=new int[]
    {
		R.drawable.background,		//大背景
		R.drawable.tmode,	//计时模式背景
		R.drawable.rmode,	//竞速模式背景
		R.drawable.hengxian,	//横线
		R.drawable.maohao,	//冒号
    };
	//数字图片
	public static int[] picNum=new int[]
    {
		R.drawable.d0,R.drawable.d1,
		R.drawable.d2,R.drawable.d3,
		R.drawable.d4,R.drawable.d5,
		R.drawable.d6,R.drawable.d7, 
		R.drawable.d8,R.drawable.d9,
    };
	public static Bitmap[] recordBitmap;
	public static Bitmap[] recordNum;
	//初始化图片的方法
	public static void initBitmap(Resources res)
	{
		recordBitmap=new Bitmap[picId.length];
		recordNum=new Bitmap[picNum.length];
		for(int i=0;i<picId.length;i++)
		{
			recordBitmap[i]=scaleToFit(BitmapFactory.decodeResource(res, picId[i]),ratio_width,ratio_height);
		}
		for(int i=0;i<picNum.length;i++)
		{
			recordNum[i]=scaleToFit(BitmapFactory.decodeResource(res, picNum[i]),ratio_width,ratio_height);
		}
	}
	public static float[][] picLocation;
	public static float[][] touchLocation;
	//初始化图片的位置信息
	public static void initLoaction()
	{
		//图片的位置信息
		float[][] tempPicLocation=new float[][]
	    {
			{0,0},//大背景图片位置
			{(SCREEN_WIDTH-recordBitmap[1].getWidth())/2,150},//计时模式图片位置
	    };
		//可触控区间
		float[][] tempTouchLocation=new float[][]
	    {
			{(SCREEN_WIDTH-recordBitmap[1].getWidth())/2,150,(SCREEN_WIDTH)/2,200},//Timing Mode的位置
			{(SCREEN_WIDTH)/2,150,(SCREEN_WIDTH+recordBitmap[1].getWidth())/2,200},//Racing Mode的位置
			{(SCREEN_WIDTH-recordBitmap[1].getWidth())/2,230,(SCREEN_WIDTH+recordBitmap[1].getWidth())/2,410},//绘制时间信息的位置
	    };
		
		picLocation=new float[tempPicLocation.length][tempPicLocation[0].length];
		touchLocation=new float[tempTouchLocation.length][tempTouchLocation[0].length];
		for(int i=0;i<tempPicLocation.length;i++)
		{ 
			for(int j=0;j<tempPicLocation[0].length;j++)
			{
				if(j%2==1)
				{//高度
					picLocation[i][j]=tempPicLocation[i][j]*ratio_height;
				} 
				else if(j%2==0)
				{//宽度
					picLocation[i][j]=tempPicLocation[i][j];
				}
			} 
		}
		for(int i=0;i<tempTouchLocation.length;i++)
		{
			for(int j=0;j<tempTouchLocation[0].length;j++)
			{
				if(j%2==1)
				{//高度
					touchLocation[i][j]=tempTouchLocation[i][j]*ratio_height;
				}
				else if(j%2==0)
				{//宽度
					touchLocation[i][j]=tempTouchLocation[i][j];
				}
			}
		}
	}
}

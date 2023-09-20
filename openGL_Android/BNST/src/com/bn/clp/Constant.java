package com.bn.clp;
import java.util.Date;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.bn.R;
//常量类  
public class Constant
{
	//新添加的，是否突破记录
	public static boolean isBreakRecord;
	//草地格子单位长度 
	public static final float GRASS_UNIT_SIZE=2.5f;
	
	//用于绘制小雷达时进行缩放的值
	public static final float Radar_Ratio=1100;

	//获取当前时间字符串的方法
	public static String getCurrTime()
	{
		String result=null;
		Date d=new Date();
		
		String month=d.getMonth()+1<10?"0"+(d.getMonth()+1):(d.getMonth()+1)+"";
		String day=d.getDate()<10?"0"+d.getDate():d.getDate()+"";
		String hours=d.getHours()<10?"0"+d.getHours():d.getHours()+"";
		String minutes=d.getMinutes()<10?"0"+d.getMinutes():d.getMinutes()+"";
		String seconds=d.getSeconds()<10?"0"+d.getSeconds():d.getSeconds()+"";
		
		result=month+":"+day+":"+hours+":"+minutes+":"+seconds;
		
		return result;
	}
	//获取游戏耗时
	public static long gameTimeUse;
	//获取游戏耗时字符串的方法
	public static String getUseTime()
	{
		String result=null;
		
		long[] timeUseTemp=new long[3];
		timeUseTemp[0]=(long) Math.floor((gameTimeUse%1000)/10);
		timeUseTemp[1]=(long) Math.floor((gameTimeUse%60000)/1000);
		timeUseTemp[2]=(long) Math.floor((gameTimeUse/60000));
		
		String minutes=timeUseTemp[2]<10?"0"+timeUseTemp[2]:timeUseTemp[2]+"";
		String seconds=timeUseTemp[1]<10?"0"+timeUseTemp[1]:timeUseTemp[1]+"";
		String minseconds=timeUseTemp[0]<10?"0"+timeUseTemp[0]:timeUseTemp[0]+"";
		
		result=minutes+":"+seconds+":"+minseconds;
		
		return result;
	}
	
	//音乐设置的相关标志位
	public static boolean BgSoundFlag;//背景音乐播放标志位
	public static boolean SoundEffectFlag;//游戏中音效播放标志位 
	
	//游戏模式选择的相关标志位
	public static boolean isSpeedMode;	//为true则为竞速模式  false则为计时模式
	
	//小雷达中其他船的位置
	public static float[][] other_Boat_XZ;
	
	//为仪表板进行自适应的常量数据 0号为480x800 1号为480x854 2号为540x960 3号为320x480
	public static int screenId=0;
	public static float screenRatio;
	
	public static final float screenRatio480x320=1.5f;//屏幕宽高比
	public static final float screenRatio800x480=1.667f;
	public static final float screenRatio854x480=1.779f;
	public static final float screenRatio960x540=1.778f;
	
	public static final float[][] Self_Adapter_Data_ON_TOUCH=
	{//换视角   	暂停		GO[每个部件四个参数，顺序为上下范围，左右范围]--新加 刹车按钮--新加时间(后视镜)按钮
		{
			0,0.104f,0,0.063f,
			0,0.104f,0.938f,1,
			0.792f,0.938f,0.875f,1,
			0.792f,0.958f,0.018f,0.220f,
			0,0.144f,0.400f,0.670f
		}, 
		{
			0,0.104f,0,0.059f,
			0,0.104f,0.941f,1,
			0.792f,1,0.855f,0.972f,
			0.792f,1,0.017f,0.220f,
			0,0.154f,0.450f,0.70f
		},
		{
			0,0.093f,0,0.052f,
			0,0.093f,0.948f,1,
			0.778f,0.963f,0.875f,0.979f,
			0.778f,0.963f,0.016f,0.208f,
			0,0.123f,0.400f,0.660f
		},
		{
			0,0.156f,0,0.104f,
			0,0.156f,0.896f,1,
			0.625f,0.938f,0.75f,0.958f,
			0.625f,0.938f,0.031f,0.278f,
			0,0.156f,0.510f,0.720f
		}
	};
	 
	public static final float[][] Self_Adapter_Data_TRASLATE= 
	{//换视角    暂停   加速器   计速器   雷达[x,y,width]	---新加一个刹车按钮          //320x480的数据暂时没有给出，需要进行测试
		{-1.15f, 0.09f, 0.264f ,1.9f, 0.09f, 0.264f, 1.65f, -1.5f, 0.64f, -1.4f, 0.40f, 0.295f, 1.3f, 0.40f, 0.31f, -1.0f, -1.53f, 0.5f},
		{-1.27f, 0.09f, 0.264f ,2.0f, 0.09f, 0.264f, 1.75f, -1.5f, 0.63f, -1.5f, 0.40f, 0.295f, 1.4f, 0.40f, 0.3f, -1.07f, -1.52f, 0.55f}, 
		{-1.27f, 0.09f, 0.264f ,2.0f, 0.09f, 0.264f, 1.75f, -1.5f, 0.63f, -1.5f, 0.40f, 0.295f, 1.4f, 0.40f, 0.3f, -1.09f, -1.52f, 0.55f}, 
		{-1f, 0.09f, 0.264f ,1.76f, 0.09f, 0.264f, 1.47f, -1.56f, 0.62f, -1.2f, 0.40f, 0.297f, 1.18f, 0.40f, 0.3f, -0.79f, -1.53f, 0.5f}
	};
	
	//为后视镜的大小和后视镜框进行自适应的常量数据 0号为480x800 1号为480x854 2号为540x960 3号为320x480
	//依次为裁剪窗口的x，y，后视镜框的y偏移量，后视镜框的宽、高
	public static final float[][] Self_Adapter_Data_HSJ_XY=
	{
		{253,390,0.82f,0.66f,0.22f}, //0.82f,0.66f,0.22f
		{280,390,0.82f,0.66f,0.22f}, //0.82f,0.66f,0.22f
		{330,450,0.84f,0.59f,0.20f}, //0.84f,0.59f,0.20f
		{280,390,0.82f,0.66f,0.22f}  //0.82f,0.66f,0.22f
	};
	
	//为竞速模式服务的参数  
	//用来帮助记录英雄船名次的常量 
	public static int RANK_FOR_HELP=1;
	//用来记录英雄船的名次
	public static int RANK_FOR_HERO_BOAT=0;
	//用来记录其他船的圈数
	public static int[] BOAT_LAP_NUMBER_OTHER;
	//其他船的速度
	public static final float[] Max_BOAT_V_OTHER={1.26f,1.19f};
	//32*32的地图矩阵的路线
	public static final int[][] PATH=
	{
		{1,0},{0,0},{0,1},{0,2},{0,3},{0,4},{1,4},{2,4},{2,5},{2,6},
		{2,7},{2,8},{3,8},{3,9},{2,9},{2,10},{2,11},{2,12},{2,13},{2,14},
		{2,15},{3,15},{4,15},{4,14},{4,13},{4,12},{4,11},{5,11},{6,11},{7,11},
		{7,12},{8,12},{9,12},{9,11},{10,11},{11,11},{12,11},{13,11},{13,12},{12,12},
		{12,13},{13,13},{13,14},{13,15},{14,15},{14,16},{13,16},{13,17},{13,18},{13,19},
		{12,19},{12,20},{11,20},{10,20},{9,20},{9,21},{8,21},{7,21},{7,20},{6,20},
		{5,20},{4,20},{4,21},{5,21},{5,22},{5,23},{5,24},{5,25},{4,25},{3,25},
		{2,25},{2,26},{2,27},{2,28},{2,29},{3,29},{4,29},{5,29},{5,28},{6,28},
		{6,27},{6,26},{7,26},{8,26},{9,26},{10,26},{10,27},{11,27},{11,26},{12,26},
		{12,25},{13,25},{14,25},{15,25},{15,26},{16,26},{17,26},{17,25},{18,25},{19,25},
		{19,26},{20,26},{21,26},{22,26},{23,26},{23,25},{22,25},{21,25},{21,24},{22,24},
		{23,24},{24,24},{25,24},{25,25},{26,25},{26,24},{27,24},{28,24},{29,24},{29,23},
		{28,23},{28,22},{29,22},{29,21},{28,21},{28,20},{29,20},{29,19},{28,19},{28,17},
		{28,16},{28,15},{27,15},{26,15},{25,15},{24,15},{24,16},{23,16},{22,16},{21,16},
		{20,16},{20,15},{20,14},{20,13},{20,12},{20,11},{21,11},{22,11},{23,11},{23,10},
		{24,10},{25,10},{26,10},{26,9},{27,9},{27,8},{28,8},{28,7},{28,6},{29,6},
		{30,6},{30,5},{29,5},{29,4},{29,3},{29,2},{28,2},{27,2},{26,2},{26,1},
		{25,1},{25,2},{24,2},{23,2},{23,3},{22,3},{22,2},{21,2},{21,1},{20,1},
		{19,1},{19,2},{18,2},{18,1},{17,1},{16,1},{15,1},{15,2},{14,2},{14,1},
		{13,1},{13,2},{12,2},{11,2},{10,2},{9,2},{8,2},{8,3},{7,3},{7,2},
		{6,2},{5,2},{4,2},{3,2},{3,1},{3,0},{2,0}
	};	
	//是否暂停游戏标志位
	public static boolean isPaused=false;
	//是否打开后视镜标志位
	public static boolean isOpenHSJ=true;
	//暂停游戏时，船的速度，以用来回到游戏
	public static float CURR_BOAT_V_PAUSE=0;
	//暂停游戏时，船的加速度，以用来回到游戏
	public static float BOAT_A_PAUSE=0;
	public static int numberOfN2=0;//当前氮气数，初始为0
	static int maxNumberOfN2=5;//最大氮气数，最多只能同时拥有5个氮气
	public static boolean halfFlag=false;//是否行驶半圈的标志位
	public static int numberOfTurns=1;//圈数  
	final static int maxOfTurns=2; //最大圈数 
	//船的半径大小
	public static final float BOAT_UNIT_SIZE=2.0f;
	//用来锁定某些参数，保证不闪屏
	static Object lockA=new Object();
	//地图、3D物体宫格数控制参数
	public static final float NUMBER_MAP=3;     
	//小船向上翘的角度值,行驶状态中
	public static float head_Angle=4;
	//小船速度为0时，小船前后晃动的最大角度值
	public static final float head_Angle_Max=4.25f;
	//小船速度为0时，小船前后晃动值的加速度
	public static final float head_Angle_A=0.25f;
	//检测在船行驶时，速度是否大于该值，当大于该值时，将碰撞标志位置为false表示可以进行下一次的碰撞声音的播放
	public final static float CURR_BOAT_V_PZ=0.3f;	
	//水面换帧线程工作标志位   
	public static boolean threadFlag=true;  
	//水面的高度值
	public static float WATER_HIGH_ADJUST=0.0f;
	//每一个格子对应的宽度以及高度    
	static final float UNIT_SIZE=60.0f;	

	//摄像机每次转动的角度
	public static float DEGREE_SPAN=0;
	//摄像机每次移动的距离 (船的当前速度)
	public static float CURR_BOAT_V=0f;
	public static float CURR_BOAT_V_TMD=0;
	//船的最终最高速度
	public static final float Max_BOAT_V_FINAL=1.3f*1.3f; 
	//船的最高速度
	public static float Max_BOAT_V=1.3f; 
	//船的最高速度
	public static final float Max_BOAT_V_VALUE=1.3f;  
	//船的加速度
	public static float BOAT_A=0.0f;	
	//摄像机初始的视线方向，0表示为z轴负方向
	public static final float DIRECTION_INI=0;     
	//摄像机距离所观察目标点的距离
	static final float DISTANCE=4.0f; 
	//摄像机初始时相应观察点的Y坐标 
	static final float CAMERA_INI_Y=2;
	//小船初始XZ坐标
	public static float YACHT_INI_X=0;	
	public static float YACHT_INI_Z=UNIT_SIZE/2+1*UNIT_SIZE;	
	//纹理图片的宽度
	static final float TEXTURE_WIDTH=2.0f;
	//纹理图片的高度
	static final float TEXTURE_HEIGHT=2.0f;
	//陆地最大高差
	public static final float LAND_MAX_HIGHEST=10f;
	//陆地上每个顶点的高度数组(直道)  
	public static float[][] yArray_ZD;
	//陆地上每个顶点的高度数组(直道带小岛)  
	public static float[][] yArray_ZD_DXD;
	//陆地上每个顶点的高度数组(弯道)
	public static float[][] yArray_WD;
	//陆地上每个顶点的高度数组(平原)
	public static float[][] yArray_PD;
	//陆地行数
	public static int ROWS;
	//陆地列数
	public static int COLS;
	
	//32*32的地图矩阵
	public static final int[][] MAP_ARRAY=
	{
		{4,0,0,0,5,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
		{1,8,8,8,10,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
		{1,8,8,8,6,0,0,2,5,4,0,2,0,0,0,5,8,8,8,8,8,8,8,8,8,4,0,0,0,5,8,8},
		{15,9,14,8,8,8,8,8,6,7,8,8,8,8,8,10,8,8,8,8,8,8,8,8,8,1,8,8,8,10,8,8},
		{8,8,3,8,8,8,8,8,8,8,8,13,11,9,11,16,8,8,8,8,4,5,8,8,8,1,8,8,8,10,8,8},
		{8,8,1,8,8,8,8,8,8,8,8,10,8,8,8,8,8,8,8,8,1,6,0,0,0,7,8,8,13,16,8,8},
		{8,8,1,8,8,8,8,8,8,8,8,10,8,8,8,8,8,8,8,8,3,8,8,8,8,8,13,9,16,8,8,8},
		{8,8,15,14,8,8,8,8,8,8,8,6,5,8,8,8,8,8,8,8,15,14,8,8,8,8,10,8,8,8,8,8},
		{8,8,4,7,8,8,8,8,8,8,8,8,10,8,8,8,8,8,8,8,8,1,8,8,8,8,10,8,8,8,8,8},
		{8,8,1,8,8,8,8,8,8,8,8,13,16,8,8,8,8,8,8,8,4,7,8,8,8,8,10,8,8,8,8,8},
		{8,8,1,8,8,8,8,8,8,8,8,10,8,8,8,8,8,8,8,8,1,8,8,8,8,8,6,5,8,8,8,8},
		{8,8,1,8,8,8,8,8,8,8,8,10,8,8,8,8,8,8,8,8,1,8,8,8,8,8,13,16,8,8,8,8},
		{8,8,3,8,8,8,8,8,8,8,8,10,4,5,8,8,8,8,8,4,7,8,8,8,8,13,16,8,8,8,8,8},
		{8,4,7,8,8,8,8,8,8,8,8,6,7,6,2,5,4,0,2,7,8,8,8,8,8,10,8,8,8,8,8,8},
		{8,15,14,8,8,8,8,8,8,8,8,8,8,8,8,6,7,8,8,8,8,8,8,8,8,12,8,8,8,8,8,8},
		{8,4,7,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,6,5,8,8,8,8,8},
		{8,1,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,10,8,8,8,8,8},   
		{8,1,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,13,16,8,8,8,8,8},
		{8,15,14,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,10,8,8,8,8,8,8},
		{8,4,7,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,6,5,8,8,8,8,8},
		{8,1,8,8,8,8,8,8,8,8,8,13,9,11,9,9,14,8,8,8,8,8,8,8,8,8,12,8,8,8,8,8},
		{8,15,14,8,8,8,8,8,8,8,8,10,8,8,8,8,1,8,8,8,8,8,8,8,13,14,10,8,8,8,8,8},
		{8,8,15,14,8,8,8,8,8,8,8,10,8,8,8,8,1,8,8,8,8,8,8,8,10,1,10,8,8,8,8,8},
		{8,8,4,7,8,8,8,8,8,8,13,16,8,8,8,8,1,8,8,8,8,8,8,8,12,15,16,8,8,8,8,8},
		{8,8,1,8,8,8,8,8,8,8,10,8,8,8,8,4,7,8,8,8,8,8,8,8,10,8,8,8,8,8,8,8},		
		{8,4,7,8,8,8,8,8,8,8,10,8,8,8,8,1,8,8,8,8,8,8,8,8,6,5,8,8,8,8,8,8},
		{8,15,14,8,8,8,8,8,8,13,16,8,8,8,8,1,8,8,8,8,8,8,8,8,13,16,8,8,8,8,8,8},		
		{8,8,1,8,8,8,8,8,13,16,8,8,8,8,8,3,8,8,8,8,8,8,8,8,10,8,8,8,8,8,8,8},
		{8,8,3,8,8,8,13,11,16,8,8,8,8,8,8,15,9,9,9,14,13,14,13,14,10,8,8,8,8,8,8,8},
		{8,8,15,9,9,14,10,8,8,8,8,8,8,8,8,8,8,8,8,15,16,15,16,15,16,8,8,8,8,8,8,8},
		{8,8,8,8,8,15,16,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8},
		{8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8}
	};
	
	//由程序自动产生直道各行顶点Y坐标的方法
	//赛道宽度
	static int KD=13;  //赛道宽度
	static int PDKD=9;	//平底宽度必须为奇数
	static int rows=15;
	public static float zdYRowFunction(float k)
	{		
		//第一平直范围0~（行数减去平底的行数/2）即0~((rows-KD)/2)
		if(k<((rows-KD)/2))
		{
			return LAND_MAX_HIGHEST;
		}
		//第一下降范围((rows-KD)/2)～((rows-PDKD)/2)
		float span=((rows-PDKD)/2)-((rows-KD)/2);
		if(k<((rows-PDKD)/2))
		{
			return LAND_MAX_HIGHEST*(-((k-((rows-PDKD)/2))/span));
		}
		//中间平直范围
		if(k<((rows-PDKD)/2)+PDKD)
		{
			return 0;
		}
		//第一上升范围
		if(k<rows-(rows-KD)/2)
		{
			float ratio=(k-((rows-PDKD)/2+PDKD))/span;
			return LAND_MAX_HIGHEST*ratio;			
		}
		
		//最后平直范围
		return LAND_MAX_HIGHEST;
	}
	
	//产生直道Y坐标数组的方法
	public static void generateZDY()
	{
		int colsPlusOne=rows+1;
		int rowsPlusOne=rows+1;		
		yArray_ZD=new float[rowsPlusOne][colsPlusOne];
		
		for(int i=0;i<rowsPlusOne;i++)
		{
			float h=zdYRowFunction(i);
			for(int j=0;j<colsPlusOne;j++)
			{
				yArray_ZD[i][j]=h;  
			}
		}
	}
	
	//产生直道带小岛Y坐标数组的方法
	public static void generateZDY_XD(Resources resources)
	{
		float[][] xddata=loadLandforms(resources,R.drawable.xd,LAND_MAX_HIGHEST);
		
		int colsPlusOne=rows+1;
		int rowsPlusOne=rows+1;
		yArray_ZD_DXD=new float[rowsPlusOne][colsPlusOne];
		  
		for(int i=0;i<rowsPlusOne;i++)
		{
			float h=zdYRowFunction(i);
			for(int j=0;j<colsPlusOne;j++)
			{
				yArray_ZD_DXD[i][j]=h+xddata[i][j];    
			}
		}
	}  
	
	//产生弯道Y坐标数组的方法
	public static void generateWDY()
	{
		int colsPlusOne=rows+1;
		int rowsPlusOne=rows+1;		
		yArray_WD=new float[rowsPlusOne][colsPlusOne];		
		for(int i=0;i<rowsPlusOne;i++)
		{
			for(int j=0;j<colsPlusOne;j++)
			{
				float p=(float) Math.sqrt(i*i+j*j);				
				float h=zdYRowFunction(p);
				if(h>LAND_MAX_HIGHEST)
				{
					h=LAND_MAX_HIGHEST;
				}   
				if(h<LAND_MAX_HIGHEST/5.0f)
				{
					h=0;
				}
				yArray_WD[i][j]=h;  
			}
		}
	}
	
	//产生平地Y坐标数组的方法
	public static void generatePDY()
	{
		int colsPlusOne=rows+1;
		int rowsPlusOne=rows+1;		
		yArray_PD=new float[rowsPlusOne][colsPlusOne];		
		for(int i=0;i<rowsPlusOne;i++)
		{
			for(int j=0;j<colsPlusOne;j++)
			{
				yArray_PD[i][j]=LAND_MAX_HIGHEST;
			}
		}
	}
	
	//从灰度图片中加载陆地上每个顶点的高度
	public static float[][] loadLandforms(Resources resources,int index,float heightOffset)
	{
		Bitmap bt=BitmapFactory.decodeResource(resources, index);
		int colsPlusOne=bt.getWidth();
		int rowsPlusOne=bt.getHeight(); 
		float[][] result=new float[rowsPlusOne][colsPlusOne];
		for(int i=0;i<rowsPlusOne;i++)
		{
			for(int j=0;j<colsPlusOne;j++)
			{
				int color=bt.getPixel(j,i);
				int r=Color.red(color);
				int g=Color.green(color); 
				int b=Color.blue(color);
				int h=(r+g+b)/3;
				result[i][j]=h*heightOffset*0.9f/255;  
			}
		}
		bt.recycle();
		return result;
	}
	
	static int directNo=3;
	//根据角度的变化得到小船船头所指方向
	public static int getDirectionNumber(float angleForSpecFrame)
	{
		float tempAngle=angleForSpecFrame%360;
		if(tempAngle>=0)
		{
			if(tempAngle>225&&tempAngle<=315)
			{
				return 2;
			}
			else if(tempAngle>135&&tempAngle<=225)
			{
				return 1;
			}
			else if(tempAngle>45&&tempAngle<=135)
			{
				return 0;
			}
			else
			{
				return 3;
			}
		}
		else
		{
			if(tempAngle>-135&&tempAngle<=-45)
			{
				return 3;
			}
			else if(tempAngle>-225&&tempAngle<=-135)
			{
				return 2;
			}
			else if(tempAngle>-315&&tempAngle<=-225)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}		
	}
	
	//新添加的小山的坐标的数组
	public static float[][] yArray_Mountion;
	//山的最大高差
	public static final float SD_HEIGHT=40;   
	public static void generateMountion(Resources resources)
	{  
		yArray_Mountion=loadLandforms(resources,R.drawable.mountion_land_0,SD_HEIGHT);
	}
	//新添加的隧道的y坐标数组 
	public static float[][] yArray_Tunnel; 
	
	public static void generateTunnel(Resources resources)
	{ 
		yArray_Tunnel=loadLandforms(resources,R.drawable.mountion_land,SD_HEIGHT);
	}
}
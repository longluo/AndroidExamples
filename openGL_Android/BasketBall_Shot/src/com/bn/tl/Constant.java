package com.bn.tl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Constant
{	
	
	
	
	public static int startY=-60;//文字其实Y坐标
	public static int wenziSize=50;//文字每一行的间隔
	public static int wenziwidth=512;
	public static int wenziHeight=512;//文字背景图片的大小
	 public static Bitmap generateWLT(String s[],int width,int height)
	   {
		   Paint paint=new Paint();
		   paint.setARGB(255, 255,255, 255);
		   paint.setTextSize(40);
		   paint.setTypeface(null);  
		   paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		   Bitmap bmTemp=Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		   Canvas canvasTemp = new Canvas(bmTemp); 	
		   for(int i=0;i<s.length;i++){
			   canvasTemp.drawText(s[i], 35, startY+i*(wenziSize)+512, paint);
		   }
		   	   
		   return bmTemp;
	   }
	   

	   
	   static int cIndex=8;
	   static String[] content=
	   {
		   "将手指放于一个已经静止",
		   "的球所在的位置，然后向",
		   "上移动手指，一段距离后" ,
		   "，松开手指，即可实现投" ,
		   "球，如果球所在位置偏于" ,
		   "屏幕的左方，则手指滑动" ,
		   "时要稍微向右移动一点，" ,
		   "给予球X正方向速度，同" ,
		   "理如果球所在位置偏于屏" ,
		   "幕右边，则滑动时向屏幕" ,
		   "左边滑动一点距离给予球" ,
		   "X负方向的速度。",
		   "",
	   };
	
	
	
	static float gFactor=1.6f;//重力加速度缩放比例
	static float vFactor=(float) Math.sqrt(gFactor);//y方向速度缩放比例
	
	static int shipingJs;//视频线程计时器
	
	
	public static boolean isnoHelpView;//是否为帮助界面，true为是
	public static boolean isnoPlay=true;//是否在播放界面或者暂停界面，true为播放状态
	
	public static float shouX=0;
	public static float shouY=-0.9f;//手的xy坐标
	
	static Bitmap welcome;//欢迎界面图
	static Bitmap welcome2;
	static Bitmap dot;//加载进度
	//这里是3D场景中加载界面中 IO加载纹理图片
	public static void loadWelcomeBitmap(Resources r,int drawableId[])
	{
		  InputStream is=null;
          try  
          {
        	  
        	  is= r.openRawResource(drawableId[0]);	
        	  welcome = BitmapFactory.decodeStream(is);
        	  is= r.openRawResource(drawableId[1]);	
        	  dot=BitmapFactory.decodeStream(is);
        	  is= r.openRawResource(drawableId[2]);
        	  welcome2=BitmapFactory.decodeStream(is);
          } 
	      finally 
	      {
            try 
            {
                is.close();
            } 
            catch(IOException e) 
            {
                e.printStackTrace();
            }
	      }
	}
	public static boolean flag=true;//物理模拟线程是否停止
	public static boolean isCJmiusic=true;//场景音乐
	public static boolean isBJmiusic=false;//背景音乐
	
	
	
	
	public static float sXtart=0;//2D界面的起始坐标
	public static float sYtart=0;
	
	//手机屏幕的宽度和高度
	public static  float SCREEN_WIDHT=480;
	public static  float SCREEN_HEIGHT=854;
	//屏幕的缩放比例
	public static float ratio_width;
	public static float ratio_height;
	public static Bitmap scaleToFit(Bitmap bm,float width_Ratio,float height_Ratio)
	{		
    	int width = bm.getWidth(); 							//图片宽度
    	int height = bm.getHeight();							//图片高度
    	Matrix matrix = new Matrix(); 
    	matrix.postScale((float)width_Ratio, (float)height_Ratio);				//图片等比例缩小为原来的fblRatio倍
    	Bitmap bmResult = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);//声明位图        	
    	return bmResult;									//返回被缩放的图片
    }
	//---------------------组号和掩码--------------------
	public static final short GROUP_HOUSE=(short)0xffff;
	public static final short MASK_HOUSE=(short)0xffff;
	
	public static final short GROUP_BALL1=(short)1;//0xffff;
	public static final short MASK_BALL1=(short)1;
	public static final short GROUP_BALL2=(short)2;//0xffff;
	public static final short MASK_BALL2=(short)2;
	public static final short GROUP_BALL3=(short)4;//0xffff;
	public static final short MASK_BALL3=(short)4;
	//----------------------------------------------------
	
	public static final float STARTBALL_1[]=new float[]{-1f,0.4f,2.0041107f};
	public static final float STARTBALL_2[]=new float[]{0,0.4f,2.0041107f};
	public static final float STARTBALL_3[]=new float[]{1f,0.4f,2.0041107f};
	public static final float STARTBALL[][]=new float[][]
	                                     {STARTBALL_1,STARTBALL_2,STARTBALL_3};//三个篮球对应位置数组
	public static final float STARTBALL_V[][]=new float[][]{
		{0.95f,10.8f*vFactor,-3.0f},
		{0,10.8f*vFactor,-3.0f},
		{-0.95f,10.8f*vFactor,-3.0f}
	};
	public static final float CAMERA_Y_SK=45;//摄像机Y轴偏倚最大值角度
	public static final float CAMERA_Y_SK_FH=5f;//摄像机返回原来位置时，每次Y偏移大小
	public static final float ZJ_LENGTH=0.20f;//l篮球支架长度
	public static final float ZJ_R=0.031f;//篮球支架半径
	public static final float LANQIU_WIDTH=4f;//篮球架的宽度
	public static final float LANQIU_HEIGHT=3.2f;//篮球架的高度
	
	public static final float LANKUANG_R=0.65f;//篮筐半径
	public static final float LANKUANG_JM_R=0.032f;//篮筐截面半径
	public static final float UNIT_SIZE=1;
	public static final float LANWANG_H=LANKUANG_R*1.5f;//篮网的高度
	public static int lanWangRaodon;//篮网扰动值
	//屋子的长、宽、高
	public static final float CHANGJING_WIDTH=4.1f; 
	public static final float CHANGJING_HEIGHT=7f;
	public static final float CHANGJING_LENGTH=4f;
	
	public static final float LANBAN_BILIXISHU=0.08f;//篮球架大小比例系数。
	public static final float LANBAN_X=0;//篮板的位置x坐标
	public static final float LANBAN_Y=5;//篮板的位置y坐标
	public static final float LANBAN_Z=-1.0f;//篮板的位置z坐标
	public static final float YBB_WIDTH=2;//仪表板宽度
	public static final float YBB_HEIGHT=0.3f;//仪表板高度
	public static final float QIU_SPAN=11.25f;//将球进行单位切分的角度
	
	
	//球的切割分数、球大小
	public static final float QIU_SPAN_SHU=15f;
	public static final float QIU_R=0.35f;//球半径
	//重力加速度
	public static float G=-10f*gFactor;
	//摄像机目标位置
	public static float CAMERA_X=0;
	public static float CAMERA_Y=CHANGJING_HEIGHT/2-0.35f;
	public static float CAMERA_Z=(CHANGJING_LENGTH+2.4f+8.5f);
	
	public static final float DISTANCE=CAMERA_Z;//LENGTH;
	
	//仪表板中单个数字的大小
	public static final float SHUZI_KUANDU=0.1f;
	public static final float SHUZI_GAODU=0.12f;
	
	public static float[] ringCenter;//篮筐中心点坐标
	public static float ringR;//篮筐半径
	//当前得分哦
	public static int defen=0;//得分
	public static int daojishi=60;//游戏倒计时
	public static int deadtimesMS;//游戏倒计时毫秒数
	
	//菜单界面
	public static final int SHENGYING_KG_JIEMIAN=1;
	public static final int CAIDAN_JIEMIAN=2;
	public static final int JIAZAI_JIEMIAN=3;
	public static final int BANGZHU_JIEMIAN=4;
	public static final int GUANYU_JIEMIAN=5;
	public static final int YOUXI_JIEMIAN=6;
	public static final int JIESHU_JIEMIAN=7;
	public static final int CAIDAN_RETRY=8;	
	public static final int JILU_JIEMIAN=9;
	
	public static float LEFT=115f; //菜单位置
    //线程标志位
	public static boolean SHENGYING_FLAG=true;//声音	标记
	public static boolean SOUND_MEMORY=false;//用于记录声音玩家的选择
	public static boolean DEADTIME_FLAG=false;//倒计时线程标记
	public static boolean MENU_FLAG=false;//菜单按钮绘制线程标记
	
	public static final FloatBuffer[][] mianFXL=new FloatBuffer[][]{//先是位置，然后是法向量
		{getBuffer(0,0.005f,0),getBuffer(0,1,0),},//地面
		{getBuffer(-CHANGJING_WIDTH/2+0.005f,0,0),getBuffer(1,0,0)},//左面
		{getBuffer(CHANGJING_WIDTH/2-0.005f,0,0),getBuffer(-1,0,0)},//右面
		{getBuffer(0,0,-CHANGJING_LENGTH/2+0.005f),getBuffer(0,0,1)},//篮板后面 的面
		{getBuffer(0,0,LANBAN_Z+LANBAN_BILIXISHU+0.01f),getBuffer(0,0,1)},//篮板影子平面
	};
	public static FloatBuffer getBuffer(float x,float y,float z){//出入三个坐标，得到该数组的缓冲
		float[] lightLocation=new float[]{0,0,0};
		FloatBuffer lightPositionFB;
		lightLocation[0]=x;
    	lightLocation[1]=y;
    	lightLocation[2]=z;
    	ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        lightPositionFB=llbb.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
        return lightPositionFB;
	}
}
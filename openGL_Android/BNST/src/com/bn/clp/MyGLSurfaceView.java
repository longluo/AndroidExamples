package com.bn.clp;
import static com.bn.clp.TDObjectData.*;
import static com.bn.clp.KEatData.*;
import static com.bn.clp.KZBJData.*;
import com.bn.R;
import static com.bn.clp.Constant.*;
import static com.bn.clp.TreeData.*;  
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.bn.core.MatrixState;
import com.bn.clp.KeyThread;
import com.bn.st.d2.MyActivity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView
{   
	//游戏中记录暂停时已经用的时间  
	public static long betweenStartAndPauseTime;
	//游戏中记录暂停时已经用的时间  
	
	//竞速模式相关参数
	//其他船的数量
    static final int qtCount=2;
    //其他船的坐标 x z angle
    static float[][] otherBoatLocation=new float[qtCount][3];
    //用来防止抖的其他船的坐标的x z angle
    static float[][] otherBoatLocationForHelp=new float[qtCount][3];
    //其他船的路径轨迹
    public ArrayList<ArrayList<float[]>> otherPaths=new ArrayList<ArrayList<float[]>>();
	
	//当前的进度值
	int curr_process=0;
	MyActivity ma;
	
	//声明渲染器的引用
	private SceneRenderer sRenderer;
	//铺在河道两侧纹理的ID
	int rt_testur_Id;
	//系统分配的水面纹理id  
	int textureFlagId;    
	//桥的纹理Id
	int bridge_id; 
	//山上石头的纹理id
	int rock_id;  
	//系统分配的灌木纹理id
	int textureShrubId0; 
	//灌木1纹理id
	int textrueShrubId1;
	//系统分配的灌木纹理id
	int textureShrubId2;	
	//灌木1纹理id
	int textrueShrubId3;
	//新添加的，飞艇需要的两个纹理id，主要有飞艇身体以及后面的尾翼
	int texAirShipBody;//飞艇身体部分的id
	int texAirShipWy;//飞艇的尾翼部分的id	
	//新添加的，飞艇需要的两个纹理id，主要有飞艇身体以及后面的尾翼
	int waterId;	
	//新添加的，河道上面的石头纹理id
	int raceTrackRockId;
	//新添加的，广告牌子需要的纹理id，柱子纹理以及广告
	int ggSzTexId;
	int[] ggTexId=new int[3];
	//游戏开始和结束的条幅纹理id
	int gameStartTexId;
	int gameEndTexId;
	//船埠头的纹理id
	int dockTexId;
	//雷达地图的纹理id
	int radarBackGroundTexId;
	//雷达指针的纹理id
	int radarZhiZhenTexId;
	//雷达中其他船的纹理id
	int radarOtherBoatTexId;
	//城堡需要的三个纹理id
	int castleTexIdA;
	int castleTexIdB;
	
	//观察点位置的三个坐标
	static float cx;
	static float cy;
	static float cz;
	//目光目标点的三个坐标
	static float tx; 
	static float ty;
	static float tz;
	//实现的角度
	public static float sight_angle=DIRECTION_INI;
	
	public static float yachtLeftOrRightAngle=0;//帆船左右转
	static float yachtLeftOrRightAngleMax=15;
	public static float yachtLeftOrRightAngleA=2.5f;
	public static final float yachtLeftOrRightAngleValue=2.5f;
	
	static float bx;//帆船x坐标
    static float bz;//帆船z坐标
    static float bxForSpecFrame;//帆船x坐标
    static float bzForSpecFrame;//帆船z坐标    
    static float angleForSpecFrame;//小船转动的角度值(含扰动)
    static float angleForSpecFrameS;//小船转动的角度值(不含扰动)
    static float cxForSpecFrame;
    static float czForSpecFrame;
        
    public static int keyState=0;//键盘状态  1-up 2-down 4-left 8-right
    public KeyThread  kt;//键盘状态监控线程
    public ThreadColl tc;//可碰撞部件监控线程
    
    static int bCol;//船所在地图的位置列
	static int bRow;//船所在地图的位置行
	static int bColOld;//船所在地图的位置列
	static int bRowOld;//船所在地图的位置行
	
	//新添加的BNDrawer的一维数组   
	BNDrawer[] bndrawer;
	//存储TDObjectForControl的集合
	public List<TDObjectForControl> tdObjectList=new ArrayList<TDObjectForControl>();
	public List<int[]> texIdList=new ArrayList<int[]>();
	
	public List<PZZ> pzzList=new ArrayList<PZZ>();
		
	//存储SpeedForControl的集合
	SpeedForEat[] speedForEat;
	public List<SpeedForControl> speedWtList=new ArrayList<SpeedForControl>();
	int speedUpTexId;//加速物件的纹理id
	int speedDownTexId;//减速物体纹理id
	public ThreadForEat tfe;
	//树
	SingleShrub ss;
	public List<ShrubForControl> treeList=new ArrayList<ShrubForControl>();
	
	//倒计时牌的绘制者
	public DaoJiShiForDraw djsfd;
	
	//天空穹
	Sky sky;
	int sky_texId;
	//存储KZBJForControl的集合
	KZBJDrawer[] kzbj_drawer;
	public List<KZBJForControl> kzbjList=new ArrayList<KZBJForControl>();
	//交通柱，交通锥
	KZBJDrawer[] kzbj_array;
	//交通柱和交通锥的纹理id
	int jt_texId;
	//缩放比例
	float ratio;
	//仪表板纹理id
	int ybbTexId;
	
	//进度资源
    static Bitmap bmbackGround;
    static Bitmap bmPgsDt;
    static Bitmap bmNum;
    static Bitmap bmPgsFgt;
    
    //游戏开始时间
    static long gameStartTime;
    
    //是否倒计时标志位
	public static boolean isDaoJiShi=true;
	//总计时是否开始标志位
	public static boolean isJiShi=false;
	//倒计时是否结束，结束后暂停和切换视角按钮可按
	public static boolean isAllowToClick=false;
	//是否在刹车的标志位
	public boolean isShaChe=false;
    
    public static void loadProgressBitmap(Resources r)
    {
    	InputStream is=null;
        try  
        {
      	  is= r.openRawResource(R.drawable.load_bj);	
      	  bmbackGround=BitmapFactory.decodeStream(is);
      	  is= r.openRawResource(R.drawable.load_dt);	
      	  bmPgsDt=BitmapFactory.decodeStream(is);
      	  is=r.openRawResource(R.drawable.load_fgt);
    	  bmPgsFgt=BitmapFactory.decodeStream(is);
      	  is= r.openRawResource(R.drawable.number);	
      	  bmNum=BitmapFactory.decodeStream(is);      	             	  
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
    //触摸监听器
    @Override
	public boolean onTouchEvent(MotionEvent e)
	{
		float x=e.getX();
		float y=e.getY();
		float yRatio=y/com.bn.st.xc.Constant.SCREEN_HEIGHT;
        float xRatio=x/com.bn.st.xc.Constant.SCREEN_WIDTH;   
		switch(e.getAction())
		{
		case MotionEvent.ACTION_DOWN:	
			if(isAllowToClick)
			{
				//加速按钮
				if
				(
						xRatio>Self_Adapter_Data_ON_TOUCH[screenId][10]&&xRatio<Self_Adapter_Data_ON_TOUCH[screenId][11]
						&&yRatio>Self_Adapter_Data_ON_TOUCH[screenId][8]&&yRatio<Self_Adapter_Data_ON_TOUCH[screenId][9]
				        &&!isPaused
				)
				{
					if(numberOfN2>0)
					{		
						if(SoundEffectFlag)
						{
							ma.shengyinBoFang(2, 0);
						}						
						numberOfN2=numberOfN2-1;
						Max_BOAT_V=Max_BOAT_V_FINAL;
						kt.dqCount=80;						
					}
					else
					{
						numberOfN2=0; 
					}
				}//暂停和开始按钮
				else if
				(
						xRatio>Self_Adapter_Data_ON_TOUCH[screenId][6]&&xRatio<Self_Adapter_Data_ON_TOUCH[screenId][7]
						&&yRatio>Self_Adapter_Data_ON_TOUCH[screenId][4]&&yRatio<Self_Adapter_Data_ON_TOUCH[screenId][5]
				)
				{
					if(isPaused)
					{//暂停态到运行态						
						CURR_BOAT_V=CURR_BOAT_V_PAUSE;
						BOAT_A=BOAT_A_PAUSE;
						DEGREE_SPAN=2f;
						CURR_BOAT_V_PAUSE=0;
						BOAT_A_PAUSE=0;
						isPaused=false; 
						kt.moveFlag=true;
						KeyThread.otherBoatFlag=true;
						//新添加的
						gameStartTime=System.currentTimeMillis();
						//新添加的
					}
					else if(!isPaused)
					{//运行态到暂停态				
						CURR_BOAT_V_PAUSE=CURR_BOAT_V;
						BOAT_A_PAUSE=BOAT_A; 
						CURR_BOAT_V=0;
						BOAT_A=0;
						DEGREE_SPAN=0;
						isPaused=true;
						kt.moveFlag=false;
						KeyThread.otherBoatFlag=false;
						//新添加的
						betweenStartAndPauseTime=gameContinueTime()+betweenStartAndPauseTime;
						//新添加的
						CURR_BOAT_V_TMD=0;
					}
				}//第一人称和第三人称按钮
				else if
				(
						xRatio>Self_Adapter_Data_ON_TOUCH[screenId][14]&&xRatio<Self_Adapter_Data_ON_TOUCH[screenId][15]
						&&yRatio>Self_Adapter_Data_ON_TOUCH[screenId][12]&&yRatio<Self_Adapter_Data_ON_TOUCH[screenId][13]
						&&!isPaused
				)
				{
					isShaChe=true;
					BOAT_A=-0.02f;					
				}
				else if
				(
						xRatio>Self_Adapter_Data_ON_TOUCH[screenId][18]&&xRatio<Self_Adapter_Data_ON_TOUCH[screenId][19]
						&&yRatio>Self_Adapter_Data_ON_TOUCH[screenId][16]&&yRatio<Self_Adapter_Data_ON_TOUCH[screenId][17]						
				)
				{
					if(isOpenHSJ)
					{
						isOpenHSJ=false;
					}
					else
					{
						isOpenHSJ=true;
					}					
				}
			}			
			break; 
		case MotionEvent.ACTION_UP:
				if
				(
						isShaChe&&!isPaused
				)
				{
					isShaChe=false;
					BOAT_A=0.025f;		
				}
			break;
		}
		return true;		
	}
	
	public MyGLSurfaceView(Context context)
	{
		super(context);
		
		ma=(MyActivity)context;
		
		this.setKeepScreenOn(true);
		
		if(isSpeedMode)
		{
			//从常量类中加载其他船的初始位置 快
			otherBoatLocation[0][0]=YACHT_INI_X-3;
			otherBoatLocation[0][1]=YACHT_INI_Z-3;
			otherBoatLocation[0][2]=0;
			//慢
			otherBoatLocation[1][0]=YACHT_INI_X+3;
			otherBoatLocation[1][1]=YACHT_INI_Z-4;
			otherBoatLocation[1][2]=0;
		}		
		
		//从常量类中加载英雄船的初始位置
        bx=YACHT_INI_X;
        bz=YACHT_INI_Z;
        
        cx=(float)(bx+Math.sin(Math.toRadians(sight_angle))*DISTANCE);;//摄像机x坐标
        cy=CAMERA_INI_Y;//摄像机y坐标
        cz=(float)(bz+Math.cos(Math.toRadians(sight_angle))*DISTANCE);//摄像机z坐标
        
        tx=(float)(cx-Math.sin(Math.toRadians(sight_angle))*DISTANCE);//观察目标点x坐标  
        ty=CAMERA_INI_Y-1.5f;//平视观察目标点y坐标
        tz=(float)(cz-Math.cos(Math.toRadians(sight_angle))*DISTANCE);//观察目标点z坐标
        
        //使小船 永远向前走
        keyState=keyState|0x1;
		
		//设置使用ES2.0
		this.setEGLContextClientVersion(2);
		sRenderer=new SceneRenderer();
		//设置渲染器   
		setRenderer(sRenderer);
		//设置渲染模式为主动渲染
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
		 //创建并启动键盘监控线程
        kt=new KeyThread(this,ma);         
                
        tc=new ThreadColl(this);        
        
        tfe=new ThreadForEat(this);              
	}

	//创建渲染器
	private class SceneRenderer implements GLSurfaceView.Renderer
	{
		//新添加的
		BackGround bgd;
		//进度条的底部
		BackGround pgs_dt;
		//进度条上面的图
		Process pgs_fgt;
		//总背景图的id
		int backGroundId;
		//进度条的底部图id
		int pgs_dt_id;
		//进度条的覆盖图的id
		int pgs_fgt_id;
		//进度条上的数字
		BackGround[] no=new BackGround[11];
		int no_texId;
		//进度条上的数字
		
		//进度条界面中大背景图的纹理坐标数组
		final float[] bg_texCoor=new float[]
        {
			0,0,  0,1,  1,1,
			0,0,  1,1,  1,0
        };
		//进度条下方的纹理坐标数组
		final float[] pgs_dt_texCoor=new float[]
        {
			0,0,  0,1f,  1,1f,
			0,0,  1,1f,  1,0
        };
		//进度条上方的覆盖图的纹理坐标数组
		final float[] pgs_fgt_texCoor=new float[]
        {
			0,0,  0,1f,  1,1f,
			0,0,  1,1f,  1,0
        };
		//声明直道的引用
		RaceTrack rtzd;
		//声明直道带小岛的引用
		RaceTrack rtzddxd;
		//声明弯道的引用
		RaceTrack rtwd;
		//声明水面的引用
		Water water;
		//船
		Boat boat;
		//快船
		Boat quickBoat;
		//慢船
		Boat slowBoat;
		//尾浪
		WeiLang wl;
		//仪表板
		Dashboard db;
		//计时器和lap
		DrawTime dt;
		//计时器和lap和氮气的纹理id
		int timeTexId;
		//加速按钮的纹理id
		int goTexId;
		//刹车按钮的纹理id
		int shacheTexId;
		//新添加的，游戏开始和结束
		StartAndEnd gameStartAndEnd;		
		//倒计时牌的纹理id
		int djsTexId;
		//雷达底图的绘制者
		com.bn.st.xc.TextureRect radar_Background;
		//雷达指针的绘制者
		com.bn.st.xc.TextureRect radar_Zhizhen;
		//雷达中其他船的绘制者
		com.bn.st.xc.TextureRect other_Radar_Zhizhen;
		//后视镜的绘制者
		com.bn.st.xc.TextureRect houshijing;
		//后视镜纹理ID
		int houshijingTexId;
		
		final float[] weilang_texCoor=new float[]
		{
			0,0,  0,1,  1,1,
			0,0,  1,1,  1,0
        };
    	public boolean isBegin=true;
    	int  frameCount=0;
    	
    	int[][] sdHz=new int[25][2];
    	int sdCount=0;
    	
    	//船的纹理id数组
    	int[] heroBoatTexId;
    	int[] quickBoatTexId;
    	int[] slowBoatTexId;
    	
    	float startST;
    	float stK;
    	
		@Override
		public void onDrawFrame(GL10 gl)
		{
			if(isBegin)
			{
				bColOld=-100;//船所在地图的位置列
				bRowOld=-100;//船所在地图的位置行
				//清除深度缓存以及颜色缓存
				GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);				
				//调用此方法计算产生正交投影矩阵
				MatrixState.setProjectOrtho(-1, 1, -1, 1, 1, 10);  
				//调用此方法产生摄像机9参数位置矩阵
				MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);
				MatrixState.copyMVMatrix();
				MatrixState.pushMatrix();
				MatrixState.translate(0, 0, -2);
				bgd.drawSelf(backGroundId);
				MatrixState.popMatrix(); 
				
				//开启混合    
	            GLES20.glEnable(GLES20.GL_BLEND);  
	            //设置混合因子
	            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				
				//绘制覆盖图的底图
				MatrixState.pushMatrix();
				MatrixState.translate(0, -0.15f, -1.99f);
				pgs_dt.drawSelf(pgs_dt_id);
				MatrixState.popMatrix();
				
				//绘制覆盖图
				MatrixState.pushMatrix();
				MatrixState.translate(0, -0.15f, -1.98f);
				pgs_fgt.drawSelf(pgs_fgt_id); 
				MatrixState.popMatrix(); 				
								
				//绘制进度条上方的数字
				String tempStr=curr_process+"";
				for(int i=0;i<tempStr.length();i++)   
				{   
					MatrixState.pushMatrix(); 
					MatrixState.translate(0.1f*(i-1), -0.15f, -1.97f);
					no[tempStr.charAt(i)-'0'].drawSelf(no_texId); 
					MatrixState.popMatrix(); 
				}
				
				//绘制百分号
				MatrixState.pushMatrix(); 
				MatrixState.translate(0.1f*tempStr.length(), -0.15f, -1.97f);
				no[10].drawSelf(no_texId); 
				MatrixState.popMatrix();
				//关闭混合
	            GLES20.glDisable(GLES20.GL_BLEND);   
	            
	            if(frameCount<2)
	            {
	            	frameCount++;
	            }
	            else
	            {
	            	this.readLoadTask();
	            }
			}
			else
			{
				if(isDaoJiShi)
				{
					djsfd.djst.start();
					isDaoJiShi=false;
				}
				//copy得到当前的纹理坐标偏移量
				startST=water.currStartST;
				//清除深度缓冲与颜色缓冲
				GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);				
				//设置投影模式矩阵
				MatrixState.setProjectFrustum(-ratio*0.7f, ratio*0.7f, -0.7f*0.7f, 1.3f*0.7f, 1, 300);
				
				synchronized(lockA)
				{
					//调用此方法产生摄像机9参数位置矩阵
					MatrixState.setCamera(cx, cy, cz, tx, ty, tz, 0, 1, 0);
					//新增加的（防止闪）
					MatrixState.copyMVMatrix();
					bxForSpecFrame=bx;
					bzForSpecFrame=bz;  
					angleForSpecFrameS=sight_angle;
					angleForSpecFrame=angleForSpecFrameS+yachtLeftOrRightAngle;					
					cxForSpecFrame=cx;
					czForSpecFrame=cz;					
					directNo=getDirectionNumber(sight_angle);
					if(isSpeedMode)
					{
						for(int i=0;i<otherBoatLocation.length;i++)
						{
							otherBoatLocationForHelp[i][0]=otherBoatLocation[i][0];
							otherBoatLocationForHelp[i][1]=otherBoatLocation[i][1];
							otherBoatLocationForHelp[i][2]=otherBoatLocation[i][2];
						}
					}					
				}
				
				bCol=(int)(Math.floor((cxForSpecFrame+UNIT_SIZE/2)/UNIT_SIZE));//船所在地图的位置列
	        	bRow=(int)(Math.floor((czForSpecFrame+UNIT_SIZE/2)/UNIT_SIZE));//船所在地图的位置行
				//绘制天空穹倒影
	            sky.drawSelf(sky_texId,bxForSpecFrame, bzForSpecFrame,1);
				
				//绘制天空穹
	            sky.drawSelf(sky_texId,bxForSpecFrame, bzForSpecFrame,0);
	            
	            //绘制筛选预备工作
	            if(bColOld!=bCol||bRowOld!=bRow)
	            {
	            	//绘制赛道准备
		            sdYb();
		            //绘制3D物体准备
		            tdYb();
		            //绘制可撞飞物体准备
		            kzYb();
		            //绘制树的预备
		            treeYb();
		            //吃了加减速物体预备
		            speedForEatYb();
	            }	            
	            
				//关闭背面剪裁
	            GLES20.glDisable(GLES20.GL_CULL_FACE);            
	            //绘制赛道及其倒影
				drawSD(startST);			
				//打开背面剪裁
	            GLES20.glEnable(GLES20.GL_CULL_FACE);
				
	            //绘制3D物体的倒影
	            DrawTDObjects(1);	            
	            //绘制3D物体
	            DrawTDObjects(0);
	               
	            //绘制船倒影
	            boat.drawSelf(bxForSpecFrame, 0.3f, bzForSpecFrame,angleForSpecFrame,1,heroBoatTexId);
	            if(isSpeedMode)
	            {
	            	 //绘制其他船 
		            for(int i=0;i<otherBoatLocationForHelp.length;i++)
		            {
		            	if(i==0)
		            	{
		            		quickBoat.drawSelf(otherBoatLocationForHelp[i][0], 0.3f, otherBoatLocationForHelp[i][1],otherBoatLocationForHelp[i][2],1,quickBoatTexId);
		            	}
		            	else if(i==1)
		            	{
		            		slowBoat.drawSelf(otherBoatLocationForHelp[i][0], 0.3f, otherBoatLocationForHelp[i][1],otherBoatLocationForHelp[i][2],1,slowBoatTexId);
		            	}	            	 
		            }
	            }
	            
	            drawKZBJ(1);
	            
	            //关闭背面剪裁
	            GLES20.glDisable(GLES20.GL_CULL_FACE);
	            //开启混合
	            GLES20.glEnable(GLES20.GL_BLEND);  
	            //设置混合因子
	            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	            //绘制树
	            drawTrees(1); 
	            //关闭混合  
	            GLES20.glDisable(GLES20.GL_BLEND);     
	            
	            //开启混合
	            GLES20.glEnable(GLES20.GL_BLEND);
	            //设置混合因子
	            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	        	drawSpeedForEat(1);
	        	//关闭混合  
	            GLES20.glDisable(GLES20.GL_BLEND);     
	        	
	            drawStartAndEnd(numberOfTurns,1);  
	            //开启混合
	            GLES20.glEnable(GLES20.GL_BLEND);  
	            //设置混合因子
	            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	            //绘制水面
				drawWater(startST);
				//关闭混合  
	            GLES20.glDisable(GLES20.GL_BLEND); 
				//打开背面剪裁
	            GLES20.glEnable(GLES20.GL_CULL_FACE);    
	            
	            drawKZBJ(0);
	            stK=wl.currStartST; 
	            boat.drawSelf(bxForSpecFrame, 0.3f, bzForSpecFrame,angleForSpecFrame,0,heroBoatTexId);
	            if(isSpeedMode)
	            {
	            	 //绘制其他船
		            for(int i=0;i<otherBoatLocationForHelp.length;i++)
		            {
		            	if(i==0)
		            	{
		            		quickBoat.drawSelf(otherBoatLocationForHelp[i][0], 0.3f, otherBoatLocationForHelp[i][1],otherBoatLocationForHelp[i][2],0,quickBoatTexId);
		            	}
		            	else if(i==1)
		            	{
		            		slowBoat.drawSelf(otherBoatLocationForHelp[i][0], 0.3f, otherBoatLocationForHelp[i][1],otherBoatLocationForHelp[i][2],0,slowBoatTexId);
		            	}	            	 
		            }
	            }	           
				MatrixState.pushMatrix(); 
				//开启混合
	            GLES20.glEnable(GLES20.GL_BLEND);
	            //设置混合因子
	            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				MatrixState.translate(bxForSpecFrame, 0.02f, bzForSpecFrame);
				MatrixState.rotate(-90, 1, 0, 0);
				MatrixState.rotate(angleForSpecFrameS, 0, 0, 1);
				wl.drawSelf(waterId,stK);
				//关闭混合  
	            GLES20.glDisable(GLES20.GL_BLEND);
				MatrixState.popMatrix();
	            //关闭背面剪裁
	            GLES20.glDisable(GLES20.GL_CULL_FACE);
	            //开启混合
	            GLES20.glEnable(GLES20.GL_BLEND);  
	            //设置混合因子
	            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	            //绘制树
	            drawTrees(0);
				//关闭混合    
	            GLES20.glDisable(GLES20.GL_BLEND);    
	            drawStartAndEnd(numberOfTurns,0);  
	            //开启混合
	            GLES20.glEnable(GLES20.GL_BLEND);
	            //设置混合因子
	            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	        	drawSpeedForEat(0);
	        	//关闭混合    
	            GLES20.glDisable(GLES20.GL_BLEND);    
	        	drawDaoJiShi();
	            if(isOpenHSJ)
	            {
	            	drawHouShiJing();
	            }  
	            //在绘制仪表板的时候已经对投影模式和摄像机的位置进行了重新的设置，所以在
	            //绘制计时器和lap的时候没有再次进行设置
	            drawYiBiaoBan();
	            drawTimeAndLap();
	            if(isSpeedMode)
	            {
	            	drawRadar();
	            }	 
	            
	            bColOld=bCol;
	            bRowOld=bRow;
	            if(isOpenHSJ)
	            {
	            	drawHSJKuang();
	            }
			}	
		}
		
		//绘制后视镜的方法
		public void drawHouShiJing()
		{
            //启用剪裁测试
        	GLES20.glEnable(GL10.GL_SCISSOR_TEST);
        	//设置区域
        	System.out.println(screenId+"  screenId");
        	GLES20.glScissor((int)Self_Adapter_Data_HSJ_XY[screenId][0],(int)Self_Adapter_Data_HSJ_XY[screenId][1],300,90);
        	//清除颜色缓存于深度缓存
            GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-1.0f*ratio, ratio, -1.7f*0.7f, 0.3f*0.7f, 1, 400);
            //调用此方法产生摄像机9参数位置矩阵
			MatrixState.setCamera
			(
				cx,
				cy,
				cz,
				(float)(cx+Math.sin(Math.toRadians(sight_angle))*DISTANCE), 
				ty+1.1f, 
				(float)(cz+Math.cos(Math.toRadians(sight_angle))*DISTANCE), 
				0, 
				1, 
				0
			);
			MatrixState.copyMVMatrix();
			
            sky.drawSelf(sky_texId,bxForSpecFrame, bzForSpecFrame,1);
            sky.drawSelf(sky_texId,bxForSpecFrame, bzForSpecFrame,0);
			//关闭背面剪裁
            GLES20.glDisable(GLES20.GL_CULL_FACE);            
			drawSD(startST);			
			//打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
			
            //绘制3D物体的倒影
            DrawTDObjects(1);	            
            //绘制3D物体
            DrawTDObjects(0);
            boat.drawSelf(bxForSpecFrame, 0.3f, bzForSpecFrame,angleForSpecFrame,1,heroBoatTexId);
            if(isSpeedMode)
            {
            	 //绘制其他船 
	            for(int i=0;i<otherBoatLocationForHelp.length;i++)
	            {
	            	if(i==0)
	            	{
	            		quickBoat.drawSelf(otherBoatLocationForHelp[i][0], 0.3f, otherBoatLocationForHelp[i][1],otherBoatLocationForHelp[i][2],1,quickBoatTexId);
	            	}
	            	else if(i==1)
	            	{
	            		slowBoat.drawSelf(otherBoatLocationForHelp[i][0], 0.3f, otherBoatLocationForHelp[i][1],otherBoatLocationForHelp[i][2],1,slowBoatTexId);
	            	}	            	 
	            }
            }
            drawKZBJ(1);
            //关闭背面剪裁
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            //绘制树
            drawTrees(1); 
            //关闭混合  
            GLES20.glDisable(GLES20.GL_BLEND);     
            //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        	drawSpeedForEat(1);
        	//关闭混合  
            GLES20.glDisable(GLES20.GL_BLEND);     
            drawStartAndEnd(numberOfTurns,1);  
            //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            //绘制水面
			drawWater(startST);
			//关闭混合  
            GLES20.glDisable(GLES20.GL_BLEND); 
			//打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);    
            drawKZBJ(0);
            boat.drawSelf(bxForSpecFrame, 0.3f, bzForSpecFrame,angleForSpecFrame,0,heroBoatTexId);
            if(isSpeedMode)
            {
            	 //绘制其他船
	            for(int i=0;i<otherBoatLocationForHelp.length;i++)
	            {
	            	if(i==0)
	            	{
	            		quickBoat.drawSelf(otherBoatLocationForHelp[i][0], 0.3f, otherBoatLocationForHelp[i][1],otherBoatLocationForHelp[i][2],0,quickBoatTexId);
	            	}
	            	else if(i==1)
	            	{
	            		slowBoat.drawSelf(otherBoatLocationForHelp[i][0], 0.3f, otherBoatLocationForHelp[i][1],otherBoatLocationForHelp[i][2],0,slowBoatTexId);
	            	}	            	 
	            }
            }	           
			MatrixState.pushMatrix(); 
			//开启混合
            GLES20.glEnable(GLES20.GL_BLEND);
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			MatrixState.translate(bxForSpecFrame, 0.02f, bzForSpecFrame);
			MatrixState.rotate(-90, 1, 0, 0);
			MatrixState.rotate(angleForSpecFrameS, 0, 0, 1);
			wl.drawSelf(waterId,stK);
			//关闭混合  
            GLES20.glDisable(GLES20.GL_BLEND);
			MatrixState.popMatrix();
            //关闭背面剪裁
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            //绘制树
            drawTrees(0);
			//关闭混合    
            GLES20.glDisable(GLES20.GL_BLEND);    
            drawStartAndEnd(numberOfTurns,0);  
            //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        	drawSpeedForEat(0);
        	//关闭混合    
            GLES20.glDisable(GLES20.GL_BLEND);    
			//禁用剪裁测试
        	GLES20.glDisable(GL10.GL_SCISSOR_TEST);  
		}
		
		//绘制后视镜框的方法		
		public void drawHSJKuang()
		{ 
			//绘制后视镜框
			//设置为正交投影矩阵
            MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 100);
			//设置摄像机基本位置
			MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0); 
			MatrixState.copyMVMatrix();
			MatrixState.pushMatrix();
			//开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            MatrixState.translate(0.01f, Self_Adapter_Data_HSJ_XY[screenId][2], -1);
			houshijing.drawSelf(houshijingTexId);
			MatrixState.popMatrix();
		}
		
		//绘制小雷达的方法
		public void drawRadar()
		{
			MatrixState.pushMatrix();
			//开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][12], Self_Adapter_Data_TRASLATE[screenId][13], -2);
			radar_Background.drawSelf(radarBackGroundTexId);			
			MatrixState.popMatrix();
			
			MatrixState.pushMatrix();  
			MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][12], Self_Adapter_Data_TRASLATE[screenId][13], -1);
			MatrixState.rotate(sight_angle, 0, 0, 1);
			radar_Zhizhen.drawSelf(radarZhiZhenTexId);
			MatrixState.popMatrix();
			
			for(int i=0;i<otherBoatLocation.length;i++)
			{
				float x_Temp=otherBoatLocationForHelp[i][0]-bxForSpecFrame;
				float z_Temp=otherBoatLocationForHelp[i][1]-bzForSpecFrame;
				
				float r_Temp=(float) Math.sqrt((x_Temp/Radar_Ratio)*(x_Temp/Radar_Ratio)+(z_Temp/Radar_Ratio)*(z_Temp/Radar_Ratio));
				
				if(r_Temp<=0.26f)
				{ 
					MatrixState.pushMatrix(); 					
					MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][12], Self_Adapter_Data_TRASLATE[screenId][13], -1f);
					MatrixState.translate(x_Temp/Radar_Ratio, -z_Temp/Radar_Ratio, 0);
					other_Radar_Zhizhen.drawSelf(radarOtherBoatTexId);
					MatrixState.popMatrix(); 
				}
			}
		}
		
		//绘制倒计时的方法 
		public void drawDaoJiShi()
		{
			MatrixState.pushMatrix();
			//开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子 
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			MatrixState.translate(0.0f, 1.8f, 90.0f);
			djsfd.drawSelf(djsTexId);
			//关闭混合
            GLES20.glDisable(GLES20.GL_BLEND); 
			MatrixState.popMatrix();
		}
		
		//绘制开始和结束标志
		public void drawStartAndEnd(int currOfTurns,int dyFlag)
		{
			MatrixState.pushMatrix();
			MatrixState.translate(UNIT_SIZE*0,LAND_MAX_HIGHEST-5,UNIT_SIZE*1f-5);
			if(currOfTurns==1&&!Constant.halfFlag)//刚开始
			{
				gameStartAndEnd.drawSelf(ggSzTexId,gameStartTexId,LAND_MAX_HIGHEST-5,dyFlag);
			}
			else if(currOfTurns==2&&Constant.halfFlag)
			{
				gameStartAndEnd.drawSelf(ggSzTexId,gameEndTexId,LAND_MAX_HIGHEST-5,dyFlag);
			}
			MatrixState.popMatrix();
		}
		
		//绘制计时器和lap的方法
		public void drawTimeAndLap()
		{
			if(!isJiShi)
			{
				dt.toTotalTime(0);
			}
			else if(isPaused)
			{//暂停状态
				dt.toTotalTime(betweenStartAndPauseTime);
			}
			else if(!isPaused)
			{//非暂停态，运行时
				if(numberOfTurns<3)
				{
					dt.toTotalTime(MyGLSurfaceView.gameContinueTime()+betweenStartAndPauseTime);
					gameTimeUse=MyGLSurfaceView.gameContinueTime()+betweenStartAndPauseTime;
				}
				else
				{
					dt.toTotalTime(gameTimeUse);
				}				
			}			
			
			MatrixState.pushMatrix();
			//开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			MatrixState.translate(-0.35f, 0.8f, -2);
			if(numberOfTurns<=2)
			{
				dt.drawSelf(timeTexId,numberOfTurns,numberOfN2,goTexId,shacheTexId,isShaChe); 
			}
			else
			{
				dt.drawSelf(timeTexId,2,numberOfN2,goTexId,shacheTexId,isShaChe);
			}
			//关闭混合
            GLES20.glDisable(GLES20.GL_BLEND);
			MatrixState.popMatrix(); 
		}
		
		//绘制仪表板的方法
		public void drawYiBiaoBan()
		{
			//根据当前速度，得到指针的选择角度
            db.changeangle(CURR_BOAT_V);
            //设置为正交投影矩阵
            MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 100);
			//设置摄像机基本位置
			MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0); 
			MatrixState.copyMVMatrix();
			MatrixState.pushMatrix();
			//开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            MatrixState.translate(Self_Adapter_Data_TRASLATE[screenId][9], Self_Adapter_Data_TRASLATE[screenId][10], -2);
			db.drawSelf(ybbTexId); 
			//关闭混合
            GLES20.glDisable(GLES20.GL_BLEND);  
			MatrixState.popMatrix();
		}
		
		//绘制各个编号的赛道
		public void drawSDSingle
		(
			int id,//赛道编号  
			int row,//赛道块在地图上的列
			int col //赛道块在地图上的行
		)
		{
			MatrixState.pushMatrix();
			MatrixState.translate(UNIT_SIZE*col, 0, UNIT_SIZE*row);
			switch(id)  
			{
			  case 0://横着的赛道（与X轴平行）	
			  case 9:
				  rtzd.drawSelf(rt_testur_Id,raceTrackRockId);				  
			  break;
			  case 1://竖着的赛道（与Z轴平行）
			  case 10:
				  MatrixState.rotate(90, 0, 1, 0);
				  rtzd.drawSelf(rt_testur_Id,raceTrackRockId);
			  break;
			  case 2://横着的赛道（与X轴平行,带小岛）
			  case 11:
				  rtzddxd.drawSelf(rt_testur_Id,raceTrackRockId);
			  break;
			  case 3://竖着的赛道（与Z轴平行,带小岛）
			  case 12:
				  MatrixState.rotate(90, 0, 1, 0);
				  rtzddxd.drawSelf(rt_testur_Id,raceTrackRockId);
			  break;
			  case 4://Z轴正方向拐到X轴正方向的弯道
			  case 13:
				  MatrixState.rotate(180, 0, 1, 0);
				  rtwd.drawSelf(rt_testur_Id,raceTrackRockId);
			  break;
			  case 5://Z轴正方向拐到X轴负方向的弯道
			  case 14:
				  MatrixState.rotate(90, 0, 1, 0);
				  rtwd.drawSelf(rt_testur_Id,raceTrackRockId);
			  break;
			  case 6://X轴负方向拐到Z轴负方向的弯道
			  case 15:
				  MatrixState.rotate(270, 0, 1, 0);
				  rtwd.drawSelf(rt_testur_Id,raceTrackRockId);
			  break;
			  case 7://X轴正方向拐到Z轴负方向的弯道 
			  case 16:
				  rtwd.drawSelf(rt_testur_Id,raceTrackRockId);
			  break;
			}
			MatrixState.popMatrix();
		}
		
		//绘制赛道中的水面
		public void drawWaterSingle
		(
			int row,//赛道块在地图上的列
			int col,//赛道块在地图上的行			
			float startST		//水面纹理坐标的偏移量
		)
		{
			MatrixState.pushMatrix();
			MatrixState.translate(UNIT_SIZE*col, 0, UNIT_SIZE*row);
			water.drawSelf(textureFlagId,startST);	 
			MatrixState.popMatrix();
		}
		
		//绘制赛道预备
		public void sdYb()
		{
			sdCount=0;
			int mrow=MAP_ARRAY.length;
			int mcol=MAP_ARRAY[0].length;
        	        	
			for(int i=0;i<mrow;i++)
			{	//循环行号				
				int rowM=i-bRow;
				if(rowM>=NUMBER_MAP||rowM<=-NUMBER_MAP)
        		{
        			continue;
        		}				
				for(int j=0;j<mcol;j++)
				{
					int colM=j-bCol;
					if(colM>=NUMBER_MAP||colM<=-NUMBER_MAP) 
        			{
        				continue;
        			}	
					if(ClipGrid.CLIP_MASK[directNo][rowM+2][colM+2])
					{
						sdHz[sdCount][0]=i;
						sdHz[sdCount][1]=j;
						sdCount++;
					}
				}
			}
		}
		
		//根据地图矩阵绘制赛道
		public void drawSD(float startST)
		{
			for(int i=0;i<sdCount;i++)
			{
				drawSDSingle
				(
					MAP_ARRAY[sdHz[i][0]][sdHz[i][1]],//赛道编号
					sdHz[i][0],//赛道块在地图上的行
					sdHz[i][1]//赛道块在地图上的列
				);
			}
		}
		//根据地图矩阵绘制赛道中的水面
		public void drawWater(float startST)
		{			
			for(int i=0;i<sdCount;i++)
			{
				if(MAP_ARRAY[sdHz[i][0]][sdHz[i][1]]!=8)
				{
					drawWaterSingle
					(
						sdHz[i][0],//赛道块在地图上的行
						sdHz[i][1],//赛道块在地图上的列
						startST
					);
				}
			}
		}
		
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			//设置视窗大小及位置 
			GLES20.glViewport(0, 0, width, height);
			//计算GLSurfaceView的宽高比
			ratio=(float)width/height;
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			synchronized(MyActivity.boatInitLock)
			{
				//设置屏幕背景色RGBA
	            GLES20.glClearColor(0.6039f,0.9333f,0.9843f,1.0f);
	            //打开背面剪裁
	            GLES20.glEnable(GLES20.GL_CULL_FACE);
	            //打开深度检测
	            GLES20.glEnable(GLES20.GL_DEPTH_TEST);   
	            //初始化变换矩阵
	            MatrixState.setInitStack();            
	            ShaderManager.compileShaderHY();
	            //大背景
	            bgd=new BackGround(ShaderManager.getTextureShaderProgram(),1,1,bg_texCoor); 
				//进度条底图
				pgs_dt=new BackGround(ShaderManager.getTextureShaderProgram(),0.9f,0.11f,pgs_dt_texCoor); 
				//进度条上面的图
				pgs_fgt=new Process(ShaderManager.getPrograssShaderProgram(),0.9f,0.11f,pgs_fgt_texCoor,0);
				//创建相应数字的对象0-9以及百分号
				for(int i=0;i<no.length;i++) 
				{  
					float[] tempTexCoor=new float[]
	  			    {
						0.091f*i,0,  0.091f*i,1,  0.091f*(i+1),1,
	  					0.091f*i,0,  0.091f*(i+1),1,  0.091f*(i+1),0
	  			    };      
					no[i]=new BackGround(ShaderManager.getTextureShaderProgram(),0.06f,0.06f,tempTexCoor); 
				}			
				//调用初始化纹理id的方法
				backGroundId=initTextureFromBitmap(bmbackGround);
				pgs_dt_id=initTextureFromBitmap(bmPgsDt);  
				pgs_fgt_id=initTextureFromBitmap(bmPgsFgt);
				no_texId=initTextureFromBitmap(bmNum);
			}
		}
		
		int step=0;
		public void readLoadTask()
		{
			if(step==13)
			{
				//调用此方法计算产生透视投影矩阵
				MatrixState.setProjectFrustum(-ratio*0.7f, ratio*0.7f, -0.7f*0.7f, 1.3f*0.7f, 1, 300);
				isBegin=false;
				return;
			}
			if(step==0)  
			{
				ShaderManager.compileShader();
				curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==1)  
			{
				//创建横着的赛道的对象
				rtzd=new RaceTrack(ShaderManager.getMountionShaderProgram(),yArray_ZD,ROWS,COLS,true);
		        //创建横着的带小岛的赛道对象
				rtzddxd=new RaceTrack(ShaderManager.getMountionShaderProgram(),yArray_ZD_DXD,ROWS,COLS,false);
		        //创建拐弯赛道的对象
				rtwd=new RaceTrack(ShaderManager.getMountionShaderProgram(),yArray_WD,ROWS,COLS,false);
		        //创建水面的对象
		        water=new Water(ShaderManager.getWaterShaderProgram(),1,1); 
		        curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==2)
			{
				//加载要绘制的物体 
		        boat=new Boat 
		        (
		        		BoatInfo.boatPartNames[BoatInfo.cuttBoatIndex],
		        		MyGLSurfaceView.this,
		        		ShaderManager.getTextureShaderProgram()
		        );	
		        heroBoatTexId=new int[BoatInfo.boatTexIdName[BoatInfo.cuttBoatIndex].length];
		        for(int i=0;i<BoatInfo.boatTexIdName[BoatInfo.cuttBoatIndex].length;i++)
		        {
		        	heroBoatTexId[i]=initTexture(BoatInfo.boatTexIdName[BoatInfo.cuttBoatIndex][i]);
		        }
		        wl=new WeiLang(0.5f,0.8f,2.4f,weilang_texCoor,ShaderManager.getWeiLangShaderProgram());	            
				waterId=initTexture(R.raw.weilang);
				//创建仪表板对象
				db=new Dashboard(ShaderManager.getTextureShaderProgram());
				//初始化仪表板纹理id 
				ybbTexId=initTexture(R.drawable.ybp);
		        curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==3)
			{
				if(isSpeedMode)
				{
					slowBoat=new Boat 
			        (
			        		BoatInfo.boatPartNames[(BoatInfo.cuttBoatIndex+2)%3],
			        		MyGLSurfaceView.this,
			        		ShaderManager.getTextureShaderProgram()
			        );
					slowBoatTexId=new int[BoatInfo.boatTexIdName[(BoatInfo.cuttBoatIndex+2)%3].length];
					for(int i=0;i<BoatInfo.boatTexIdName[(BoatInfo.cuttBoatIndex+2)%3].length;i++)
			        {
						slowBoatTexId[i]=initTexture(BoatInfo.boatTexIdName[(BoatInfo.cuttBoatIndex+2)%3][i]);
			        }
				}				
				//创建绘制的树木
		        ss=new SingleShrub(ShaderManager.getTextureShaderProgram());
		        //天空穹
		        sky=new Sky(ShaderManager.getTextureShaderProgram(),UNIT_SIZE*2.5f);
		        curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==4)
			{
				//初始化3D物体列表
		        bndrawer=new BNDrawer[]
		      	{
		        	new Bridge(ShaderManager.getTextureShaderProgram()),//桥
		      		new Tunnel						//隧道
		      		(
		      			ShaderManager.getTextureShaderProgram(),
		      			ShaderManager.getMountionShaderProgram(),
		      			Constant.yArray_Tunnel,
		      			Constant.yArray_Tunnel.length-1,
		      			Constant.yArray_Tunnel[0].length-1
		      		),
		      		new Mountion					//山
		      		(
		      			ShaderManager.getMountionShaderProgram(),
		      			Constant.yArray_Mountion,
		      			Constant.yArray_Mountion.length-1,
		      			Constant.yArray_Mountion[0].length-1
		      		),
		      		new B_YZ(ShaderManager.getBYZTextureShaderProgram()),//半崖子
		      		new AirShip(ShaderManager.getTextureShaderProgram()),//飞艇
		      		new Poster(ShaderManager.getTextureShaderProgram()),//广告牌子
		      		new Dock(ShaderManager.getTextureShaderProgram()),//船埠头
		      		new Castle(ShaderManager.getTextureShaderProgram()),//城堡
		      	};     
		        curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==5)
			{
				 //可碰撞物体集合的初始化
		        kzbj_array=new KZBJDrawer[]
		        {
		            new TrafficCylinder(ShaderManager.getTextureShaderProgram(),0.5f,0.5f,1),
		            new Cone(ShaderManager.getTextureShaderProgram(),0.5f,1),
		        };
		        //新添加的，初始化可以吃掉的物体的列表
		        speedForEat=new SpeedForEat[]
                {
		        	new Speed(ShaderManager.getTextureShaderProgram()),
		        	new Speed(ShaderManager.getTextureShaderProgram())
                };
		        curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==6)
			{
				//初始化铺在河道两侧纹理
		        rt_testur_Id=initTexture(R.drawable.grass); 
		        textureFlagId=initTexture(R.drawable.water);	
		        db=new Dashboard(ShaderManager.getTextureShaderProgram());
				ybbTexId=initTexture(R.drawable.ybp);
				dt=new DrawTime(ShaderManager.getTextureShaderProgram());
				timeTexId=initTexture(R.drawable.time);
				goTexId=initTexture(R.drawable.go);
				shacheTexId=initTexture(R.drawable.shache);  
				//后视镜边框
				houshijing=new com.bn.st.xc.TextureRect(ShaderManager.getTextureShaderProgram(),Self_Adapter_Data_HSJ_XY[screenId][3],Self_Adapter_Data_HSJ_XY[screenId][4]);
				houshijingTexId=initTexture(R.raw.houshijing);
		        curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==7)
			{
				 //桥的纹理id
		        bridge_id=initTexture(R.drawable.bridge_cm);
		    	//山上石头的纹理id
		    	rock_id=initTexture(R.drawable.rock);
		    	djsTexId=initTexture(R.drawable.daojishi);
				djsfd=new DaoJiShiForDraw(ShaderManager.getTextureShaderProgram(),ma,MyGLSurfaceView.this);
				if(isSpeedMode)
				{
					quickBoat=new Boat 
			        (
			        		BoatInfo.boatPartNames[(BoatInfo.cuttBoatIndex+1)%3],
			        		MyGLSurfaceView.this,
			        		ShaderManager.getTextureShaderProgram()
			        );
					quickBoatTexId=new int[BoatInfo.boatTexIdName[(BoatInfo.cuttBoatIndex+1)%3].length];
					for(int i=0;i<BoatInfo.boatTexIdName[(BoatInfo.cuttBoatIndex+1)%3].length;i++)
			        {
						quickBoatTexId[i]=initTexture(BoatInfo.boatTexIdName[(BoatInfo.cuttBoatIndex+1)%3][i]);
			        }
				}				
		    	curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==8)
			{
				//灌木0纹理id
		    	textureShrubId0=initTexture(R.drawable.shrub);
		    	//灌木1纹理id
		    	textrueShrubId1=initTexture(R.drawable.shrub1);		
		    	if(isSpeedMode)
		    	{
		    		//雷达数据的生成
			    	radarBackGroundTexId=initTexture(R.drawable.rador_bg);
			    	radarZhiZhenTexId=initTexture(R.drawable.rador_plane);
			    	radarOtherBoatTexId=initTexture(R.drawable.other_boat);
			    	radar_Background=new com.bn.st.xc.TextureRect(ShaderManager.getTextureShaderProgram(),Self_Adapter_Data_TRASLATE[screenId][14],0.3f);
			    	radar_Zhizhen=new com.bn.st.xc.TextureRect(ShaderManager.getTextureShaderProgram(),Self_Adapter_Data_TRASLATE[screenId][14]*0.167f,0.05f);
			    	other_Radar_Zhizhen=new com.bn.st.xc.TextureRect(ShaderManager.getTextureShaderProgram(),Self_Adapter_Data_TRASLATE[screenId][14]*0.067f,0.02f);
		    	}		    	
		    	curr_process=8*(step+1); 
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==9)
			{
				textureShrubId2=initTexture(R.drawable.shrub2); 
		    	textrueShrubId3=initTexture(R.drawable.shrub3);
		    	//游戏开始和结束
				gameStartAndEnd=new StartAndEnd(ShaderManager.getTextureShaderProgram());
				gameStartTexId=initTexture(R.drawable.game_start);
				gameEndTexId=initTexture(R.drawable.game_end);
		    	curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==10)
			{
				//天空穹对应的纹理id
		    	sky_texId=initTexture(R.drawable.sky);
		    	//交通柱和交通锥对应的纹理id
		    	jt_texId=initTexture(R.drawable.jt_wl);
		    	//加速物件和减速物件的纹理id
				speedUpTexId=initTexture(R.drawable.speed_up);
				speedDownTexId=initTexture(R.drawable.speed_down);
		    	curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==11)
			{
				texAirShipBody=initTexture(R.drawable.airship_st);
				texAirShipWy=initTexture(R.drawable.airship_wy);
				raceTrackRockId=initTexture(R.drawable.hd_rock);
				//广告牌子对应的纹理id
				ggSzTexId=initTexture(R.drawable.gg_sz);
				ggTexId[0]=initTexture(R.drawable.gg_0);
				ggTexId[1]=initTexture(R.drawable.gg_1);
				ggTexId[2]=initTexture(R.drawable.gg_2);
		        //船埠头的纹理id
		        dockTexId=initTexture(R.drawable.wood);
		        //城堡需要的三个纹理id
				castleTexIdA=initTexture(R.drawable.castle0);
				castleTexIdB=initTexture(R.drawable.castle1);
		        
				//初始化3D物体列表
		        initTDObjectList();
		        curr_process=8*(step+1);
				pgs_fgt.percent=0.0833f*(step+1);
				step++;
			}
			else if(step==12)
			{
				 //初始化树列表
		        initTreeList();
		        initKzbjList();
		        //初始化可以吃掉的物体列表
		        initCanEatList();
		        if(isSpeedMode&&otherPaths.size()==0)
		        {
		        	 //初始化其他各个船的路径
					for(int i=0;i<qtCount;i++)
					{
						ArrayList<float[]> pathTemp=PathUtil.generatePath();
						pathTemp.get(0)[0]=otherBoatLocation[i][0];
						pathTemp.get(0)[1]=otherBoatLocation[i][1];
						otherPaths.add(pathTemp);
					}
		        }		       
		        curr_process=100; 
				pgs_fgt.percent=1.0f;
				step++;
			}
		}		
	}
	
	//初始化纹理，获得纹理的id
	public int initTexture(int drawableId)
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
			1,          //产生的纹理id的数量
			textures,   //纹理id的数组
			0           //偏移量
		);    
		int textureId=textures[0];    		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
        
        //通过输入流加载图片
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
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
        //通过输入流加载图片
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );
        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
        return textureId;
	}
	
	public int initTextureFromBitmap(Bitmap bitmapTmp)
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
			1,          //产生的纹理id的数量
			textures,   //纹理id的数组
			0           //偏移量
		);    
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
        
        //实际加载纹理
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
        		bitmapTmp, 			  //纹理图像
        		0					  //纹理边框尺寸
        );       
        return textureId;
	}
	
	//3D部件预备
	int[] tdHz=new int[64];
	int tdCount=0;
	public void tdYb()
	{
		tdCount=0;
		
		for(int i=0;i<tdObjectList.size();i++)
		{
			int rowM=tdObjectList.get(i).rows-bRow;
			int colM=tdObjectList.get(i).cols-bCol;			
			
			if(rowM>=NUMBER_MAP||rowM<=-NUMBER_MAP||
			  colM>=NUMBER_MAP||colM<=-NUMBER_MAP)
    		{
    			continue;
    		}	
			
			if(ClipGrid.CLIP_MASK[directNo][rowM+2][colM+2])
			{
				tdHz[tdCount]=i;
				tdCount++;
			}
		}
	}
	
	
	//绘制方法
	public void DrawTDObjects(int dyFlag)
	{
		for(int i=0;i<tdCount;i++)
		{
			tdObjectList.get(tdHz[i]).drawSelf(texIdList.get(tdHz[i]),dyFlag);
		}
	}
	
	
	//绘制树的预备方法
	List<ShrubForControl> treeListTemp=new ArrayList<ShrubForControl>();
	public void treeYb()
	{
		treeListTemp.clear();  		
		//循环列表   
		for(int i=0;i<treeList.size();i++)
		{			  
			int rowM=treeList.get(i).rows-bRow;
			int colM=treeList.get(i).cols-bCol;
			
			//进行25宫格循环判断
			if(rowM>=NUMBER_MAP||rowM<=-NUMBER_MAP||
			   colM>=NUMBER_MAP||colM<=-NUMBER_MAP)
    		{
    			continue;
    		}	
			
			if(ClipGrid.CLIP_MASK[directNo][rowM+2][colM+2])
			{
				treeListTemp.add(treeList.get(i));
			}
		}   
	}
	
	public void drawTrees(int dyFlag)
	{  
		//进行远近排序
		if(dyFlag==1)
		{
			Collections.sort(treeListTemp);
		}		
		for(int i=0;i<treeListTemp.size();i++)
		{
			//绘制树木
            ss.drawSelf
            (
            		treeListTemp.get(i).texIds, 
            		treeListTemp.get(i).id,
            		treeListTemp.get(i).xoffset,
            		treeListTemp.get(i).yoffset,
            		treeListTemp.get(i).zoffset,
            		dyFlag
            );
		}
	}
	
	//可碰撞物体（交通锥、交通柱）部件预备
	int[] kzHz=new int[64];
	int kzCount=0;
	public void kzYb()
	{
		kzCount=0;
		for(int i=0;i<kzbjList.size();i++)
		{
			int rowM=kzbjList.get(i).row-bRow;
			int colM=kzbjList.get(i).col-bCol;
			
			//进行25宫格循环判断
			if(rowM>=NUMBER_MAP||rowM<=-NUMBER_MAP||
					  colM>=NUMBER_MAP||colM<=-NUMBER_MAP)
    		{
    			continue;
    		}			
			
			if(ClipGrid.CLIP_MASK[directNo][rowM+2][colM+2])
			{
				kzHz[kzCount]=i;
				kzCount++;
			}
		}		
	}
	
	//绘制可碰撞物体的方法
	public void drawKZBJ(int dyFlag)
    {
		for(int i=0;i<kzCount;i++)
		{
			kzbjList.get(kzHz[i]).drawSelf(jt_texId,dyFlag);
		}
    }
	
	//初始化List<TDObjectForControl> tdObjectList集合的方法
	public void initTDObjectList()
	{
		int tempId=0;
		for(int i=0;i<PART_LIST.length;i++)
		{
			if(PART_LIST[i][0]==0)//桥  
			{
				tempId=0;
				int[] tempArray=new int[]
				{
					bridge_id
				};
				texIdList.add(tempArray);
				//竖直的桥1
				if(PART_LIST[i][4]==0)
				{
					pzzList.add(new PZZ(PART_LIST[i][1]+6.3f,PART_LIST[i][3]-6.3f,PART_LIST[i][5],PART_LIST[i][6],0));
					pzzList.add(new PZZ(PART_LIST[i][1]+23.1f,PART_LIST[i][3]-6.3f,PART_LIST[i][5],PART_LIST[i][6],0));
				}
				else if(PART_LIST[i][4]==-90)//横着的桥0
				{					
					pzzList.add(new PZZ(PART_LIST[i][1]+2.1f,PART_LIST[i][3]+6.3f,PART_LIST[i][5],PART_LIST[i][6],-90));
					pzzList.add(new PZZ(PART_LIST[i][1]+2.1f,PART_LIST[i][3]+23.1f,PART_LIST[i][5],PART_LIST[i][6],-90));
				}
			}
			else if(PART_LIST[i][0]==1)//隧道
			{
				tempId=1;
				int[] tempArray=new int[]
				{
					bridge_id,rt_testur_Id,rock_id
				};
				texIdList.add(tempArray);
			}
			else if(PART_LIST[i][0]==2)//山
			{
				tempId=2;
				int[] tempArray=new int[]
				{
					rt_testur_Id,rock_id
				};
				texIdList.add(tempArray);
			}
			else if(PART_LIST[i][0]==3)//半崖子
			{
				tempId=3;
				int[] tempArray=new int[]
				{
					rt_testur_Id,rock_id
				};
				texIdList.add(tempArray);
			}
			else if(PART_LIST[i][0]==4)//飞艇
			{
				tempId=4;
				int[] tempArray=new int[]
				{
					texAirShipBody,texAirShipWy
				};
				texIdList.add(tempArray);
			}
			else if(PART_LIST[i][0]==5)//广告牌子
			{
				tempId=5;
				int[] tempArray=new int[]
				{
					ggSzTexId,ggTexId[0],ggTexId[1],ggTexId[2]
				};
				texIdList.add(tempArray);
			}
			else if(PART_LIST[i][0]==6)//船埠头
			{
				tempId=6;
				int[] tempArray=new int[]
				{
					dockTexId
				};
				texIdList.add(tempArray);
			}
			else if(PART_LIST[i][0]==7)//城堡
			{
				tempId=7;
				int[] tempArray=new int[]
				{
					castleTexIdA,castleTexIdB 
				};
				texIdList.add(tempArray);
			}
			tdObjectList.add
			(
				new TDObjectForControl
				(
					bndrawer[tempId],
					(int)(PART_LIST[i][0]),
					PART_LIST[i][1],
					PART_LIST[i][2],
					PART_LIST[i][3],
					PART_LIST[i][4],
					(int)(PART_LIST[i][5]),
					(int)(PART_LIST[i][6])
				)
			);
		}
	}
	
	public void initTreeList()
	{
		for(int i=0;i<Tree_Data.length;i++)
		{
			if(Tree_Data[i][0]==0)//第0种树
			{
				treeList.add
				(
					new ShrubForControl
					(
						(int)Tree_Data[i][4],
						(int)Tree_Data[i][5],
						Tree_Data[i][1],
						Tree_Data[i][2],
						Tree_Data[i][3],
						(int)Tree_Data[i][0],
						textureShrubId0
					)	
				);
			}
			else if(Tree_Data[i][0]==1)//第1种树  
			{
				treeList.add
				(
					new ShrubForControl  
					(
						(int)Tree_Data[i][4],
						(int)Tree_Data[i][5],
						Tree_Data[i][1],
						Tree_Data[i][2],
						Tree_Data[i][3],
						(int)Tree_Data[i][0],
						textrueShrubId1
					)	
				);				
			}
			else if(Tree_Data[i][0]==2)//第1种树  
			{
				treeList.add
				(
					new ShrubForControl  
					(
						(int)Tree_Data[i][4],
						(int)Tree_Data[i][5],
						Tree_Data[i][1],
						Tree_Data[i][2],
						Tree_Data[i][3],
						(int)Tree_Data[i][0],
						textureShrubId2
					)	
				);				
			}	
			else if(Tree_Data[i][0]==3)//第1种树  
			{
				treeList.add
				(
					new ShrubForControl  
					(
						(int)Tree_Data[i][4],
						(int)Tree_Data[i][5],
						Tree_Data[i][1],
						Tree_Data[i][2],
						Tree_Data[i][3],
						(int)Tree_Data[i][0],
						textrueShrubId3
					)	
				);				
			}	
		}
	}
	
	//初始化可碰撞物件列表集合的方法
	public void initKzbjList()
	{
		for(int i=0;i<KZBJ_ARRAY.length;i++)
		{
			if(KZBJ_ARRAY[i][0]==0)//交通柱
			{
				kzbjList.add
				(
					new KZBJForControl
					(
						kzbj_array[0],
						(int)KZBJ_ARRAY[i][0],
						KZBJ_ARRAY[i][1],
						KZBJ_ARRAY[i][2],
						KZBJ_ARRAY[i][3],
						(int)KZBJ_ARRAY[i][4],
						(int)KZBJ_ARRAY[i][5],
						ma
					)
				);
			}
			else if(KZBJ_ARRAY[i][0]==1)//交通锥
			{
				kzbjList.add
				(
					new KZBJForControl
					(
						kzbj_array[1],
						(int)KZBJ_ARRAY[i][0],
						KZBJ_ARRAY[i][1],
						KZBJ_ARRAY[i][2],
						KZBJ_ARRAY[i][3],
						(int)KZBJ_ARRAY[i][4],
						(int)KZBJ_ARRAY[i][5],
						ma
					)
				);
			}
		}
	}
	public static long gameContinueTime()//获取游戏时间
    {
    	return System.currentTimeMillis()-gameStartTime;
    } 
	//新添加的，初始化可以吃掉物体的列表
	public void initCanEatList()
	{
		int tempIndex=0;
		for(int i=0;i<KEAT_ARRAY.length;i++)
		{
			if(KEAT_ARRAY[i][0]==0)//加氮气
			{
				tempIndex=0;
			}
			else if(KEAT_ARRAY[i][0]==1)//减速
			{
				tempIndex=1;//speedWtTexId
			}
			
			speedWtList.add
			(
				new SpeedForControl
				(
					speedForEat[tempIndex],
					(int)KEAT_ARRAY[i][0],
					KEAT_ARRAY[i][1],
					KEAT_ARRAY[i][2],
					KEAT_ARRAY[i][3],
					KEAT_ARRAY[i][4],
					KEAT_ARRAY[i][5],
					KEAT_ARRAY[i][6],
					ma
				)
			);
		}
	}
	
	//加减速预备
	int[] speedHz=new int[64];
	int speedCount=0;
	public void speedForEatYb()
	{
		speedCount=0;
		for(int i=0;i<speedWtList.size();i++)
		{
			int rowM=(int) (speedWtList.get(i).rows-bRow);
			int colM=(int) (speedWtList.get(i).cols-bCol);
			
			//进行25宫格循环判断
			if(rowM>=NUMBER_MAP||rowM<=-NUMBER_MAP||
					  colM>=NUMBER_MAP||colM<=-NUMBER_MAP)
    		{
    			continue;
    		}			
			
			if(ClipGrid.CLIP_MASK[directNo][rowM+2][colM+2])
			{
				speedHz[speedCount]=i;
				speedCount++;
			}
		}
	}	
	
	//新添加的，绘制加速、减速箱子
	public void drawSpeedForEat(int dyFlag)
	{
        for(int i=0;i<speedCount;i++)
        {
        	//绘制交通锥、交通柱
			if(speedWtList.get(speedHz[i]).id==0)//加氮气的
			{
				speedWtList.get(speedHz[i]).drawSelf(speedUpTexId,dyFlag);//这里的纹理id需要替换为氮气的
			}
			else if(speedWtList.get(speedHz[i]).id==1)//减速的
			{
				speedWtList.get(speedHz[i]).drawSelf(speedDownTexId,dyFlag);//这里的纹理id需要替换为减速的 
			}
        }
	}
}
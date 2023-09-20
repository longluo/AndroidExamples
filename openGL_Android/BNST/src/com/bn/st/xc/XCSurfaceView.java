package com.bn.st.xc;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.bn.R;
import com.bn.clp.BoatInfo;
import com.bn.core.MatrixState;
import com.bn.st.d2.MyActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import static com.bn.st.xc.Constant.*;

public class XCSurfaceView extends GLSurfaceView 
{
	final float TOUCH_SCALE_FACTOR = 180.0f/SCREEN_WIDTH;//角度缩放比例
    SceneRenderer mRenderer;//场景渲染器
    float mPreviousX;//上次的触控位置X坐标
    float mPreviousY;//上次的触控位置Y坐标

    int tex_index=0;//广告纹理id索引   
    
    int textureUpId;//系统分配的游戏前进虚拟按钮纹理id
    int textureDownId;//系统分配的游戏后退虚拟按钮纹理id
    int texWallId[]=new int[3];  //广告墙纹理Id数组
    int texFloorId;//地面纹理id
    
    //船的纹理id数组
    int[] heroBoatTexId;
    int[] quickBoatTexId;
    int[] slowBoatTexId;
    
    //船的纹理图片数组
    static Bitmap[] heroBoatTexBitmap;
    static Bitmap[] quickBoatTexBitmap;
    static Bitmap[] slowBoatTexBitmap;
	
    static Bitmap bmUp;//前进虚拟按钮
    static Bitmap bmDown;//后退虚拟按钮
    static Bitmap[] bmaWall=new Bitmap[3];//广告墙纹理数组
    static Bitmap bmFloor;//地面
    
	private float yAngle=0;//绕Y轴转动角
	private float xAngle=20;//仰角
	//摄像机坐标
	private float cx;
	private float cz;
	private float cy;
	//目标点坐标
	private float tx=0;
	private float tz=0;
	private float ty=-HOUSE_GAO/3;
	//摄像机头顶指向
	private float upX=0;
	private float upY=1;
	private float upZ=0;
	//----创建物体----------------------
	HouseForDraw house;//房间
	DisplayStation displayStation;//展台
	Boat boat[]=new Boat[3];//船
	TextureRect button;//按钮
	LoadedObjectVertexNormalXC rome;//创建罗马柱子
	
	private float half_width_button=0.15f;
//	private float half_height_button=0.1f;
	private float offset_X_Button1=-0.7f;
	private float offset_Y_Button1=-0.8f;
	private float offset_X_Button2=0.7f;
	private float offset_Y_Button2=-0.8f;
	//按钮的范围
	private float[] button1;
	private float[] button2;
	
	public boolean flagForThread=true;//线程标志
	
	public int index_boat=0;//船的索引
	private float ratio;//绘制投影矩阵缩放比例
	
	//--------测试惯性---------------
	private boolean flag_go;//允许惯性标志位 
	final int countGoStepInitValue=35;
	private int countGoStep=0;
	private float acceleratedVelocity=0.06f;
	private float ori_angle_speed=7;//初始角速度
	private float curr_angle_speed;//当前角速度
	private boolean isMoved;//判断是否移动过
	//---------纹理墙墙上的广告---------------
	public boolean flag_display=true;//广告标志位
	public XCSurfaceView(Context context)
	{
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
        this.setKeepScreenOn(true);
        cx=(float)(tx+Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle))*XC_DISTANCE);//摄像机x坐标 
        cz=(float)(tz+Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle))*XC_DISTANCE);//摄像机z坐标 
        cy=(float)(ty+Math.sin(Math.toRadians(xAngle))*XC_DISTANCE);//摄像机y坐标 
    }
	//触摸事件回调方法
	@Override 
    public boolean onTouchEvent(MotionEvent e) 
	{
        float y = e.getY();//得到按下的XY坐标
        float x = e.getX();
        float domain_span=5;
        switch (e.getAction())
        {
        case MotionEvent.ACTION_MOVE:
            float dx = x - mPreviousX;//计算触控笔X位移
			float dy = y-  mPreviousY;//
        	yAngle -= dx * TOUCH_SCALE_FACTOR;//
        	xAngle -= dy * TOUCH_SCALE_FACTOR/2.0f;//仰角改变
        	if(xAngle>45)//仰角的最大值
        	{
        		xAngle=45;
        	}
        	if(xAngle<10){//仰角的最小值
        		xAngle=10;
        	}

            if(Math.abs(dx)>domain_span)//确定一个阈值
            {
	            isMoved=true;//表示当前移动过
	            //设置当前角速度
	            curr_angle_speed=ori_angle_speed*(-dx/SCREEN_WIDTH);
	            if(dx>20||dx<-20)
	            {
	            	if(curr_angle_speed>0)
		            {
		            	curr_angle_speed=curr_angle_speed+ori_angle_speed;
		            }  
		            else
		            {
		            	curr_angle_speed=curr_angle_speed-ori_angle_speed;
		            }
	            }
	            
	            //设置加速度大小
	            acceleratedVelocity=0.03f;
	            //若当前角速度大于零则加速度置为负
	            if(curr_angle_speed>0)
	            {
	            	acceleratedVelocity=-0.03f;
	            }
            }
            cx=(float)(tx+Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle))*XC_DISTANCE);//摄像机x坐标 
            cz=(float)(tz+Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle))*XC_DISTANCE);//摄像机z坐标 
            cy=(float)(ty+Math.sin(Math.toRadians(xAngle))*XC_DISTANCE);//摄像机y坐标
            break;
            
        case MotionEvent.ACTION_DOWN :
        	flag_go=false;
        	if(x>button1[0]&&x<button1[1]&&y>button1[2]&&y<button1[3])
        	{
        		index_boat=(index_boat-1+3)%3;
        	}
        	if(x>button2[0]&&x<button2[1]&&y>button2[2]&&y<button2[3])
        	{
        		index_boat=(index_boat+1)%3;
        	}
        	BoatInfo.cuttBoatIndex=index_boat;
        	break;
        case MotionEvent.ACTION_UP: 
        	if(isMoved)
        	{
        		flag_go=true;
        		countGoStep=countGoStepInitValue;
        		isMoved=false;
        	}
        	break;
        }
        mPreviousX = x;//记录触控笔位置   
        mPreviousY=y;
        cx=(float)(tx+Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle))*XC_DISTANCE);//摄像机x坐标 
        cz=(float)(tz+Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle))*XC_DISTANCE);//摄像机z坐标 
        cy=(float)(ty+Math.sin(Math.toRadians(xAngle))*XC_DISTANCE);//摄像机y坐标
        return true;
    }
	
	//船部件LoadedObjectVertexNormal列表
	static LoadedObjectVertexTexXC[][] parts=new LoadedObjectVertexTexXC[3][];
	static 
	{
		parts[0]=new LoadedObjectVertexTexXC[BoatInfo.boatPartNames[0].length];
		parts[1]=new LoadedObjectVertexTexXC[BoatInfo.boatPartNames[1].length];
		parts[2]=new LoadedObjectVertexTexXC[BoatInfo.boatPartNames[2].length];
	}
	
	static LoadedObjectVertexNormalXC romeData;
	
	public static void loadVertexFromObj(Resources r)
	{
		romeData=LoadUtilXC.loadFromFileVertexOnly("rome.obj",r, 1f, 1f, 1f);
		
		for(int j=0;j<BoatInfo.boatPartNames.length;j++)
		{
			for(int i=0;i<BoatInfo.boatPartNames[j].length;i++)  
			{  
				parts[j][i]=LoadUtilTexXC.loadFromFileVertexOnly
				(
					BoatInfo.boatPartNames[j][i], 
					r,
					ShaderManager.getCommTextureShaderProgram()
			     );			
			}
		}
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
    	//long olds;
    	//long currs;
        public void onDrawFrame(GL10 gl) 
        { 
        	//currs=System.nanoTime();			
			//System.out.println(1000000000.0/(currs-olds)+"FPS");
			//olds=currs;
        	 
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT); 
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2.5f, 1000);           
            MatrixState.setCamera(cx,cy,cz,tx,ty,tz,upX,upY,upZ);
            MatrixState.copyMVMatrix();
            //初始化光源位置
            MatrixState.setLightLocation(cx, cy+5, cz+3);            
            //--------------------------绘制船在墙壁上的倒影-------------------------------------------            
            drawBoatMirrorOnWall();
           
            //在画透明墙
            GLES20.glEnable(GLES20.GL_BLEND);//开启混合
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            house.drawTransparentWall();
            GLES20.glDisable(GLES20.GL_BLEND); //关闭混合
            
            house.drawFloor(texFloorId);//绘制地板
            //绘制广告墙
            house.drawTexWall(texWallId,tex_index);
            
            //--------------------------绘制船在展台上的倒影-------------------------------------------
            //绘制展台的不透明的圆柱
            MatrixState.pushMatrix();
            MatrixState.translate(0, -Constant.HOUSE_GAO/2, 0);
            displayStation.drawSelfCylinder();
            MatrixState.popMatrix();
            
            //绘制船在展台上的倒影
            MatrixState.pushMatrix();
            drawBoatShadow();
            MatrixState.popMatrix(); 
            
            //绘制透明圆面,开启混合
            GLES20.glEnable(GLES20.GL_BLEND);//开启混合
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);//设置混合因子
            displayStation.drawTransparentCircle();
            GLES20.glDisable(GLES20.GL_BLEND); //关闭混合            
            
            //绘制真实船
            drawBoat();
            //绘制柱子
            drawRomeColumn();
            //绘制上一个下一个按钮
            drawButton();
        }  
        public void onSurfaceChanged(GL10 gl, int width, int height)
        {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            ratio = (float) width / height;            
            virtualButton();
            new Thread()
            {
				@Override
            	public void run()
            	{
            		while(flagForThread)
            		{
            			try 
            			{
            				//这里进行惯性测试
            				if(flag_go)//如果允许惯性
            				{
            					countGoStep--;
            					if(countGoStep<=0)
            					{
            						curr_angle_speed=curr_angle_speed+acceleratedVelocity;//计算当前角速度
            					}
            					
            					if(Math.abs(curr_angle_speed)>0.1f)
            					{
            						yAngle=yAngle+curr_angle_speed;
                					cx=(float)(tx+Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle))*XC_DISTANCE);//摄像机x坐标 
                				    cz=(float)(tz+Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle))*XC_DISTANCE);//摄像机z坐标 
                				    cy=(float)(ty+Math.sin(Math.toRadians(xAngle))*XC_DISTANCE);//摄像机y坐标 
            					}
            					else
            					{
            						curr_angle_speed=0;
            						flag_go=false;
            					}
            				}
							Thread.sleep(10);
						}
            			catch (InterruptedException e)
            			{
							e.printStackTrace();
						}
            		}
            	}
            }.start();
        }
        
        
        public void onSurfaceCreated(GL10 gl, EGLConfig config) 
        {	    
        	synchronized(MyActivity.boatInitLock)
        	{        		
            	ShaderManager.compileShaderReal();            	
            	textureUpId=initTextureFromBitmap(bmUp);//上按钮
                textureDownId=initTextureFromBitmap(bmDown);//下按钮
                texWallId[0]=initTextureFromBitmap(bmaWall[0]);//广告纹理图1    
                texWallId[1]=initTextureFromBitmap(bmaWall[1]);//广告纹理图2
                texWallId[2]=initTextureFromBitmap(bmaWall[2]);//广告纹理图3
                texFloorId=initTextureFromBitmap(bmFloor);//地面纹理图3   
                
                heroBoatTexId=new int[heroBoatTexBitmap.length];
                quickBoatTexId=new int[quickBoatTexBitmap.length];
                slowBoatTexId=new int[slowBoatTexBitmap.length];
                
                for(int i=0;i<heroBoatTexBitmap.length;i++)
                {
                	heroBoatTexId[i]=initTextureFromBitmap(heroBoatTexBitmap[i]);
                }
                
                for(int i=0;i<quickBoatTexBitmap.length;i++)
                {
                	quickBoatTexId[i]=initTextureFromBitmap(quickBoatTexBitmap[i]);
                }
                
                for(int i=0;i<slowBoatTexBitmap.length;i++)
                {
                	slowBoatTexId[i]=initTextureFromBitmap(slowBoatTexBitmap[i]);
                }
            	
                //设置屏幕背景色RGBA
                GLES20.glClearColor(1f,1f,1f, 1.0f);
                                
                //立体物体==========================================================
                house=new HouseForDraw();//创建房间
                displayStation=new DisplayStation(RADIUS_DISPLAY, LENGTH_DISPLAY);//创建展台
                boat[0]=new Boat(parts[0],XCSurfaceView.this);//创建船只ltf
                boat[1]=new Boat(parts[1],XCSurfaceView.this);//创建船只cjg
                boat[2]=new Boat(parts[2],XCSurfaceView.this);//创建船只pjh
            	button=new TextureRect(ShaderManager.getCommTextureShaderProgram(), half_width_button, XC_Self_Adapter_Data_TRASLATE[com.bn.clp.Constant.screenId][0]);//绘制相关按钮
            	
             	//创建罗马柱对象
            	romeData.initShader(ShaderManager.getColorshaderProgram());
            	rome=romeData;
            	//开启背面剪裁   
                GLES20.glEnable(GLES20.GL_CULL_FACE);
                //打开深度检测
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);
                //打开抖动
                GLES20.glEnable(GLES20.GL_DITHER);
                MatrixState.setInitStack();
                //--------------------------绘制纹理墙------------------------------------------------
    	          new Thread()
    	          {
    	          	@Override
    	          	public void run()
    	          	{
    	          		while(flag_display)
    	          		{
    	          			tex_index=(tex_index+1)%texWallId.length;//每隔两秒换一幅纹理图
    	          			
    	        			try
    	              		{
    	                          Thread.sleep(2000);
    	              		}
    	              		catch(InterruptedException e)
    	              		{
    	              			e.printStackTrace();
    	              		}
    	          		}
    	          	}
    	          }.start();
              //--------------------------绘制纹理墙------------------------------------------------
        	}
        }
        //绘制柱子
        public void drawRomeColumn()
        {
        	float ratio_column=0.55f;
        	float column_height=-18;
        	float adjust=3f;//调整值
           
            //绘制第一个柱子
            MatrixState.pushMatrix();
            MatrixState.rotate(30, 0, 1, 0);
            MatrixState.translate( 0,column_height, -WALL_WIDHT*2+adjust);
            MatrixState.scale(ratio_column, ratio_column, ratio_column);            
            rome.drawSelf(1.0f);
            MatrixState.popMatrix();
            
            //逆时针绘制第二根柱子，此时为正方向  
            MatrixState.pushMatrix();
            MatrixState.rotate(90, 0, 1, 0);
            MatrixState.translate( 0,column_height, -WALL_WIDHT*2+adjust);
            MatrixState.scale(ratio_column, ratio_column, ratio_column);
            rome.drawSelf(1.0f);
            MatrixState.popMatrix();
            
            //逆时针绘制第三根柱子，此时为正方向
            MatrixState.pushMatrix();
            MatrixState.rotate(150, 0, 1, 0);
            MatrixState.translate( 0,column_height, -WALL_WIDHT*2+adjust);
            MatrixState.scale(ratio_column, ratio_column, ratio_column);
            rome.drawSelf(1.0f);
            MatrixState.popMatrix();
            
            //逆时针绘制第四根柱子，此时为正方向
            MatrixState.pushMatrix();
            MatrixState.rotate(210, 0, 1, 0);  
            MatrixState.translate( 0,column_height, -WALL_WIDHT*2+adjust);
            MatrixState.scale(ratio_column, ratio_column, ratio_column);
            rome.drawSelf(1.0f);
            MatrixState.popMatrix();
            
            //逆时针绘制第五根柱子，此时为正方向
            MatrixState.pushMatrix();
            MatrixState.rotate(270, 0, 1, 0);
            MatrixState.translate( 0,column_height, -WALL_WIDHT*2+adjust);
            MatrixState.scale(ratio_column, ratio_column, ratio_column);
            rome.drawSelf(1.0f);
            MatrixState.popMatrix();
            
            //逆时针绘制第六根柱子，此时为正方向
            MatrixState.pushMatrix();
            MatrixState.rotate(330, 0, 1, 0);
            MatrixState.translate( 0,column_height, -WALL_WIDHT*2+adjust);
            MatrixState.scale(ratio_column, ratio_column, ratio_column);
            rome.drawSelf(1.0f);
            MatrixState.popMatrix();
        }

        //绘制船的方法
        public void drawBoat()
        {
        	 MatrixState.pushMatrix();
             switch(index_boat) 
             {  
             case 0://腾飞的船   
                  MatrixState.translate(0, -10f, 0);             	  
                  MatrixState.scale(RATIO_BOAT, RATIO_BOAT, RATIO_BOAT);
                  boat[0].drawSelf(heroBoatTexId);  
             	break;   
             case 1://大广的船  
            	 MatrixState.translate(0, -10f, 0);
                  MatrixState.scale(RATIO_BOAT, RATIO_BOAT, RATIO_BOAT);
                  boat[1].drawSelf(quickBoatTexId);
             	break;
             case 2://彭彭的船
             	  MatrixState.translate(0, -10f, 0);             	  
                  MatrixState.scale(RATIO_BOAT, RATIO_BOAT, RATIO_BOAT);
                  boat[2].drawSelf(slowBoatTexId);
             	break;
             }
             MatrixState.popMatrix();
        }
        
        //绘制船的倒影的方法
        public void drawBoatShadow()
        {
			//关闭背面剪裁
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            MatrixState.pushMatrix();   
			switch(index_boat) 
            {  
            case 0://腾飞的船  
                 MatrixState.translate(-1f, -14f, 0); 
                 MatrixState.rotate(180, 0, 0, 1);
                 MatrixState.scale(RATIO_BOAT, RATIO_BOAT, RATIO_BOAT);
                 boat[0].drawSelf(heroBoatTexId);  
            	break;   
            case 1://大广的船  
           	 	MatrixState.translate(0.6f, -14f, 0);
           	 	MatrixState.rotate(180, 0, 0, 1);
                MatrixState.scale(RATIO_BOAT, RATIO_BOAT, RATIO_BOAT);
                boat[1].drawSelf(quickBoatTexId);
            	break;
            case 2://彭彭的船
            	MatrixState.translate(0, -14f, 0); 
            	MatrixState.rotate(180, 0, 0, 1);
                MatrixState.scale(RATIO_BOAT, RATIO_BOAT, RATIO_BOAT);
                boat[2].drawSelf(slowBoatTexId);
            	break;
            }
            MatrixState.popMatrix();
            //打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }
        //绘制船在墙中的镜像
        public void drawBoatMirrorOnWall()
        {
        	final float ydistance=6f;
        	final float zdistance=-43f;
        	int k=(int) Math.abs(yAngle)/360+2;
        	float yAngleTemp=(yAngle+360*k)%360;
        	final int span=68;
        	//在第一面墙        	
        	if(yAngleTemp<=span&&yAngleTemp>=0||yAngleTemp>=360-span&&yAngleTemp<=360)
        	{
        		MatrixState.pushMatrix();
            	MatrixState.translate(0, ydistance, zdistance);
            	MatrixState.rotate(180, 0, 1, 0);
            	MatrixState.rotate(-15, 1, 0, 0);
            	drawBoat();
            	MatrixState.popMatrix();   
        	}  
        	     	
        	//逆时针在第二面墙画镜像========================?
        	int bzAngle;
        	
        	//逆时针在第三个墙面镜像  
        	bzAngle=120;
        	if(yAngleTemp>bzAngle-span&&yAngleTemp<bzAngle+span)
        	{
		    	MatrixState.pushMatrix();
		    	MatrixState.rotate(120, 0, 1, 0);
		    	MatrixState.translate(0, ydistance, zdistance);
		    	MatrixState.rotate(-90, 0, 1, 0);
		    	MatrixState.rotate(-15, 0, 0, 1);
		    	drawBoat();
		    	MatrixState.popMatrix();    
        	}
        	//逆时针第五个面
        	bzAngle=240;
        	if(yAngleTemp>bzAngle-span&&yAngleTemp<bzAngle+span)
        	{
	        	MatrixState.pushMatrix();
	        	MatrixState.rotate(240, 0, 1, 0);
	        	MatrixState.translate(0, ydistance, zdistance);
	        	MatrixState.rotate(90, 0, 1, 0);
	        	MatrixState.rotate(15, 0, 0, 1);
	        	drawBoat();
	        	MatrixState.popMatrix();
        	}
        }       
     
        //绘制相关按钮
        public void drawButton()
        {
        	//设置正交矩阵
        	MatrixState.setProjectOrtho(-1,1,-1,1,1,100);
        	//设置摄像机
        	MatrixState.setCamera(0, 0, 0, 0, 0,-1, 0, 1, 0);
        	MatrixState.copyMVMatrix();
        	 //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        	MatrixState.pushMatrix();
        	MatrixState.translate(-0.7f, -0.8f, -1);
        	button.drawSelf(textureUpId);//绘制后移按钮
            MatrixState.popMatrix();
            MatrixState.pushMatrix();
            MatrixState.translate(0.7f, -0.8f, -1);
            button.drawSelf(textureDownId);//绘制后移按钮
            MatrixState.popMatrix();
            //关闭混合
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }
	//虚拟按钮的监听方法
	public void virtualButton()
	{
		//按钮1左边所占的比例
		float leftEdge=(float)(1-half_width_button+offset_X_Button1)/2*SCREEN_WIDTH;
		float rightEdge=(float)(1+half_width_button+offset_X_Button1)/2*SCREEN_WIDTH;
		float topEdge=(float)(1-XC_Self_Adapter_Data_TRASLATE[com.bn.clp.Constant.screenId][0]-offset_Y_Button1)/2*SCREEN_HEIGHT;
		float bottomEdge=(float)(1+XC_Self_Adapter_Data_TRASLATE[com.bn.clp.Constant.screenId][0]-offset_Y_Button1)/2*SCREEN_HEIGHT;
		button1=new float[]{leftEdge,rightEdge,topEdge,bottomEdge};
		//按钮2左边所占的比例
		leftEdge=(float)(1-half_width_button+offset_X_Button2)/2*SCREEN_WIDTH;
		rightEdge=(float)(1+half_width_button+offset_X_Button2)/2*SCREEN_WIDTH;
		topEdge=(float)(1-XC_Self_Adapter_Data_TRASLATE[com.bn.clp.Constant.screenId][0]-offset_Y_Button2)/2*SCREEN_HEIGHT;
		bottomEdge=(float)(1+XC_Self_Adapter_Data_TRASLATE[com.bn.clp.Constant.screenId][0]-offset_Y_Button2)/2*SCREEN_HEIGHT;
		button2=new float[]{leftEdge,rightEdge,topEdge,bottomEdge};
		
		
	}
	public int initTextureFromBitmap(Bitmap bitmapTmp)//textureId
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
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);
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
	
   	public static void loadWelcomeBitmap(Resources r)
	{
		  InputStream is=null;
          try  
          {
        	  is= r.openRawResource(R.drawable.up);	
        	  bmUp=BitmapFactory.decodeStream(is);
        	  is= r.openRawResource(R.drawable.down);	
        	  bmDown=BitmapFactory.decodeStream(is);
        	  is= r.openRawResource(R.drawable.gg1);	
        	  bmaWall[0]=BitmapFactory.decodeStream(is);
        	  is= r.openRawResource(R.drawable.gg2);	
        	  bmaWall[1]=BitmapFactory.decodeStream(is);
        	  is= r.openRawResource(R.drawable.gg3);	
        	  bmaWall[2]=BitmapFactory.decodeStream(is);
        	  is= r.openRawResource(R.drawable.floor);	
        	  bmFloor=BitmapFactory.decodeStream(is); 

    	      heroBoatTexBitmap=new Bitmap[parts[0].length];
        	  quickBoatTexBitmap=new Bitmap[parts[1].length];
        	  slowBoatTexBitmap=new Bitmap[parts[2].length];  
        	  
        	  for(int i=0;i<parts[0].length;i++)
        	  {
        		  is= r.openRawResource(BoatInfo.boatTexIdName[0][i]);	
        		  heroBoatTexBitmap[i]=BitmapFactory.decodeStream(is);
        	  }
        	  
        	  for(int i=0;i<parts[1].length;i++)
        	  {
        		  is= r.openRawResource(BoatInfo.boatTexIdName[1][i]);	
        		  quickBoatTexBitmap[i]=BitmapFactory.decodeStream(is);
        	  }
        	  
        	  for(int i=0;i<parts[2].length;i++)
        	  {
        		  is= r.openRawResource(BoatInfo.boatTexIdName[2][i]);	
        		  slowBoatTexBitmap[i]=BitmapFactory.decodeStream(is);
        	  }
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
}

package com.bn.tl;
import static com.bn.tl.Constant.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CapsuleShapeZ;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Transform;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
public class GLGameView extends GLSurfaceView
{	
	private final float TOUCH_SCALE_FACTOR = 180.0f/320/4;//角度缩放比例
	BasketBall_Shot_Activity father;//主Activity的引用
	SceneRenderer myRenderer;//渲染器
	float xAngle=30f;//仰角
	float yAngle=0f;
    float upX=0;
    float upY=1;  
    float upZ=0;//up轴
	boolean isnoCamear=false;//摄像机是否需要回归原点，是否将球投出去了已经，投出去了则返回
	float cx=CAMERA_X; //摄像机位置
	float cy=CAMERA_Y; 
	float cz=CAMERA_Z;  
	float tx=0;//目标点位置
	float ty=CAMERA_Y;
	float tz=0;
    float mPreviousX;//上次的触控位置X坐标
    float mPreviousY;//上次的触控位置Y坐标
    DiscreteDynamicsWorld shijie;//世界对象
    CollisionDispatcher dispatcher;
    CollisionShape lifangti;//共用的立方体，用来做篮板
    CollisionShape pingmian[];//共用的平面形状
    CollisionShape basketballShape;//共用球体
    TianjiaBody planeS[];//刚体平面
    CollisionShape lanquanjiaonang;//篮圈胶囊
    BasketBallTextureByVertex ball;//圆球
    LanWang lanWuang;
    ArrayList<BasketBallForDraw> ballLst=new ArrayList<BasketBallForDraw>();//装有所有球的列表
    WenLiJuXing downPanel;//下平面
    WenLiJuXing frontPanel;//前平面
    WenLiJuXing leftPanel;//左平面
    LanBan backboard;//篮板
    Yuanzhu zj;//支架
    MoXing lankuang;//篮筐	
    HuiZhiShuZi shuzi;//数字
    WenLiJuXing ybb;//仪表板
    HuanYingJieMianJuXing wr;//欢迎界面对象
    HuanYingJieMianJuXing dot;//进度条对象
    WenLiJuXing shipingjimian;//视频界面下的四个按钮矩形
    WenLiJuXing wenziJuxing;//文字矩形
    int curr_process=0;//当前进度
    BasketBallForDraw curr_ball;//临时篮球的引用
    float touch_x=0f;//定义触摸点的位置
    float touch_y=0f;
    Bitmap bm_floor;//------------------这里是所有纹理的bitmap对象----------
    Bitmap bm_swall1;
    Bitmap bm_swall3;
    Bitmap bm_swall2;
    Bitmap bm_basketball;
    Bitmap bm_lanban2;
    Bitmap bm_yibiaoban;
    Bitmap bm_number;
    Bitmap bm_basketnet;//篮网
    Bitmap bm_shou;//帮助界面下的手
    Bitmap bm_stop;//停止按钮
    Bitmap bm_pause;//暂停按钮
    Bitmap bm_play;//播放按钮
    float ratio;//视口的缩放比例
    boolean isStart=false;//是否开始绘制游戏场景了
    JiaoNangTianjiaBody zjJiaonang1;//篮板支柱
	JiaoNangTianjiaBody zjJiaonang2;
	int jiaolanggeshu=8;//组成篮圈的胶囊个数
	JiaoNangTianjiaBody langquanJiaonang[]=new JiaoNangTianjiaBody[jiaolanggeshu];
	public int dibanTexId;//地板纹理
	public int basketbalolid;//篮球纹理
	public int zuobianQiangID;//左边墙纹理
	public int youbianQiangID;//右边墙纹理
	public int houmianQiangID;//篮板后面平面纹理
	public int lanbanId;//篮板纹理
	public int shijianxiansBeijingId;//顶上时间显示背景纹理
	public int shuziId;//数字纹理
	public int welcomeid;//加载纹理
	public int dotId;//加载进度条
	public int lanwangId;//篮网纹理
	public int wenziId=-1;//文件矩形纹理
	public int shouId;//抓
	public int stopId;//停止按钮
	public int pauseId;//暂停按钮
	public int playId;//播放按钮
	boolean isFirst=true;//是否是第一帧
	boolean hasLoadOk=false;//是否已经加载完成
	long start;//记录加载完成的时间
    
	public GLGameView(Context context) 	{
		super(context);
		father=(BasketBall_Shot_Activity)context;//设置摄像机的位置		
        cx=(float)(tx+Math.cos(Math.toRadians(xAngle))*
        		Math.sin(Math.toRadians(yAngle))*DISTANCE);//摄像机x坐标 
        cz=(float)(tz+Math.cos(Math.toRadians(xAngle))*
        		Math.cos(Math.toRadians(yAngle))*DISTANCE);//摄像机z坐标 
        cy=(float)(ty+Math.sin(Math.toRadians(xAngle))*DISTANCE);//摄像机y坐标 
		deadtimesMS=0;//初始化倒计时
		flag=true;//物理模拟线程运行标志位
		Constant.defen=0;//得分清零
		this.setEGLContextClientVersion(2);//设置渲染模式为2.0
		myRenderer=new SceneRenderer();//创建渲染器
		setRenderer(myRenderer);//设置渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
	}
	//初始化物理世界的方法
	public void initWorld()	{
		//创建碰撞检测配置信息对象
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		//创建碰撞检测算法分配者对象，其功能为扫描所有的碰撞检测对，并确定适用的检测策略对应的算法
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		BroadphaseInterface overlappingPairCache = new DbvtBroadphase();
		//创建推动约束解决者对象
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		//创建物理世界对象
		shijie = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		//设置重力加速度
		shijie.setGravity(new Vector3f(0, G, 0));
		//创建共用的立方体
		lifangti=new BoxShape(new Vector3f(36*LANBAN_BILIXISHU/2,21*LANBAN_BILIXISHU/2,LANBAN_BILIXISHU/2));
		//创建共用的平面形状
		pingmian=new CollisionShape[]{
				new StaticPlaneShape(new Vector3f(0, 1f, 0f), 0.05f),//地面
				new StaticPlaneShape(new Vector3f(0, 0, -1), 0),//前面
				new StaticPlaneShape(new Vector3f(0, 0, 1), 0),//后面
				new StaticPlaneShape(new Vector3f(1, 0, 0), 0),//左面
				new StaticPlaneShape(new Vector3f(-1, 0, 0), 0),//右面
		};
		//创建篮球刚体
		basketballShape=new SphereShape(QIU_R);
		lanquanjiaonang=new CapsuleShapeZ(ZJ_R, 
				QIU_R*(float)(Math.cos(Math.toRadians((180-360/jiaolanggeshu)/2)))*2-ZJ_R*2);//篮圈的胶囊
	}
	 @Override 
	public boolean onTouchEvent(MotionEvent e) 	{
	        float y = e.getY();
	        float x = e.getX();
	        boolean isDianjibb=false;//是否点击到篮球
	        switch (e.getAction()){
	        case MotionEvent.ACTION_DOWN:
	        	if(isnoHelpView){//如果是视频播放界面
	        		if(x>0&&x<96*ratio_width&&y>704*ratio_height){//点击停止播放视频，进入游戏界面
	        			for(int i=0;i<3;i++){
	        				Transform tt=new Transform();
                		    tt.origin.set(new Vector3f(STARTBALL[(i)%3][0],STARTBALL[(i)%3][1],STARTBALL[(i)%3][2]));//设置位置
                		    ballLst.get((i)%3).body.setCenterOfMassTransform(tt);	
	        			}
	        			isnoHelpView=false;//界面设置为游戏界面
	        			isnoPlay=true;
	        			defen=1;//得分归零
	        		}
	        		else if(x>384*ratio_width&&y>704*ratio_height){
	        			isnoPlay=!isnoPlay;//播放或者暂停按钮处
	        		}	        		
	        		return true;	
	        	}	        	
	        	ArrayList<BasketBallForDraw> ballLstt=new ArrayList<BasketBallForDraw>();//装有所有球的列表
				for(BasketBallForDraw ball:ballLst){
					ballLstt.add(ball);
				}
	        	//记录按下的位置
	        	touch_x=x;
	        	touch_y=y;
	        	float x3d=CHANGJING_WIDTH*touch_x/SCREEN_WIDHT-0.5f*CHANGJING_WIDTH;
	            float y3d=CHANGJING_HEIGHT*(SCREEN_HEIGHT-touch_y)/SCREEN_HEIGHT;
	            for(BasketBallForDraw ball:ballLstt){
	            	 //当前球的位置
		            float ball_x=ball.body.getWorldTransform(new Transform()).origin.x;
		            float ball_y=ball.body.getWorldTransform(new Transform()).origin.y;
		            float ball_z=ball.body.getWorldTransform(new Transform()).origin.z;
		            float ball_scale=1.5f*QIU_R;//篮球的半径
		        	if(x3d<ball_x+ball_scale&&x3d>ball_x-ball_scale&&
		        		y3d<ball_y+ball_scale&&y3d>ball_y-ball_scale&&
		        		ball_z>1.55f){
		        		curr_ball=ball;
		        		break;
		        	}}
	            if(curr_ball==null){//如果点击处都不是球的位置
	        		  for(BasketBallForDraw ball:ballLst){
	        			  if(!ball.body.wantsSleeping()){//只要有一个还不是静止的	        			  
	        				  isDianjibb=true;//说明玩家就不能变动摄像机位置
	        			 } }}
	            if(!isDianjibb&&curr_ball==null){
	            	isnoCamear=true;
	            }
	        	break;
	        case MotionEvent.ACTION_UP:
	        	float dx=x-touch_x;//X方向上的移动距离
	        	float max_fingerTouch = 110*ratio_height;//这里设置手指最大的触摸距离
	            float dy=(y-touch_y)>0?0:((y-touch_y)<-max_fingerTouch?-max_fingerTouch:(y-touch_y));//Y方向上的移动距离
	            isnoCamear=false;//视角开始相回滚
	            isDianjibb=false;
	            if(curr_ball!=null)//&&curr_ball.body.wantsSleeping())
	            {
	            	float vTZ=1f;
	            	Vector3f linearVelocity=curr_ball.body.getLinearVelocity(new Vector3f());
	            	 if(linearVelocity.x>-vTZ&&linearVelocity.x<vTZ&&
		        		linearVelocity.y<vTZ&&linearVelocity.y>-vTZ&&
		        		linearVelocity.z>-vTZ&&linearVelocity.z<vTZ)
	            	 {
	 		           	float vx=dx*10/SCREEN_WIDHT;
	 		           	float vy=-dy*76*vFactor/SCREEN_HEIGHT;
	 		           	float vz=dy*28/SCREEN_HEIGHT;  
	 		           	curr_ball.body.activate();
	 		           	curr_ball.body.setLinearVelocity(new Vector3f(vx,vy,vz));//设置线速度
	 		           	curr_ball.body.setAngularVelocity(new Vector3f(5,0,0));//设置角速度
	 		           	curr_ball=null;
	            	 }
	            }  
	            curr_ball=null;    
	        	break;  
	        case MotionEvent.ACTION_MOVE:
	        	if(isnoCamear)//如果其为点击时所有球都停止，并且为触摸移动屏幕改变视角
	        	{
	        		float ddy = y - mPreviousY;//计算触控笔Y位移
		            xAngle += ddy * TOUCH_SCALE_FACTOR;//方位角改变    
		            if(xAngle<0)//如果当前摄像机仰角小于0,将其仰角强制为0
		            {
		            	xAngle=0;
		            }
		            if(xAngle>35)//当摄像机仰角大于35,将其强制为35;
		            {
		            	xAngle=35;
		            }
	        		cx=(float)(tx+Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle))*DISTANCE);//摄像机x坐标 
        	        cz=(float)(tz+Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle))*DISTANCE);//摄像机z坐标 
        	        cy=(float)(ty+Math.sin(Math.toRadians(xAngle))*DISTANCE);//摄像机y坐标
	        	}
	        	break;
	        }
	        mPreviousY = y;//记录触控笔位置
	        mPreviousX = x;//记录触控笔位置
	        return true;
	    }
	public class SceneRenderer implements GLSurfaceView.Renderer
	{
		
		
		//初始化3D物件的shader
		public void initShader()
		{		
	    	 ball.initShader(ShaderManager.getShadowshaderProgram());//球
	    	 lanWuang.initShader(ShaderManager.getBasketNetShaderProgram());//篮网getBasketNetShaderProgram
	         backboard.initShader(ShaderManager.getCommTextureShaderProgram());//篮板
	         zj.initShader(ShaderManager.getLigntAndTexturehaderProgram());//支架
	         lankuang.initShader(ShaderManager.getLigntAndTexturehaderProgram());//篮筐
	         downPanel.initShader(ShaderManager.getCommTextureShaderProgram());//地面
	         frontPanel.initShader(ShaderManager.getCommTextureShaderProgram());//后面
	         leftPanel.initShader(ShaderManager.getCommTextureShaderProgram());//左面
	         ybb.initShader(ShaderManager.getCommTextureShaderProgram());//仪表板
	         shuzi.intShader(ShaderManager.getBlackgroundShaderProgram());//数字       
	         if(isnoHelpView){
	        	 shipingjimian.initShader(ShaderManager.getCommTextureShaderProgram());//视频播放界面
	        	 wenziJuxing.initShader(ShaderManager.getCommTextureShaderProgram());//文字矩形getBlackgroundShaderProgram
	         }
	         
		} 
		//初始化3D欢迎界面的shader
		public void initShaderWelcome()
		{         
	    	 wr.intShader(ShaderManager.getCommTextureShaderProgram());
	    	 dot.intShader(ShaderManager.getCommTextureShaderProgram());
		}
		
		@Override
		public void onDrawFrame(GL10 gl)  
		{			
			
			//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            if(!hasLoadOk)
            {  
            	//绘制欢迎界面
            	MatrixState.pushMatrix();  
                MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);
                MatrixState.setCamera(0, 0, 0, 0, 0,-1, 0, 1, 0); 
                
                MatrixState.pushMatrix();
                MatrixState.translate(0, 0, -2);
                MatrixState.rotate(90, 1, 0, 0);
                wr.drawSelf(welcomeid);//绘制欢迎界面
                MatrixState.popMatrix();
                drawProcessBar();
                MatrixState.popMatrix();
                if(isFirst)
                {
                	isFirst=false;
                }
                else
                {
                	initTaskReal();
                	curr_process++;
                	if(curr_process>7)
                	{
                		hasLoadOk=true; 
                    	start=System.currentTimeMillis();
                	}
                	
                }
            }
            else if(System.currentTimeMillis()-start>7000)
            {
              //调用此方法计算产生透视投影矩阵
              MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
              //调用此方法产生摄像机9参数位置矩阵
              MatrixState.setCamera(cx, cy, cz, tx, ty,tz, 0, 1, 0); 
              MatrixState.pushMatrix();
              drawHouse();//绘制整个场景
              //初始化光源位置
              MatrixState.setLightLocation(3, CHANGJING_HEIGHT*1.7f, 5);
              drawBasketboard();//绘制篮架
              //开启混合
              GLES20.glEnable(GLES20.GL_BLEND);  
              //设置混合因子
              GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
              ArrayList<BasketBallForDraw> ballLstt=new ArrayList<BasketBallForDraw>();//装有所有球的列表
				for(BasketBallForDraw ball:ballLst){
					ballLstt.add(ball);
				}
              for(BasketBallForDraw bf:ballLstt)
              {
              	bf.drawSelf(basketbalolid,1,0,0);//地板影子
              	bf.drawSelf(basketbalolid,1,3,0);//后面影子      	
              }	     
              //关闭混合  
              GLES20.glDisable(GLES20.GL_BLEND);     
              for(BasketBallForDraw bf:ballLstt)
              {
              	bf.drawSelf(basketbalolid,0,0,0);//绘制所有球
              }              
              GLES20.glEnable(GLES20.GL_DEPTH_TEST);
              
              
              //开启混合
              GLES20.glEnable(GLES20.GL_BLEND);  
              //设置混合因子
              GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
              for(BasketBallForDraw bf:ballLst)
              {
              	bf.drawSelf(basketbalolid,1,4,1);//篮板上影子              	
              }	     
                   
              
              //绘制篮网
              MatrixState.pushMatrix();
  	          MatrixState.translate(LANBAN_X,  LANBAN_Y-LANQIU_HEIGHT/6, LANBAN_Z+ZJ_LENGTH+LANKUANG_R);
              MatrixState.translate(0, -LANWANG_H-LANKUANG_JM_R, 0);          
              lanWuang.drawSelf(lanwangId,
              		lanWangRaodon
              );
              MatrixState.popMatrix();
             
            //关闭混合  
              GLES20.glDisable(GLES20.GL_BLEND);
  			  drawDeshBoard();//绘制仪表盘
  			  
  			  MatrixState.popMatrix();
  			  onDrawShiping();//绘制播放界面
  			  
            }
            else
            {   
            	curr_process++;
            	MatrixState.pushMatrix();
                MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);
                MatrixState.setCamera(0, 0, 0, 0, 0,-1, 0, 1, 0); 
                MatrixState.pushMatrix();
                MatrixState.translate(0, 0, -2);
                MatrixState.rotate(90, 1, 0, 0);
                wr.drawSelf(welcomeid);
                MatrixState.popMatrix();
                drawProcessBar();
                MatrixState.popMatrix();
                //这里发射第一个球
                if(!isStart&&System.currentTimeMillis()-start>6890)
                {
                	isStart=true;
                	
                	father.curr=WhichView.GAME_VIEW;
                	ArrayList<BasketBallForDraw> ballLstt=new ArrayList<BasketBallForDraw>();//装有所有球的列表
    				for(BasketBallForDraw ball:ballLst){
    					ballLstt.add(ball);
    				}
                	if(isnoHelpView){
                		float vx=STARTBALL_V[0][0];//*10/SCREEN_WIDHT;
    		           	float vy=STARTBALL_V[0][1];
    		           	float vz=STARTBALL_V[0][2];
    		           	ballLstt.get(0).body.activate();
    		           	ballLstt.get(0).body.setLinearVelocity(new Vector3f(vx,vy,vz));//设置线速度
    		           	ballLstt.get(0).body.setAngularVelocity(new Vector3f(5,0,0));//设置角速度
    		           	startY=0;
    		           	new Thread(){
                			int array_id=1;//发射第几个球
                			boolean isnoFashe=false;//是否可以进行手的动画
                    		@Override
                    		public void run(){
                    			while(isnoHelpView){
                    				if(!isnoPlay){//如果是暂停界面
                    					continue;
                    				}
                    				ArrayList<BasketBallForDraw> ballLstt=new ArrayList<BasketBallForDraw>();//装有所有球的列表
                    				for(BasketBallForDraw ball:ballLst){
                    					ballLstt.add(ball);
                    				}
                    				startY-=2;
                    				startY%=1100;//文字y坐标
                    				array_id%=3;
                    				shipingJs+=100;
                					if(!isnoFashe&&(shipingJs)%5000==4000){
                					Transform tt=new Transform();
                        		    tt.origin.set(new Vector3f(STARTBALL[(array_id+2)%3][0],STARTBALL[(array_id+2)%3][1],STARTBALL[(array_id+2)%3][2]));//设置位置
                        		    ballLstt.get((array_id+2)%3).body.setCenterOfMassTransform(tt);	
                					shouX=STARTBALL[array_id][0]/2;//ballLst.get(array_id).body.getMotionState().getWorldTransform(new Transform()).origin.x/2;
                					shouY=-0.9f;
                					isnoFashe=true;
                					}
                    				if(isnoFashe){
                    					if(array_id%3==0){
                    						shouX+=0.03f;
                    					}else if(array_id%3==2){
                    						shouX-=0.03f;
                    					}
                    					shouY+=0.1f;
                    					if(shouY>1.2f){
                    						shouY=5f;
                    					}
                    				}else{
                    					shouY=5f;
                    				}
                    				if((shipingJs)%5000==0){
                    						isnoFashe=false;//发射完毕后                    						
                    						shouY=4;
                    						float vx=STARTBALL_V[array_id][0];//*10/SCREEN_WIDHT;
                        		           	float vy=STARTBALL_V[array_id][1];
                        		           	float vz=STARTBALL_V[array_id][2]; 
                        		           	ballLstt.get(array_id).body.activate();
                        		           	ballLstt.get(array_id).body.setLinearVelocity(new Vector3f(vx,vy,vz));//设置线速度
                        		           	ballLstt.get(array_id).body.setAngularVelocity(new Vector3f(5,0,0));//设置角速度
                        		           	array_id++;
                    					}
                    				try {
    									Thread.sleep(100);
    								} catch (InterruptedException e) {
    									e.printStackTrace();
    								}
                    				
                    			}
                    		}
                    	}.start();
                	}else{
                		float vx=0;//*10/SCREEN_WIDHT;
    		           	float vy=10.1f*vFactor;
    		           	float vz=-3.0f; 
    		           	ballLstt.get(1).body.activate();
    		           	ballLstt.get(1).body.setLinearVelocity(new Vector3f(vx,vy,vz));//设置线速度
    		           	ballLstt.get(1).body.setAngularVelocity(new Vector3f(5,0,0));//设置角速度
                	}
                }
            }
		}
		//绘制视频界面上的各个按钮
		public void onDrawShiping(){
			if(!isnoHelpView){//如果不是视频播放界面就不绘制
				return;
			}
			
        	if(wenziId!=-1)
        	{
        		GLES20.glDeleteTextures(1, new int[]{wenziId}, 0);
        	}
			
        	//生成文字纹理
        	Bitmap bm=Constant.generateWLT(Constant.content, wenziwidth, wenziHeight);
        	wenziId=initTexture(bm,true);
			
        	
			
			
			MatrixState.pushMatrix();//绘制背景
			MatrixState.setProjectOrtho(-1, 1, -1, 1, 1, 10);
			MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);//恢复矩阵
			
			//开启混合
			GLES20.glEnable(GLES20.GL_BLEND);  
			//设置混合因子
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
			
			 MatrixState.pushMatrix();
             MatrixState.translate(0,-0.05f, -2f);
             MatrixState.rotate(90, 1, 0, 0);
         	//绘制矩形
             wenziJuxing.drawSelf(wenziId);  //绘制文字按钮 
             MatrixState.popMatrix();
             
			
			MatrixState.pushMatrix();
            MatrixState.translate(-0.8f, -0.88f, -2f);
            MatrixState.rotate(90, 1, 0, 0);
        	//绘制矩形
            shipingjimian.drawSelf(stopId);  //绘制停止按钮 
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            MatrixState.translate(0.8f, -0.88f, -2f);
            MatrixState.rotate(90, 1, 0, 0);
            if(isnoPlay){//如果在播放中绘制暂停按钮
            shipingjimian.drawSelf(pauseId);  //绘制暂停按钮 
            }
            else{
            shipingjimian.drawSelf(playId);  //绘制播放按钮 	
            }
            MatrixState.popMatrix();
			
            MatrixState.pushMatrix();
            MatrixState.translate(shouX,shouY, -1.8f);
            MatrixState.rotate(90, 1, 0, 0);
        	//绘制矩形
            shipingjimian.drawSelf(shouId);  //绘制手按钮 
            MatrixState.popMatrix();
            
            //关闭混合
            GLES20.glDisable(GLES20.GL_BLEND);
            
            MatrixState.popMatrix();
            
		}
		
		//绘制进度条
		public void drawProcessBar()
		{
			float height=0f;
			 GLES20.glEnable(GLES20.GL_BLEND);  
             //设置混合因子
             GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            	 MatrixState.pushMatrix();
    			 MatrixState.translate(0f, height, -1.8f);
                 MatrixState.rotate(90, 1, 0, 0);
                 MatrixState.rotate(-curr_process*10, 0, 1, 0);
                 dot.drawSelf(dotId);
                 MatrixState.popMatrix();
             GLES20.glDisable(GLES20.GL_BLEND);
		}
		//绘制篮板
		public void drawBasketboard()
		{
			//开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			MatrixState.pushMatrix();
            MatrixState.translate(LANBAN_X, LANBAN_Y, LANBAN_Z-0.03f);
            backboard.drawSelf(lanbanId);//绘制篮板       
			MatrixState.popMatrix();  
			//关闭混合
            GLES20.glDisable(GLES20.GL_BLEND);
            
			MatrixState.pushMatrix();
            MatrixState.translate(LANBAN_X-LANBAN_BILIXISHU, LANBAN_Y-LANQIU_HEIGHT/6, LANBAN_Z+ZJ_LENGTH/2);
            MatrixState.rotate(90, 0, 1, 0);
			zj.drawSelf();//绘制左边支架
			MatrixState.popMatrix();
			
			MatrixState.pushMatrix();
            MatrixState.translate(LANBAN_X+LANBAN_BILIXISHU, LANBAN_Y-LANQIU_HEIGHT/6, LANBAN_Z+ZJ_LENGTH/2);
            MatrixState.rotate(90, 0, 1, 0);
			zj.drawSelf();//绘制右边支架
			MatrixState.popMatrix();
			
			MatrixState.pushMatrix();
	        MatrixState.translate(LANBAN_X,  LANBAN_Y-LANQIU_HEIGHT/6, LANBAN_Z+ZJ_LENGTH+LANKUANG_R);
            lankuang.drawSelf();//篮筐
          
            MatrixState.popMatrix();
		}
		//绘制仪表板
		public void drawDeshBoard()
		{
			MatrixState.pushMatrix();//绘制背景
			MatrixState.setProjectOrtho(-1, 1, -1, 1, 1, 10);
			MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);//恢复矩阵
			
			MatrixState.pushMatrix();
            MatrixState.translate(0, 1-YBB_HEIGHT/2, -2f);
            MatrixState.rotate(90, 1, 0, 0);
        	//绘制矩形
            ybb.drawSelf(shijianxiansBeijingId);  //绘制背景   
            MatrixState.popMatrix();
            //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            
            MatrixState.pushMatrix();
            MatrixState.translate(-0.65f, 1-YBB_HEIGHT/2-0.03f, -1.5f);
        	//绘制矩形
            shuzi.drawSelf(defen,shuziId);     //绘制进球个数   
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            MatrixState.translate(0.6f, 1-YBB_HEIGHT/2-0.03f, -1.5f);
        	//绘制矩形
            shuzi.drawSelf(daojishi-(int)(deadtimesMS/1000),shuziId);  //绘制时间 
            MatrixState.popMatrix();
            //关闭混合
            GLES20.glDisable(GLES20.GL_BLEND);
            MatrixState.popMatrix();
		}
		//绘制整个房间
		public void drawHouse()
		{
			 //保护现场
            MatrixState.pushMatrix();
        	//绘制矩形
            downPanel.drawSelf(dibanTexId);  //绘制地板        
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, CHANGJING_HEIGHT, 0);
            //矩形转动
            MatrixState.rotate(180, 0,0,1);
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            //矩形转动
            MatrixState.rotate(90, 1,0, 0);
            MatrixState.rotate(-90, 0,0, 1);
            MatrixState.translate(0, -CHANGJING_WIDTH/2, -CHANGJING_HEIGHT/2);
        	//绘制矩形
            leftPanel.drawSelf(zuobianQiangID);  //绘制左边墙     
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            //矩形转动
            MatrixState.rotate(90, 1,0, 0);
            MatrixState.rotate(90, 0,0, 1);
            MatrixState.translate(0, -CHANGJING_WIDTH/2, -CHANGJING_HEIGHT/2);
        	//绘制矩形
            leftPanel.drawSelf(youbianQiangID);  //绘制右边墙     
            MatrixState.popMatrix();
            
            MatrixState.pushMatrix();
            //矩形转动
            MatrixState.rotate(90, 1,0, 0);
            MatrixState.translate(0, -CHANGJING_LENGTH/2, -CHANGJING_HEIGHT/2);
        	//绘制矩形
            frontPanel.drawSelf(houmianQiangID);  //绘制右边墙     
            MatrixState.popMatrix();
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) 
		{
			 //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
        	ratio= (float) width / height;
            //打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            //初始化纹理
            if(isnoHelpView){
            	 welcomeid=initTexture(Constant.welcome2,false);//加载界面的纹理
            }else{
            	welcomeid=initTexture(Constant.welcome,false);//加载界面的纹理
            }
           
            dotId=initTexture(Constant.dot, false);
		}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//设置屏幕背景色RGBA
            GLES20.glClearColor(0.0f,0.0f,0.0f, 1.0f);  
            //初始化光源位置
            MatrixState.setLightLocation(3, 7, 5);
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //初始化变换矩阵
            MatrixState.setInitStack(); 
            ShaderManager.compileShader();//这里编译3D场景中欢迎欢迎界面中的shader
            initShaderWelcome();//初始化欢迎界面的shader
		}
		public void initTaskReal()
		{
			initBitmap();
			if(curr_process==2)
			{
				defen=0;//还原得分
				daojishi=60;//还原倒计时					
				SHENGYING_FLAG=SOUND_MEMORY;//还原声音选择
				DEADTIME_FLAG=true;//开启倒计时				
				initObject(GLGameView.this.getResources());
				ShaderManager.compileShaderReal();
			}
			if(curr_process==3){
				initShader();
			}
			if(curr_process==4){
				dibanTexId=initTexture(bm_floor,true);//地板纹理
				zuobianQiangID=initTexture(bm_swall1,true);//左墙纹理
				youbianQiangID=initTexture(bm_swall3,true);//右墙纹理
				houmianQiangID=initTexture(bm_swall2,true);//后墙纹理
				basketbalolid=initTexture(bm_basketball,true);//篮球纹理
				lanbanId=initTexture(bm_lanban2,true);//篮板纹理
				shijianxiansBeijingId=initTexture(bm_yibiaoban,true);//顶上时间等显示纹理 
				shuziId=initTexture(bm_number,true);	//数字bm_basketnet
				lanwangId=initTexture(bm_basketnet,true);//篮网纹理
				
				if(isnoHelpView){
					shouId=initTexture(bm_shou,true);;//抓
					stopId=initTexture(bm_stop,true);;//停止按钮
					pauseId=initTexture(bm_pause,true);;//暂停按钮
					playId=initTexture(bm_play,true);;//播放按钮
				}
				
				
			}
			
            	 
		}
	}
	public void initObjectWelcome(Resources r)//创建欢迎界面的物体对象
	{
		wr=new HuanYingJieMianJuXing(SCREEN_WIDHT/SCREEN_HEIGHT*2,2);
		dot=new HuanYingJieMianJuXing(0.4f, 0.4f);
	}
	public  void initObject(Resources r)//初始化对象方法	
	{	
		initWorld();//初始化物理世界
		lankuang=MoXingJiaZai.loadFromFileVertexOnly("lankuang.obj", r);//篮筐
        ball=new BasketBallTextureByVertex(QIU_R);//创建球
        lanWuang=new LanWang(LANKUANG_R,0.55f*LANKUANG_R,LANWANG_H,8);//创建篮网
        downPanel=new WenLiJuXing(CHANGJING_WIDTH, CHANGJING_LENGTH+1);//下
        frontPanel=new WenLiJuXing(CHANGJING_WIDTH, CHANGJING_HEIGHT);//前
        leftPanel=new WenLiJuXing(CHANGJING_LENGTH, CHANGJING_HEIGHT);//左
        backboard=new LanBan//创建篮板
        (
        		LANBAN_BILIXISHU/2,
        		36*LANBAN_BILIXISHU,21*LANBAN_BILIXISHU, r
        );
        zj=new Yuanzhu(ZJ_LENGTH, ZJ_R, 30, 1, r);//篮球支架
        shuzi=new HuiZhiShuZi(r);//创建数字
        ybb=new WenLiJuXing(YBB_WIDTH,YBB_HEIGHT);//创建仪表板
        ballLst.clear();
        ballLst.add
        (
        		new BasketBallForDraw(ball, basketballShape, shijie, 1,
        		STARTBALL_1[0],STARTBALL_1[1],STARTBALL_1[2],GROUP_BALL1,MASK_BALL1)
        );
        ballLst.add
        (
        		new BasketBallForDraw(ball, basketballShape, shijie, 1, 
        		STARTBALL_2[0],STARTBALL_2[1],STARTBALL_2[2],GROUP_BALL2,MASK_BALL2)
        );
        ballLst.add
        (
        		new BasketBallForDraw(ball, basketballShape, shijie, 1, 
        		STARTBALL_3[0],STARTBALL_3[1],STARTBALL_3[2],GROUP_BALL3,MASK_BALL3)
        );
        planeS=new TianjiaBody[]
        {
	        new TianjiaBody(pingmian[0], shijie, 1, 0, 0, 0,1,1),//地面
//	        new TianjiaBody(pingmian[1], shijie, 1, 0, CHANGJING_HEIGHT*3/2, 0,1,1),//屋顶
	        new TianjiaBody(pingmian[1], shijie, 1, 0, CHANGJING_HEIGHT/2, CHANGJING_LENGTH/2+QIU_R*2,0,0),//前面
	        new TianjiaBody(pingmian[2], shijie, 1, 0, CHANGJING_HEIGHT/2, -CHANGJING_LENGTH/2,1,1),//后面
	        new TianjiaBody(pingmian[3], shijie, 1, -CHANGJING_WIDTH/2, CHANGJING_HEIGHT/2, 0,1,1),//左面
	        new TianjiaBody(pingmian[4], shijie, 1, CHANGJING_WIDTH/2, CHANGJING_HEIGHT/2, 0,1,1),//右面
       
	        new TianjiaBody(lifangti, shijie, 1, 
	        		LANBAN_X, LANBAN_Y, LANBAN_Z,1,1),//篮板添加进物理世界
        };
        
        for(int i=0;i<jiaolanggeshu;i++){//创建篮筐胶囊
        	langquanJiaonang[i]=new JiaoNangTianjiaBody(lanquanjiaonang, shijie, 0,
            		LANBAN_X+LANKUANG_R*(float)(Math.cos(Math.toRadians(i*360/jiaolanggeshu))), 
            		LANBAN_Y-LANQIU_HEIGHT/6, 
            		LANBAN_Z+ZJ_LENGTH+LANKUANG_R+LANKUANG_R*(float)(Math.sin(Math.toRadians(i*360/jiaolanggeshu)))+ZJ_R,
            		1,1,0,-(360/jiaolanggeshu/2)-360/jiaolanggeshu*i,0);
        }
        if(isnoHelpView){
        	shipingjimian=new WenLiJuXing(0.3f,0.18f);//创建视频播放界面按钮矩形  
        	wenziJuxing=new WenLiJuXing(1.8f,1.45f);//创建文字矩形
        }
        
        
        new Thread()
        {
        	public void run()
        	{
        		while(flag)
        		{            			
        			try 
        			{
        				if(isnoHelpView&&!isnoPlay){//如果是播放视频界面，为停止界面则不进行物理模拟
        					continue;
        				}
            			shijie.stepSimulation(1f/60.f,5);
            			 ballControlUtil();//判断篮球是否进球
            			 if(!isnoHelpView){//如果不是视频播放界面，时间才改变
            				 deadtimesMS+=10; 
            			 }
            			 
            			 if(deadtimesMS>=daojishi*1000)
            			 {
            				 SQLiteUtil.insertTime(Constant.defen);//将分数记录进数据库
            				 flag=false;//现场停止
            				 father.shengyinBoFang(2, 0);//播放游戏结束声音 
            				 father.xiaoxichuli.sendEmptyMessage(JIESHU_JIEMIAN);
            			 }  
            			 
            			 if(xAngle>0&&!isnoCamear)
            			 {
            				 xAngle-=CAMERA_Y_SK_FH;
            				 cx=(float)(tx+Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle))*DISTANCE);//摄像机x坐标 
            			     cz=(float)(tz+Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle))*DISTANCE);//摄像机z坐标 
            			     cy=(float)(ty+Math.sin(Math.toRadians(xAngle))*DISTANCE);//摄像机y坐标 
            			 }
            			 if(xAngle<0&&!isnoCamear)
            			 {
            				 xAngle+=CAMERA_Y_SK_FH;
            				 cx=(float)(tx+Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle))*DISTANCE);//摄像机x坐标 
            			     cz=(float)(tz+Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle))*DISTANCE);//摄像机z坐标 
            			     cy=(float)(ty+Math.sin(Math.toRadians(xAngle))*DISTANCE);//摄像机y坐标 
            			 }
						Thread.sleep(10);
					} 
        			catch (Exception e) 
					{
						e.printStackTrace();
					}
        		}
        	}
        }.start();
	}
	public  void ballControlUtil()//这里主要是用于对球的物理引擎部分进行操作
	{
		for(BasketBallForDraw bf:ballLst)
        {
			//如果篮球是运动的,那么每一次跟地板碰撞的时候给篮球增加一个沿Z轴正方向的冲量
			if(bf.body.isActive()&&SYSUtil.isCollided(shijie, bf.body,planeS[0].gangti))
			{
				bf.body.applyForce(new Vector3f(0,0,60), new Vector3f(0,0,0));
			}
			//获取篮球的运动组件
			Transform transform = bf.body.getMotionState().getWorldTransform(new Transform());
			//获取篮球在世界坐标中的位置
			float position_Y=transform.origin.y;
			float position_X=transform.origin.x;
			float position_Z=transform.origin.z;
			//获取篮球的速度和旋转
			Vector3f linearVelocity=bf.body.getLinearVelocity(new Vector3f());
			Vector3f angularVelocity=bf.body.getAngularVelocity(new Vector3f());
			float linearVelocityDomain=0.08f;//线性速度阈值
			float angularVelocityDomain=1.45f;//角速度阈值
			//总的线速度和总的角速度
			float allLinearV=Math.abs(linearVelocity.x)+Math.abs(linearVelocity.y)+Math.abs(linearVelocity.z);
			float allAngularV=Math.abs(angularVelocity.x)+Math.abs(angularVelocity.y)+Math.abs(angularVelocity.z);
			//当篮球位于最前方时,设置其静止的阈值
			if(bf.body.isActive()&&position_Y<STARTBALL_3[1]+0.05)
			{
				bf.body.setLinearVelocity(new Vector3f(0,linearVelocity.y,linearVelocity.z));
			}
			if(bf.body.isActive()&&allLinearV<linearVelocityDomain&&allAngularV<angularVelocityDomain&&position_Y<STARTBALL_3[1]+0.05)
			{
				bf.body.setActivationState(CollisionObject.WANTS_DEACTIVATION);
			}
			//这里对篮球的进框进行处理
			//这里获取篮筐的中心位置坐标值
			float lankuang_X=LANBAN_X;
			float lankuang_Y=LANBAN_Y-LANQIU_HEIGHT/4;
			float lankuang_Z=LANBAN_Z+ZJ_LENGTH+LANKUANG_R;
			float lankuang_Radius=LANKUANG_R;  
			float ball_Radius=QIU_R;
			//如果篮球刚好位于篮筐中
			float temp_distance=(float) Math.sqrt((position_X-lankuang_X)*(position_X-lankuang_X)+(position_Z-lankuang_Z)*(position_Z-lankuang_Z));
			if(linearVelocity.y<0&&temp_distance<(lankuang_Radius-ball_Radius)&&position_Y>lankuang_Y)
			{
				bf.ball_State=1;
			}
			if(bf.ball_State==1&&position_Y<lankuang_Y&&
					position_X>lankuang_X-LANKUANG_R&&position_X<lankuang_X+LANKUANG_R&&
					position_Z>lankuang_Z-LANKUANG_R&&position_Z<lankuang_Z+LANKUANG_R
			)
			{
				Constant.defen++;//得分数加一哦
				new Thread(){
					@Override
					public void run()
					{
						int lanWang=0;
						int zTime=0;
						while(zTime<360){
							lanWang++;
							lanWangRaodon=lanWang%4;
							zTime+=60;
							try {
								Thread.sleep(60);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						lanWangRaodon=0;
					}
				}.start();
				bf.ball_State=0;
				father.shengyinBoFang(3, 1);//播放进球声音
			}
			Vector3f vt;
			vt=bf.body.getLinearVelocity(new Vector3f());
			if((vt.y>1f&&vt.y<6.5f)//判断是否和地面撞
					&&SYSUtil.isCollided(shijie,bf.body,planeS[0].gangti))
			{
				father.shengyinBoFang(1, 0);
			}
			else if
			(
					vt.x>1f&&
					SYSUtil.isCollided(shijie,bf.body,planeS[3].gangti)
			)
			{//左面
					father.shengyinBoFang(1, 0);
			}
			else if
			(		vt.x<-1&&
					SYSUtil.isCollided(shijie,bf.body,planeS[4].gangti)
			)
			{//右面
					father.shengyinBoFang(1, 0);
			}
			else if
			(	
					vt.z>1&&
					SYSUtil.isCollided(shijie,bf.body,planeS[2].gangti)
			)
			{//后面
					father.shengyinBoFang(1, 0);
			}
			if(SYSUtil.isCollided(shijie,bf.body,planeS[5].gangti))//篮板
			{
				 if((Math.abs(vt.x)+Math.abs(vt.y)+Math.abs(vt.z))>2)
				 {
						 father.shengyinBoFang(1, 0); 
				 }
				 bf.isnoLanBan=1;
			}
			else
			{
				bf.isnoLanBan=0;
			}
			
        }
	}
	public void initBitmap()//这里主要是用于将图片加载成Bitmap
	{
		 if(curr_process==0){
			 bm_floor=loadTexture(R.drawable.floor);
		     bm_swall1=loadTexture(R.drawable.swall1);
		     bm_swall3=loadTexture(R.drawable.swall3);
		     bm_swall2=loadTexture(R.drawable.swall2);
		     bm_basketball=loadTexture(R.drawable.basketball);
		     bm_lanban2=loadTexture(R.drawable.lanban);
		 }
		 if(curr_process==1){		 
		     
			  bm_yibiaoban=loadTexture(R.drawable.yibiaoban);
		     bm_number=loadTexture(R.drawable.number);
		     bm_basketnet=loadTexture(R.drawable.basketnet);
		     if(isnoHelpView){
		    	 bm_shou= loadTexture(R.drawable.shou);//抓
		    	 bm_stop=loadTexture(R.drawable.stop);//停止播放视频按钮
		    	 bm_pause=loadTexture(R.drawable.pause);//暂停按钮
		    	 bm_play=loadTexture(R.drawable.play);//播放按钮
		    	 
		     }
		 }
	}
	
	//通过IO加载图片
	public Bitmap loadTexture(int drawableId)
	{
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
        return bitmapTmp;
	}
	public int initTexture(Bitmap bitmapTmp,boolean needRrelease)
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
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D
        (
         GLES20.GL_TEXTURE_2D, //纹理类型
          0,   
          GLUtils.getInternalFormat(bitmapTmp), 
          bitmapTmp, //纹理图像
          GLUtils.getType(bitmapTmp), 
          0 //纹理边框尺寸
         );   
        
        if(needRrelease)
        {
        	bitmapTmp.recycle(); //纹理加载成功后释放图片
        }
        return textureId;
	}
}

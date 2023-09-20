package com.bn.archieModel;
import android.opengl.GLES20;
import com.bn.commonObject.BallTextureByVertex;
import com.bn.commonObject.CubeForDraw;
import com.bn.commonObject.NumberForDraw;
import com.bn.commonObject.TextureRect;
import com.bn.core.MatrixState;
import com.bn.gameView.BombForControl;
import com.bn.gameView.GLGameView;

import static com.bn.gameView.Constant.*;
import static com.bn.gameView.GLGameView.cx;
import static com.bn.gameView.GLGameView.cz;
/*
 * 高射炮的控制类
 */
/*
 * 绘制高射炮
 * 其中原点位于炮台的几何中心处
 * 那么 挡板上升的距离为  挡板的高度的一半和炮台高度的一半,左右的偏移量为炮管的半径
 * 炮管最低端的位置为向上移动的距离为  : 炮台高度一半+炮管长度一半
 */
public class ArchieForControl
{
	GLGameView gv;//主绘制类
	public float[] position=new float[3];//高射炮的摆放位置
	public float[] targetPosition=new float[3];//高射炮的旋转轴处的位置
	public float[] barrel_center_position=new float[3];//炮管中心处的坐标
	public float[] bomb_position_init=new float[3];//高射炮发射炮弹的初始位置
	
	int row;
	int col;
	private float oldTime=0;//记录上次发射的时间
	//--------------------
	BarrelForDraw barrel;//炮管
	BarbetteForDraw barbette;//炮台
	CubeForDraw cube;//创建挡板
	private BallTextureByVertex bomb_ball;//炮弹
	public float barrel_elevation=30;//炮管的仰角
	public float barrel_direction=0;//炮管的方向角
	
	float barrel_down_X=0;//炮管底端的坐标
	public float barrel_down_Y=barbette_length/2+cube_height/2;
	float barrel_down_Z=0;
	
	public float barrel_curr_X;//炮管几何中心的X坐标
	public float barrel_curr_Y;//炮管几何中心的Y坐标
	public float barrel_curr_Z;//炮管几何中心的Z坐标
	
	public NumberForDraw nm;//数字引用
	public TextureRect backgroundRect;//背景
	public float yAnglexue;//血转动
	public float xue_scale=0.4f;//血缩放比例
	
	public int blood=100;//血
	public int drawblood;
	
	public TextureRect mark_plane;//标记矩形
	//标记军火库位置的颜色矩形位置
	float arsenal_x,arsenal_y,arsenal_z;
	
	public boolean this_isno_Lock;//是否被锁定
	public TextureRect mark_lock;//标记被锁定的矩形
	//-----------------------------
	public ArchieForControl(GLGameView gv,BarrelForDraw barrel,BarbetteForDraw barbette,CubeForDraw cube,
							BallTextureByVertex bomb_ball,float []position,int row,int col,
							TextureRect backgroundRect,NumberForDraw nm,TextureRect mark_plane,TextureRect mark_lock	
	)
	{
		this.col=col;
		this.row=row;
		this.nm=nm;
		this.backgroundRect=backgroundRect;
		
		this.gv=gv;
		this.barrel=barrel;//初始化炮管
		this.barbette=barbette;//初始化炮台
		this.cube=cube;//初始化挡板
		this.bomb_ball=bomb_ball;//炮弹
		this.position[0]=position[0];
		this.position[1]=position[1];
		this.position[2]=position[2];
		this.targetPosition[0]=position[0];
		this.targetPosition[1]=position[1]+barrel_down_Y;
		this.targetPosition[2]=position[2];
		
		this.mark_plane=mark_plane;
		this.mark_lock=mark_lock;
		
		arsenal_x=-scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-targetPosition[0])/(MapArray[mapId].length*WIDTH_LALNDFORM);
		arsenal_y=scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-targetPosition[2])/(MapArray[mapId].length*WIDTH_LALNDFORM);
	}
	//绘制方法
	public void drawSelf(int[] texBarbetteId,int texCubeId,int[] texBarrelId,int i,int j,int rowR,int colR,
			int backgroundRectId,int numberID,int locktexId
	)
	{
		if(row<i||row>rowR||col<j||col>colR)
		{
			return;
		}
		drawblood=blood;
		
		MatrixState.pushMatrix();
		MatrixState.translate(position[0], position[1], position[2]);
		//这里计算炮管的姿态
		barrel_curr_Y=(float) (barrel_down_Y+Math.sin(Math.toRadians(barrel_elevation))*barrel_length/2);//炮管的Y坐标
		barrel_curr_Z=(float) (barrel_down_Z-Math.cos(Math.toRadians(barrel_elevation))*barrel_length/2);//炮管的Z坐标
		
		
		
		
		MatrixState.pushMatrix();
		MatrixState.rotate(barrel_direction,0, 1, 0);
		//绘制炮台
		MatrixState.pushMatrix();
		barbette.drawSelf(texBarbetteId);
		MatrixState.popMatrix();
		//创建左挡板
		MatrixState.pushMatrix();
		MatrixState.translate(-barrel_radius-cube_length/2, cube_height/2+barbette_length/2, 0);
		cube.drawSelf(texCubeId);
		MatrixState.popMatrix();
		//创建右挡板
		MatrixState.pushMatrix();
		MatrixState.translate(barrel_radius+cube_length/2, cube_height/2+barbette_length/2, 0);
		cube.drawSelf(texCubeId);
		MatrixState.popMatrix();
		//绘制炮管
		MatrixState.pushMatrix();
		//先移动
		MatrixState.translate(0, barrel_curr_Y, barrel_curr_Z);
		//后旋转
		MatrixState.rotate(barrel_elevation-90, 1, 0, 0);
		barrel.drawSelf(texBarrelId);
		MatrixState.popMatrix();
		MatrixState.popMatrix();
		
		MatrixState.popMatrix();
		
		
		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
    	if(drawblood>=0){
    		
    		MatrixState.pushMatrix();
    		MatrixState.translate(position[0], position[1]+65, position[2]);	
    		MatrixState.scale(xue_scale, xue_scale, xue_scale);
    		MatrixState.rotate(yAnglexue, 0,1, 0);
    		backgroundRect.bloodValue=drawblood*2-100+6;
    		backgroundRect.drawSelf(backgroundRectId);      	  
        	MatrixState.popMatrix();
		}
    	if(this_isno_Lock)
    	{
			MatrixState.pushMatrix();//绘制锁定的
			MatrixState.translate(position[0], position[1]+20, position[2]);	
			MatrixState.rotate(yAnglexue, 0,1, 0);
			MatrixState.rotate(rotationAngle_Plane_Z, 0,0, 1);
			MatrixState.scale(1.1f, 1.1f, 0);	
			mark_lock.drawSelf(locktexId);
	    	MatrixState.popMatrix();
		}
    	
    	GLES20.glDisable(GLES20.GL_BLEND);
    	
    	
		
		
		
	}
	public void drawSelfMark(int texId){//标记矩形,仪表板
		MatrixState.pushMatrix();
    	MatrixState.translate(arsenal_x,arsenal_y,0);
    	mark_plane.drawSelf(texId);
    	MatrixState.popMatrix();
	}
	
	//这里计算标志板的朝向
	public void calculateBillboardDirection()
	{//根据摄像机位置计算焰火粒子面朝向
		float currX_span=position[0]-cx;
		float currZ_span=position[2]-cz;
		if(currZ_span<0)
		{
			yAnglexue=(float)Math.toDegrees(Math.atan(currX_span/currZ_span));	
		}else if(currZ_span==0){
			yAnglexue=currX_span>0?90:-90;
		}
		else 
		{
			yAnglexue=180+(float)Math.toDegrees(Math.atan(currX_span/currZ_span));	
		}
		
		if(isno_Lock){
			this_isno_Lock=false;
			return;
		}
		//计算其是否被锁定
		float x1,y1,z1,x2,y2,z2;
		x1=position[0]-PLANE_X;
		y1=position[1]-PLANE_Y;
		z1=position[2]-PLANE_Z;
		float distance1=(float) Math.sqrt(x1*x1+y1*y1+z1*z1);
		
		if(distance1>minimumdistance){//如果距离超出范围，或者已经有一个被锁定了，则自己不能被锁定
			this_isno_Lock=false;
			return;
		}//计算飞机飞行的方向向量
		x2=directionX;//-(float) (Math.cos(Math.toRadians(rotationAngle_Plane_X))*Math.sin(Math.toRadians(rotationAngle_Plane_Y)));
		y2=directionY;//(float) (Math.sin(Math.toRadians(rotationAngle_Plane_X)));
		z2=directionZ;//-(float) (Math.cos(Math.toRadians(rotationAngle_Plane_X))*Math.cos(Math.toRadians(rotationAngle_Plane_Y)));
		
		float cosa=(float) Math.acos((x1*x2+y1*y2+z1*z2)/(distance1*1));
		if(cosa<Lock_angle){
			if(Lock_Arch!=null){
			Lock_Arch.this_isno_Lock=false;
			}
			this.this_isno_Lock=true;			
			minimumdistance=distance1;//最小距离设置为该距离
			nx=x1;ny=y1+10;nz=z1;//发射子弹方向向量
			isno_Lock=true;//已经被锁定
			Lock_Arch=this;//自己被锁定		
		}else{
			this_isno_Lock=false;
		}
		
	}
	//时时改变高射炮的角度
	public void go()
	{
		calculateBillboardDirection();
		//这里获取飞机的位置
		float curr_planeX=PLANE_X;
		float curr_planeY=PLANE_Y;
		float curr_planeZ=PLANE_Z;
		
		//计算当前高射炮的目标点和飞机之间的距离   这里是    平方
		float curr_distance=(curr_planeX-targetPosition[0])*(curr_planeX-targetPosition[0])+
							(curr_planeY-targetPosition[1])*(curr_planeY-targetPosition[1])+
							(curr_planeZ-targetPosition[2])*(curr_planeZ-targetPosition[2]);
		curr_distance=(float) Math.sqrt(curr_distance);
		if(curr_distance>ARCHIE_MAX_DISTANCE)
		{
			return;//如果超出高射炮的扫面范围,那么直接返回
		}
		//这里计算高度差
		float curr_y_span=curr_planeY-targetPosition[1];
		if(curr_y_span<=0)
		{
			return;//如果小于0,那么直接返回
		}
		//这里计算高射炮的仰角和方位角
		float curr_elevation=(float) Math.toDegrees( Math.asin(curr_y_span/curr_distance));
		barrel_elevation=curr_elevation;//仰角
		//根据反正切计算方位角
		float curr_x_span=curr_planeX-targetPosition[0];
		float curr_z_span=curr_planeZ-targetPosition[2];
		float curr_direction=(float)Math.toDegrees(Math.atan(curr_x_span/curr_z_span));
		if(curr_x_span==0&&curr_z_span==0)
		{
			barrel_direction=curr_direction=0;
		}
		else if(curr_z_span>=0)
		{
			barrel_direction=curr_direction=curr_direction+180;
		}
		else
		{
			barrel_direction=curr_direction;
		}
		//---------------如何可以发炮
		//这里计算高射炮炮弹的初始位置
		if(System.nanoTime()-oldTime>1000000000)//每隔一秒发射一次炮弹
		{
			bomb_position_init[0]=(float) (targetPosition[0]-Math.cos(Math.toRadians(barrel_elevation))*
								Math.sin(Math.toRadians(barrel_direction))*barrel_length);//X
			bomb_position_init[1]=(float) (targetPosition[1]+Math.sin(Math.toRadians(barrel_elevation))*
									barrel_length);//Y
			bomb_position_init[2]=(float) (targetPosition[2]-Math.cos(Math.toRadians(barrel_elevation))*
							   Math.cos(Math.toRadians(barrel_direction))*barrel_length);//Z
//			发射炮弹
			archie_bomb_List.add(new BombForControl(gv,bomb_ball,bomb_position_init,barrel_elevation,
								barrel_direction));
			gv.activity.playSound(7,0);
			oldTime=System.nanoTime();
		}
	}
}

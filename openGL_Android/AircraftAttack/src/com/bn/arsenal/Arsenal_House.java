package com.bn.arsenal;


import static com.bn.gameView.Constant.ARSENAL_Y;
import static com.bn.gameView.Constant.BUTTON_RADAR_BG_WIDTH;
import static com.bn.gameView.Constant.Lock_angle;
import static com.bn.gameView.Constant.Lock_arsenal;
import static com.bn.gameView.Constant.MapArray;
import static com.bn.gameView.Constant.PLANE_X;
import static com.bn.gameView.Constant.PLANE_Y;
import static com.bn.gameView.Constant.PLANE_Z;
import static com.bn.gameView.Constant.WIDTH_LALNDFORM;
import static com.bn.gameView.Constant.directionX;
import static com.bn.gameView.Constant.directionY;
import static com.bn.gameView.Constant.directionZ;
import static com.bn.gameView.Constant.isOvercome;
import static com.bn.gameView.Constant.isno_Lock;
import static com.bn.gameView.Constant.mapId;
import static com.bn.gameView.Constant.minimumdistance;
import static com.bn.gameView.Constant.nx;
import static com.bn.gameView.Constant.ny;
import static com.bn.gameView.Constant.nz;
import static com.bn.gameView.Constant.rotationAngle_Plane_Z;
import static com.bn.gameView.Constant.scalMark;
import static com.bn.gameView.GLGameView.cx;
import static com.bn.gameView.GLGameView.cz;
import android.opengl.GLES20;

import com.bn.commonObject.NumberForDraw;
import com.bn.commonObject.TextureRect;
import com.bn.core.MatrixState;
public class Arsenal_House 
{
	public House house;//模型
	public float tx,ty,tz;//模型位置
	public NumberForDraw nm;//数字引用
	public TextureRect bfh;//百分号
	public TextureRect backgroundRect;//背景
	public int blood=com.bn.gameView.Constant.arsenal_blood;//血
	public int drawblood;
	float yAngle=0;//血显示转动角度
	
	public TextureRect mark_plane;//标记矩形
	//标记军火库位置的颜色矩形位置
	float arsenal_x,arsenal_y,arsenal_z;
	
	public boolean this_isno_Lock;//是否被锁定
	public TextureRect mark_lock;//标记被锁定的矩形
	
	int row;//所在行列
	int col;
	public Arsenal_House(House house,float tx,float ty,float tz,TextureRect mark_plane,TextureRect mark_lock,int col,int row){
		this.mark_lock=mark_lock;
		this.house=house;
		this.tx=tx;
		this.ty=ty;
		this.tz=tz;		
		this.col=col;
		this.row=row;
		this.mark_plane=mark_plane;
		arsenal_x=-scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-tx)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
		arsenal_y=scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-tz)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
	}
	public void drawSelf(int texFrot,int texSide,int texRoof,int texAnnulus,float yuanAngle,
			int backgroundRectId,int numberID,int locktexId,int ii,int jj,int rowR,int colR){
		if(row<ii||row>rowR||col<jj||col>colR)
		{
			return;
		}
		calculateBillboardDirection();
		drawblood=blood;
		MatrixState.pushMatrix();
    	MatrixState.translate(tx,ty,tz);
    	house.drawSelf(texFrot, texSide, texRoof,texAnnulus,yuanAngle,
    			 backgroundRectId, numberID,drawblood,yAngle);
    	MatrixState.popMatrix();
    	GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    	if(this_isno_Lock&&!isOvercome)
    	{
			MatrixState.pushMatrix();//绘制锁定的
			MatrixState.translate(tx, ty+ARSENAL_Y/2, tz);	
			MatrixState.rotate(yAngle, 0,1, 0);
			MatrixState.rotate(rotationAngle_Plane_Z, 0,0, 1);
			MatrixState.scale(3.5f, 3.5f, 0);			
			mark_lock.drawSelf(locktexId);
	    	MatrixState.popMatrix();
		}
    	GLES20.glDisable(GLES20.GL_BLEND);
	}
	public void drawSelfMark(int texId){
		MatrixState.pushMatrix();
    	MatrixState.translate(arsenal_x,arsenal_y,0);
    	mark_plane.drawSelf(texId);
    	MatrixState.popMatrix();
	}
	
	//这里计算标志板的朝向
	public void calculateBillboardDirection()
	{//根据摄像机位置计算焰火粒子面朝向
		float currX_span=tx-cx;
		float currZ_span=tz-cz;
		if(currZ_span<0)
		{
			yAngle=(float)Math.toDegrees(Math.atan(currX_span/currZ_span));	
		}else if(currZ_span==0){
			yAngle=currX_span>0?90:-90;
		}
		else 
		{
			yAngle=180+(float)Math.toDegrees(Math.atan(currX_span/currZ_span));	
		}
		//计算其是否被锁定
		float x1,y1,z1,x2,y2,z2;
		x1=tx-PLANE_X;
		y1=ty+ARSENAL_Y/2-PLANE_Y;
		z1=tz-PLANE_Z;
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
			if(Lock_arsenal!=null){
				Lock_arsenal.this_isno_Lock=false;
				}
			this.this_isno_Lock=true;			
			minimumdistance=distance1;//最小距离设置为该距离
			nx=x1;ny=y1+ARSENAL_Y/3;nz=z1;//发射子弹方向向量
			isno_Lock=true;//已经被锁定
			Lock_arsenal=this;	
		}else{
			this_isno_Lock=false;
		}
	}
}

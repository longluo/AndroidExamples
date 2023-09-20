package com.bn.planeModel;

import static com.bn.gameView.Constant.BUTTON_RADAR_BG_WIDTH;
import static com.bn.gameView.Constant.ENEMYPLANE_SPAN;
import static com.bn.gameView.Constant.Lock_Distance;
import static com.bn.gameView.Constant.Lock_angle;
import static com.bn.gameView.Constant.MapArray;
import static com.bn.gameView.Constant.PLANE_RATIO;
import static com.bn.gameView.Constant.PLANE_X;
import static com.bn.gameView.Constant.PLANE_Y;
import static com.bn.gameView.Constant.PLANE_Z;
import static com.bn.gameView.Constant.WIDTH_LALNDFORM;
import static com.bn.gameView.Constant.bullet_List;
import static com.bn.gameView.Constant.directionX;
import static com.bn.gameView.Constant.directionY;
import static com.bn.gameView.Constant.directionZ;
import static com.bn.gameView.Constant.enemy_plane_place;
import static com.bn.gameView.Constant.isno_Hit;
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

import java.util.Collections;

import android.opengl.GLES20;

import com.bn.commonObject.NumberForDraw;
import com.bn.commonObject.TextureRect;
import com.bn.core.MatrixState;
import com.bn.gameView.BulletForControl;
import com.bn.gameView.GLGameView;


public class EnemyPlane 
{
	public Plane plane;//模型引用
	public float tx,ty,tz;//初始位置
	public float xAngle,yAngle,zAngle;//方向向量
	public float arsenal_x,arsenal_y;//在仪表板中的位置
	public float thisnx,thisny,thisnz;
	
	
	public NumberForDraw nm;//数字引用
	public TextureRect backgroundRect;//背景
	public float xue_scale=0.4f;//血缩放比例
	public TextureRect mark_lock;//标记被锁定的矩形
	public TextureRect mark_plane;//标记矩形
	
	public boolean this_isno_Lock=false;//自己是否被锁定，被锁定就绘制锁定矩形
	public int blood=100;//坦克的生命值
	int drawblood;
	float yAnglexue;
	public int id;//代表是第几个飞机
	float distance1;//敌机和飞机的距离
	GLGameView gv;
	long oldTime;
	public EnemyPlane(GLGameView gv,Plane plane,float tx,float ty,float tz,float xAngle,float yAngle,float zAngle,
			TextureRect backgroundRect,NumberForDraw nm,TextureRect mark_plane,TextureRect mark_lock,int id
	){
		this.gv=gv;
		this.id=id;
		this.nm=nm;
		this.backgroundRect=backgroundRect;
		this.mark_plane=mark_plane;
		this.mark_lock=mark_lock;
		
		
		
		this.plane=plane;
		this.tx=tx;this.ty=ty;this.tz=tz;
		this.xAngle=xAngle;
		this.yAngle=yAngle;
		this.zAngle=zAngle;
		thisnx=-(float) (ENEMYPLANE_SPAN*Math.sin(Math.toRadians(yAngle)));
		thisnz=-(float) (ENEMYPLANE_SPAN*Math.cos(Math.toRadians(yAngle)));
		thisny=0;
		arsenal_x=scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-PLANE_X)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
		arsenal_y=-scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-PLANE_Z)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
	
	}
	public void drawSelf(int texBodyHeadId,int texScrewId,int texBodyBackId,int texCabinId,//绘制飞机
			 int texFrontWingId,int texFrontWing2Id,int texCylinder3Id,int texCylinderId,
			 int texCylinder2Id,int texBackWingId,int texTopWingId,
			 int backgroundRectId,int numberID,int locktexId
	){
		
		drawblood=blood;
		MatrixState.pushMatrix();
		
		MatrixState.translate(tx, ty, tz);
		MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);	
		MatrixState.scale(PLANE_RATIO, PLANE_RATIO, PLANE_RATIO);
		plane.drawSelf(texBodyHeadId, texScrewId, texBodyBackId, texCabinId, texFrontWingId,
				texFrontWing2Id, texCylinder3Id, texCylinderId, texCylinder2Id, texBackWingId, texTopWingId);
		MatrixState.popMatrix();
		
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		if(drawblood>=0){
			
			
			MatrixState.pushMatrix();
			MatrixState.translate(tx, ty+30, tz);	
			MatrixState.scale(xue_scale, xue_scale, xue_scale);
			MatrixState.rotate(yAnglexue, 0,1, 0);
			MatrixState.rotate(rotationAngle_Plane_Z, 0,0, 1);
			backgroundRect.bloodValue=drawblood*2-100+6;
			backgroundRect.drawSelf(backgroundRectId);      	  
	    	MatrixState.popMatrix();
		}
		if(this_isno_Lock)
		{
			MatrixState.pushMatrix();//绘制锁定的
			MatrixState.translate(tx, ty, tz);	
			MatrixState.rotate(yAnglexue, 0,1, 0);
			MatrixState.scale(0.8f, 0.8f,0.8f);
			mark_lock.drawSelf(locktexId);
	    	MatrixState.popMatrix();
		}
		GLES20.glDisable(GLES20.GL_BLEND);
	}
	public void drawSelfMark(int texId)//仪表盘上的标记位置的颜色框
	{
		MatrixState.pushMatrix();
    	MatrixState.translate(arsenal_x,arsenal_y,0);
    	plane.mark_plane.drawSelf(texId);
    	MatrixState.popMatrix();
	}
	public void go()
	{
		if(this_isno_Lock)//如果被锁定，敌机朝着飞机飞
		{
			thisnx=-nx;
			thisny=-ny;
			thisnz=-nz;
			xAngle=(float) Math.toDegrees(Math.atan(thisny/Math.sqrt(thisnx*thisnx+thisnz*thisnz)));
			
		}
		else
		{			
			thisny=0;
			if(ty<enemy_plane_place[mapId][id][1]){//飞机高度
				thisny+=4;
			}else if(ty>enemy_plane_place[mapId][id][1]){
				thisny-=4;
			}
			if(distance1>Lock_Distance*2f){//如果飞机在其一定范围内，则敌机飞向飞机
				thisnx=-tx+PLANE_X;
				thisny=-ty+PLANE_Y;
				thisnz=-tz+PLANE_Z;
			}
		}
		if(thisnz<0)
		{
			yAngle=(float)Math.toDegrees(Math.atan(thisnx/thisnz));	
		}
		else if(thisnz==0)
		{
			yAngle=thisnx>0?90:-90;
		}
		else 
		{
			yAngle=180+(float)Math.toDegrees(Math.atan(thisnx/thisnz));	
		}
		float n=(float) (Math.sqrt(thisnx*thisnx+thisny*thisny+thisnz*thisnz));
		tx+=ENEMYPLANE_SPAN*thisnx/n;
		tz+=ENEMYPLANE_SPAN*thisnz/n;
		ty+=ENEMYPLANE_SPAN*thisny/n;
		calculateBillboardDirection();
		//计算标记飞机位置的坐标
		arsenal_x=-scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-tx)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
		arsenal_y=scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-tz)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
		if(arsenal_x*arsenal_x+arsenal_y*arsenal_y>BUTTON_RADAR_BG_WIDTH*BUTTON_RADAR_BG_WIDTH*0.4f*0.4f){
			arsenal_x=(float) (arsenal_x*0.4f*
			(BUTTON_RADAR_BG_WIDTH/Math.sqrt(arsenal_x*arsenal_x+arsenal_y*arsenal_y)));
			arsenal_y=(float) (arsenal_y*0.4f*
			(BUTTON_RADAR_BG_WIDTH/Math.sqrt(arsenal_x*arsenal_x+arsenal_y*arsenal_y)));
		}
		if(distance1>Lock_Distance*2)
		{
			return;
		}
		boolean isLock=isno_Lock;
		
		 //这里进行发射子弹
	    if(this_isno_Lock&&System.nanoTime()-oldTime>300000000)//这里设置连续发射子弹
	    {
	    	isno_Lock=false;
    		//向列表中添加子弹对象
    		try
    		{
    			
    			bullet_List.add(new BulletForControl(gv,gv.bullet_rect, tx, ty, tz, 
    					xAngle, yAngle,xAngle,
    					yAngle,zAngle,0,1));
    			bullet_List.add(new BulletForControl(gv,gv.bullet_rect, tx, ty, tz, 
    					xAngle, yAngle,xAngle,
    					yAngle,zAngle,1,1));
    			Collections.sort(bullet_List);
    		}
    		catch(Exception ee)
    		{
    			ee.printStackTrace();
    		}
    		oldTime=System.nanoTime();
	    }
	    isno_Lock=isLock;
	}
	//这里计算标志板的朝向
	public void calculateBillboardDirection()
	{//根据摄像机位置计算焰火粒子面朝向
		float currX_span=tx-cx;
		float currZ_span=tz-cz;
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
		x1=tx-PLANE_X;
		y1=ty-PLANE_Y;
		z1=tz-PLANE_Z;
		distance1=(float) Math.sqrt(x1*x1+y1*y1+z1*z1);
		
		if(distance1>Lock_Distance){//如果距离超出范围，或者已经有一个被锁定了，则自己不能被锁定
			this_isno_Lock=false;
			return;
		}//计算飞机飞行的方向向量
		if(distance1<20)
		{
			gv.activity.playSound(3,0);
			isno_Hit=true;//飞机被击中了一下
			gv.plane.blood-=10;//飞机血减少一滴
			gv.activity.shake();//手机震动一次
		}
		x2=directionX;//-(float) (Math.cos(Math.toRadians(rotationAngle_Plane_X))*Math.sin(Math.toRadians(rotationAngle_Plane_Y)));
		y2=directionY;//(float) (Math.sin(Math.toRadians(rotationAngle_Plane_X)));
		z2=directionZ;//-(float) (Math.cos(Math.toRadians(rotationAngle_Plane_X))*Math.cos(Math.toRadians(rotationAngle_Plane_Y)));
		
		float cosa=(float) Math.acos((x1*x2+y1*y2+z1*z2)/(distance1*1));
		if(cosa<Lock_angle){
			this.zAngle+=40;
			this.this_isno_Lock=true;			
			minimumdistance=distance1;//最小距离设置为该距离
			nx=x1;ny=y1;nz=z1;//发射子弹方向向量
			isno_Lock=true;//已经被锁定
		}else{
			this_isno_Lock=false;
			this.zAngle=0;
		}
	}
}

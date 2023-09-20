package com.bn.clp;

import static com.bn.clp.Constant.*;

import com.bn.core.MatrixState;
import com.bn.st.d2.MyActivity;

import android.opengl.GLES20;
import static com.bn.clp.MyGLSurfaceView.*;

//可碰撞部件控制类,交通柱的id为0，交通锥的id为1   
public class KZBJForControl
{
	KZBJDrawer kzbjdrawer;
	int id;//对应的碰撞物id，0表示交通筒；1表示障碍物
	boolean state=false;//false表示可被碰撞，true表示被撞后飞行中，否则不可碰撞。
	float x;//摆放的初始位置
	float y;
	float z;
	
	float alpha;//转动角度
	float alphaX;//转动轴向量
	float alphaY;
	float alphaZ;
	
	float currentX;//飞行中的当前位置
	float currentY;
	float currentZ;
	
	int row;//位置所在地图行和列
	int col;
	
	float vx;//飞行中的速度分量
	float vy;
	float vz;
	
	float time_Fly;//飞行累计时间
	
	MyActivity ma;
	
	public KZBJForControl(KZBJDrawer kzbjdrawer,int id,float x,float y,float z,int row,int col,MyActivity ma)
	{
		this.kzbjdrawer=kzbjdrawer;
		this.id=id;
		this.x=x;
		this.y=y;
		this.z=z;
		this.row=row;
		this.col=col;
		this.ma=ma;
	}
	
	public void drawSelf(int texId,int dyFlag)
	{
		if(dyFlag==0)//绘制实体
		{
			MatrixState.pushMatrix();
			if(!state)
			{//原始状态绘制
				MatrixState.translate(x, y, z);
				//MyGLSurfaceView类中的该控制类的列表
				ma.gameV.kzbj_array[id].drawSelf(texId);
			}
			else
			{//飞行中绘制
				if(currentY>-40) 
				{//如果已经飞行到地面以下，就不再绘制
					MatrixState.translate(currentX, currentY, currentZ);
					MatrixState.rotate(alpha,alphaX, alphaY, alphaZ);
					ma.gameV.kzbj_array[id].drawSelf(texId);
				}
			}
			MatrixState.popMatrix();
		}
		else if(dyFlag==1)//绘制倒影
		{
			//实际绘制时Y的零点
			float yTranslate=y;
			//进行镜像绘制时的调整值
			float yjx=(0-yTranslate)*2;
			
			MatrixState.pushMatrix();
			//关闭背面剪裁
            GLES20.glDisable(GLES20.GL_CULL_FACE);
			if(!state)
			{//原始状态绘制
				MatrixState.translate(x, y, z);
				MatrixState.translate(0, yjx, 0);
				MatrixState.scale(1, -1, 1);
				//MyGLSurfaceView类中的该控制类的列表
				ma.gameV.kzbj_array[id].drawSelf(texId);
			}
			else
			{//飞行中绘制
				if(currentY>-40) 
				{//如果已经飞行到地面以下，就不再绘制
					MatrixState.translate(currentX, currentY, currentZ);
					MatrixState.rotate(alpha,alphaX, alphaY, alphaZ);
					MatrixState.translate(0, yjx, 0);
					MatrixState.scale(1, -1, 1);
					ma.gameV.kzbj_array[id].drawSelf(texId);
				}
			}
			//打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
			MatrixState.popMatrix();			
		}
	}
	
	//根据船的位置计算出船头位置，并判断是否与某个可撞物体碰撞
	public void checkColl(float bX,float bZ,float carAlphaTemp)
	{
		//首先求出碰撞检测点坐标
		float bPointX=(float) (bX-BOAT_UNIT_SIZE*Math.sin(Math.toRadians(sight_angle)));
		float bPointZ=(float) (bZ-BOAT_UNIT_SIZE*Math.cos(Math.toRadians(sight_angle)));		
		
		//计算碰撞点在地图上的行和列
		float carCol=(float) Math.floor((bPointX+UNIT_SIZE/2)/UNIT_SIZE);
		float carRow=(float) Math.floor((bPointZ+UNIT_SIZE/2)/UNIT_SIZE);
		
		if(carRow==row&&carCol==col)
		{//如果大家在同一个格子里，进行严格的碰撞检测KZBJBJ
			double disP2=(bPointX-x)*(bPointX-x)+(bPointZ-z)*(bPointZ-z);
			//若船头距离目标小于4则为碰撞
			if(disP2<=4)
			{
				if(SoundEffectFlag)
				{
					ma.shengyinBoFang(4, 0); 
				}				
				state=true;//设置状态为飞行中状态
				time_Fly=0;//飞行持续时间清零
				alpha=0;
				alphaX=(float) (-20*Math.cos(Math.toRadians(carAlphaTemp)));
				alphaY=0;
				alphaZ=(float) (20*Math.sin(Math.toRadians(carAlphaTemp)));
				currentX=x;//设置飞行起始点为原始摆放点
				currentY=y;
				currentZ=z;
				//根据船的行进方向确定飞行速度的三个分量
				vx=(float) (-20*Math.sin(Math.toRadians(carAlphaTemp)));
				vy=15;
				vz=(float) (-10*Math.cos(Math.toRadians(carAlphaTemp)));
			}
		}
	}
	
	//飞行移动方法，线程定时调用此方法，实现可撞物体飞行
	public void go()
	{
		if(!state)
		{//如果不在飞行状态中不需要go
			return;
		}
		
		time_Fly=time_Fly+0.3f;//飞行持续时间增加
		alpha=alpha+10;
		//根据飞行速度的三个分量及飞行持续时间与飞行起点计算当前位置
		currentX=x+vx*time_Fly;
		currentZ=z+vz*time_Fly;
		currentY=y+vy*time_Fly-0.5f*5*time_Fly*time_Fly;//5为重力加速度
		//当碰撞物体飞行落到地面以下2000时恢复原位
		if(currentY<-2000)
		{
			state=false;			
		}
	}
}
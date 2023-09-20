package com.bn.tl;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import android.opengl.GLES20;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class BasketBallForDraw 
{
	BasketBallTextureByVertex ball;//绘制球
	RigidBody body;//对应的刚体对象
	float cx,cy,cz;//篮球所在位置
	//篮球的状态标志位
	int ball_State=0;//1表示处于篮筐上部且向下运动.其他情况下为0
	//篮球的X速度状态
	public  int isnoLanBan=0;//是否和篮板在前一刻碰撞了，0表示没有碰撞
	public  int isnoLanQuan=0;//是否和篮圈在前一刻碰撞了，0表示没有碰撞，1表示碰撞了
	public BasketBallForDraw
	(
		BasketBallTextureByVertex ball,CollisionShape colShape,
		DiscreteDynamicsWorld dynamicsWorld,float mass,float cx,float cy,float cz,short group,short mask
	)
	{
		this.cx=cx;this.cy=cy;this.cz=cz;
		this.ball=ball;
		//设置刚体的密度
		boolean isDynamic = (mass != 0f);
		Vector3f localInertia = new Vector3f(0, 0, 0);//中心坐标
		if (isDynamic) 
		{
			colShape.calculateLocalInertia(mass, localInertia);//设置密度
		}
		//创建刚体的初始变换对象
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(new Vector3f(cx, cy, cz));//位置
		//创建刚体的运动状态对象
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		//创建刚体信息对象
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo
		(
			mass, myMotionState, colShape, localInertia
		);
		//创建刚体
		body = new RigidBody(rbInfo);
		//设置反弹系数
		body.setRestitution(0.4f);
		//设置摩擦系数
		body.setFriction(0.8f);
		//首次是静止的
		body.setActivationState(CollisionObject.WANTS_DEACTIVATION);
		dynamicsWorld.addRigidBody(body,group,mask);
	}
	public void drawSelf(int ballTexId,int isShadow,int planeId,int isLanbanYy)
	{		
		try
		{
			//获取这个箱子的变换信息对象
			Transform trans = body.getMotionState().getWorldTransform(new Transform());
			Quat4f ro=trans.getRotation(new Quat4f());
			//保护现场
	        MatrixState.pushMatrix();
			//进行移位变换
			//进行旋转变换    		
			cx=trans.origin.x;
			cy=trans.origin.y;    
			cz=trans.origin.z;
			
			if(isShadow==1)
			{
				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			}
			else if(cz>3*Constant.QIU_R&&cy<Constant.QIU_R*2.5f)
			{
				GLES20.glDisable(GLES20.GL_DEPTH_TEST);
			}
			else
			{
				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			}
     
	        MatrixState.translate(cx,cy,cz);
			if(ro.x!=0||ro.y!=0||ro.z!=0)
			{
				float[] fa=SYSUtil.fromSYStoAXYZ(ro);
				MatrixState.rotate(fa[0], fa[1], fa[2], fa[3]);
			}   
			ball.drawSelf(ballTexId, isShadow,planeId,isLanbanYy);
			MatrixState.popMatrix();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

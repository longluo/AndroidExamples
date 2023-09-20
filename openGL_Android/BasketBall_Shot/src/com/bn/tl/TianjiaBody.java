package com.bn.tl;
import javax.vecmath.Vector3f;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class TianjiaBody
{
	RigidBody gangti;
	public int isnoLanBan=0;//是否和篮板在前一刻碰撞了，0表示没有碰撞
	public TianjiaBody(CollisionShape colShape,
			DiscreteDynamicsWorld dynamicsWorld,float mass,
			float cx,float cy,float cz,float restitution,float friction){
		
		//创建刚体的初始变换对象
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set(new Vector3f(cx, cy, cz));	//设置位置	
		Vector3f localInertia = new Vector3f(0, 0, 0);	//惯性设置为零	
		//创建刚体的运动状态对象
		DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
		//创建刚体信息对象
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0, myMotionState, colShape, localInertia);
		//创建刚体
		gangti = new RigidBody(rbInfo);
		//设置反弹系数
		gangti.setRestitution(restitution);
		//设置摩擦系数
		gangti.setFriction(1f);
		//将刚体添加进物理世界
		dynamicsWorld.addRigidBody(gangti, Constant.GROUP_HOUSE, Constant.MASK_HOUSE);
	}
}

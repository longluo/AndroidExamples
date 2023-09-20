package com.bn.core;

import android.opengl.Matrix;

/*
	将手机当前在yaw、pitch、row三个轴的姿态数据通过此工具类计算转换成赛艇的转向数据，以实现对赛艇的方向控制。
 */
public class RotateUtil
{
	//angle为弧度 gVector  为重力向量[x,y,z,1]
	//返回值为旋转后的向量----绕x轴旋转变换矩阵
	static float[] pm=new float[16];
	static float[] rv=new float[4];
	public static float[] pitchRotate(float angle,float[] gVector)
	{		
		Matrix.setRotateM(pm, 0, angle, 1, 0, 0);
		Matrix.multiplyMV(rv,0, pm, 0, gVector, 0);
		return rv;
	}
	
	//angle为弧度 gVector  为重力向量[x,y,z,1]
	//返回值为旋转后的向量--绕y轴旋转变换矩阵
	public static float[] rollRotate(float angle,float[] gVector)
	{
		Matrix.setRotateM(pm, 0, angle, 0, 1, 0);
		Matrix.multiplyMV(rv,0, pm, 0, gVector, 0);
		return rv;
	}		
	
	//angle为弧度 gVector  为重力向量[x,y,z,1]
	//返回值为旋转后的向量--绕z轴旋转变换矩阵
	public static float[] yawRotate(float angle,float[] gVector)
	{
		Matrix.setRotateM(pm, 0, angle, 0, 0, 1);
		Matrix.multiplyMV(rv,0, pm, 0, gVector, 0);
		return rv;
	}
	
	static float[] gVector=new float[4];
	static int[] result=new int[2];
	public static int[] getDirectionDot(float[] values)
	{
		float yawAngle=-values[0];//获取Yaw轴旋转角度弧度
		float pitchAngle=-values[1];//获取Pitch轴旋转角度弧度
		float rollAngle=-values[2];//获取Roll轴旋转角度弧度
		/*
		 * 算法思想为手机在一个姿态后首先虚拟出一个重力向量，
		 * 然后三次选装把手机恢复到原始姿态，期间重力向量伴随
		 * 变化，最后重力向量往手机平面上一投影。
		 */
		
		//虚拟一个重力向量
		gVector[0]=0;
		gVector[1]=0;
		gVector[2]=-100;
		gVector[3]=1;
		
		/*
		 * 在这里需要注意沿三个空间方向x,y,z轴所旋转的角度的恢复顺序，由于Yaw轴始终指向竖直向上（重力加速度反向），和
		 * 标准的空间坐标系的z轴一样，所以 可以通过负向旋转直接进行角度恢复；沿yaw轴将转过的角度恢复后，此时的pitch轴
		 * 就变成了空间坐标系中的x轴，沿pitch（x）轴将 转过 的角度恢复，此时的roll轴就修正为了空间坐标系中的y轴，最后
		 * 按照y轴将转过的角度恢复，则此时手机平面所在的平面变成了空间坐标 系中x-y平面，而附着于手机平面上的重力加速度
		 * 的则是一个与手机平面相交的向量，将该向量投影到手机平面，通过投影点就可以计算出小球要滚动的方向
		 * 如果不按照上述顺序进行角度恢复，则空间坐标的计算转换将会非常复杂，而上述方法中每一步的角度恢复都是基于标准
		 * 的坐标系轴，而对标准坐标轴的转换在计算机图形
		 * 学中很容易实现
		 */
		
		//yaw轴恢复
		gVector=RotateUtil.yawRotate(yawAngle,gVector);		
		//pitch轴恢复
		gVector=RotateUtil.pitchRotate(pitchAngle,gVector);			
		//roll轴恢复
		gVector=RotateUtil.rollRotate(rollAngle,gVector);
		
		result[0]=(int) gVector[0];
		result[1]=(int) gVector[1];
		return result;
	}	
}
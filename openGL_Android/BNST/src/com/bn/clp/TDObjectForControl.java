package com.bn.clp;

import android.opengl.GLES20;

import com.bn.core.MatrixState;
/*
 * 该类的主要作用是控制相应3D物体的绘制，
 * 需要获得相应3D物体的对象，objectId物件的id，物体相应的x、y、z位置、旋转的角度yAngle 
 */
public class TDObjectForControl
{
	BNDrawer bndrawer;//3D物体的对象
	int objectId;//objectId物件的id
	float x;
	float y;
	float z;
	float yAngle;
	int rows;
	int cols;
	public TDObjectForControl(BNDrawer bndrawer,int objectId,float x,float y,float z,float yAngle,int rows,int cols)
	{
		this.bndrawer=bndrawer;
		this.objectId=objectId;
		this.x=x;
		this.y=y;
		this.z=z;
		this.yAngle=yAngle;
		this.rows=rows;
		this.cols=cols;
	}
	//自定义的绘制方法drawSelf，由于在该方法中需要对矩阵进行平移以及旋转变换，所以首先需要pushMatrix，最后需要popMatrix
	public void drawSelf(int[] texId,int dyFlag)
	{
		if(dyFlag==0)//绘制实体
		{
			MatrixState.pushMatrix();
			MatrixState.translate(x, y, z);
			MatrixState.rotate(yAngle, 0, 1, 0);
			bndrawer.drawSelf(texId,dyFlag);
			MatrixState.popMatrix();
		}
		else if(dyFlag==1)//绘制倒影
		{
			//实际绘制时Y的零点
			float yTranslate=y;
			//进行镜像绘制时的调整值
			float yjx=(0-yTranslate)*2;
			
			//关闭背面剪裁
            GLES20.glDisable(GLES20.GL_CULL_FACE);
			MatrixState.pushMatrix();
			MatrixState.translate(x, y, z);
			MatrixState.rotate(yAngle, 0, 1, 0);
			MatrixState.translate(0, yjx, 0);
			MatrixState.scale(1, -1, 1);
			bndrawer.drawSelf(texId,dyFlag);
			MatrixState.popMatrix();
			//打开背面剪裁
            GLES20.glEnable(GLES20.GL_CULL_FACE);
		}
	}
}
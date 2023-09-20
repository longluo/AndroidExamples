package com.bn.archieModel;
import com.bn.commonObject.*;
import com.bn.core.MatrixState;
/*
 * 绘制炮管,用于绘制高射炮的炮管部分
 */
public class BarrelForDraw 
{
	CylinderForDraw longCylinder;//长炮筒
	CylinderForDraw shortCylinder;//短炮筒
	CircleForDraw bigCircle;//大圆,用于短炮筒
	CircleForDraw shortCircle;//小圆,用于长炮筒
	
	private float radius_ratio=1.2f;//短炮筒半径与长炮筒半径的比例
	private float cylinder_ratio=0.2f;//短炮筒占长炮筒的比例
	private float length_long;//长炮筒的长度
	private float length_short;//短炮筒的长度
	private float radius_short;//短炮筒的半径
	
	public BarrelForDraw(float length,float radius,int mProgram)
	{
		this.length_long=length;
		this.length_short=length*cylinder_ratio;
		this.radius_short=radius*radius_ratio;
		longCylinder=new CylinderForDraw(radius, length, mProgram);//绘制长炮筒
		shortCircle=new CircleForDraw(mProgram, radius);//长炮筒端口圆
		shortCylinder=new CylinderForDraw(radius_short, length_short, mProgram);//绘制短炮筒
		bigCircle=new CircleForDraw(mProgram, radius_short);//短炮筒端口圆
	}
	public void drawSelf(int texBarrelId[])//其中0表示长炮筒圆柱,1表示长炮筒圆面,2表示短炮筒圆柱,3表示短炮筒圆面
	{
		//--------------------绘制长炮筒------------------------
		//绘制长炮筒
		MatrixState.pushMatrix();
		longCylinder.drawSelf(texBarrelId[0]);
		MatrixState.popMatrix();
		//绘制长炮筒上部端口圆
		MatrixState.pushMatrix();
		MatrixState.rotate(-90, 1, 0, 0);
		MatrixState.translate(0, 0, length_long/2);
		shortCircle.drawSelf(texBarrelId[1]);
		MatrixState.popMatrix();
		//绘制长炮筒下部的圆
		MatrixState.pushMatrix();
		MatrixState.rotate(90, 1, 0, 0);
		MatrixState.translate(0, 0, length_long/2);
		shortCircle.drawSelf(texBarrelId[1]);
		MatrixState.popMatrix();
		//---------------------绘制短炮筒-------------------------
		MatrixState.pushMatrix();
		MatrixState.translate(0, length_long/2-length_short, 0);
		
		MatrixState.pushMatrix();
		shortCylinder.drawSelf(texBarrelId[2]);
		MatrixState.popMatrix();
		//绘制长炮筒上部端口圆
		MatrixState.pushMatrix();
		MatrixState.rotate(-90, 1, 0, 0);
		MatrixState.translate(0, 0, length_short/2);
		bigCircle.drawSelf(texBarrelId[3]);
		MatrixState.popMatrix();
		//绘制长炮筒下部的圆
		MatrixState.pushMatrix();
		MatrixState.rotate(90, 1, 0, 0);
		MatrixState.translate(0, 0, length_short/2);
		bigCircle.drawSelf(texBarrelId[3]);
		MatrixState.popMatrix();
		
		MatrixState.popMatrix();
	} 
}

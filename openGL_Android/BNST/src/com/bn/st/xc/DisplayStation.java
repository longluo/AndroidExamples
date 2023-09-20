package com.bn.st.xc;
import static com.bn.st.xc.Constant.*;

import com.bn.core.MatrixState;

import android.opengl.GLES20;
/*
 * 用于绘制展台
 */
public class DisplayStation 
{
	CylinderTextureByVertex cylinderStation;//站台圆柱
	private float aspan=8;//水平切分
	private float lspan=1;//竖直切分
	private float length;
	CircleForDraw circle;//绘制圆面
	
	private float alpha1=1.0f;
	private float alpha2=0.5f;
	public DisplayStation(float radius,float length)
	{
		this.length=length;
		cylinderStation=new CylinderTextureByVertex(ShaderManager.getColorshaderProgram(),radius,length,aspan,lspan,COLOR_CYLINDER);
		circle=new CircleForDraw(ShaderManager.getColorshaderProgram(), aspan, radius,COLOR_CIRCLE);
	}
	//绘制自身的圆柱面
	public void drawSelfCylinder()
	{
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		cylinderStation.drawSelf(alpha1);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
	}
	//绘制自身的圆面
	public void drawSelfCircle()
	{
		MatrixState.pushMatrix();
		MatrixState.translate(0, length/2, 0);
		MatrixState.rotate(-90, 1, 0, 0);
		circle.drawSelf(alpha1);
		MatrixState.popMatrix();
	}
	//画透明的圆面
	public void drawTransparentCircle()
	{
		MatrixState.pushMatrix();
		MatrixState.translate(0, -Constant.HOUSE_GAO/2+length/2,0);
		MatrixState.rotate(-90, 1, 0, 0);
		circle.drawSelf(alpha2);
		MatrixState.popMatrix();
	}
}

package com.bn.commonObject;
import com.bn.core.MatrixState;

/*	绘制长方体,其中心点位于原点 其中长平行于X轴,宽平行于Z轴,高平行于Y轴
*	分别由长宽高来指定
*/
public class CubeForDraw 
{
	//长方体的三个面
	TextureRect sideXY;//前面
	TextureRect sideYZ;//侧面
	TextureRect sideXZ;//上面
	float length;
	float width;
	float height;
    public CubeForDraw
    (
    	float length,
    	float width,
    	float height,
    	int mProgram
    )
    {
    	this.length=length;
    	this.width=width;
    	this.height=height;
    	sideXY=new TextureRect( length, height,mProgram);//创建前面
    	sideYZ=new TextureRect( width, height,mProgram);//创建侧面
    	sideXZ=new TextureRect(length, width, mProgram);//创建上面
    }
    public void drawSelf(int texId)
    {        
    	//绘制前面
    	MatrixState.pushMatrix();
    	MatrixState.translate(0, 0,width/2);
    	sideXY.drawSelf(texId);
    	MatrixState.popMatrix();
    	//绘制后面
    	MatrixState.pushMatrix();
    	MatrixState.rotate(180, 0, 1, 0);
    	MatrixState.translate(0, 0, width/2);
    	sideXY.drawSelf(texId);
    	MatrixState.popMatrix();
    	//绘制左面
    	MatrixState.pushMatrix();
    	MatrixState.rotate(-90, 0, 1, 0);
    	MatrixState.translate(0,0, length/2);
    	sideYZ.drawSelf(texId);
    	MatrixState.popMatrix();
    	//绘制右面
    	MatrixState.pushMatrix();
    	MatrixState.rotate(90, 0, 1, 0);
    	MatrixState.translate(0,0, length/2);
    	sideYZ.drawSelf(texId);
    	MatrixState.popMatrix();
    	//绘制上面
    	MatrixState.pushMatrix();
    	MatrixState.rotate(-90, 1, 0, 0);
    	MatrixState.translate(0,0, height/2);
    	sideXZ.drawSelf(texId);
    	MatrixState.popMatrix();
    	//绘制下面
    	MatrixState.pushMatrix();
    	MatrixState.rotate(90, 1, 0, 0);
    	MatrixState.translate(0,0, height/2);
    	sideXZ.drawSelf(texId);
    	MatrixState.popMatrix();
    }
    public void drawSelf(int texId,int texId1)
    {        
    	//绘制前面
    	MatrixState.pushMatrix();
    	MatrixState.translate(0, 0,width/2);
    	sideXY.drawSelf(texId);
    	MatrixState.popMatrix();
    	//绘制后面
    	MatrixState.pushMatrix();
    	MatrixState.rotate(180, 0, 1, 0);
    	MatrixState.translate(0, 0, width/2);
    	sideXY.drawSelf(texId);
    	MatrixState.popMatrix();
    	//绘制左面
    	MatrixState.pushMatrix();
    	MatrixState.rotate(-90, 0, 1, 0);
    	MatrixState.translate(0,0, length/2);
    	sideYZ.drawSelf(texId);
    	MatrixState.popMatrix();
    	//绘制右面
    	MatrixState.pushMatrix();
    	MatrixState.rotate(90, 0, 1, 0);
    	MatrixState.translate(0,0, length/2);
    	sideYZ.drawSelf(texId);
    	MatrixState.popMatrix();
    	//绘制上面
    	MatrixState.pushMatrix();
    	MatrixState.rotate(-90, 1, 0, 0);
    	MatrixState.translate(0,0, height/2);
    	sideXZ.drawSelf(texId1);
    	MatrixState.popMatrix();   
    }
}

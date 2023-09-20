package com.bn.st.xc;
import static com.bn.st.xc.Constant.*;

import com.bn.core.MatrixState;

//此类主要是用来绘制房间
public class HouseForDraw 
{
	//地面的长、宽和向下平移的距离
	private float floorWidth=60;
	private float floorHeight=60;
	private float floorDownOffset=-WALL_HEIGHT;
	
	//墙的宽高   这里是指  宽高的一半
	private float wallWidth=WALL_WIDHT;
	private float wallHeight=WALL_HEIGHT;
	private float wall_z_offset=(float) Math.cos((float)(Math.PI/6))*wallWidth*2;
	
	TextureRect floor;//地板
	ColorLightRect wall;//围墙
	TextureRect wall_tex;//纹理墙，用来放置广告
	
	//透明度
	private float alpha1=1.0f;
	private float alpha2=0.3f;
	
	//绘制展厅的方法
	public HouseForDraw ()	
	{  
		//创建地面
		floor=new TextureRect(ShaderManager.getCommTextureShaderProgram(),floorWidth,floorHeight);
		//创建围墙
		wall=new ColorLightRect(ShaderManager.getColorshaderProgram(),wallWidth,wallHeight,HOUSE_COLOR[1]);
		//创建放置广告的纹理墙
		wall_tex=new TextureRect(ShaderManager.getCommTextureShaderProgram(),wallWidth,wallHeight);
	}
	//绘制地板的方法
	public void drawFloor(int texId)
	{
		MatrixState.pushMatrix();
		MatrixState.translate(0, floorDownOffset,0);
    	MatrixState.rotate(-90, 1, 0, 0);
    	floor.drawSelf(texId);
		MatrixState.popMatrix();
	}
	public void drawSelf()//不透明
	{
		//绘制围墙1
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0,-wall_z_offset);
		wall.drawSelf(alpha1);
		MatrixState.popMatrix();
		//绘制围墙3
		MatrixState.pushMatrix();
		MatrixState.rotate(120, 0,1, 0);
		MatrixState.translate(0,0, -wall_z_offset);
		wall.drawSelf(alpha1);
		MatrixState.popMatrix();
		//绘制围墙5
		MatrixState.pushMatrix();
		MatrixState.rotate(240, 0,1, 0);
		MatrixState.translate(0,0, -wall_z_offset);
		wall.drawSelf(alpha1);
		MatrixState.popMatrix();
	}
	
	//绘制广告墙的方法
	public void drawTexWall(int[] texId,int index)
	{
		//绘制围墙2
		MatrixState.pushMatrix();
		MatrixState.rotate(60, 0,1, 0);
		MatrixState.translate(0,0, -wall_z_offset);
		wall_tex.drawSelf(texId[index]);
		MatrixState.popMatrix();
		
		//绘制围墙4
		MatrixState.pushMatrix();
		MatrixState.rotate(180, 0,1, 0);
		MatrixState.translate(0,0, -wall_z_offset);
		wall_tex.drawSelf(texId[index]);
		MatrixState.popMatrix();
		
		//绘制围墙6
		MatrixState.pushMatrix();
		MatrixState.rotate(300, 0,1, 0);
		MatrixState.translate(0,0, -wall_z_offset);
		wall_tex.drawSelf(texId[index]);
		MatrixState.popMatrix();
	}
	
	public void drawTransparentWall()
	{
		//绘制围墙1
		MatrixState.pushMatrix();
		MatrixState.translate(0, 0,-wall_z_offset+0.05f);
		wall.drawSelf(alpha2);
		MatrixState.popMatrix();
		//绘制围墙3
		MatrixState.pushMatrix();
		MatrixState.rotate(120, 0,1, 0);
		MatrixState.translate(0,0, -wall_z_offset+0.05f);
		wall.drawSelf(alpha2);
		MatrixState.popMatrix();
		//绘制围墙5
		MatrixState.pushMatrix();
		MatrixState.rotate(240, 0,1, 0);
		MatrixState.translate(0,0, -wall_z_offset+0.05f);
		wall.drawSelf(alpha2);
		MatrixState.popMatrix();
	}
}

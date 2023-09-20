package com.bn.archieModel;
import static com.bn.gameView.Constant.*;

import com.bn.commonObject.*;
import com.bn.core.MatrixState;
/*
 * 绘制高射炮
 * 其中原点位于炮台的几何中心处
 * 那么 挡板上升的距离为  挡板的高度的一半和炮台高度的一半,左右的偏移量为炮管的半径
 * 炮管最低端的位置为向上移动的距离为  : 炮台高度一半+炮管长度一半
 */
public class ArchieForDraw    
{
	BarrelForDraw barrel;//炮管
	BarbetteForDraw barbette;//炮台
	CubeForDraw cube;//创建挡板
	public float barrel_elevation=30;//炮管的仰角
	public float barrel_direction=0;//炮管的方向角
	
	float barrel_down_X=0;//炮管底端的坐标
	public float barrel_down_Y=barbette_length/2+cube_height/2;
	float barrel_down_Z=0;
	
	public float barrel_curr_X;//炮管几何中心的X坐标
	public float barrel_curr_Y;//炮管几何中心的Y坐标
	public float barrel_curr_Z;//炮管几何中心的Z坐标
	
	public ArchieForDraw(BarrelForDraw barrel,BarbetteForDraw barbette,CubeForDraw cube)
	{
		//创建炮管
		this.barrel=barrel;
		this.barbette=barbette;
		this.cube=cube;
	}
	public void drawSelf(int texBarbetteId[],int texCubeId,int texBarrelId[])
	{
		//这里计算炮管的姿态
		barrel_curr_Y=(float) (barrel_down_Y+Math.sin(Math.toRadians(barrel_elevation))*barrel_length/2);//炮管的Y坐标
		barrel_curr_Z=(float) (barrel_down_Z-Math.cos(Math.toRadians(barrel_elevation))*barrel_length/2);//炮管的Z坐标
		MatrixState.pushMatrix();
		MatrixState.rotate(barrel_direction,0, 1, 0);
		//绘制炮台
		MatrixState.pushMatrix();
		barbette.drawSelf(texBarbetteId);
		MatrixState.popMatrix();
		//创建左挡板
		MatrixState.pushMatrix();
		MatrixState.translate(-barrel_radius-cube_length/2, cube_height/2+barbette_length/2, 0);
		cube.drawSelf(texCubeId);
		MatrixState.popMatrix();
		//创建右挡板
		MatrixState.pushMatrix();
		MatrixState.translate(barrel_radius+cube_length/2, cube_height/2+barbette_length/2, 0);
		cube.drawSelf(texCubeId);
		MatrixState.popMatrix();
		//绘制炮管
		MatrixState.pushMatrix();
		//先移动
		MatrixState.translate(0, barrel_curr_Y, barrel_curr_Z);
		//后旋转
		MatrixState.rotate(barrel_elevation-90, 1, 0, 0);
		barrel.drawSelf(texBarrelId);
		MatrixState.popMatrix();
		MatrixState.popMatrix();
	}
}

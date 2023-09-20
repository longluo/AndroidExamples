package com.bn.planeModel;
import static com.bn.gameView.Constant.BODYBACK_A;
import static com.bn.gameView.Constant.BODYBACK_B;
import static com.bn.gameView.Constant.BODYBACK_C;
import static com.bn.gameView.Constant.BODYHEAD_A;
import static com.bn.gameView.Constant.BODYHEAD_B;
import static com.bn.gameView.Constant.BODYHEAD_C;
import static com.bn.gameView.Constant.BUTTON_RADAR_BG_WIDTH;
import static com.bn.gameView.Constant.CABIN_A;
import static com.bn.gameView.Constant.CABIN_B;
import static com.bn.gameView.Constant.CABIN_C;
import static com.bn.gameView.Constant.ENEMYPLANE_SIZE;
import static com.bn.gameView.Constant.MapArray;
import static com.bn.gameView.Constant.PLANE_SIZE;
import static com.bn.gameView.Constant.PLANE_X;
import static com.bn.gameView.Constant.PLANE_Z;
import static com.bn.gameView.Constant.WIDTH_LALNDFORM;
import static com.bn.gameView.Constant.mapId;
import static com.bn.gameView.Constant.plane_blood;
import static com.bn.gameView.Constant.scalMark;

import com.bn.commonObject.TextureRect;
import com.bn.core.MatrixState;
import com.bn.gameView.GLGameView;
/*
 * 整体飞机的架构
 */
public class Plane
{
	GLGameView gv;		//MySurfaceView 的引用
	DrawSpheroid bodyback;		//机身引用
	DrawSpheroid bodyhead;		//机头引用
	DrawSpheroid cabin;			//机舱引用
	Plane_Wing frontwing;		//前机翼
	Plane_Wing frontwing2;		//前机翼
	Plane_BackWing backwing;	//后机翼
	Plane_TopWing topwing;		//上尾翼
	Column cylinder;			//圆柱体
	Column cylinder2;			//圆柱体
	Column cylinder3;			//炮管
	Airscrew screw;   			//螺旋桨
    float initAngleY=-90;		//初始时沿x轴的倾角
    float[] planePartLWH=		//获得飞机的长高宽
	{
    	BODYBACK_B*2,BODYBACK_C*2,BODYBACK_A+BODYHEAD_A,	//机身
	};
    
    public int blood=plane_blood;
    
    public TextureRect mark_plane;//标记矩形
	//标记军火库位置的颜色矩形位置
	public float arsenal_x,arsenal_y,arsenal_z;
    
	public Plane(GLGameView gv,int mProgram, TextureRect mark_plane) 
	{
		this.gv=gv;      
		//获得各部件的引用 
		bodyback=new DrawSpheroid(BODYBACK_A*PLANE_SIZE,BODYBACK_B*PLANE_SIZE,BODYBACK_C*PLANE_SIZE,18,-90,90,-90,90,mProgram);
		bodyhead=new DrawSpheroid(BODYHEAD_A*PLANE_SIZE,BODYHEAD_B*PLANE_SIZE,BODYHEAD_C*PLANE_SIZE,18,-90,90,-90,90,mProgram);
		cabin=new DrawSpheroid(CABIN_A*PLANE_SIZE,CABIN_B*PLANE_SIZE,CABIN_C*PLANE_SIZE,18,0,360,-90,90,mProgram);
		
		frontwing = new Plane_Wing(0.4f*PLANE_SIZE*1.5f,0.12f*PLANE_SIZE*1.5f,0.004f*PLANE_SIZE*1.5f,mProgram);		
		frontwing2 = new Plane_Wing(0.4f*PLANE_SIZE*1.5f,0.12f*PLANE_SIZE*1.5f,0.004f*PLANE_SIZE*1.5f,mProgram);
		backwing = new Plane_BackWing(0.14f*PLANE_SIZE*1.5f,0.06f*PLANE_SIZE*1.5f,0.004f*PLANE_SIZE*1.5f,mProgram);
		topwing = new Plane_TopWing(0.05f*PLANE_SIZE*1.5f,0.07f*PLANE_SIZE*1.5f,0.01f*PLANE_SIZE*1.5f,mProgram);
	 	
		cylinder = new Column(0.18f*PLANE_SIZE,0.006f*PLANE_SIZE,mProgram);//机身圆柱	
		cylinder2 = new Column(0.1f*PLANE_SIZE,0.015f*PLANE_SIZE,mProgram);//机身圆柱
		cylinder3 = new Column(0.15f*PLANE_SIZE,0.02f*PLANE_SIZE,mProgram);//机身圆
		screw =  new Airscrew(0.30f*PLANE_SIZE,mProgram);
		
		this.mark_plane=mark_plane;
		arsenal_x=scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-PLANE_X)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
		arsenal_y=-scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-PLANE_Z)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
	}
	public void drawSelf(int texBodyHeadId,int texScrewId,int texBodyBackId,int texCabinId,
						 int texFrontWingId,int texFrontWing2Id,int texCylinder3Id,int texCylinderId,
						 int texCylinder2Id,int texBackWingId,int texTopWingId)
	{
		MatrixState.pushMatrix();
		MatrixState.rotate(initAngleY, 0, 1, 0);
		MatrixState.pushMatrix();
		MatrixState.rotate(180, 0, 1, 0);
		bodyhead.drawSelf(texBodyHeadId); //画机头			
		MatrixState.rotate(90, 0, 1, 0);
		MatrixState.translate(0, 0, 0.2f*ENEMYPLANE_SIZE);
		screw.drawSelf(texScrewId); //螺旋桨	
		MatrixState.popMatrix();
		
		MatrixState.pushMatrix();
		MatrixState.rotate(180, 1, 0, 0);
		bodyback.drawSelf(texBodyBackId);//画机身
		MatrixState.popMatrix();
		
		MatrixState.pushMatrix();
		MatrixState.translate(0, BODYBACK_B*ENEMYPLANE_SIZE/5f, 0);
		cabin.drawSelf(texCabinId);						//机舱
		MatrixState.popMatrix();
		
		//前机翼
		MatrixState.pushMatrix();
		MatrixState.rotate(90, 0, 1, 0);
		MatrixState.rotate(-90, 1, 0, 0);
		MatrixState.translate(0, 0, 0.12f*PLANE_SIZE);
        frontwing.drawSelf(texFrontWingId);					//上前机翼	
        MatrixState.translate(0, 0, -0.2f*PLANE_SIZE);
        frontwing2.drawSelf(texFrontWing2Id);				//下前机翼
        MatrixState.translate(-0.12f*PLANE_SIZE, 0,0.03f*PLANE_SIZE );
        cylinder3.drawSelf(texCylinder3Id);					//机身圆柱1
        MatrixState.translate(0.24f*PLANE_SIZE, 0,0 );
        cylinder3.drawSelf(texCylinder3Id);					//机身圆柱2
        MatrixState.popMatrix();
        
        //机翼圆柱
        MatrixState.pushMatrix();
        MatrixState.translate(0.07f*PLANE_SIZE, 0.016f*PLANE_SIZE, -0.4f*PLANE_SIZE);
        cylinder.drawSelf(texCylinderId);
        MatrixState.translate(-0.14f*PLANE_SIZE, 0, 0);
        cylinder.drawSelf(texCylinderId);
        MatrixState.translate(0, 0, 0.8f*PLANE_SIZE);
        cylinder.drawSelf(texCylinderId);
        MatrixState.translate(0.14f*PLANE_SIZE, 0, 0);
        cylinder.drawSelf(texCylinderId);
        MatrixState.popMatrix();
        
        //机身圆柱 
        MatrixState.pushMatrix();
        MatrixState.translate(0, 0.096f*PLANE_SIZE, 0.08f*PLANE_SIZE);
        MatrixState.rotate(30, 1, 0, 0);
        cylinder2.drawSelf(texCylinder2Id);
        MatrixState.translate(0,  -0.096f*PLANE_SIZE, -0.16f*PLANE_SIZE);
        MatrixState.rotate(-60, 1, 0, 0);
        cylinder2.drawSelf(texCylinder2Id);
        MatrixState.popMatrix();
        
      //尾翼			
        MatrixState.pushMatrix();												
        MatrixState.translate(0.6f*PLANE_SIZE, 0, 0);
        MatrixState.rotate(90, 0, 1, 0);
        MatrixState.rotate(-90, 1, 0, 0);
        backwing.drawSelf(texBackWingId);
        MatrixState.popMatrix();
 
        //上尾翼
        MatrixState.pushMatrix();	
        MatrixState.translate(0.6f*PLANE_SIZE, 0, 0);
        topwing.drawSelf(texTopWingId); 
        MatrixState.popMatrix();
        MatrixState.popMatrix();
	}
	public void drawSelfMark(int texId){//仪表盘上的标记位置的颜色框
		MatrixState.pushMatrix();
    	MatrixState.translate(arsenal_x,arsenal_y,0);
    	mark_plane.drawSelf(texId);
    	MatrixState.popMatrix();
	}
}
package com.bn.clp;

import static com.bn.clp.Constant.*;
import static com.bn.clp.MyGLSurfaceView.sight_angle;
import com.bn.core.MatrixState;
import com.bn.st.d2.MyActivity;
 
//可吃物体的控制类
public class SpeedForControl
{
	long pzTime;//碰撞时的时间
	
	SpeedForEat speed;//物件的引用
	int id;//物件id
	float x;
	float y;
	float z;
	static float angleY;
	float rows;
	float cols;
	boolean isDrawFlag=true;//是否绘制的标志位
	MyActivity ma;
	public SpeedForControl(SpeedForEat speedForEat,int id,float x,float y,float z,float angleY,float rows,float cols,MyActivity ma)
	{
		this.speed=speedForEat;
		this.id=id;
		this.x=x;
		this.y=y;
		this.z=z;
		this.rows=rows;
		this.cols=cols;
		this.ma=ma;
	}
	
	//绘制方法
	public void drawSelf(int texId,int dyFlag)
	{
		if(isDrawFlag)
		{			
			if(dyFlag==0)//绘制实体
			{
				MatrixState.pushMatrix();
				MatrixState.translate(x, y, z);
				MatrixState.rotate(angleY, 0, 1, 0);
				speed.drawSelf(texId);
				MatrixState.popMatrix();
			}
			else if(dyFlag==1)//绘制倒影
			{
				//实际绘制时Y的零点
				float yTranslate=y;
				//进行镜像绘制时的调整值
				float yjx=(0-yTranslate)*2;				
				MatrixState.pushMatrix();
				MatrixState.translate(x, y, z);
				MatrixState.rotate(angleY, 0, 1, 0);
				MatrixState.translate(0, yjx, 0);
				MatrixState.scale(1, -1, 1);
				speed.drawSelf(texId);
				MatrixState.popMatrix();
				}
		}
	}
	
	//计算是否发生碰撞
	//根据船的位置计算出船头位置，并判断是否与某个可撞物体碰撞
	public void checkColl(float bX,float bZ,float carAlphaTemp)
	{
		//首先求出碰撞检测点坐标
		float bPointX=(float) (bX-BOAT_UNIT_SIZE*Math.sin(Math.toRadians(sight_angle)));
		float bPointZ=(float) (bZ-BOAT_UNIT_SIZE*Math.cos(Math.toRadians(sight_angle)));		
		
		//计算碰撞点在地图上的行和列
		float carCol=(float) Math.floor((bPointX+UNIT_SIZE/2)/UNIT_SIZE);
		float carRow=(float) Math.floor((bPointZ+UNIT_SIZE/2)/UNIT_SIZE);
		
		if(carRow==rows&&carCol==cols&&isDrawFlag)
		{//如果大家在同一个格子里，进行严格的碰撞检测KZBJBJ
			double disP2=(bPointX-x)*(bPointX-x)+(bPointZ-z)*(bPointZ-z);
			//这里的4为一个测试值，以后需要根据实际情况更改
			if(disP2<=4)
			{//碰撞了
				if(id==0)//吃了增加氮气的物件
				{
					if(numberOfN2<maxNumberOfN2)
					{
						numberOfN2=numberOfN2+1;//氮气的数量值增加1
					} 
					if(SoundEffectFlag)
					{
						ma.shengyinBoFang(3, 0);
					}					
					isDrawFlag=false;
					pzTime=System.currentTimeMillis();
				}
				else if(id==1)//吃掉减速  
				{
					if(SoundEffectFlag)
					{
						ma.shengyinBoFang(3, 0);
					}					 
					CURR_BOAT_V=CURR_BOAT_V/2;//吃掉减速物体之后，速度变为原先的一半
					isDrawFlag=false;
					pzTime=System.currentTimeMillis();
				}
			} 
		}
	}
	//该方法是判断已经吃掉的物体是否已经经过50秒，如果经过50秒，则重新显示
	public void checkEatYet()
	{
		//如果物件的绘制标志位为true，并且时间差不大于60秒
		if(!isDrawFlag&&((System.currentTimeMillis()-pzTime)%60000/1000>=50))
		{
			isDrawFlag=true;
		}
	}
	

}
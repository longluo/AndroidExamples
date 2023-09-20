package com.bn.clp;
import static com.bn.clp.Constant.*;
import static com.bn.clp.MyGLSurfaceView.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bn.st.d2.DBUtil;
import com.bn.st.d2.MyActivity;

//监听键盘状态的线程
public class KeyThread extends Thread
{
	MyGLSurfaceView mgmv; 
	MyActivity ma;
	public boolean flag=true;
	public boolean moveFlag=true;
	public static boolean otherBoatFlag=true;
	boolean pzFlag=false;
	public static boolean upFlag=true;
	float bOldZ;
	
	int[] stepIndex;//大步索引
	int[] stepStatus;//0-大步 0-小步  2-结束
	  
	int[] stepIndexC;//小步索引
	float[][] bdbqs;//本大步起始
	float[][] bdbjb;//本大步渐变
	int[] bdbzxb;//本大步总小步
	
	int dqCount=0;//氮气计数器
	
	public KeyThread(MyGLSurfaceView mv,MyActivity ma)
	{
		this.setName("KeyThread");
		
		this.mgmv=mv;
		this.ma=ma;
		stepIndex=new int[MyGLSurfaceView.qtCount];
  	    stepStatus=new int[MyGLSurfaceView.qtCount];
  	    stepIndexC=new int[MyGLSurfaceView.qtCount];
  	    bdbqs=new float[MyGLSurfaceView.qtCount][2];
  	    bdbjb=new float[MyGLSurfaceView.qtCount][2];
  	    bdbzxb=new int[MyGLSurfaceView.qtCount];
	}
	
	public void run() 
	{ 
		while(flag)
		{		
			if(moveFlag)
			{
				synchronized(lockA)
				{
					if(!mgmv.isShaChe)
					{
						if(CURR_BOAT_V<Max_BOAT_V)
						{
							CURR_BOAT_V=CURR_BOAT_V+BOAT_A;
						}
						else if(CURR_BOAT_V>Max_BOAT_V)
						{
							CURR_BOAT_V=CURR_BOAT_V-BOAT_A;
						}
					}
					else
					{
						if(CURR_BOAT_V>0)
						{
							CURR_BOAT_V=CURR_BOAT_V+BOAT_A;
						}
						else
						{
							CURR_BOAT_V=0;
						}
					}
					
					CURR_BOAT_V_TMD=CURR_BOAT_V/Max_BOAT_V;
					if(CURR_BOAT_V_TMD>1)
					{
						CURR_BOAT_V_TMD=1;
					}
					
					if((MyGLSurfaceView.keyState&0x1)!=0) 
					{//有UP键按下
						float xOffset=0;//此步的X位移
			    		float zOffset=0;//此步的Z位移  
			    		
			    		xOffset=(float)-Math.sin(Math.toRadians(sight_angle))*CURR_BOAT_V;
		  			    zOffset=(float)-Math.cos(Math.toRadians(sight_angle))*CURR_BOAT_V;
		  			    
		  			    //计算运动后的船的XZ值
		  			    float tempbx=bx+xOffset;
		  			    float tempbz=bz+zOffset;
	  			    
		  			    //判断船头所在位置的陆地高度是否低于水面并且没有与桥墩发生碰撞
		  			    if(isYachtHeadCollectionsWithLand(tempbx,tempbz)&&!isPZ(tempbx,tempbz))
		  			    {
		  			    	bOldZ=bz;
		  			    	
		  			    	bx=tempbx;   
		  			    	bz=tempbz;
		  			    	if(CURR_BOAT_V>CURR_BOAT_V_PZ)
		  			    	{
		  			    		pzFlag=false;
		  			    	}	
		  			    }  
		  			    else
		  			    {
		  			    	Constant.CURR_BOAT_V=0;
		  			    	if(pzFlag==false&&SoundEffectFlag)
		  			    	{
		  			    		ma.shengyinBoFang(1, 0);
		  			    		pzFlag=true;
		  			    	}
		  			    }
					}					
					
					if((MyGLSurfaceView.keyState&0x4)!=0)
					{//有left键按下
						//向左转动帆船
						sight_angle=sight_angle+DEGREE_SPAN;
						//帆船视觉上向左斜
						if(yachtLeftOrRightAngle<yachtLeftOrRightAngleMax)
						{
							yachtLeftOrRightAngle=yachtLeftOrRightAngle+yachtLeftOrRightAngleA;
						}
						else
						{
							yachtLeftOrRightAngle=yachtLeftOrRightAngleMax;
						}
					}
					else if((MyGLSurfaceView.keyState&0x8)!=0)
					{//有right键按下
						//向右转动帆船
						sight_angle=sight_angle-DEGREE_SPAN; 
						//帆船视觉上向右斜
						if(yachtLeftOrRightAngle>-yachtLeftOrRightAngleMax)
						{
							yachtLeftOrRightAngle=yachtLeftOrRightAngle-yachtLeftOrRightAngleA;
						}
						else
						{
							yachtLeftOrRightAngle=-yachtLeftOrRightAngleMax;
						}
					}
					else
					{//若左后键都没有按下，则帆船视觉上不倾斜
						if(yachtLeftOrRightAngle<0)
						{
							yachtLeftOrRightAngle=yachtLeftOrRightAngle+yachtLeftOrRightAngleA;
						}
						else if(yachtLeftOrRightAngle>0)
						{
							yachtLeftOrRightAngle=yachtLeftOrRightAngle-yachtLeftOrRightAngleA;
						}
					}
					
//					if(isFirstPersonView)
//					{
//						//设置新的摄像机XZ坐标
//				    	cx=(float)(bx);//摄像机x坐标
//				        cz=(float)(bz);//摄像机z坐标
//						
//						//设置新的观察目标点XZ坐标
//				    	tx=(float)(cx-Math.sin(Math.toRadians(sight_angle))*DISTANCE+0.5f);//观察目标点x坐标 
//				        tz=(float)(cz-Math.cos(Math.toRadians(sight_angle))*DISTANCE);//观察目标点z坐标   
//					}
//					else
//					{
						//设置新的摄像机XZ坐标
				    	cx=(float)(bx+Math.sin(Math.toRadians(sight_angle-yachtLeftOrRightAngle/2))*DISTANCE);//摄像机x坐标
				        cz=(float)(bz+Math.cos(Math.toRadians(sight_angle-yachtLeftOrRightAngle/2))*DISTANCE);//摄像机z坐标
				    	
				    	//设置新的观察目标点XZ坐标
				    	tx=(float)(cx-Math.sin(Math.toRadians(sight_angle-yachtLeftOrRightAngle/2))*DISTANCE);//观察目标点x坐标 
				        tz=(float)(cz-Math.cos(Math.toRadians(sight_angle-yachtLeftOrRightAngle/2))*DISTANCE);//观察目标点z坐标  
//					}
				}
			}
			
			isHalfForBoat(bx,bz);
			isOneCycleForBoat(bx,bz);  			
			
			if(otherBoatFlag)
			{
				for(int i=0;i<mgmv.otherPaths.size();i++)
	  		  	{
	  			  if(stepStatus[i]==0)
	  			  {//若是大步调整
	  				  ArrayList<float[]> pathCurr=mgmv.otherPaths.get(i); 
	      			  bdbqs[i][0]=pathCurr.get(stepIndex[i])[0];
	      			  bdbqs[i][1]=pathCurr.get(stepIndex[i])[1];
	      			  stepIndexC[i]=0;
	      			  float bdbjsX=pathCurr.get((stepIndex[i]+1)%pathCurr.size())[0];
	      			  float bdbjsZ=pathCurr.get((stepIndex[i]+1)%pathCurr.size())[1];
	      			  double distance=Math.sqrt((bdbjsX-bdbqs[i][0])*(bdbjsX-bdbqs[i][0])+(bdbjsZ-bdbqs[i][1])*(bdbjsZ-bdbqs[i][1]));
	      			       
	      			  if(distance<1)
	      			  {
	      				  stepIndex[i]=stepIndex[i]+1;
	      				  if(stepIndex[i]==pathCurr.size())
	          			  {
	          				  stepIndex[i]=0; 
	          				  Constant.BOAT_LAP_NUMBER_OTHER[i]=Constant.BOAT_LAP_NUMBER_OTHER[i]+1;
	          				  //若圈数到了
	          				  if(Constant.BOAT_LAP_NUMBER_OTHER[i]==3)
	          				  {
	          					stepStatus[i]=2;
	          					Constant.RANK_FOR_HELP=Constant.RANK_FOR_HELP+1;
	          				  }
	          			  } 
	      			  }
	      			  else
	      			  {
	      				  bdbzxb[i]=(int) (distance/Constant.Max_BOAT_V_OTHER[i]);
	          			  bdbjb[i][0]=(bdbjsX-bdbqs[i][0])/bdbzxb[i];
	          			  bdbjb[i][1]=(bdbjsZ-bdbqs[i][1])/bdbzxb[i];
	          			  
	          			  float degree=(float) Math.toDegrees(Math.atan2(bdbjb[i][0], bdbjb[i][1]));
	          			  MyGLSurfaceView.otherBoatLocation[i][2]=degree+180;
	          			  
	          			  stepStatus[i]=1;
	          			  stepIndex[i]=stepIndex[i]+1;
	          			  
	          			  if(stepIndex[i]==pathCurr.size())
	          			  {
	          				  stepIndex[i]=0; 
	          				  Constant.BOAT_LAP_NUMBER_OTHER[i]=Constant.BOAT_LAP_NUMBER_OTHER[i]+1;
	          				  //若圈数到了
	          				  if(Constant.BOAT_LAP_NUMBER_OTHER[i]==3)
	          				  {
	          					stepStatus[i]=2;
	          					Constant.RANK_FOR_HELP=Constant.RANK_FOR_HELP+1;
	          				  }
	          			  } 
	          			  if(stepStatus[i]!=2)
	          			  {
	          				  MyGLSurfaceView.otherBoatLocation[i][0]=bdbqs[i][0]+ bdbjb[i][0]*stepIndexC[i];
	              			  MyGLSurfaceView.otherBoatLocation[i][1]=bdbqs[i][1]+ bdbjb[i][1]*stepIndexC[i]; 
	              			  stepIndexC[i]++; 
	          			  }
	      			  }
	  			  }
	  			  else if(stepStatus[i]==1)
	  			  {
	      			  MyGLSurfaceView.otherBoatLocation[i][0]=bdbqs[i][0]+ bdbjb[i][0]*stepIndexC[i];
	      			  MyGLSurfaceView.otherBoatLocation[i][1]=bdbqs[i][1]+ bdbjb[i][1]*stepIndexC[i]; 
	      			  stepIndexC[i]++;
	      			  if(stepIndexC[i]>=bdbzxb[i])
	      			  {
	      				  stepStatus[i]=0;
	      			  }
	  			  }         			  
	  		  	}
			}
			
			if(moveFlag)
			{
				if(dqCount>0)
				{
					dqCount--;
				}
				else if(dqCount<=0)
				{
					dqCount=0;
					Max_BOAT_V=Max_BOAT_V_VALUE;
				}
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	} 
	//判断是否与桥墩发生碰撞的方法
	public boolean isPZ(float bx,float bz)
	{
		
		//首先求出碰撞检测点坐标
		float bPointX=(float) (bx-BOAT_UNIT_SIZE*Math.sin(Math.toRadians(sight_angle)));
		float bPointZ=(float) (bz-BOAT_UNIT_SIZE*Math.cos(Math.toRadians(sight_angle)));
		
		//计算碰撞点在地图上的行和列
		float carCol=(float) Math.floor((bPointX+UNIT_SIZE/2)/UNIT_SIZE);  
		float carRow=(float) Math.floor((bPointZ+UNIT_SIZE/2)/UNIT_SIZE);
		
		for(PZZ temp:ma.gameV.pzzList)
		{ 
			if(temp.row==carRow&&temp.col==carCol)
			{
				if(temp.isIn(bPointX, bPointZ))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	//判断船头所在位置的陆地高度是否低于水面
	public boolean isYachtHeadCollectionsWithLand(float bx,float bz)
	{
		final float PRE_UNIT_SIZE=UNIT_SIZE/(yArray_ZD.length-1);
		
		//首先求出碰撞检测点坐标
		float tempbx=(float) (bx-BOAT_UNIT_SIZE*Math.sin(Math.toRadians(sight_angle)));
		float tempbz=(float) (bz-BOAT_UNIT_SIZE*Math.cos(Math.toRadians(sight_angle)));  
		
	    //计算碰撞点在地图上的行和列
		float col=(float) Math.floor((tempbx+UNIT_SIZE/2)/UNIT_SIZE);
		float row=(float) Math.floor((tempbz+UNIT_SIZE/2)/UNIT_SIZE);	
		
		int id=MAP_ARRAY[(int) row][(int) col];   
		if(id==8)
		{
			return true;
		}

		float colx=col*UNIT_SIZE-UNIT_SIZE/2;
		float rowz=row*UNIT_SIZE-UNIT_SIZE/2;
				
		//计算碰撞点在对应的行列格子中的x、z坐标，每个小格子的中心点即为该格子的坐标原点
		float rawXIn=tempbx-colx;
		float rawZIn=tempbz-rowz;
	    
	    float xIn=rawXIn;
		float zIn=rawZIn;
		
		if(id==1||id==10)
    	{
    		xIn=UNIT_SIZE-rawZIn;
    		zIn=rawXIn;
    	}
		else if(id==3||id==12)
    	{
    		xIn=UNIT_SIZE-rawZIn;
    		zIn=rawXIn;
    	}
		else if(id==4||id==13)
    	{
    		xIn=UNIT_SIZE-rawXIn;
    		zIn=UNIT_SIZE-rawZIn;
    	}
    	else if(id==5||id==14)
    	{
    		xIn=UNIT_SIZE-rawZIn;
    		zIn=rawXIn;
    	}
    	else if(id==6||id==15)
    	{
    		xIn=rawZIn;
    		zIn=UNIT_SIZE-rawXIn;
    	}
		
		 float[][] yArrayCurr=null;
		 if(id==0||id==1||id==9||id==10)	//直道(包括横竖)
		 {
			 yArrayCurr=yArray_ZD;
		 }
		 else if(id==2||id==3||id==11||id==12)	//直道(包括横竖带小山)
		 {
			 yArrayCurr=yArray_ZD_DXD;
		 }
		 else if(id==4||id==5||id==6||id==7||id==13||id==14|id==15|id==16)	//弯道
		 {
			 yArrayCurr=yArray_WD;
		 }
		//计算船头对应的陆地格子的行、列
	    int tempCol=(int)(xIn/PRE_UNIT_SIZE);
	    int tempRow=(int)(zIn/PRE_UNIT_SIZE);		    
    	
    	//计算船头对应的陆地格子的四个点的坐标 
	    float x0=tempCol*PRE_UNIT_SIZE;
	    float z0=tempRow*PRE_UNIT_SIZE;
	    float y0=yArrayCurr[tempRow][tempCol]; 
	        
	    float x1=x0+PRE_UNIT_SIZE;
	    float z1=z0;
	    float y1=yArrayCurr[tempRow][tempCol+1];
	    
	    float x2=x0+PRE_UNIT_SIZE;
        float z2=z0+PRE_UNIT_SIZE;
        float y2=yArrayCurr[tempRow+1][tempCol+1];
        
	    float x3=x0;
	    float z3=z0+PRE_UNIT_SIZE;
	    float y3=yArrayCurr[tempRow+1][tempCol];
	    		    
	    //船头处的陆地高度
	    float cty=0;
	    
	    if(isInTriangle(x0,z0,x1,z1,x3,z3,xIn,zIn))
	    {//判断帆船船头是否位于0-1-3三角形
	    	//求0-1-3面在船头处的高度
	    	cty=fromXZToY
		    (
			    	x0,y0,z0,				    	
			    	x3,y3,z3,
			    	x1,y1,z1,
			    	xIn,zIn
			 );
	    }
	    else
	    {
	    	//求1-2-3面在船头处的高度
	    	cty=fromXZToY
		    (
			    	x1,y1,z1,
			    	x3,y3,z3,
			    	x2,y2,z2,
			    	xIn,zIn
			);
	    }
	    if(cty<=0)
	    {//若船头处的陆地低于水面则返回true
	    	return true;
	    }
	    return false;
	}
	
	//判断一个点是否在三角形内的方法
	//基本算法思想是首先求要被判断的点到三角形三个顶点的矢量1、2、3
	//然后三个矢量求叉积，若三个叉积同号则点位于三角形内，否则位于三角形外
	public boolean isInTriangle
	(
			//三角形第一个点的XY坐标
			float x1,
			float y1,
			//三角形第二个点的XY坐标
			float x2,
			float y2,
			//三角形第三个点的XY坐标
			float x3,
			float y3,
			//被判断点的XY坐标
			float dx,
			float dy
	)
	{
		//被判断点到三角形第一个点的矢量
		float vector1x=dx-x1;
		float vector1y=dy-y1;
		
		//被判断点到三角形第二个点的矢量
		float vector2x=dx-x2;
		float vector2y=dy-y2;
		
		//被判断点到三角形第三个点的矢量
		float vector3x=dx-x3;
		float vector3y=dy-y3;
		
		//计算第1、2矢量个叉积
		float crossProduct1=vector1x*vector2y-vector1y*vector2x;
		
		//计算第2、3矢量个叉积
		float crossProduct2=vector2x*vector3y-vector2y*vector3x;
		
		//计算第3、1矢量个叉积
		float crossProduct3=vector3x*vector1y-vector3y*vector1x;
		
		if(crossProduct1<0&&crossProduct2<0&&crossProduct3<0)
		{//若三个叉积同号返回true
			return true;
		}
		
		if(crossProduct1>0&&crossProduct2>0&&crossProduct3>0)
		{//若三个叉积同号返回true
			return true;
		}
		
		return false;
	}
	
	//计算由三个点0、1、2确定的平面在指定XZ坐标处的高度
	//基本算法思想，首先求出0号点到1、2号点的矢量
	//然后这两个矢量求叉积得到三角形平面的法矢量{A,B,C}
	//接着通过法矢量和0号点坐标可以写出三角形平面的方程
	// A(x-x0)+B(y-y0)+c(z-z0)=0
	//然后可以推导出指定xz坐标处y的求值公式
	//y=(C(z0-z)+A(x0-x))/B+y0;
	//最后通过求值公式求出指定xz坐标处y的值
	public float fromXZToY
	(
		float tx0,float ty0,float tz0,//确定平面的点0
		float tx1,float ty1,float tz1,//确定平面的点1
		float tx2,float ty2,float tz2,//确定平面的点2
		float ctx,float ctz//船头的XZ坐标
	)
	{
		//求出0号点到1号点的矢量
        float x1=tx1-tx0;
        float y1=ty1-ty0;
        float z1=tz1-tz0;
        //求出0号点到2号点的矢量
        float x2=tx2-tx0;
        float y2=ty2-ty0;
        float z2=tz2-tz0;
        //求出两个矢量叉积矢量在XYZ轴的分量ABC
        float A=y1*z2-y2*z1;
        float B=z1*x2-z2*x1;
        float C=x1*y2-x2*y1;
        //通过求值公式求指定xz处的y值
		float yResult=(C*(tz0-ctz)+A*(tx0-ctx))/B+ty0;
		//返回结果
		return yResult;
	}
	
	//新添加的，主要是判断是否一圈结束====================================================
	final float RACE_HALF_X=14*UNIT_SIZE;
	final float RACE_HALF_Z=20*UNIT_SIZE;
	final float RACE_BEGIN_X=30;
	final float RACE_BEGIN_Z=90;
	public void isHalfForBoat(float carTempX,float carTempZ)
	{
		double dis=Math.sqrt
		(
			(carTempX-RACE_HALF_X)*	(carTempX-RACE_HALF_X)
			+(carTempZ-RACE_HALF_Z)*(carTempZ-RACE_HALF_Z)
		);
		if(dis<=120)
		{
			halfFlag=true;
		}
	}
	//是否跑完一圈的方法
	public void isOneCycleForBoat(float carTempX,float carTempZ)
	{
		double dis=Math.sqrt
		(
				(carTempX-RACE_BEGIN_X)*(carTempX-RACE_BEGIN_X)
				+(carTempZ-RACE_BEGIN_Z)*(carTempZ-RACE_BEGIN_Z)
		);
		
		if(dis<=60&&bOldZ>RACE_BEGIN_Z&&carTempZ<=RACE_BEGIN_Z)
		{
			if(halfFlag==true)
			{
				numberOfTurns=numberOfTurns+1;//圈数加1
				if(numberOfTurns==3&&isSpeedMode)	//若为竞速模式，在跑完圈数后，获得当前的名次，并且把速度和加速度设置为0
				{
					RANK_FOR_HERO_BOAT=RANK_FOR_HELP;
					CURR_BOAT_V=0;
					BOAT_A=0;
					String currSysTime=Constant.getCurrTime();
					String currUseTime=Constant.getUseTime();
					DBUtil.insertRcRDatabase(currSysTime, currUseTime, RANK_FOR_HERO_BOAT); 
					//弹出对话框
					ma.hd.sendEmptyMessage(12);
				}
				else if(numberOfTurns==3&&!isSpeedMode)		//若为计时模式
				{
					CURR_BOAT_V=0;
					BOAT_A=0;
					String currSysTime=Constant.getCurrTime();
					String currUseTime=Constant.getUseTime();
					List<String> alist=DBUtil.getTimeFromJSDatabase();
					isBreakRecord=isFast(currUseTime,alist);
					DBUtil.insertJSDatabase(currSysTime, currUseTime);
					//弹出是否破纪录的对话框
					ma.hd.sendEmptyMessage(11);
				}
			} 
			halfFlag=false;
		}
		else if(dis<=60&&bOldZ<=RACE_BEGIN_Z&&carTempZ>RACE_BEGIN_Z)
		{
			halfFlag=false;
		}
	}	
	//本次玩家用时是否为最短时间的判断方法
	public static boolean isFast(String currTime,List<String> aList)
	{//这里的true表是当前时间为最短时间，即破纪录，这里把当前时间先当做最短时间
		boolean result=false;
		//将当前的时间以“:”分开
		String[] str=currTime.split(":");
		int currT=Integer.parseInt(str[0])*60*100+Integer.parseInt(str[1])*100+Integer.parseInt(str[2]);
		List<Integer> tempInteger=new ArrayList<Integer>();
		for(int i=0;i<aList.size();i++)
		{
			String[] tempStr=aList.get(i).split(":");
			tempInteger.add(Integer.parseInt(tempStr[0])*60*100+Integer.parseInt(tempStr[1])*100+Integer.parseInt(tempStr[2]));
		}
		Collections.sort(tempInteger);
		if(tempInteger.size()==0)
		{
			result=true;
		}
		else if(currT<tempInteger.get(0))
		{
			result=true;
		}
		return result;
	}
}
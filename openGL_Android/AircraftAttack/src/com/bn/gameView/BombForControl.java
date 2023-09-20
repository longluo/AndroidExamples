package com.bn.gameView;
import static com.bn.gameView.Constant.ANGLE_X_Z;
import static com.bn.gameView.Constant.ARCHIBALD_X;
import static com.bn.gameView.Constant.ARCHIBALD_Y;
import static com.bn.gameView.Constant.ARCHIBALD_Z;
import static com.bn.gameView.Constant.ARCHIE_BOMB_VELOCITY;
import static com.bn.gameView.Constant.ARSENAL_X;
import static com.bn.gameView.Constant.ARSENAL_Y;
import static com.bn.gameView.Constant.ARSENAL_Z;
import static com.bn.gameView.Constant.ArchieArray;
import static com.bn.gameView.Constant.BOMB_MAX_DISTANCE;
import static com.bn.gameView.Constant.BOMB_VELOCITY;
import static com.bn.gameView.Constant.PLANE_X;
import static com.bn.gameView.Constant.PLANE_X_R;
import static com.bn.gameView.Constant.PLANE_Y;
import static com.bn.gameView.Constant.PLANE_Y_R;
import static com.bn.gameView.Constant.PLANE_Z;
import static com.bn.gameView.Constant.TANK_BOMB_VELOCITY;
import static com.bn.gameView.Constant.archie_List;
import static com.bn.gameView.Constant.archie_bomb_List;
import static com.bn.gameView.Constant.bomb_List;
import static com.bn.gameView.Constant.fire_index;
import static com.bn.gameView.Constant.gradeArray;
import static com.bn.gameView.Constant.isno_Hit;
import static com.bn.gameView.Constant.isno_Lock;
import static com.bn.gameView.Constant.mapId;
import static com.bn.gameView.Constant.nx;
import static com.bn.gameView.Constant.ny;
import static com.bn.gameView.Constant.nz;
import static com.bn.gameView.Constant.tank_bomb_List;
import static com.bn.gameView.GLGameView.arsenal;
import static com.bn.gameView.GLGameView.baoZhaList;
import static com.bn.gameView.GLGameView.bombRect;
import static com.bn.gameView.GLGameView.bombRectr;
import static com.bn.gameView.GLGameView.bomb_height;
import static com.bn.gameView.GLGameView.enemy;
import static com.bn.gameView.GLGameView.tankeList;

import java.util.Iterator;

import com.bn.archieModel.ArchieForControl;
import com.bn.arsenal.Arsenal_House;
import com.bn.commonObject.BallTextureByVertex;
import com.bn.commonObject.DrawBomb;
import com.bn.core.MatrixState;
import com.bn.planeModel.EnemyPlane;
import com.bn.tankemodel.TanKe;

//炮弹的控制类
public class BombForControl
{
	GLGameView gv;
	private BallTextureByVertex bomb_ball;//子弹类
	//定义发射炮弹时飞机的仰角和方位角
	private float curr_elevation;
	private float  curr_direction;
	//炮弹的位置
	private float curr_x;
	private float curr_y;
	private float curr_z;
	private float distance;//飞行距离
	//飞机发射炮弹锁定
	private boolean islocked;
	private float curr_nx;
	private float curr_ny;
	private float curr_nz;
	private float average;//平均向量
	//飞机发射炮弹的构造器
	public BombForControl(GLGameView gv,BallTextureByVertex bomb_ball,float plane_x,float plane_y,float plane_z,
			float plane_elevation,float plane_direction,float rotationAngle_Plane_X,float rotationAngle_Plane_Y,
			float rotationAngle_Plane_Z)
	{
		this.gv=gv;
		//创建纹理球
		this.bomb_ball=bomb_ball;
		this.curr_elevation=plane_elevation;
		this.curr_direction=plane_direction;
		this.islocked=isno_Lock;
		if(islocked)
		{
			curr_nx=nx;
			curr_ny=ny;
			curr_nz=nz;
			average=(float) Math.sqrt(curr_nx*curr_nx+curr_ny*curr_ny+curr_nz*curr_nz);
		}
		//初始化炮弹的发射位置
		initData(plane_x,plane_y,plane_z,rotationAngle_Plane_X,rotationAngle_Plane_Y,rotationAngle_Plane_Z);
	}
	//高射炮和坦克发射炮弹的构造器
	public BombForControl(GLGameView gv,BallTextureByVertex bomb_ball,float[]init_position,float init_elevation,float init_direction)
	{
		this.gv=gv;
		this.bomb_ball=bomb_ball;
		curr_x=init_position[0];//
		curr_y=init_position[1];//
		curr_z=init_position[2];//
		curr_elevation=init_elevation;
		curr_direction=init_direction;
	}
	//确定飞机机翼炮弹的发射位置
	public void initData(float plane_x,float plane_y,float plane_z,float rotationAngle_Plane_X,
			float rotationAngle_Plane_Y,float rotationAngle_Plane_Z)
	{
		//设定左机翼发射炮弹的位置
		curr_x=plane_x;
		curr_y=plane_y;
		curr_z=plane_z;
		//炮弹位置的相关参数
		float length;
		float ori_y;
		float ori_z;
		length=12;
		if(fire_index!=1)//左机翼发射炮弹
		{
			ori_y=90;
			ori_z=-2;
		}
		else//右机翼发射炮弹
		{
			ori_y=-90;
			ori_z=-2;
		}
		//确定炮弹的最终位置
		curr_y=curr_y-(float)Math.sin(Math.toRadians(rotationAngle_Plane_Z+ori_z))*length;
		curr_x=curr_x-(float)Math.cos(Math.toRadians(rotationAngle_Plane_Z+ori_z))*(float)Math.sin(Math.toRadians(rotationAngle_Plane_Y+ori_y))*length;
		curr_z=curr_z-(float)Math.cos(Math.toRadians(rotationAngle_Plane_Z+ori_z))*(float)Math.cos(Math.toRadians(rotationAngle_Plane_Y+ori_y))*length;
	}
	public void drawSelf(int texId)
	{
		MatrixState.pushMatrix();
		MatrixState.translate(curr_x, curr_y, curr_z);
		bomb_ball.drawSelf(texId);
		MatrixState.popMatrix();		
	}
	//飞机发射炮弹
	public void go()
	{
		distance+=BOMB_VELOCITY;//子弹的行驶路程增加
		if(distance>=BOMB_MAX_DISTANCE)//如果子弹步长超出了
		{	
			
			Iterator<BombForControl> ite=bomb_List.iterator();
			gv.activity.playSound(1,0);
			while(ite.hasNext())
			{
				if(ite.next()==this)
				{
					ite.remove();
					return;
				}
			}
			
		}	
		float tyy;
		if((tyy=KeyThread.isYachtHeadCollectionsWithLandPaodan(curr_x,curr_y,curr_z))>0){
			baoZhaList.add(new DrawBomb(bombRectr,curr_x,tyy,curr_z));//如果炮弹撞击地面
			Iterator<BombForControl> ite=bomb_List.iterator();
			while(ite.hasNext())
			{
				if(ite.next()==this)
				{
					ite.remove();
					return;
				}
			}
		}
		//判断是否和军火库发生碰撞
		for(Arsenal_House as:arsenal){
			if(//军火库还存在
					curr_y>as.ty&&curr_y<as.ty+ARSENAL_Y
					&&curr_x>as.tx-ARSENAL_X&&curr_x<as.tx+ARSENAL_X
					&&curr_z>as.tz-ARSENAL_Z&&curr_z<as.tz+ARSENAL_Z){
				
				as.blood-=ArchieArray[mapId][8][0];//军火库的血减1			
				
				if(as.blood<1){//如果军火库被炸毁
					gv.activity.playSound(0,0);
					try
					{
						Iterator<Arsenal_House> ite=arsenal.iterator();
						while(ite.hasNext())
						{
							if(ite.next()==as)
							{
								ite.remove();
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					baoZhaList.add(new DrawBomb(bombRect,as.tx,as.ty+bomb_height/2,as.tz));
					gradeArray[1]+=ArchieArray[mapId][12][3];//得分增加,
				}
				Iterator<BombForControl> ite=bomb_List.iterator();
				while(ite.hasNext())
				{
					if(ite.next()==this)
					{
						ite.remove();
						
						return;
					}
				}
				
			}
		}
		

		for(ArchieForControl afc:archie_List){//查看有没有击中高射炮
			if(curr_y>afc.position[1]&&curr_y<afc.position[1]+ARCHIBALD_Y*2
					&&curr_x>afc.position[0]-ARCHIBALD_X*2&&curr_x<afc.position[0]+ARCHIBALD_X*2
					&&curr_z>afc.position[2]-ARCHIBALD_Z*2&&curr_z<afc.position[2]+ARCHIBALD_Z*2){
				
				afc.blood-=ArchieArray[mapId][8][2];//高射炮的血减10
				if(afc.blood<0){
					gv.activity.playSound(0,1);
					try
					{
						Iterator<ArchieForControl> ite=archie_List.iterator();
						while(ite.hasNext())
						{
							if(ite.next()==afc)
							{
								ite.remove();
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					baoZhaList.add(new DrawBomb(bombRect,curr_x,afc.position[1]+ARCHIBALD_Y,curr_z));
					gradeArray[1]+=ArchieArray[mapId][12][1];//得分增加,
				}
				Iterator<BombForControl> ite=bomb_List.iterator();
				while(ite.hasNext())
				{
					if(ite.next()==this)
					{
						ite.remove();
						
						return;
					}
				}
				
			}
		}
		for(TanKe afc:tankeList)//查看有没有击中坦克
		{
			if(curr_y>afc.ty&&curr_y<afc.ty+ARCHIBALD_Y
					&&curr_x>afc.tx-ARCHIBALD_X&&curr_x<afc.tx+ARCHIBALD_X
					&&curr_z>afc.tz-ARCHIBALD_Z&&curr_z<afc.tz+ARCHIBALD_Z)
			{
				afc.blood-=ArchieArray[mapId][8][1];//坦克的血减1
				if(afc.blood<=0)
				{
					gv.activity.playSound(0,1);
					try
					{
						Iterator<TanKe> ite=tankeList.iterator();
						while(ite.hasNext())
						{
							if(ite.next()==afc)
							{
								ite.remove();
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					gradeArray[1]+=ArchieArray[mapId][12][0];//得分增加,
				}
				baoZhaList.add(new DrawBomb(bombRect,curr_x,curr_y+bomb_height/5,curr_z));
				Iterator<BombForControl> ite=bomb_List.iterator();
				while(ite.hasNext())
				{
					if(ite.next()==this)
					{
						ite.remove();
						
						return;
					}
				}
				
			}
		}
		//有没有击中敌机
		for(EnemyPlane afc:enemy){//查看有没有击中
			if(curr_y>afc.ty-PLANE_Y_R&&curr_y<afc.ty+PLANE_Y_R
					&&curr_x>afc.tx-PLANE_X_R&&curr_x<afc.tx+PLANE_X_R
					&&curr_z>afc.tz-ANGLE_X_Z&&curr_z<afc.tz+ANGLE_X_Z){
				
				afc.blood-=ArchieArray[mapId][8][3];//敌机的血减1
					gv.activity.playSound(8,0);
				if(afc.blood<=0){
					gv.activity.playSound(0,1);
					try
					{
						Iterator<EnemyPlane> ite=enemy.iterator();
						while(ite.hasNext())
						{
							if(ite.next()==afc)
							{
								ite.remove();
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					gradeArray[1]+=ArchieArray[mapId][12][2];//得分增加,
				}
				baoZhaList.add(new DrawBomb(bombRect,curr_x,curr_y+bomb_height/5,curr_z));
				Iterator<BombForControl> ite=bomb_List.iterator();
				while(ite.hasNext())
				{
					if(ite.next()==this)
					{
						ite.remove();
						
						return;
					}
				}
				
			}
		}
		//这里判断是否锁定目标
		if(islocked)//如果锁定目标
		{
			 curr_x+=curr_nx/average*BOMB_VELOCITY;
			 curr_z+=curr_nz/average*BOMB_VELOCITY;
			 curr_y+=curr_ny/average*BOMB_VELOCITY;
		}
		else
		{
		//计算炮弹下一步的位置
		//计算当前仰角和方向角
			curr_x=curr_x-(float)(Math.cos(Math.toRadians(curr_elevation))*Math.sin(Math.toRadians(curr_direction))*BOMB_VELOCITY);
			curr_z=curr_z-(float)(Math.cos(Math.toRadians(curr_elevation))*Math.cos(Math.toRadians(curr_direction))*BOMB_VELOCITY);
			curr_y=curr_y+(float)(Math.sin(Math.toRadians(curr_elevation))*BOMB_VELOCITY);//飞机的位置
		}
	}
	//高射炮发射炮弹
	public void go_archie()
	{
		float curr_planeX=PLANE_X;
		float curr_planeY=PLANE_Y;
		float curr_planeZ=PLANE_Z;
		distance+=ARCHIE_BOMB_VELOCITY;//子弹的行驶路程增加
		if(distance>=BOMB_MAX_DISTANCE)//如果子弹步长超出了
		{	
			try
			{
				Iterator<BombForControl> ite=archie_bomb_List.iterator();
				while(ite.hasNext())
				{
					if(ite.next()==this)
					{
						ite.remove();
						return;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		//这里对炮弹是否击中飞机进行判断
		float curr_distance=(curr_planeX-curr_x)*(curr_planeX-curr_x)+
		(curr_planeY-curr_y)*(curr_planeY-curr_y)+
		(curr_planeZ-curr_z)*(curr_planeZ-curr_z);
		if(curr_distance<500)//炮弹与飞机相撞
		{
			gv.activity.playSound(1,0);
			isno_Hit=true;//飞机被击中了一下
			gv.plane.blood-=ArchieArray[mapId][9][1];//飞机血减少一滴
			gv.activity.shake();//手机震动一次
			try
			{
				Iterator<BombForControl> ite=archie_bomb_List.iterator();
				while(ite.hasNext())
				{
					if(ite.next()==this)
					{
						ite.remove();
						return;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			} 
			return ;
		}
		//计算炮弹下一步的位置
		//计算当前仰角和方向角
		curr_x=curr_x-(float)(Math.cos(Math.toRadians(curr_elevation))*Math.sin(Math.toRadians(curr_direction))*ARCHIE_BOMB_VELOCITY);
		curr_z=curr_z-(float)(Math.cos(Math.toRadians(curr_elevation))*Math.cos(Math.toRadians(curr_direction))*ARCHIE_BOMB_VELOCITY);
		curr_y=curr_y+(float)(Math.sin(Math.toRadians(curr_elevation))*ARCHIE_BOMB_VELOCITY);//飞机的位置
	}
	
	//坦克发射炮弹
	public void go_tank()
	{
		
		float curr_planeX=PLANE_X;
		float curr_planeY=PLANE_Y;
		float curr_planeZ=PLANE_Z;
		distance+=TANK_BOMB_VELOCITY;//子弹的行驶路程增加
		if(distance>=BOMB_MAX_DISTANCE)//如果子弹步长超出了
		{	
			try
			{
				Iterator<BombForControl> ite=tank_bomb_List.iterator();
				while(ite.hasNext())
				{
					if(ite.next()==this)
					{
						ite.remove();
						return;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		//这里对炮弹是否击中飞机进行判断
		float curr_distance=(curr_planeX-curr_x)*(curr_planeX-curr_x)+
		(curr_planeY-curr_y)*(curr_planeY-curr_y)+
		(curr_planeZ-curr_z)*(curr_planeZ-curr_z);
		if(curr_distance<500)//炮弹与飞机相撞
		{
			gv.activity.playSound(1,0);
			isno_Hit=true;//飞机杯击中一下了
			gv.plane.blood-=ArchieArray[mapId][9][0];//飞机血减少一滴
			gv.activity.shake();//手机震动一次
			try
			{
				Iterator<BombForControl> ite=tank_bomb_List.iterator();
				while(ite.hasNext())
				{
					if(ite.next()==this)
					{
						ite.remove();
						return;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return ;
		}
		//计算炮弹下一步的位置
		//计算当前仰角和方向角
		curr_x=curr_x-(float)(Math.cos(Math.toRadians(curr_elevation))*Math.sin(Math.toRadians(curr_direction))*TANK_BOMB_VELOCITY);
		curr_z=curr_z-(float)(Math.cos(Math.toRadians(curr_elevation))*Math.cos(Math.toRadians(curr_direction))*TANK_BOMB_VELOCITY);
		curr_y=curr_y+(float)(Math.sin(Math.toRadians(curr_elevation))*TANK_BOMB_VELOCITY);
	}
}

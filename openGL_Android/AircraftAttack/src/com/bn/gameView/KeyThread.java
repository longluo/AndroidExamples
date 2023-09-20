package com.bn.gameView;
import static com.bn.gameView.Constant.ABOUT_HEIGHT;
import static com.bn.gameView.Constant.ANGLE_X_Z;
import static com.bn.gameView.Constant.ARSENAL_X;
import static com.bn.gameView.Constant.ARSENAL_Y;
import static com.bn.gameView.Constant.ARSENAL_Z;
import static com.bn.gameView.Constant.ArchieArray;
import static com.bn.gameView.Constant.BUTTON_RADAR_BG_WIDTH;
import static com.bn.gameView.Constant.BaoZha_scal;
import static com.bn.gameView.Constant.Crash_DISTANCE_start;
import static com.bn.gameView.Constant.Crash_DISTANCE_stop;
import static com.bn.gameView.Constant.DIRECTION_CAMERA;
import static com.bn.gameView.Constant.DIRECTION_CAMERA_SPAN;
import static com.bn.gameView.Constant.DISTANCE;
import static com.bn.gameView.Constant.ELEVATION_CAMERA;
import static com.bn.gameView.Constant.ELEVATION_CAMERA_DOWN;
import static com.bn.gameView.Constant.ELEVATION_CAMERA_ORI;
import static com.bn.gameView.Constant.ELEVATION_CAMERA_SPAN;
import static com.bn.gameView.Constant.ELEVATION_CAMERA_UP;
import static com.bn.gameView.Constant.HELP_HEIGHT;
import static com.bn.gameView.Constant.LANDS_HEIGHT_ARRAY;
import static com.bn.gameView.Constant.LAND_HIGHEST;
import static com.bn.gameView.Constant.LAND_UNIT_SIZE;
import static com.bn.gameView.Constant.Lock_Distance;
import static com.bn.gameView.Constant.MENU_BUTTON_WIDTH;
import static com.bn.gameView.Constant.MapArray;
import static com.bn.gameView.Constant.PLANE_DOWN_ROTATION_DOMAIN_X;
import static com.bn.gameView.Constant.PLANE_HEIGHT_MAX;
import static com.bn.gameView.Constant.PLANE_MOVE_SPAN;
import static com.bn.gameView.Constant.PLANE_ROTATION_SPEED_SPAN_X;
import static com.bn.gameView.Constant.PLANE_ROTATION_SPEED_SPAN_Z;
import static com.bn.gameView.Constant.PLANE_UP_ROTATION_DOMAIN_X;
import static com.bn.gameView.Constant.PLANE_X;
import static com.bn.gameView.Constant.PLANE_X_R;
import static com.bn.gameView.Constant.PLANE_Y;
import static com.bn.gameView.Constant.PLANE_Y_R;
import static com.bn.gameView.Constant.PLANE_Z;
import static com.bn.gameView.Constant.RADAR_DIRECTION;
import static com.bn.gameView.Constant.TRANSFER_Y;
import static com.bn.gameView.Constant.WATER_HEIGHT;
import static com.bn.gameView.Constant.WEAPON_INDEX;
import static com.bn.gameView.Constant.WIDTH_LALNDFORM;
import static com.bn.gameView.Constant.archie_List;
import static com.bn.gameView.Constant.archie_bomb_List;
import static com.bn.gameView.Constant.bomb_List;
import static com.bn.gameView.Constant.bomb_number;
import static com.bn.gameView.Constant.bullet_List;
import static com.bn.gameView.Constant.bullet_number;
import static com.bn.gameView.Constant.directionX;
import static com.bn.gameView.Constant.directionY;
import static com.bn.gameView.Constant.directionZ;
import static com.bn.gameView.Constant.fire_index;
import static com.bn.gameView.Constant.gradeArray;
import static com.bn.gameView.Constant.house_height;
import static com.bn.gameView.Constant.house_length;
import static com.bn.gameView.Constant.house_width;
import static com.bn.gameView.Constant.isFireOn;
import static com.bn.gameView.Constant.isCrashCartoonOver;
import static com.bn.gameView.Constant.isCrash;
import static com.bn.gameView.Constant.isOvercome;
import static com.bn.gameView.Constant.isVideo;
import static com.bn.gameView.Constant.isno_Hit;
import static com.bn.gameView.Constant.isno_Lock;
import static com.bn.gameView.Constant.isno_Vibrate;
import static com.bn.gameView.Constant.is_button_return;
import static com.bn.gameView.Constant.isno_draw_arsenal;
import static com.bn.gameView.Constant.isno_draw_plane;
import static com.bn.gameView.Constant.keyState;
import static com.bn.gameView.Constant.lock;
import static com.bn.gameView.Constant.mapId;
import static com.bn.gameView.Constant.minimumdistance;
import static com.bn.gameView.Constant.nx;
import static com.bn.gameView.Constant.ny;
import static com.bn.gameView.Constant.nz;
import static com.bn.gameView.Constant.planezAngle;
import static com.bn.gameView.Constant.rotationAngle_Plane_X;
import static com.bn.gameView.Constant.rotationAngle_Plane_Y;
import static com.bn.gameView.Constant.rotationAngle_Plane_Z;
import static com.bn.gameView.Constant.rotationAngle_SkyBall;
import static com.bn.gameView.Constant.scalMark;
import static com.bn.gameView.Constant.tank_bomb_List;
import static com.bn.gameView.GLGameView.arsenal;
import static com.bn.gameView.GLGameView.baoZhaList;
import static com.bn.gameView.GLGameView.bombRect;
import static com.bn.gameView.GLGameView.bombRectr;
import static com.bn.gameView.GLGameView.cop_archie_List;
import static com.bn.gameView.GLGameView.cop_archie_bomb_List;
import static com.bn.gameView.GLGameView.cop_bomb_List;
import static com.bn.gameView.GLGameView.cop_bullet_List;
import static com.bn.gameView.GLGameView.cx;
import static com.bn.gameView.GLGameView.cy;
import static com.bn.gameView.GLGameView.cz;
import static com.bn.gameView.GLGameView.enemy;
import static com.bn.gameView.GLGameView.isVideoPlaying;
import static com.bn.gameView.GLGameView.tankeList;
import static com.bn.gameView.GLGameView.treeList;
import static com.bn.gameView.GLGameView.tx;
import static com.bn.gameView.GLGameView.ty;
import static com.bn.gameView.GLGameView.tz;

import java.util.Collections;
import java.util.Date;


import com.bn.arsenal.Arsenal_House;
import com.bn.commonObject.DrawBomb;
import com.bn.commonObject.Tree;
import com.bn.core.SQLiteUtil;

public class KeyThread extends Thread 
{
	public boolean flag_go;//线程标志位
	private boolean isPlaneNoUp;//飞机不再上升标志位
	GLGameView gv;//主场景类的引用
	private float oldTimeBullet=0;//用于记录上次发射子弹的时间
	private float oldTimeBomb=0;//用于记录上次发射子弹的时间
	int time;
	public float planeY;//飞机坠毁处的地面高度
	int playId=1;//视频播放
	public boolean isno_adjust;//是否调整飞机方向
	int playIdArray;//数组下标
	public KeyThread(GLGameView gv)
	{
		this.gv=gv;
		flag_go=true;
		tx=PLANE_X=MapArray[mapId].length*WIDTH_LALNDFORM/2;
		ty=PLANE_Y;
		tz=PLANE_Z=MapArray[mapId].length*WIDTH_LALNDFORM/2;
		isVideo=true;
	}
	@Override
	public void run()
	{
		while(flag_go)
		{
			if(gv.isGameOn)//如果游戏开始
			{
				synchronized(lock)
				{
					if(!isVideo&&is_button_return)//按下暂停按钮，或者返回按钮
					{
						continue;
					}
					time+=50;			
					if(isVideo)//如果是视频播放时
					{
						if(!isVideoPlaying)
						{
							continue;
						}
						PLANE_MOVE_SPAN=40;//飞机速度
						float nx,nz;
						nx=ArchieArray[mapId][6][(playIdArray)*2]*WIDTH_LALNDFORM-PLANE_X;
						nz=ArchieArray[mapId][6][(playIdArray)*2+1]*WIDTH_LALNDFORM-PLANE_Z;
						  
						float distance=(float) Math.sqrt(nx*nx+nz*nz);
					    if(nz<0)
						{
					    	rotationAngle_Plane_Y=DIRECTION_CAMERA=(float)Math.toDegrees(Math.atan(nx/nz));	
						}
					    else if(nz==0)
					    {
							rotationAngle_Plane_Y=DIRECTION_CAMERA=nx>0?90:-90;
						}
						else 
						{
							rotationAngle_Plane_Y=DIRECTION_CAMERA=180+(float)Math.toDegrees(Math.atan(nx/nz));	
						}
					    rotationAngle_Plane_X=0;
					    ELEVATION_CAMERA=0;
					    rotationAngle_Plane_Z=0;
					    PLANE_Y=330;
						if(distance<40)
						{
							playIdArray++;
							playIdArray%=ArchieArray[mapId][6].length/2;
						}
					}
					//=====特别行动时记录时间
					if(gv.isGameMode==1&&!is_button_return&&!isVideo&&System.nanoTime()-gv.oldTime>1000000000)
        			{
						gv.goTime--;
						gv.oldTime=System.nanoTime();
        			}
					//-----特别行动--------------------
					if(gv.isGameMode==1)
					{
						//时间到了
						if(gv.goTime<0)
						{
							//行动失败
							gv.isSpecActionState=2;
							if(gv.activity.bgMusic[1].isPlaying())
							{
								gv.activity.bgMusic[1].pause();
							}
							isOvercome=true;
						}
						//时间没有到----飞机炸毁了---------------------------
						else if(gv.plane.blood<=0)
						{
							//行动失败
							gv.isSpecActionState=2;
							if(gv.activity.bgMusic[1].isPlaying())
							{
								gv.activity.bgMusic[1].pause();
							}
							isCrash=true;//飞机坠毁
						}
						//时间还没有到,成功晋级
						else
						{
							switch(mapId)
							{
							case 3://霹雳行动
								if(enemy.size()==0)//战机
								{
									if(gv.activity.bgMusic[1].isPlaying())
									{
										gv.activity.bgMusic[1].pause();
									}
									isOvercome=true;
									//行动成功
									gv.isSpecActionState=1;
								}
								break;
							case 4://沙漠风暴
								if(tankeList.size()==0&&archie_List.size()==0)//坦克和高射炮
								{
									if(gv.activity.bgMusic[1].isPlaying())
									{
										gv.activity.bgMusic[1].pause();
									}
									isOvercome=true;
									//行动成功
									gv.isSpecActionState=1;
								}
								break;
							case 5://斩首行动
								if(arsenal.size()==0)//军火库完
								{
									if(gv.activity.bgMusic[1].isPlaying())
									{
										gv.activity.bgMusic[1].pause();
									}
									isOvercome=true;
									//行动成功
									gv.isSpecActionState=1;
								}
								break;
							}
						}
					}
					//------战役模式下飞机爆炸--------
					if(gv.isGameMode==0&&gv.plane.blood<=0)//如果飞机的血没了，则飞机坠毁
					{
						if(gv.activity.bgMusic[1].isPlaying())
						{
							gv.activity.bgMusic[1].pause();
						}
						isCrash=true;
						isFireOn=false;
						gradeArray[0]=mapId;
						gradeArray[2]=time/1000;//耗时分钟
						Date d=new Date();
						String month=d.getMonth()+1>=10?d.getMonth()+1+"":"0"+d.getMonth()+1;
						String day=d.getDate()>=10?d.getDate()+"":"0"+d.getDate();
						String date=d.getYear()+1900+""+month+""+day;
						String sql="insert into plane values("+"'"+gradeArray[0]+""+"'"+",'"+gradeArray[1]+""+"'," +
								"'"+gradeArray[2]+""+"','"+date+"');";
				        SQLiteUtil.insert(sql);
					}
					//-----战役模式-------成功过关
					if(gv.isGameMode==0&&arsenal.size()==0&&tankeList.size()==0&&archie_List.size()==0&&enemy.size()==0)
					{
						if(gv.activity.bgMusic[1].isPlaying())
						{
							gv.activity.bgMusic[1].pause();
						}
						gradeArray[0]=mapId;
						gradeArray[2]=time/1000;//耗时分钟
						Date d=new Date();
						String month=d.getMonth()+1>=10?d.getMonth()+1+"":"0"+d.getMonth()+1;
						String day=d.getDate()>=10?d.getDate()+"":"0"+d.getDate();
						String date=d.getYear()+1900+""+month+""+day;
						String sql="insert into plane values("+"'"+gradeArray[0]+""+"'"+",'"+gradeArray[1]+""+"'," +
										"'"+gradeArray[2]+""+"','"+date+"');";
				        SQLiteUtil.insert(sql);
						isOvercome=true;
					}
					if(isCrash)//如果飞机坠毁
					{
						isFireOn=false;
						archie_List.clear();//高射炮清零
						bomb_List.clear();//炮弹清零
						archie_bomb_List.clear();//高射炮炮弹清零
						bullet_List.clear();//飞机发射的子弹清零
						flag_go=false;//关掉此线程
				    		new Thread()
				    		{
				    			int time=0;
				    			int thistime=0;
				    			boolean isnoStart;
				    			public void run()
				    			{
				    				isno_draw_arsenal=false;//不绘制军火库
				    				while(time<15000)
				    				{
				    					time+=50;
				    					planeY=	isYachtHeadCollectionsWithLandPaodan(PLANE_X,PLANE_Y-PLANE_Y_R,PLANE_Z);//查看是否碰撞地面
				    					if(planeY<0&&!isnoStart)//如果没有碰撞到地，飞机坠毁下降
				    					{
				    						PLANE_Y-=10;
				    						tx=PLANE_X;//摄像机目标位置跟着飞机走，摄像机位置不变
				    			    		ty=PLANE_Y;
				    			    		tz=PLANE_Z;
				    					}
				    					else 
				    					{//如果已经坠毁到地面，拉伸摄像机，
				    						if(!isnoStart)
				    						{
				    							thistime=time;//记录动画播放了的时间
				    							isnoStart=true;
					    						ty=PLANE_Y=isYachtHeadCollectionsWithLandPaodan(PLANE_X,PLANE_Y-PLANE_Y_R,PLANE_Z)+PLANE_Y_R;
				    						}
				    					}
				    					if(isnoStart&&time-thistime<=2000)
				    					{
				    						ELEVATION_CAMERA=30;
				    						cx=(float)(tx+Math.cos(Math.toRadians(ELEVATION_CAMERA))*Math.sin(Math.toRadians(DIRECTION_CAMERA))*Crash_DISTANCE_start);//摄像机x坐标 
				    					    cz=(float)(tz+Math.cos(Math.toRadians(ELEVATION_CAMERA))*Math.cos(Math.toRadians(DIRECTION_CAMERA))*Crash_DISTANCE_start);//摄像机z坐标 
				    					    cy=(float)(ty+Math.sin(Math.toRadians(ELEVATION_CAMERA))*Crash_DISTANCE_start);//摄像机y坐标
				    					    if(Crash_DISTANCE_start<Crash_DISTANCE_stop*4){
				    					    	 Crash_DISTANCE_start+=20f;
				    					    	 rotationAngle_Plane_Y=DIRECTION_CAMERA+=5;//摄像机围着飞机转动
				    					    }
				    					    else
				    					    {
				    					    	rotationAngle_Plane_Y=DIRECTION_CAMERA+=5;//摄像机围着飞机转动
				    					    }
				    					   if(time%800==0)
				    					   {
				    						   baoZhaList.add(new DrawBomb(bombRectr,PLANE_X,PLANE_Y,PLANE_Z));//添加爆炸效果
				    						   gv.activity.playSound(1,0);
				    					   }
				    					}
				    					else if(isnoStart&&time-thistime>2000&&time-thistime<=5500)//播放最后大爆炸
				    					{
				    						if(time-thistime<4000)
				    						{
				    							 if(time%150==0)
				    							 {
						    						   BaoZha_scal+=0.3f;
						    					 }
				    						}
				    						else if(time-thistime>=4000&&time-thistime<=4500)
				    						{
				    							isno_draw_plane=false;//不会做飞机
				    							isno_draw_arsenal=false;//不绘制军火库
				    							if(time%100==0){
					    							 BaoZha_scal-=3.5f;
					    							 if(BaoZha_scal<0){
					    								 baoZhaList.add(new DrawBomb(bombRect,PLANE_X,PLANE_Y,PLANE_Z));//添加爆炸效果
					    								 BaoZha_scal=0;
					    							 }
						    					   }
				    						}else if(time-thistime>=4500)
				    						{
				    							 isCrashCartoonOver=true;//动画播放完毕
				    							 isno_draw_plane=false;//不绘制飞机
					    						 gv.activity.playSound(0,1);
					    						 break;
				    						}
				    					}
				    					try 
				    					{
											Thread.sleep(50);
										}
				    					catch (InterruptedException e) 
				    					{
											e.printStackTrace();
										}
				    				}
				    				isCrashCartoonOver=true;//动画播放完毕
				    			}
				    		}.start();
						break;
					}
					if(isOvercome)//如果完成游戏
					{
						isFireOn=false;
						flag_go=false;
						archie_List.clear();//高射炮清零
						bomb_List.clear();//炮弹清零
						archie_bomb_List.clear();//高射炮炮弹清零
						bullet_List.clear();//飞机发射的子弹清零
						isCrashCartoonOver=true;//动画播放完毕
						break;
					}
					//如果有up键按下的话
					if((!isPlaneNoUp)&&(keyState&0x1)!=0)
					{
						//飞机向上仰
						if(rotationAngle_Plane_X<PLANE_UP_ROTATION_DOMAIN_X)
						{
							rotationAngle_Plane_X+=PLANE_ROTATION_SPEED_SPAN_X;
						}
						//摄像机向上仰
						if(ELEVATION_CAMERA>ELEVATION_CAMERA_DOWN)
						{
							ELEVATION_CAMERA-=ELEVATION_CAMERA_SPAN;
						}
					}
					//如果有down键按下的话
					else if((keyState&0x2)!=0)
					{
						//飞机向下俯
						if(rotationAngle_Plane_X>PLANE_DOWN_ROTATION_DOMAIN_X)
						{
							rotationAngle_Plane_X-=PLANE_ROTATION_SPEED_SPAN_X;
						}
						//摄像机向下俯
						if(ELEVATION_CAMERA<ELEVATION_CAMERA_UP)
						{
							ELEVATION_CAMERA+=ELEVATION_CAMERA_SPAN;
						}
					}
					//如果up和down都没有按下的话,则飞机平行飞行   摄像机持平
					else if((isPlaneNoUp||((keyState&0x1)==0)&&((keyState&0x2)==0)))
					{
						if(isno_Lock)
						{
							if(!isno_adjust){//
								isno_adjust=true;
								rotationAngle_Plane_Y=(float) Math.toDegrees(Math.atan(nx/nz));
						if(nx==0&&nz==0)
						{
							rotationAngle_Plane_Y=0;
							
						}		
						if(nz>0)
						{
							rotationAngle_Plane_Y+=180;
						}
								rotationAngle_Plane_X=(float) Math.toDegrees(Math.atan(ny/Math.sqrt(nx*nx+nz*nz)));
								DIRECTION_CAMERA=rotationAngle_Plane_Y;//摄像机方向和飞机飞行方向一致
							}
						}
						else
						{
							isno_adjust=false;
							//飞机校正
							if(Math.abs(rotationAngle_Plane_X)<PLANE_ROTATION_SPEED_SPAN_X+0.1f)//如果飞机的俯仰角度小于一个阈值,则置为0
							{
								rotationAngle_Plane_X=0;//飞机前后持平
							}
							else if(rotationAngle_Plane_X>0)//如果飞机处于仰的位置,则角度减少
							{
								rotationAngle_Plane_X-=PLANE_ROTATION_SPEED_SPAN_X;
							}
							else//如果飞机处于俯的位置,则角度增加
							{
								rotationAngle_Plane_X+=PLANE_ROTATION_SPEED_SPAN_X;
							}
							//摄像机校正
							if(Math.abs(ELEVATION_CAMERA-ELEVATION_CAMERA_ORI)<ELEVATION_CAMERA_SPAN+0.1f)//如果飞机的俯仰角度小于一个阈值,则置为0
							{
								ELEVATION_CAMERA=ELEVATION_CAMERA_ORI;//摄像机仰角矫正好
							}
							else if(ELEVATION_CAMERA-ELEVATION_CAMERA_ORI>0)//如果摄像机处于仰的位置,则角度减少
							{
								ELEVATION_CAMERA-=ELEVATION_CAMERA_SPAN;
							}
							else//如果摄像机处于俯的位置,则角度增加
							{
								ELEVATION_CAMERA+=ELEVATION_CAMERA_SPAN;
							}
						}
					}
					if(!isVideo)
					{
						//如果有left键按下的话
						if((keyState&0x4)!=0)
						{
							//旋转速度的比例
							float temp_ratio=Math.abs(gv.activity.directionDotXY[0]-gv.activity.lr_domain)/30;
							//摄像机左转
							DIRECTION_CAMERA+=DIRECTION_CAMERA_SPAN*temp_ratio;
							//飞机向左旋转
							rotationAngle_Plane_Y+=DIRECTION_CAMERA_SPAN*temp_ratio;
							//飞机视觉上向左旋转倾斜
							rotationAngle_Plane_Z=gv.activity.directionDotXY[0]*0.9f;
						}
						//如果有right键按下的话
						else if((keyState&0x8)!=0)
						{
							float temp_ratio=Math.abs(gv.activity.directionDotXY[0]-gv.activity.lr_domain)/30;
							//摄像机右转
							DIRECTION_CAMERA-=DIRECTION_CAMERA_SPAN*temp_ratio;
							//飞机向右旋转
							rotationAngle_Plane_Y-=DIRECTION_CAMERA_SPAN*temp_ratio;
							//飞机视觉上向右旋转倾斜
							rotationAngle_Plane_Z=gv.activity.directionDotXY[0]*0.9f;
						}
						//既没有向左又没有向右,飞机姿势摆正
						else if((keyState&0x3)==0)
						{
							//飞机左右倾斜校正
							if(Math.abs(rotationAngle_Plane_Z)<PLANE_ROTATION_SPEED_SPAN_Z+0.5f)
							{
								rotationAngle_Plane_Z=0;
							}
							else if(rotationAngle_Plane_Z<0)
							{
								rotationAngle_Plane_Z+=PLANE_ROTATION_SPEED_SPAN_Z;
							}
							else
							{
								rotationAngle_Plane_Z-=PLANE_ROTATION_SPEED_SPAN_Z;
							}
							//飞机视觉上direction与摄像机direction校正
							if(Math.abs(rotationAngle_Plane_Y-DIRECTION_CAMERA)<2.1f)
							{
								rotationAngle_Plane_Y=DIRECTION_CAMERA;
							}
							else if(rotationAngle_Plane_Y-DIRECTION_CAMERA<0)
							{
								rotationAngle_Plane_Y+=2;
							}
							else
							{
								rotationAngle_Plane_Y-=2;
							}
						}	
					}
					planezAngle+=10;//飞机螺旋机转动角度
					planezAngle%=360;
					//飞机向前行驶,确定飞机的位置
		    		PLANE_X-=Math.sin(Math.toRadians(rotationAngle_Plane_Y))*Math.cos(Math.toRadians(rotationAngle_Plane_X))*PLANE_MOVE_SPAN;
		    		PLANE_Z-=Math.cos(Math.toRadians(rotationAngle_Plane_Y))*Math.cos(Math.toRadians(rotationAngle_Plane_X))*PLANE_MOVE_SPAN;
		    		float PLANE_Yt=(float) (PLANE_Y+Math.sin(Math.toRadians(rotationAngle_Plane_X))*PLANE_MOVE_SPAN);
		    		//判断飞机不能出去
		    		int gellSize=2;
		    		if(PLANE_X<(-gellSize+0.5f)*WIDTH_LALNDFORM)
		    		{
		    			PLANE_X=(-gellSize+0.5f)*WIDTH_LALNDFORM;
		    		}
		    		else if(PLANE_X>(MapArray[mapId].length+gellSize-0.5f)*WIDTH_LALNDFORM)
		    		{
		    			PLANE_X=(MapArray[mapId].length+gellSize-0.5f)*WIDTH_LALNDFORM;
		    		}
		    		if(PLANE_Z<(-gellSize+0.5f)*WIDTH_LALNDFORM)
		    		{
		    			PLANE_Z=(-gellSize+0.5f)*WIDTH_LALNDFORM;
		    		}
		    		else if(PLANE_Z>(MapArray[mapId].length+gellSize-0.5f)*WATER_HEIGHT)
		    		{
		    			PLANE_Z=(MapArray[mapId].length+gellSize-0.5f)*WATER_HEIGHT;
		    		}
		    		try
		    		{
		    			//碰撞检测
			    		if(!isnoHitHill(PLANE_X,PLANE_Yt,PLANE_Z,rotationAngle_Plane_Y,rotationAngle_Plane_X))
			    		{
			    			PLANE_Y=PLANE_Yt;
			    		}
			    		else
			    		{
			    			if(!isVideo)
			    			{
			    				gv.plane.blood-=ArchieArray[mapId][9][4];
			    				gv.activity.playSound(0,0);//播放飞机爆炸的声音
			    				gv.activity.shake();//手机震动一次
			    			}
			    		}
		    		}catch(Exception e)
		    		{
		    			e.printStackTrace();
		    		}
		    		//这里对飞机的高度进行检测,如果到达一定高度后,则飞机不再上升
		    		if(PLANE_Y>=PLANE_HEIGHT_MAX)
		    		{
		    			isPlaneNoUp=false;
		    			PLANE_Y=PLANE_HEIGHT_MAX;
		    		}
		    		else
		    		{
		    			isPlaneNoUp=false;
		    		}
		    		//天空穹旋转角度
		    		rotationAngle_SkyBall+=0.1f;
		    		//这里计算雷达指针的旋转角度
		    		RADAR_DIRECTION=rotationAngle_Plane_Y;
					//重新计算摄像机的位置
		    		tx=PLANE_X;
		    		ty=PLANE_Y;
		    		tz=PLANE_Z;
		    		cx=(float)(tx+Math.cos(Math.toRadians(ELEVATION_CAMERA))*Math.sin(Math.toRadians(DIRECTION_CAMERA))*DISTANCE);//摄像机x坐标 
				    cz=(float)(tz+Math.cos(Math.toRadians(ELEVATION_CAMERA))*Math.cos(Math.toRadians(DIRECTION_CAMERA))*DISTANCE);//摄像机z坐标 
				    cy=(float)(ty+Math.sin(Math.toRadians(ELEVATION_CAMERA))*DISTANCE);//摄像机y坐标
		    		//计算标记飞机位置的坐标
		    		gv.plane.arsenal_x=-scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-tx)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
		    		gv.plane.arsenal_y=scalMark*BUTTON_RADAR_BG_WIDTH*(MapArray[mapId].length*WIDTH_LALNDFORM/2-tz)/(MapArray[mapId].length*WIDTH_LALNDFORM/2);
		    		if(gv.plane.arsenal_x*gv.plane.arsenal_x+gv.plane.arsenal_y*gv.plane.arsenal_y>BUTTON_RADAR_BG_WIDTH*BUTTON_RADAR_BG_WIDTH*0.4f*0.4f){
		    			gv.plane.arsenal_x=(float) (gv.plane.arsenal_x*0.4f*
		    			(BUTTON_RADAR_BG_WIDTH/Math.sqrt(gv.plane.arsenal_x*gv.plane.arsenal_x+gv.plane.arsenal_y*gv.plane.arsenal_y)));
		    			gv.plane.arsenal_y=(float) (gv.plane.arsenal_y*0.4f*
		    			(BUTTON_RADAR_BG_WIDTH/Math.sqrt(gv.plane.arsenal_x*gv.plane.arsenal_x+gv.plane.arsenal_y*gv.plane.arsenal_y)));
		    		}
		    		
				    try
				    {
				    	Collections.sort(treeList);//对树排序
				    }
				    catch(Exception e)
				    {
				    	e.printStackTrace();
				    }
				    // 计算树的朝向
				    for(Tree tree:treeList)
				    {
				    	try
				    	{
				    		tree.calculateBillboardDirection();
				    	}
				    	catch(Exception e)
				    	{
				    		e.printStackTrace();
				    	}
			    	}
				    //这里进行发射子弹
				    if(WEAPON_INDEX==0&&isFireOn&&(System.nanoTime()-oldTimeBullet>150000000))//这里设置连续发射子弹
				    {
		        		//向列表中添加子弹对象 
		        		try
		        		{
		        			if(bullet_number>0)
		        			{
		        				bullet_List.add(new BulletForControl(gv,gv.bullet_rect, PLANE_X, PLANE_Y, PLANE_Z, 
			        					rotationAngle_Plane_X, rotationAngle_Plane_Y,rotationAngle_Plane_X,
			        					rotationAngle_Plane_Y,rotationAngle_Plane_Z,0,0));
			        			bullet_List.add(new BulletForControl(gv,gv.bullet_rect, PLANE_X, PLANE_Y, PLANE_Z, 
			        					rotationAngle_Plane_X, rotationAngle_Plane_Y,rotationAngle_Plane_X,
			        					rotationAngle_Plane_Y,rotationAngle_Plane_Z,1,0));
			        			gv.activity.playSound(3,0);//播放子弹的声音
			        			bullet_number-=2;
			        			Collections.sort(bullet_List);
		        			}
		        		}
		        		catch(Exception ee)
		        		{
		        			ee.printStackTrace();
		        		}
		        		oldTimeBullet=System.nanoTime();
				    }
				    //这里飞机进行发射炮弹
				    if(WEAPON_INDEX==1&&isFireOn&&(System.nanoTime()-oldTimeBomb>1000000000))//这里发射炮弹
	        		{
	        			if(bomb_number>0)
	        			{
			        		//向列表中添加子弹对象
			        		try
			        		{
			        			gv.activity.shake();
			        			bomb_List.add(new BombForControl(gv,gv.bullet_ball, PLANE_X, PLANE_Y, PLANE_Z, 
			        					rotationAngle_Plane_X, rotationAngle_Plane_Y,rotationAngle_Plane_X,rotationAngle_Plane_Y,
			        					rotationAngle_Plane_Z));
			        		}
			        		catch(Exception ee)
			        		{
			        			ee.printStackTrace();
			        		}
			        		//设定发射炮弹的机翼位置
			        		fire_index=(fire_index+1)%2;
			        		gv.activity.playSound(4,0);//播放声音
			        		bomb_number--;
			        		isno_Vibrate=true;
			        		oldTimeBomb=System.nanoTime();
	        			}
	        		}
				    //对高射炮进行行模拟
				    if(!isVideo){//如果是视频播放界面则不对其进行模拟
				    	 //对子弹飞行进行模拟
					    for(int i=0;i<cop_bullet_List.size();i++)
					    {
					    	try
					    	{
					    		cop_bullet_List.get(i).go();
					    	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    }
				    	//对炮弹的飞行进行模拟
					    for(int i=0;i<cop_bomb_List.size();i++)
					    {
					    	try
					    	{
					    		cop_bomb_List.get(i).go();
					    	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    }
					    minimumdistance=Lock_Distance;//距离跳到最大
				    	isno_Lock=false;//已经有东西被锁定标志置为false
				    	//飞机飞行的方向向量
				    	directionX=-(float) (Math.cos(Math.toRadians(rotationAngle_Plane_X))*Math.sin(Math.toRadians(rotationAngle_Plane_Y)));
				    	directionY=(float) (Math.sin(Math.toRadians(rotationAngle_Plane_X)));
				    	directionZ=-(float) (Math.cos(Math.toRadians(rotationAngle_Plane_X))*Math.cos(Math.toRadians(rotationAngle_Plane_Y)));
				    	
				    	for(Arsenal_House ah:arsenal)
				    	{
				    		//计算军火库
				    		ah.calculateBillboardDirection();//查看军火库是否被锁定
				    	}
				    	 //对坦克进行模拟
					    for(int i=0;i<tankeList.size();i++)
					    {
					    	try
					    	{
					    		tankeList.get(i).tank_go();
					    	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    }
					    //对高射炮进行模拟
				    	for(int i=0;i<cop_archie_List.size();i++)
					    {
					    	try
					    	{
					    		cop_archie_List.get(i).go();
					    	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    }
				    	
				    	//对敌机模拟
					    for(int i=0;i<enemy.size();i++)
					    {
					    	try
					    	{
					    		enemy.get(i).go();
					    	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    }
					    //对坦克炮弹进行模拟
					    for(int i=0;i<tank_bomb_List.size();i++)
					    {
					    	try
					    	{
					    		tank_bomb_List.get(i).go_tank();
					    	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    }
					    //对高射炮炮弹进行模拟
					    for(int i=0;i<cop_archie_bomb_List.size();i++)
					    {
					    	try
					    	{
					    		cop_archie_bomb_List.get(i).go_archie();
					    	}
					    	catch(Exception e)
					    	{
					    		e.printStackTrace();
					    	}
					    }
				    } 
				}
			}
			else//-----------------菜单界面-------------------------------
			{
				//这里进行惯性测试
				if(gv.hasInertia)//如果有惯性
				{
					gv.curr_angle_speed=gv.curr_angle_speed+gv.curr_acceleratedSpeed;//计算当前角速度
					if(Math.abs(gv.curr_angle_speed)>2f)
					{
						if(gv.missile_rotation+gv.curr_angle_speed>0)
						{
							gv.missile_rotation=0;
							gv.hasInertia=false;
							gv.curr_angle_speed=0;
						}
						else if(gv.missile_rotation+gv.curr_angle_speed<-225)
						{
							gv.missile_rotation=-225;
							gv.hasInertia=false;
							gv.curr_angle_speed=0;
						}
						else
						{
							gv.missile_rotation+=gv.curr_angle_speed;
						}
					}
					else
					{
						gv.curr_angle_speed=0;
						gv.hasInertia=false;
						gv.auto_adjust=true;//这里启动智能调整
					}
				}
				if(gv.auto_adjust)//这里需要启动智能调整
				{
					gv.curr_menu_index=(int) (Math.abs(gv.missile_rotation-22.5f)/45%8);
					if(Math.abs(gv.missile_rotation+gv.curr_menu_index*45)<3.1f)
					{
						gv.activity.playSound(9,0);//播放菜单旋转的声音
						gv.missile_rotation=-gv.curr_menu_index*45;
						gv.auto_adjust=false;//不再调整
					}
					else if(gv.missile_rotation+gv.curr_menu_index*45>3.1f)
					{
						gv.missile_rotation-=3f;
					}
					else if(gv.missile_rotation+gv.curr_menu_index*45<3.1f)
					{
						gv.missile_rotation+=3f;
					}
				}
				//这里进行导弹下落的模拟
				if(gv.isMissileDowning)
				{
					gv.missile_ZOffset_Speed+=gv.missile_ZOffset_AcceSpeed;
					gv.missile_ZOffset=gv.missile_ZOffset+gv.missile_ZOffset_Speed;
					if(gv.missile_ZOffset<-100)
					{
						gv.activity.playSound(2,0);
						gv.isDrawBaozha=true;//绘制爆炸图
						gv.isMissileDowning=false;
						gv.missile_rotation=0;
					}
				}
				//这里对导弹菜单按钮右移进行模拟
				if(gv.menu_button_move)
				{
					gv.menu_button_XOffset+=gv.menu_button_speed;
					if(gv.menu_button_XOffset>MENU_BUTTON_WIDTH/2+gv.ratio)
					{
						gv.menu_button_move=false;
						gv.isMissileDowning=true;
						gv.activity.playSound(4,0);//播放导弹下落的声音
					}
				}
				//这里对导弹爆炸进行模拟
				if(gv.isDrawBaozha)
				{
					gv.baozha_ratio+=gv.baozha_increase;
					if(gv.baozha_ratio>1.0f)
					{
						gv.isMenuLevel=2;//进入二级菜单
						gv.isDrawBaozha=false;
						gv.menu_button_XOffset=0;//导弹菜单按钮的位置复位
						gv.baozha_ratio=0;
						gv.missile_ZOffset_Speed=0;
						gv.missile_ZOffset=gv.missile_ZOffset_Ori;
					}
				}
				//这里对关舱进行模拟
				if(gv.doorState==0)
				{
					gv.door_YOffset-=gv.door_YSpan;//机舱门运动
					if(gv.door_YOffset<0.5f)//机舱门关上了
					{
						gv.door_YOffset=0.5f;
						gv.door_YSpan=-gv.door_YSpan;
						gv.doorState=2;//机舱门为关闭
						
					}
					else if(gv.door_YOffset>1.5f)//机舱门打开了
					{
						gv.door_YOffset=1.5f;
						gv.door_YSpan=-gv.door_YSpan;
						gv.doorState=1;//机舱门为打开
					}
				}
				//这里将导弹菜单旋转到Exit处
				if(gv.moveToExit)
				{
					gv.missile_rotation-=40;
					if(gv.missile_rotation<-225)
					{
						gv.activity.shake();
						gv.missile_rotation=-225;
						gv.moveToExit=false;
						gv.curr_menu_index=5;//当前为Exit菜单处
					}
				}
				//这里对帮助界面进行模拟
				if(gv.doorState==2&&gv.curr_menu_index==3)
				{
					gv.help_YOffset+=0.004f;
					if(gv.help_YOffset-HELP_HEIGHT/2>1)
					{
						gv.help_YOffset=-1-HELP_HEIGHT/2;
					}
					if(gv.help_YOffset+HELP_HEIGHT/2<-1)
					{
						gv.help_YOffset=1+HELP_HEIGHT/2;
					}
				}
				//这里对关于界面进行模拟
				if(gv.doorState==2&&gv.curr_menu_index==4)
				{
					gv.about_YOffset+=0.004f;
					if(gv.about_YOffset-ABOUT_HEIGHT/2>1)
					{
						gv.about_YOffset=-1-ABOUT_HEIGHT/2;
					}
					if(gv.about_YOffset+ABOUT_HEIGHT/2<-1)
					{
						gv.about_YOffset=1+ABOUT_HEIGHT/2;
					}
				}
				//当处于选择飞机场景中时
				if(2==gv.isMenuLevel)
				{
					gv.planeRotate=(gv.planeRotate+1)%360;
				}
				
			}
			try
			{
				Thread.sleep(50);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
		}
	}
	public boolean isnoHitHill(float fjX,float fjY,float fjZ,float yAngle,float xAngle)
	{
		float fjLeftX,fjRightX;//,fjBackLeftX,fjBackRightX;//飞机包围盒四个顶点的坐标
		float fjLeftY,fjRightY;//,fjBackLeftY,fjBackRightY;
		float fjLeftZ,fjRightZ;//,fjBackLeftZ,fjBackRightZ;
		fjLeftX=fjX-(float)(Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle+ANGLE_X_Z))
				*PLANE_X_R/(Math.sin(Math.toRadians(ANGLE_X_Z))));;
		fjLeftY=fjY-TRANSFER_Y-PLANE_Y_R+(float)(Math.sin(Math.toRadians(xAngle))
				*PLANE_X_R/(Math.sin(Math.toRadians(ANGLE_X_Z))));
		fjLeftZ=fjZ-(float)(Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle+ANGLE_X_Z))
				*PLANE_X_R/(Math.sin(Math.toRadians(ANGLE_X_Z))));
		if(isYachtHeadCollectionsWithLand(fjLeftX,fjLeftY,fjLeftZ)){
			return true;		
		}
		
		fjRightX=fjX-(float)(Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle-ANGLE_X_Z))
				*PLANE_X_R/(Math.sin(Math.toRadians(ANGLE_X_Z))));;
		fjRightY=fjY-TRANSFER_Y-PLANE_Y_R+(float)(Math.sin(Math.toRadians(xAngle))
				*PLANE_X_R/(Math.sin(Math.toRadians(ANGLE_X_Z))));
		fjRightZ=fjZ-(float)(Math.cos(Math.toRadians(xAngle))*Math.cos(Math.toRadians(yAngle-ANGLE_X_Z))
				*PLANE_X_R/(Math.sin(Math.toRadians(ANGLE_X_Z))));
		
		if(isYachtHeadCollectionsWithLand(fjRightX,fjRightY,fjRightZ)){
			return true;		
		}
		return false;
	}
	//判断某一点是否在山地下面
	public  boolean isYachtHeadCollectionsWithLand(float ctx,float ctyh,float ctz)
	{
		//判断其是否撞军火库了
		for(Arsenal_House as:arsenal)
		{
			if(ctyh>as.ty&&ctyh<as.ty+ARSENAL_Y//与军火库相撞了
					&&ctx>as.tx-ARSENAL_X&&ctx<as.tx+ARSENAL_X
					&&ctz>as.tz-ARSENAL_Z&&ctz<as.tz+ARSENAL_Z){
				gv.plane.blood-=ArchieArray[mapId][9][3];
				PLANE_Y=as.ty+ARSENAL_Y;
				isno_Hit=true;
				return true;
			}	
		}
		//判断是否与平民房相碰
		for(int i=0;i<ArchieArray[mapId][4].length/2;i++)
		{
			float positionX=ArchieArray[mapId][4][2*i]*WIDTH_LALNDFORM;//,LAND_HIGHEST+house_height/2, ArchieArray[mapId][4][2*i+1]*WIDTH_LALNDFORM
			float positionZ=ArchieArray[mapId][4][2*i+1]*WIDTH_LALNDFORM;
			if
			(
					ctx>(positionX-house_length/2)&&ctx<(positionX+house_length/2)&&
					ctz>(positionZ-house_width/2)&&ctz<(positionZ+house_width/2)&&
					ctyh>LAND_HIGHEST&&ctyh<(LAND_HIGHEST+house_height)
			)
			{
				gv.plane.blood-=ArchieArray[mapId][9][4];
				PLANE_Y+=house_height;
				isno_Hit=true;
				return true;
			}
		}
		
		float UNIT_SIZE=LAND_UNIT_SIZE;//陆地每一个格子的大小
		int COLS=LANDS_HEIGHT_ARRAY[0].length;//WATER_COLS;//行数和列数
		int ROWS=LANDS_HEIGHT_ARRAY[0].length;//WATER_ROWS;
		
		float cellCount=0;//需要移动的格子数
		
		float smallBlockLength=WIDTH_LALNDFORM;//UNIT_SIZE*COLS;//每一个中格子的大小
		ctx=ctx+cellCount*smallBlockLength;
		ctz=ctz+cellCount*smallBlockLength;//将地图移动到都大于零的区域， 此处只有四个中格子组成的地图
		int col=(int)(ctx/smallBlockLength);//飞机所在的行列
		int row=(int)(ctz/smallBlockLength);
		if(col<0||row<0||col>MapArray[mapId].length-1||row>MapArray[mapId].length-1)
		{
			if(ctyh<0){
				PLANE_Y+=0-ctyh;
				return true;//如果是地图版块以为，如果小于水面则为碰撞到地了
			}else{
				return false;
			}
		}
		ctx=ctx-col*smallBlockLength;
		ctz=ctz-row*smallBlockLength;//将该点移动到相对于原点的坐标处
		int mapArrayId=MapArray[mapId][row][col];//此处还得从写
		float moderXZ=0;//中间变量
		switch (mapArrayId) {
		case 4://旋转九十度
			moderXZ=ctz;
			ctz=ctx;
			ctx=smallBlockLength-moderXZ;
			mapArrayId=0;
			break;
		case 5://旋转180度
			ctx=smallBlockLength-ctx;
			ctz=smallBlockLength-ctz;
			mapArrayId=0;
			break;
		case 6://旋转270度
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=0;
			break;
			
		case 7://旋转九十度
			moderXZ=ctz;
			ctz=ctx;
			ctx=smallBlockLength-moderXZ;
			mapArrayId=1;
			break;
		case 8://旋转180度
			ctx=smallBlockLength-ctx;
			ctz=smallBlockLength-ctz;
			mapArrayId=1;
			break;
		case 9://旋转270度
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=1;
			break;
			
			
		case 10://旋转九十度
			moderXZ=ctz;
			ctz=ctx;
			ctx=smallBlockLength-moderXZ;
			mapArrayId=2;
			break;
		case 11://旋转180度
			ctx=smallBlockLength-ctx;
			ctz=smallBlockLength-ctz;
			mapArrayId=2;
			break;
		case 12://旋转270度
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=2;
			break;
		case 13:
		case 21:
			if(ctyh<LAND_HIGHEST)
			{
				PLANE_Y+=LAND_HIGHEST-ctyh;
				return true;
			}
			else
			{
				return false;
			}
		case 14:
			if(ctyh<0)
			{
				PLANE_Y+=0-ctyh;
				return true;
			}
			else
			{
				return false;
			}
		case 15:
			mapArrayId=4;
			break;
		case 16:
			mapArrayId=5;
			break;
		case 17:
			mapArrayId=6;
			break;
		case 18:
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=3;
			break;
		case 19:
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=6;
			break;
		case 20:
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=5;
			break;
		
		default:
			break;
		}
		try
		{
			int tempCol=(int)(ctx/UNIT_SIZE)%7;//得到所在小模块的行列
		    int tempRow=(int)(ctz/UNIT_SIZE)%7;
			float yArray[][]=Constant.LANDS_HEIGHT_ARRAY[mapArrayId];//根据模块Id得出该模块的Y轴数组
			float x0=tempCol*UNIT_SIZE;
		    float z0=tempRow*UNIT_SIZE; 
		    float y0=yArray[tempRow][tempCol];
	    
		    float x1=x0+UNIT_SIZE;
		    float z1=z0;
		    float y1=yArray[tempRow][(tempCol+1)%COLS];
		    
		    float x2=x0+UNIT_SIZE;
	        float z2=z0+UNIT_SIZE;
	        float y2=yArray[(tempRow+1)%ROWS][(tempCol+1)%COLS];
   
		    float x3=x0;
		    float z3=z0+UNIT_SIZE;
		    float y3=yArray[(tempRow+1)%ROWS][(tempCol)%COLS];    
		    //船头处的陆地高度
		    float cty=0;
		    if(isInTriangle(x0,z0,x1,z1,x3,z3,ctx,ctz))
		    {//判断该点是否位于0-1-3三角形
		    	//求0-1-3面在船头处的高度
		    	cty=fromXZToY
			    (
				    	x0,y0,z0,
				    	x1,y1,z1,
				    	x3,y3,z3,
				    	ctx,ctz
				 );
		    }
		    else if(isInTriangle(x2,z2,x3,z3,x1,z1,ctx,ctz))
		    {
		    	//求1-2-3面在改点处的高度
		    	cty=fromXZToY
			    (
				    	x1,y1,z1,
				    	x2,y2,z2,
				    	x3,y3,z3,
				    	ctx,ctz
				);
		    }	    
		    if(cty>ctyh)
		    {//若飞机处的陆地低于飞机高度则返回true
		    	PLANE_Y+=cty-ctyh;
		    	return true;
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	    return false;
	}
	
	//判断某一点是否在山地下面
	public static float isYachtHeadCollectionsWithLandPaodan(float ctx,float ctyh,float ctz)
	{
		float UNIT_SIZE=LAND_UNIT_SIZE;//陆地每一个格子的大小
		int COLS=LANDS_HEIGHT_ARRAY[0].length;//WATER_COLS;//行数和列数
		int ROWS=LANDS_HEIGHT_ARRAY[0].length;//WATER_ROWS;
		
		float cellCount=0;//需要移动的格子数
		
		float smallBlockLength=WIDTH_LALNDFORM;//UNIT_SIZE*COLS;//每一个中格子的大小
		ctx=ctx+cellCount*smallBlockLength;
		ctz=ctz+cellCount*smallBlockLength;//将地图移动到都大于零的区域， 此处只有四个中格子组成的地图
		int col=(int)(ctx/smallBlockLength);//飞机所在的行列
		int row=(int)(ctz/smallBlockLength);
		if(col<0||row<0||col>MapArray[mapId].length-1||row>MapArray[mapId].length-1)
		{
			if(ctyh<0)
			{
				return 0;//如果是地图版块以为，如果小于水面则为碰撞到地了
			}
			else
			{
				return -5;
			}
		}
		ctx=ctx-col*smallBlockLength;
		ctz=ctz-row*smallBlockLength;//将该点移动到相对于原点的坐标处
		int mapArrayId=MapArray[mapId][row][col];//此处还得从写
		float moderXZ=0;//中间变量
		switch (mapArrayId) 
		{
		case 4://旋转九十度
			moderXZ=ctz;
			ctz=ctx;
			ctx=smallBlockLength-moderXZ;
			mapArrayId=0;
			break;
		case 5://旋转180度
			ctx=smallBlockLength-ctx;
			ctz=smallBlockLength-ctz;
			mapArrayId=0;
			break;
		case 6://旋转270度
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=0;
			break;
			
		case 7://旋转九十度
			moderXZ=ctz;
			ctz=ctx;
			ctx=smallBlockLength-moderXZ;
			mapArrayId=1;
			break;
		case 8://旋转180度
			ctx=smallBlockLength-ctx;
			ctz=smallBlockLength-ctz;
			mapArrayId=1;
			break;
		case 9://旋转270度
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=1;
			break;
			
			
		case 10://旋转九十度
			moderXZ=ctz;
			ctz=ctx;
			ctx=smallBlockLength-moderXZ;
			mapArrayId=2;
			break;
		case 11://旋转180度
			ctx=smallBlockLength-ctx;
			ctz=smallBlockLength-ctz;
			mapArrayId=2;
			break;
		case 12://旋转270度
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=2;
			break;
		case 13:
			if(ctyh<LAND_HIGHEST){
				
				return LAND_HIGHEST;
			}else{
				return -5;
			}
		case 14:
			if(ctyh<0)
			{
				return 0;
			}
			else
			{
				return -5;
			}
		case 15:
			mapArrayId=4;
			break;
		case 16:
			mapArrayId=5;
			break;
		case 17:
			mapArrayId=6;
			break;
		case 18:
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=3;
			break;
		case 19:
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=6;
			break;
		case 20:
			moderXZ=ctz;			
			ctz=smallBlockLength-ctx;
			ctx=moderXZ;
			mapArrayId=5;
			break;
		default:
			return -5;
		}
		int tempCol=(int)(ctx/UNIT_SIZE)%7;//得到所在小模块的行列
	    int tempRow=(int)(ctz/UNIT_SIZE)%7;
	    
		float yArray[][]=Constant.LANDS_HEIGHT_ARRAY[mapArrayId];//根据模块Id得出该模块的Y轴数组
		
		float x0=tempCol*UNIT_SIZE;
	    float z0=tempRow*UNIT_SIZE; 
	    float y0=yArray[tempRow][tempCol];
	    
	    float x1=x0+UNIT_SIZE;
	    float z1=z0;
	    float y1=yArray[tempRow][(tempCol+1)%COLS];
	    
	    float x2=x0+UNIT_SIZE;
        float z2=z0+UNIT_SIZE;
        float y2=yArray[(tempRow+1)%ROWS][(tempCol+1)%COLS];
    
	    float x3=x0;
	    float z3=z0+UNIT_SIZE;
	    float y3=yArray[(tempRow+1)%ROWS][(tempCol)%COLS];    
	    //船头处的陆地高度
	    float cty=0;
	    
	    if(isInTriangle(x0,z0,x1,z1,x3,z3,ctx,ctz))
	    {//判断该点是否位于0-1-3三角形
	    	//求0-1-3面在船头处的高度
	    	cty=fromXZToY
		    (
			    	x0,y0,z0,
			    	x1,y1,z1,
			    	x3,y3,z3,
			    	ctx,ctz
			 );
	    }
	    else if(isInTriangle(x2,z2,x3,z3,x1,z1,ctx,ctz))
	    {
	    	//求1-2-3面在改点处的高度
	    	cty=fromXZToY
		    (
			    	x1,y1,z1,
			    	x2,y2,z2,
			    	x3,y3,z3,
			    	ctx,ctz
			);
	    }	    
	    if(cty>ctyh)
	    {//若飞机处的陆地低于飞机高度则返回true
	    	return cty;
	    }
	    
	   	    
	    return -5;
	}
	
	
	
	//判断一个点是否在三角形内的方法
	//基本算法思想是首先求要被判断的点到三角形三个顶点的矢量1、2、3
	//然后三个矢量求叉积，若三个叉积同号则点位于三角形内，否则位于三角形外
	public static boolean isInTriangle
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
	public static  float fromXZToY
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


}

package com.bn.clp;

import static com.bn.clp.MyGLSurfaceView.*;

//检查与可吃掉的物体是否发生碰撞
public class ThreadForEat extends Thread
{
	MyGLSurfaceView surface;
	public boolean flag=true;
	
	public ThreadForEat(MyGLSurfaceView surface)
	{ 
		this.surface=surface;
		this.setName("ThreadForEat");
	}
	 @Override
	public void run()
	{
		while(flag)  
		{
			for(SpeedForControl ksfcTemp:surface.speedWtList)
			{
				if(ksfcTemp.isDrawFlag)
				{
					ksfcTemp.checkColl(bxForSpecFrame, bzForSpecFrame, angleForSpecFrame);
				}
				else
				{
					ksfcTemp.checkEatYet();
				}
			}
			try
			{
				Thread.sleep(50);//50
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
package com.bn.clp;

import static com.bn.clp.Constant.CURR_BOAT_V;
import static com.bn.clp.Constant.head_Angle;
import static com.bn.clp.Constant.head_Angle_A;
import static com.bn.clp.Constant.head_Angle_Max;

//晶体旋转线程
public class RotateThread extends Thread
{	
	public RotateThread()
	{
		this.setName("RotateThread");
	}
	
	public void run()
	{
		while(Constant.threadFlag)
		{	
			SpeedForControl.angleY=(SpeedForControl.angleY+6)%360;
			
			if(CURR_BOAT_V==0)
			{
				if(head_Angle<=head_Angle_Max&&KeyThread.upFlag)
				{
					head_Angle=head_Angle+head_Angle_A;
					if(head_Angle==head_Angle_Max)
					{
						KeyThread.upFlag=false;
					}
				}
				else if(head_Angle>=-head_Angle_Max&&!KeyThread.upFlag)
				{
					head_Angle=head_Angle-head_Angle_A;
					if(head_Angle==-head_Angle_Max)
					{
						KeyThread.upFlag=true;
					}
				}
			}  
			else
			{
				if(head_Angle<4)
				{
					head_Angle=head_Angle+head_Angle_A;
				}
				else
				{
					head_Angle=head_Angle-head_Angle_A;
				}
			}
			
			try
			{
				Thread.sleep(60);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
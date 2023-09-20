package com.bn.clp;

public class PZZ 
{
	final float UNIT_SIZE=2.1f;//这里的2.1f与桥类中的相同，需要关联起来
	float x;
	float z;
	float xL=UNIT_SIZE;
	float zL=2*UNIT_SIZE;
	float row;
	float col;
	float whichBridge;//是哪种桥，0度和-90度的桥x和z分别增加的值是相反的。
	
	public PZZ(float x,float z,float row,float col,float whichBridge)
	{
		this.x=x;
		this.z=z;
		this.row=row;
		this.col=col;
		this.whichBridge=whichBridge;
	}
	
	public boolean isIn(float bx,float bz)
	{
		if(whichBridge==0)
		{
			if(bx>=x&&bx<=x+xL&&bz>=z&&bz<=z+zL)
			{
				return true;
			}
			else
			{
				return false; 
			}
		}
		else
		{
			if(bx>=x&&bx<=x+zL&&bz>=z&&bz<=z+xL)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
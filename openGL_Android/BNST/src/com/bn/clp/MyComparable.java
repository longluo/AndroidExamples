package com.bn.clp;

import static com.bn.clp.MyGLSurfaceView.*;
import java.util.Comparator;

public class MyComparable implements Comparator<float[]>
{
	public int compare(float another[], float another2[]) 
	{
		//重写的比较两个灌木离摄像机距离的方法
		float xs=another[0]-cxForSpecFrame;
		float zs=another[1]-czForSpecFrame;
		
		float xo=another2[0]-cxForSpecFrame;
		float zo=another2[1]-czForSpecFrame;
		
		float disA=(float)Math.sqrt(xs*xs+zs*zs);
		float disB=(float)Math.sqrt(xo*xo+zo*zo);
		
		return ((disA-disB)==0)?0:((disA-disB)>0)?-1:1;  
	}
}
package com.bn.tl;

import static com.bn.tl.Constant.*;
import android.content.res.Resources;

public class HuiZhiShuZi 
{
	WenLiJuXing[] shuzi=new WenLiJuXing[10];
	public HuiZhiShuZi(Resources r)
	{
		//生成0-9十个数字的纹理矩形
		for(int i=0;i<10;i++)
		{
			shuzi[i]=new WenLiJuXing
            (
            	SHUZI_KUANDU,
            	SHUZI_GAODU,r,
            	 new float[]
		             {
		           	  0.1f*i,0, 0.1f*i,1, 0.1f*(i+1),1,
		           	  0.1f*i,0, 0.1f*(i+1),1,  0.1f*(i+1),0
		             }
             ); 
		}
	}
	 //初始化shader
	 public void intShader(int mProgram)
	 {
    	for(WenLiJuXing fl:shuzi)
    	{
    		fl.initShader(mProgram);
    	}
    }
	public void drawSelf(int score,int texId)//传入数字和纹理坐标
	{		
		String scoreStr=score+"";
		
		for(int i=0;i<scoreStr.length();i++)
		{//将得分中的每个数字字符绘制
			char c=scoreStr.charAt(i);
			
			 MatrixState.pushMatrix();
	         MatrixState.translate(i*SHUZI_KUANDU, 0, 0);
	         MatrixState.rotate(90, 1, 0, 0);
	         shuzi[c-'0'].drawSelf(texId);		         
	         MatrixState.popMatrix();			
		}
	}
}
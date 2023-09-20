package com.bn.commonObject;
import com.bn.core.MatrixState;
//绘制0-9十个数字
public class NumberForDraw 
{
	//创建10个数字的纹理矩形
	public TextureRect[] number;
	String scoreStr;//数字字符串
	float width;
	float height;
	public NumberForDraw(int numberSize,float width,float height,int mProgram)
	{
		number=new TextureRect[numberSize];
		this.width=width;
		this.height=height;
		//生成十个数字的纹理矩形
		for(int i=0;i<numberSize;i++)
		{
			number[i]=new TextureRect
            (
            		width,
            		height,
        		new float[]
	            {
	           	  1f/numberSize*i,0,1f/numberSize*i,1, 1f/numberSize*(i+1),0,
	           	  1f/numberSize*(i+1),1,
	            },
	            mProgram
             ); 
		}
	}
	public void drawSelf(String score,int texId)//传入数字和纹理坐标
	{		
		scoreStr=score;
		MatrixState.pushMatrix();
		MatrixState.translate(-scoreStr.length()*width, 0, 0);
		for(int i=0;i<scoreStr.length();i++)//将得分中的每个数字字符绘制
		{
			char c=scoreStr.charAt(i);
	        MatrixState.translate(width, 0, 0);
	        number[c-'0'].drawSelf(texId);		         
		}
		MatrixState.popMatrix();	
	}
	public void drawSelfLeft(String score,int texId)//传入数字和纹理坐标 左对齐
	{		
		scoreStr=score;
		MatrixState.pushMatrix();
		for(int i=0;i<scoreStr.length();i++)//将得分中的每个数字字符绘制
		{
			char c=scoreStr.charAt(i);
	        MatrixState.translate(width, 0, 0);
	        number[c-'0'].drawSelf(texId);		         
		}
		MatrixState.popMatrix();	
	}
}
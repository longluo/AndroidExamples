package com.bn.clp;

import static com.bn.clp.Constant.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.bn.core.MatrixState;

//表示单个灌木的类
public class SingleShrub
{
	//六瓣的列表
	List<float []> all=new ArrayList<float []>();
	//用于绘制各个瓣的纹理矩形
	ShrubForDraw sfd;
	
	//纹理数组，每个树的纹理坐标可能不同
	float[] texList=
	{
		 0,0, 0,1, 0.5f,0,
 	     0.5f, 0,0,1, 0.5f,1
	};
	
	public SingleShrub(int programId)
	{		
		//初始化用于绘制单个灌木的对象
    	sfd=new ShrubForDraw
    	(
    		programId,
    		texList
    	);  
	}
		
	public void drawSelf(int texIds,int id,float x,float y,float z,int dyFlag)
	{		
    	//生成六瓣树的信息
		float[][] tempData=
		{
			{x,z,0},
			{x+GRASS_UNIT_SIZE/2, z+GRASS_UNIT_SIZE*0.866f, 60},
			{x+GRASS_UNIT_SIZE*1.5f, z+GRASS_UNIT_SIZE*0.866f, 120},
			{x+GRASS_UNIT_SIZE*2, z, 180},
			{x+GRASS_UNIT_SIZE*1.5f, z-GRASS_UNIT_SIZE*0.866f, 240},
			{x+GRASS_UNIT_SIZE/2, z-GRASS_UNIT_SIZE*0.866f, 300}
		};		
		all=Arrays.asList(tempData);
				
		//对六瓣进行排序
		Collections.sort(all, new MyComparable());
		
		if(dyFlag==0)//绘制实体 
		{
			//循环列表进行树木的绘制  
			for(float []tt:all)
			{
				MatrixState.pushMatrix();
				MatrixState.translate(tt[0], y, tt[1]);
				MatrixState.rotate(tt[2], 0, 1, 0);
				sfd.drawSelf(texIds);		
				MatrixState.popMatrix();   
			}
		}
		else if(dyFlag==1)//绘制倒影
		{
			//实际绘制时Y的零点============!!!!!!!!!!!????????????
			float yTranslate=y;
			//进行镜像绘制时的调整值
			float yjx=(0-yTranslate)*2;
			
			//循环列表进行树木的绘制
			for(float []tt:all)
			{
				MatrixState.pushMatrix();				
				MatrixState.translate(tt[0], y, tt[1]);
				MatrixState.rotate(tt[2], 0, 1, 0);
				MatrixState.translate(0, yjx, 0);
				MatrixState.scale(1, -1, 1);
				sfd.drawSelf(texIds);		
				MatrixState.popMatrix();   
			}			
		}
	}
}
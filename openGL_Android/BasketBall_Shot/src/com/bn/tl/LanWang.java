package com.bn.tl;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;

public class LanWang {
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用  
    int maTexCoorHandle; //顶点纹理坐标属性引用  
    int maNormalHandle; //顶点法向量属性引用      
    int muraodonHandle;//扰动值引用
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   
	public LanWang(float R,float r,float height,int hSection){
		ArrayList<Float> alVertix=new ArrayList<Float>();
		float h=height/hSection;//每一小段的高度
		int dians=18;//每次切分度数
		int arey=360/dians;
		for(int i=0;i<hSection;i++){
			for(int j=0;j<arey;j++){
				float hr=i*h;//上一个分段的高度
				float Rhr1=r+(hr/height)*(R-r);//上一个分段的半径
				float hr2=(i+1)*h;//下一个分段的高度
				float Rhr2=r+(hr2/height)*(R-r);//下一段的半径
				
				float x1=Rhr1*(float)(Math.cos(Math.toRadians(dians*j)));
				float y1=hr;
				float z1=Rhr1*(float)(Math.sin(Math.toRadians(dians*j)));
				
				float x2=Rhr1*(float)(Math.cos(Math.toRadians(dians*(1+j))));
				float y2=hr;
				float z2=Rhr1*(float)(Math.sin(Math.toRadians(dians*(1+j))));
				
				float x3=Rhr2*(float)(Math.cos(Math.toRadians(dians*j)));
				float y3=hr2;
				float z3=Rhr2*(float)(Math.sin(Math.toRadians(dians*j)));
				
				float x4=Rhr2*(float)(Math.cos(Math.toRadians(dians*(1+j))));
				float y4=hr2;
				float z4=Rhr2*(float)(Math.sin(Math.toRadians(dians*(1+j))));
				
				//构建第一三角形
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);        		
        		//构建第二三角形
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		
        		//反面的
        		//构建第一三角形
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
        		//构建第二三角形
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
			}
		}
		
		 	vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
	        //将alVertix中的坐标值转存到一个float数组中
	        float vertices[]=new float[vCount*3];
	    	for(int i=0;i<alVertix.size();i++)
	    	{
	    		vertices[i]=alVertix.get(i);
	    	}
	    	//创建顶点坐标数据缓冲
	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本系统操作顺序
	        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
	        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
	        mVertexBuffer.position(0);//设置缓冲区起始位置

	        //创建绘制顶点法向量缓冲
	        //顶点纹理坐标数据的初始化================begin============================
	        float texCoor[]=generateTexCoor
	    	(
	   			 (int)(360/dians), //纹理图切分的列数
	   			hSection  //纹理图切分的行数
	   	    );
	        //创建顶点纹理坐标数据缓冲
	        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
	        cbb.order(ByteOrder.nativeOrder());//设置字节顺序为本系统操作顺序
	        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
	        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
	        mTexCoorBuffer.position(0);//设置缓冲区起始位置
	}
	//初始化着色器的initShader方法
    public void initShader(int mProgram)
    {
    	this.mProgram=mProgram; 
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取扰动帧引用ID
        muraodonHandle=GLES20.glGetUniformLocation(mProgram, "uraodon");
    }
    public void drawSelf(int texId,int raodon)
	{
		 //制定使用某套shader程序
   	 	GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //传入要扰动的帧数
        GLES20.glUniform1i(muraodonHandle, raodon);
        //传入顶点位置数据
        GLES20.glVertexAttribPointer  
        (
        		maPositionHandle,   
        		3, 
        		GLES20.GL_FLOAT, 
        		false,
               3*4,   
               mVertexBuffer
        );       
        //传入顶点纹理坐标数据
        GLES20.glVertexAttribPointer  
        (
       		maTexCoorHandle, 
        		2, 
        		GLES20.GL_FLOAT, 
        		false,
               2*4,   
               mTexCoorBuffer
        );   
        //传入位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
	}
	 //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2*2]; 
    	float sizew=1.0f/bw;//列数
    	float sizeh=1.0f/bh;//行数
    	int c=0;
    	for(int i=0;i<bh;i++)
    	{
    		for(int j=0;j<bw;j++)
    		{
    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
    			float s=(bw-j)*sizew;
    			float t=(bh-i)*sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s;
    			result[c++]=t-sizeh;
    			
    			result[c++]=s-sizew;
    			result[c++]=t-sizeh;
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			result[c++]=s-sizew;
    			result[c++]=t-sizeh;
    			
    			result[c++]=s-sizew;
    			result[c++]=t;
    			
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			
    			
    			result[c++]=s-sizew;
    			result[c++]=t-sizeh;
    			
    			result[c++]=s;
    			result[c++]=t-sizeh;
    			
    			
    			
    			result[c++]=s;
    			result[c++]=t;
    			
    			
    			
    			result[c++]=s-sizew;
    			result[c++]=t;
    			
    			result[c++]=s-sizew;
    			result[c++]=t-sizeh;
    			    			
    		}
    	}
    	return result;
    }                                              

}

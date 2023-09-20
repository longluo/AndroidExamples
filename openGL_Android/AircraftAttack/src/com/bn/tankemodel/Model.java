package com.bn.tankemodel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.bn.core.MatrixState;

//加载后的物体——携带顶点信息，自动计算面平均法向量
public class Model 
{	
	int mProgram;//自定义渲染管线着色器程序id  
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本   
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲  
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;  
    public Model(Resources r,float[] vertices,float texCoors[],int mProgram)
    {    	
    	//初始化顶点数据的initVertexData方法
    	initVertexData(vertices,texCoors);   
    	initShader(mProgram);
    }
   //初始化顶点数据的initVertexData方法
    public void initVertexData(float[] vertices,float texCoors[])
    {
    	//顶点坐标数据的初始化================begin============================
    	vCount=vertices.length/3;   
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //顶点纹理坐标数据的初始化================begin============================  
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTexCoorBuffer = tbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(texCoors);//向缓冲区中放入顶点纹理坐标数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }
   //调用初始化着色器的initShader方法
    public void initShader(int mProgram)
    {
    	this.mProgram=mProgram;
    	//获得顶点坐标数据的引用
		maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
		//顶点纹理坐标的引用id
		maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
		muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
    }
	public void drawSelf(int texId) 
	{
			//使用某套指定的Shader程序
			GLES20.glUseProgram(mProgram);
			//将最终变换矩阵传入到Shader程序中
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			//传入坐标数据
			GLES20.glVertexAttribPointer
			(
				maPositionHandle, 
				3, 
				GLES20.GL_FLOAT, 
				false, 
				3*4, 
				mVertexBuffer
			);
			//传入纹理坐标数据
			GLES20.glVertexAttribPointer
			(
				maTexCoorHandle, 
				2, 
				GLES20.GL_FLOAT, 
				false, 
				2*4, 
				mTexCoorBuffer
			);
			//允许顶点位置数据数组
	        GLES20.glEnableVertexAttribArray(maPositionHandle);  
	        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
	        //绑定纹理
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
	        //绘制加载的物体
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);  
	}
}

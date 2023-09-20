package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import com.bn.core.MatrixState;
 
//可吃物体
public class Speed extends SpeedForEat
{
	Diamond dd;
	final float UNIT_SIZE=1.0f;
	final float width=UNIT_SIZE;
	final float height=UNIT_SIZE*2;
	public Speed(int programId)
	{
		dd=new Diamond(programId,width,height);
	} 
	//总的绘制方法drawSelf
	public void drawSelf(int texId)
	{
		dd.drawSelf(texId);
	}
	//内部类——菱形
	private class Diamond
	{
		//自定义Shader程序的引用
		int mProgram;
		//总变换矩阵的引用id
		int muMVPMatrixHandle;
		//顶点属性的引用id
		int maPositionHandle;
		//顶点纹理坐标的引用id
		int maTexCoorHandle;
		
		//顶点坐标数据缓冲
		FloatBuffer mVertexBuffer;
		//顶点纹理坐标数据缓冲
		FloatBuffer mTexCoorBuffer;
		int vCount=0;//顶点数量
		
		//R为圆柱底部的半径，r为圆柱上部的半径，angle_span表示的是切分的角度
		public Diamond(int programId,float width,float height)
		{
			initVertexData(width,height);
			initShader(programId);
		}
		//初始化坐标数据的initVertexData方法
		public void initVertexData(float width,float height)
		{
			float[] vertex=new float[]
            {
				0,height,0,   -width,0,-width,   -width,0,width,
				0,height,0,   -width,0,width,   width,0,width,
				0,height,0,   width,0,width,   width,0,-width,
				0,height,0,   width,0,-width,   -width,0,-width,
				
				0,-height,0,   -width,0,width,   -width,0,-width,
				0,-height,0,   width,0,width,   -width,0,width,
				0,-height,0,   width,0,-width,   width,0,width,
				0,-height,0,   -width,0, -width,  width,0,-width, 
            };
			vCount=24;//顶点数量
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=new float[]
            {
				0.5f,0,   0,1,   1,1,
				0.5f,0,   0,1,   1,1,
				0.5f,0,   0,1,   1,1,
				0.5f,0,   0,1,   1,1,
				
				0.5f,0,   0,1,   1,1,
				0.5f,0,   0,1,   1,1,
				0.5f,0,   0,1,   1,1,
				0.5f,0,   0,1,   1,1,
            };
			ByteBuffer tbb=ByteBuffer.allocateDirect(texcoor.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texcoor);
			mTexCoorBuffer.position(0);
		}
		//初始化着色器程序的initShader方法
		public void initShader(int programId)
		{
			mProgram=programId;
			//获得顶点坐标数据的引用
			maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
			//顶点纹理坐标的引用id
			maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
			muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		}
		//自定义的绘制方法
		public void drawSelf(int texId)
		{
			//使用某套指定的Shader程序
			GLES20.glUseProgram(mProgram);
			//将最终变换矩阵传入到Shader程序中
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
			//传入顶点坐标数据
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
	        
	        //绘制纹理矩形
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
		}
	}
}
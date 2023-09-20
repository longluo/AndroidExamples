package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import com.bn.core.MatrixState;

public class Process
{
	//自定义渲染管线的id
	int mProgram;
	//总变化矩阵引用的id
	int muMVPMatrixHandle;
	//顶点位置属性引用id
	int maPositionHandle;
	//顶点纹理坐标属性引用id
	int maTexCoorHandle;
	int maPrograss;//j进度条位置坐标
	
	//顶点数据缓冲和纹理坐标数据缓冲
	FloatBuffer mVertexBuffer;  
	FloatBuffer mTexCoorBuffer;
	//顶点数量
	int vCount=0;
	
	//循环标志位
	boolean flag=true;
	float currPrograss;//当前进度
	float width;
	float percent;
	//percent表示进度值
	public Process(int programId,float width,float height,float[] texCoor,float percent)
	{
		this.width=width;
		this.percent=percent;
		//初始化顶点数据  
		initVertexData(width,height,texCoor);
		initShader(programId);
		
		new Thread()
		{
			@Override
			public void run()
			{
				while(flag)
				{
					if(currPrograss>=Process.this.width)
					{
						flag=false;
						break;
					}
					currPrograss=percentToWidth(Process.this.percent,Process.this.width);
					try
					{
						Thread.sleep(100);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	//初始化顶点数据的方法
	public void initVertexData(float width,float height,float[] texCoor)
	{
		float[] vertex=new float[]
	    {
			-width,height,0,
			-width,-height,0,
			width,-height,0,

			-width,height,0,
			width,-height,0,
			width,height,0,
	    };
		vCount=vertex.length/3;
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer=vbb.asFloatBuffer();
		mVertexBuffer.put(vertex);
		mVertexBuffer.position(0);
		
		//纹理坐标数据缓冲
		ByteBuffer tbb=ByteBuffer.allocateDirect(texCoor.length*4);
		tbb.order(ByteOrder.nativeOrder());
		mTexCoorBuffer=tbb.asFloatBuffer();
		mTexCoorBuffer.put(texCoor); 
		mTexCoorBuffer.position(0);
	}
	
	//初始化着色器的方法
	public void initShader(int programId) 
	{
		//基于顶点着色器与片元着色器创建程序
        mProgram =programId;
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //进度的引用id
        maPrograss= GLES20.glGetUniformLocation(mProgram, "aPrograss");
	}
	
	//自定义的绘制方法drawSelf
	public void drawSelf(int texId)
	{
		//制定使用某套shader程序
   	 	GLES20.glUseProgram(mProgram); 
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //将当前的进度传入到Shader程序
        GLES20.glUniform1f(maPrograss, currPrograss);
        
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
	
	//将百分比对应成与width相关的数据，percent为百分比，width为相应的屏幕的半宽度
	public float percentToWidth(float percent,float width)
	{
		return percent*width*2-width;
	}
}
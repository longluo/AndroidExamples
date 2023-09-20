package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import com.bn.core.MatrixState;

public class WeiLang
{
	//自定义渲染管线着色器程序id
	int mProgram; 
	//总变化矩阵引用的id
	int muMVPMatrixHandle;
	//顶点位置属性引用id
	int maPositionHandle;
	//顶点纹理坐标属性引用id
	int maTexCoorHandle;
	
	int maSTOffset;	//水面纹理图的偏移量引用id
	int muTMD;//速度透明度参数
	
	float currStartST=0;	//水面纹理坐标的当前起始坐标0~1
	
	//顶点数据缓冲和纹理坐标数据缓冲
	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoorBuffer;
	//顶点数量
	int vCount=0;
	
	public WeiLang(float a,float b,float height,float[] texCoor,int programId)
	{
		initVertexData(a,b,height,texCoor);
		initShader(programId);
		//启动一个线程定时换帧
    	new Thread()
    	{   
    		public void run()   
    		{
    			while(Constant.threadFlag)
    			{
    				//所谓水面定时换帧只是修改每帧起始角度即可，
    				//水面顶点Y坐标的变化由顶点着色单元完成
    				currStartST=(currStartST+0.1f)%1;
        			try 
        			{
    					Thread.sleep(100);  
    				} catch (InterruptedException e) 
    				{
    					e.printStackTrace();
    				}
    			}     
    		}    
    	}.start();  
	}
	//初始化顶点数据的方法
	public void initVertexData(float a,float b,float height,float[] texCoor)
	{
		float[] vertex=new float[]
	    {
			-a,height/3,0,   
			-b,-height*2,0,
			b,-height*2,0,

			-a,height/3,0,
			b,-height*2,0,
			a,height/3,0,
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
        mProgram = programId;
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取水面纹理图偏移量的引用id
        maSTOffset=GLES20.glGetUniformLocation(mProgram, "stK");  
        //获取尾浪速度透明度的引用id
        muTMD=GLES20.glGetUniformLocation(mProgram, "tmd");  
	}
	
	//自定义的绘制方法drawSelf
	public void drawSelf(int texId,float startST)
	{
		//制定使用某套shader程序
   	 	GLES20.glUseProgram(mProgram); 
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //将水面纹理图的st偏移量传入shader程序
        GLES20.glUniform1f(maSTOffset, startST);
        //将尾浪速度透明度传入shader程序
        GLES20.glUniform1f(muTMD, Constant.CURR_BOAT_V_TMD);
        
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
}
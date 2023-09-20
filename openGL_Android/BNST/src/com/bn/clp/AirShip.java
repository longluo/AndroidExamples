package com.bn.clp;

import static com.bn.clp.Constant.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import com.bn.core.MatrixState;
import android.opengl.GLES20;
   
//该类为飞艇类
public class AirShip extends BNDrawer
{     
	static float feitingX;//飞艇所在运行轨迹中心点位置
	static float feitingY; 
	static float feitingZ;
	
	final static float NORMAL_SIZE=5.0f;
	static float A=UNIT_SIZE-10;//飞艇运动椭球的a半径————x方向
	static float B=UNIT_SIZE-10;//飞艇运动椭球的b半径————z方向
	
	static float angle=360;//飞艇当前角度 
	
	static float angle_Rotate=0;//飞艇扰动角度
	
	static float height=1f;//飞艇上下扰动高差
	static float angle_Y=270;//正玄曲线当前帧角度 
	
	public static GoThread goThread;//运动线程
	DrawSpheroid bodyback;
	DrawSpheroid bodyhead;
	DrawSpheroid cabin;   
	DrawWeiba weiba;
	//飞艇身体椭圆的三个轴分量
	final static float BODYBACK_A=3f*NORMAL_SIZE;
	final static float BODYBACK_B=1f*NORMAL_SIZE;
	final static float BODYBACK_C=1f*NORMAL_SIZE;
	//飞艇身体小椭圆的三个轴分量
	final static float BODYHEAD_A=2f*NORMAL_SIZE;  
	final static float BODYHEAD_B=1f*NORMAL_SIZE;
	final static float BODYHEAD_C=1f*NORMAL_SIZE;
	
	final static float WEIBA_WIDTH=0.3f*NORMAL_SIZE;
	final static float WEIBA_HEIGHT=0.3f*NORMAL_SIZE;
	
	final static float CABIN_A=0.4f*NORMAL_SIZE;
	final static float CABIN_B=0.2f*NORMAL_SIZE;
	final static float CABIN_C=0.2f*NORMAL_SIZE;
	
	public AirShip(int programId)
	{
		bodyback=new DrawSpheroid(programId,BODYBACK_A,BODYBACK_B,BODYBACK_C,30,-90,90,-90,90);
		bodyhead=new DrawSpheroid(programId,BODYHEAD_A,BODYHEAD_B,BODYHEAD_C,30,-90,90,-90,90);
		cabin=new DrawSpheroid(programId,CABIN_A,CABIN_B,CABIN_C,30,0,360,-90,90);
		weiba=new DrawWeiba(programId,WEIBA_WIDTH,WEIBA_HEIGHT);
		goThread=new GoThread();
		goThread.start();
	}
	
	//绘制方法
	public void drawSelf(int[] texId, int dyFlag)
	{
		feitingX=(float) (A*Math.cos(Math.toRadians(angle)));
		feitingY=(float) (height*Math.sin(Math.toRadians(angle_Y)));
		feitingZ=(float) (B*Math.sin(Math.toRadians(angle)));
		
		MatrixState.pushMatrix();
        
		MatrixState.translate
        (
    		feitingX, 
    		feitingY, 
    		feitingZ
        ); 
		MatrixState.rotate(angle_Rotate-90, 0, 1, 0);
		
		//飞艇后方
		MatrixState.pushMatrix();
		bodyback.drawSelf(texId[0]);
		MatrixState.popMatrix();
		//飞艇前方
		MatrixState.pushMatrix();
		MatrixState.rotate(180, 0, 1, 0);
		MatrixState.rotate(180, 1, 0, 0);   
		bodyhead.drawSelf(texId[0]);
		MatrixState.popMatrix();
		//下侧的飞艇
		MatrixState.pushMatrix();
		
		MatrixState.translate(BODYHEAD_C*0.2f, -BODYHEAD_B, 0);
		cabin.drawSelf(texId[0]);
		MatrixState.popMatrix();
		
		//尾翼部分 
		//上侧
		MatrixState.pushMatrix();
		MatrixState.translate(BODYBACK_A*0.8f, BODYBACK_C*0.7f, 0);
		weiba.drawSelf(texId[1]);
		MatrixState.popMatrix();
		//前侧
		MatrixState.pushMatrix();
		MatrixState.translate(BODYBACK_A*0.8f, 0, BODYBACK_B*0.7f);
		MatrixState.rotate(90, 1, 0, 0);
		weiba.drawSelf(texId[1]);
		MatrixState.popMatrix();
		//后侧
		MatrixState.pushMatrix();
		MatrixState.translate(BODYBACK_A*0.8f, 0, -BODYBACK_B*0.7f);
		MatrixState.rotate(-90, 1, 0, 0);
		weiba.drawSelf(texId[1]);
		MatrixState.popMatrix();
		//下侧
		MatrixState.pushMatrix();
		MatrixState.translate(BODYBACK_A*0.8f, -BODYBACK_C*0.7f, 0);
		MatrixState.rotate(180, 1, 0, 0);
		weiba.drawSelf(texId[1]);
		MatrixState.popMatrix();
		
        MatrixState.popMatrix();
	} 
	
	
	
	//绘制飞艇身体的椭球
	private class DrawSpheroid
	{
		//自定义渲染管线的id
		int mProgram;
		//总变化矩阵引用的id
		int muMVPMatrixHandle;
		//顶点位置属性引用id
		int maPositionHandle;
		//顶点纹理坐标属性引用id
		int maTexCoorHandle;
		
		//顶点数据缓冲和纹理坐标数据缓冲
		FloatBuffer mVertexBuffer;
		FloatBuffer mTexCoorBuffer;
		//顶点数量
		int vCount=0;
		
		public DrawSpheroid
		(
			int programId,float a,float b,float c,float angleSpan,
			float hAngleBegin,float hAngleOver,float vAngleBegin,float vAngleOver
		)
		{
			initVertexData(a,b,c,angleSpan,hAngleBegin,hAngleOver,vAngleBegin,vAngleOver);
			initShader(programId);
		}
		//初始的顶点数据
		public void initVertexData
		(
			float a,float b,float c,
			float angleSpan,
			float hAngleBegin,float hAngleOver,
			float vAngleBegin,float vAngleOver
		)
		{
			ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标
			for(float vAngle=vAngleBegin;vAngle<vAngleOver;vAngle=vAngle+angleSpan)//垂直方向angleSpan度一份
	        {
	        	for(float hAngle=hAngleBegin;hAngle<hAngleOver;hAngle=hAngle+angleSpan)//水平方向angleSpan度一份
	        	{//纵向横向各到一个角度后计算对应的此点在球面上的坐标    		
	        		float x0=(float)(a*Math.cos(Math.toRadians(vAngle))*Math.cos(Math.toRadians(hAngle)));
	        		float y0=(float)(b*Math.cos(Math.toRadians(vAngle))*Math.sin(Math.toRadians(hAngle)));
	        		float z0=(float)(c*Math.sin(Math.toRadians(vAngle)));
	        		
	        		float x1=(float)(a*Math.cos(Math.toRadians(vAngle))*Math.cos(Math.toRadians(hAngle+angleSpan)));
	        		float y1=(float)(b*Math.cos(Math.toRadians(vAngle))*Math.sin(Math.toRadians(hAngle+angleSpan)));
	        		float z1=(float)(c*Math.sin(Math.toRadians(vAngle)));
	        		
	        		float x2=(float)(a*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.cos(Math.toRadians(hAngle+angleSpan)));
	        		float y2=(float)(b*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.sin(Math.toRadians(hAngle+angleSpan)));
	        		float z2=(float)(c*Math.sin(Math.toRadians(vAngle+angleSpan)));
	        		
	        		float x3=(float)(a*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.cos(Math.toRadians(hAngle)));
	        		float y3=(float)(b*Math.cos(Math.toRadians(vAngle+angleSpan))*Math.sin(Math.toRadians(hAngle)));
	        		float z3=(float)(c*Math.sin(Math.toRadians(vAngle+angleSpan)));
	        		
	        		//将计算出来的XYZ坐标加入存放顶点坐标的ArrayList        		
	        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);  
	        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
	        		alVertix.add(x0);alVertix.add(y0);alVertix.add(z0);
	        		      		
	        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
	        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
	        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);
	        		
	        	}
	        } 	
	        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
	    	
	        //将alVertix中的坐标值转存到一个int数组中
	        float[] vertices=new float[vCount*3];
	    	for(int i=0;i<alVertix.size();i++)
	    	{
	    		vertices[i]=alVertix.get(i);
	    	}
	        //创建顶点坐标数据缓冲
	        //vertices.length*4是因为一个整数四个字节
	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
	        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
	        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
	        mVertexBuffer.position(0);//设置缓冲区起始位置
	        
	    	//获取切分整图的纹理数组
	    	float[] texCoorArray= 
	         generateTexCoor
	    	 (
	    			 (int)((hAngleOver-hAngleBegin)/angleSpan), //纹理图切分的列数
	    			 (int)((vAngleOver-vAngleBegin)/angleSpan)  //纹理图切分的行数 
	    	);
			
			ByteBuffer tbb=ByteBuffer.allocateDirect(texCoorArray.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texCoorArray);
			mTexCoorBuffer.position(0);
		}
		//初始化着色器程序的initShader方法
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
		}
		//实际的绘制方法
		public void drawSelf(int texId)
		{
			//制定使用某套shader程序
	   	 	GLES20.glUseProgram(mProgram); 
	        //将最终变换矩阵传入shader程序
	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
			//为画笔指定顶点位置数据
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
		
		//自动切分纹理产生纹理数组的方法
	    public float[] generateTexCoor(int bw,int bh)
	    {
	    	float[] result=new float[bw*bh*6*2]; 
	    	float sizew=1.0f/bw;//列数
	    	float sizeh=1.0f/bh;//行数
	    	int c=0;
	    	for(int i=0;i<bh;i++)
	    	{
	    		for(int j=0;j<bw;j++)
	    		{
	    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
	    			float s=j*sizew;
	    			float t=i*sizeh;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t;
	    			
	    			result[c++]=s;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s;
	    			result[c++]=t;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s;
	    			result[c++]=t+sizeh;
	    		}
	    	}
	    	return result;
	    }  
	}
	//绘制飞艇的尾部
	private class DrawWeiba
	{
		//自定义渲染管线的id
		int mProgram;
		//总变化矩阵引用的id
		int muMVPMatrixHandle;
		//顶点位置属性引用id
		int maPositionHandle;
		//顶点纹理坐标属性引用id
		int maTexCoorHandle;
		
		//顶点数据缓冲和纹理坐标数据缓冲
		FloatBuffer mVertexBuffer;
		FloatBuffer mTexCoorBuffer;
		//顶点数量
		int vCount=0;
		
		public DrawWeiba(int programId,float width,float height)
		{
			initVertexData(width,height);
			initShader(programId);
		}
		//初始的顶点数据的initVertexData方法
		public void initVertexData(float width,float height)
		{
			float[] vertices=new float[]
	        {
				-width,height,0,
				-width*1.5f,-height,0,
				width,-height,0,
				
				-width,height,0,
				width,-height,0,
				width,height,0,
				
				-width,height,0,
				width,height,0,
				width,-height,0,
				
				-width,height,0,
				width,-height,0,
				-width*1.5f,-height,0,
	        };
			vCount=12;
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
	        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
	        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
	        mVertexBuffer.position(0);//设置缓冲区起始位置
	        
	    	//获取切分整图的纹理数组
	    	float[] texCoorArray=new float[]
            {
	    		0.2f,0,  0,1,  1,1,
	    		0.2f,0,  1,1,  1,0,
	    		
	    		0.2f,0,  1,0,  1,1,
	    		0.2f,0,  1,1,  0,1
            };
			
			ByteBuffer tbb=ByteBuffer.allocateDirect(texCoorArray.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texCoorArray);
			mTexCoorBuffer.position(0);
		}
		//初始化着色器程序的initShader方法
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
		}
		//实际的绘制方法
		public void drawSelf(int texId)
		{
			//制定使用某套shader程序
	   	 	GLES20.glUseProgram(mProgram); 
	        //将最终变换矩阵传入shader程序
	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
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
	//飞艇运动线程
	public class GoThread extends Thread 
	{	
		public GoThread()
		{
			this.setName("GoThread");
		}
		
		public void run()
		{
			while(Constant.threadFlag)
			{					 
				angle=angle-0.2f;
				angle_Y=angle_Y+30;
				angle_Rotate=angle_Rotate+0.2f;
				if(angle<=0)
				{
					angle=360;
				}
				if(angle_Y>=360)
				{
					angle_Y=0;
				}
				if(angle_Rotate>=360)
				{
					angle_Rotate=0;
				}
				try
				{
					Thread.sleep(200);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
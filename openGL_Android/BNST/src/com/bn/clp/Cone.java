package com.bn.clp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import android.opengl.GLES20;
import com.bn.core.MatrixState;
 
//交通柱
public class Cone extends KZBJDrawer
{
	//交通柱上方的圆柱
	Cone_In cone_in;
	//交通柱下侧的底
	Pedestal pedestal;
	//交通柱下侧的六角
	Cylinder cylinder;
	//切分的角度
	final float ANGLE_SPAN=20;
	final float UNIT_SIZE=1.0f;
	final float HEIGHT=0.2f;
	//下面的片切分的角度
	float SPAN=60;  
	public Cone(int programId,float R,float R2)
	{
		//圆锥的上半部分
		cone_in=new Cone_In(programId,R,ANGLE_SPAN,UNIT_SIZE);
		cylinder=new Cylinder(programId,R2,R2,60,HEIGHT); 
		pedestal=new Pedestal(programId,R2,SPAN); 
	}
	//总的绘制方法drawSelf
	public void drawSelf(int texId)
	{
		//绘制交通柱的圆柱部分
		cone_in.drawSelf(texId);
		//绘制交通柱下方底座的上面
		MatrixState.pushMatrix();
		MatrixState.translate(0, -UNIT_SIZE, 0);
		pedestal.drawSelf(texId);
		MatrixState.popMatrix();
		//绘制交通柱下方底座的侧面
		MatrixState.pushMatrix();
		MatrixState.translate(0, -UNIT_SIZE-HEIGHT, 0);
		cylinder.drawSelf(texId); 
		MatrixState.popMatrix();
		//绘制交通柱下方底座的下面 
		MatrixState.pushMatrix();
		MatrixState.translate(0, -UNIT_SIZE-HEIGHT*2, 0);
		MatrixState.rotate(180, 1, 0, 0);
		pedestal.drawSelf(texId); 
		MatrixState.popMatrix();
	}
	//内部类——圆锥
	private class Cone_In
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
		public Cone_In(int programId,float R,float angle_span,float height)
		{
			initVertexData(R,angle_span,height);
			initShader(programId);
		}
		//初始化坐标数据的方法
		public void initVertexData(float R,float angle_span,float height)
		{
			List<Float> tempList=new ArrayList<Float>();
			//将交通锥的上侧顶点添加到List集合中
			tempList.add(0f);
			tempList.add(height);
			tempList.add(0f);
			for(float vAngle=0;vAngle<=360;vAngle=vAngle+angle_span)
			{
				float x=(float) (R*Math.cos(Math.toRadians(vAngle)));
				float y=-height;
				float z=(float) (-R*Math.sin(Math.toRadians(vAngle)));
				
				tempList.add(x); tempList.add(y); tempList.add(z);
			}
			vCount=tempList.size()/3;//顶点数量
			float[] vertex=new float[tempList.size()];
			for(int i=0;i<tempList.size();i++)
			{
				vertex[i]=tempList.get(i);
			}
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=generateTexCoor((int)(360/angle_span+1),1,1);
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
			//为画笔指定坐标数据
			GLES20.glVertexAttribPointer
			(
				maPositionHandle, 
				3, 
				GLES20.GL_FLOAT, 
				false, 
				3*4, 
				mVertexBuffer
			);
			//为画笔指定纹理坐标数据
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
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vCount); 
		}    
		//自动切分纹理产生纹理数组的方法   
	    public float[] generateTexCoor(int bh,float width,float height)
	    {
	    	float[] result=new float[bh*2+2];
	    	//每一列对应的宽度
	    	float tempW=width/bh;
	    	int c=0;
	    	result[c++]=0.5f*width;
	    	result[c++]=0;
	    	for(int i=0;i<bh;i++)
	    	{
	    		result[c++]=i*tempW;
	    		result[c++]=height;
	    	}
	    	return result;
	    }
	}
	//下方的底座
	private class Pedestal
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
		
		//R为圆柱底部的半径，r为圆柱上部的半径
		public Pedestal(int programId,float R,float span)
		{
			initVertexData(R,span);
			initShader(programId);
		}
		//初始化坐标数据的initVertexData方法
		public void initVertexData(float R,float span)
		{
			List<Float> alist=new ArrayList<Float>();
			for(float vAngle=0;vAngle<360;vAngle=vAngle+span)
			{
				float x0=0;
				float y0=0;
				float z0=0;
				
				float x1=(float) (R*Math.cos(Math.toRadians(vAngle)));
				float y1=0;
				float z1=(float) (-R*Math.sin(Math.toRadians(vAngle)));
				
				float x2=(float) (R*Math.cos(Math.toRadians(vAngle+span)));
				float y2=0;
				float z2=(float) (-R*Math.sin(Math.toRadians(vAngle+span)));
				
				alist.add(x0); alist.add(y0); alist.add(z0);
				alist.add(x1); alist.add(y1); alist.add(z1);
				alist.add(x2); alist.add(y2); alist.add(z2);
			}
			vCount=alist.size()/3;
			float[] vertex=new float[alist.size()];
			for(int i=0;i<alist.size();i++)
			{
				vertex[i]=alist.get(i);
			}
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=generateTexCoor(span,1,1);
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
	        
	        //绘制纹理矩形
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
		}
		//自动切分纹理产生纹理数组的方法,triangleSize表示的是切分的三角形份数
	    public float[] generateTexCoor(float angle_span,float width,float height)
	    {
	    	float[] result=new float[(int) (360/angle_span*3*2)];
	    	int c=0;
	    	for(float i=0;i<360;i=i+angle_span)
	    	{
	    		result[c++]=0.5f*width;
	    		result[c++]=0.5f*height;
	    		
	    		result[c++]=(float) (0.5f+0.5f*Math.cos(Math.toRadians(i)))*width;
	    		result[c++]=(float) (0.5f-0.5f*Math.sin(Math.toRadians(i)))*height;
	    		
	    		result[c++]=(float) (0.5f+0.5f*Math.cos(Math.toRadians(i+angle_span)))*width;
	    		result[c++]=(float) (0.5f-0.5f*Math.sin(Math.toRadians(i+angle_span)))*height;
	    	}
	    	return result;
	    }
	}
	//内部类——圆柱
	private class Cylinder
	{
		//自定义着色器程序的引用
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
		public Cylinder(int programId,float R,float r,float angle_span,float height)
		{
			initVertexData(R,r,angle_span,height);
			initShader(programId);
		}
		//初始化坐标数据的方法
		public void initVertexData(float R,float r,float angle_span,float height)
		{
			List<Float> tempList=new ArrayList<Float>();
			for(float vAngle=0;vAngle<360;vAngle=vAngle+angle_span)
			{
				float x0=(float) (r*Math.cos(Math.toRadians(vAngle)));
				float y0=height; 
				float z0=(float) (-r*Math.sin(Math.toRadians(vAngle)));
				
				float x1=(float) (R*Math.cos(Math.toRadians(vAngle))); 
				float y1=-height;
				float z1=(float) (-R*Math.sin(Math.toRadians(vAngle)));
				
				float x2=(float) (R*Math.cos(Math.toRadians(vAngle+angle_span)));
				float y2=-height;
				float z2=(float) (-R*Math.sin(Math.toRadians(vAngle+angle_span)));
				
				float x3=(float) (r*Math.cos(Math.toRadians(vAngle+angle_span)));
				float y3=height;
				float z3=(float) (-r*Math.sin(Math.toRadians(vAngle+angle_span)));
				
				tempList.add(x0); tempList.add(y0); tempList.add(z0);
				tempList.add(x1); tempList.add(y1); tempList.add(z1);
				tempList.add(x3); tempList.add(y3); tempList.add(z3);

				tempList.add(x3); tempList.add(y3); tempList.add(z3);
				tempList.add(x1); tempList.add(y1); tempList.add(z1);
				tempList.add(x2); tempList.add(y2); tempList.add(z2);
			}
			vCount=tempList.size()/3;//顶点数量
			float[] vertex=new float[tempList.size()];
			for(int i=0;i<tempList.size();i++)
			{
				vertex[i]=tempList.get(i);
			}
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=generateTexCoor((int)(360/angle_span),1,1,1);
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
	        
	        //绘制纹理矩形
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
		}
		//自动切分纹理产生纹理数组的方法
	    public float[] generateTexCoor(int bw,int bh,float width,float height)
	    {
	    	float[] result=new float[bw*bh*6*2]; 
	    	float sizew=width/bw;//列数
	    	float sizeh=height/bh;//行数
	    	int c=0;
	    	for(int i=0;i<bh;i++)
	    	{
	    		for(int j=0;j<bw;j++)
	    		{
	    			//每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
	    			float s=j*sizew;
	    			float t=i*sizeh;
	    			
	    			result[c++]=s;
	    			result[c++]=t;
	    			
	    			result[c++]=s;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t;    			
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t;
	    			
	    			result[c++]=s;
	    			result[c++]=t+sizeh;
	    			
	    			result[c++]=s+sizew;
	    			result[c++]=t+sizeh;    			
	    		}
	    	}
	    	return result;
	    }
	}
}
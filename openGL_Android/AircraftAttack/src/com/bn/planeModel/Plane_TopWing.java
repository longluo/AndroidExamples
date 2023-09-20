package com.bn.planeModel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.bn.core.MatrixState;
//纹理矩形,用于绘制上面的机翼    顶点和纹理
public class Plane_TopWing 
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本
    
	private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer   mTextureBuffer;//顶点着色数据缓冲
    float mAngleX;
    float mAngleY;
    int vCount = 42;
    
    public Plane_TopWing(float width,float height,float length,int mProgram)
    {
    	this.mProgram=mProgram;
    	initVertexData(width,height,length);
    	initShader();
    }
    public void initVertexData(float width,float height,float length)
    {
        float vertices[]=new float[]
        {
        	-5.0f/6.0f*width,4.0f/3.0f*height,0,//A
        	-width,height,-length,//B
        	-width,height,length,//C
        	
        	-5.0f/6.0f*width,4.0f/3.0f*height,0,//A
        	-width,height,length,//C
        	width,height,length,//D
        	
        	-5.0f/6.0f*width,4.0f/3.0f*height,0,//A
        	width,height,length,//D
        	5.0f/6.0f*width,4.0f/3.0f*height,0,//O
        	
        	5.0f/6.0f*width,4.0f/3.0f*height,0,//O
        	width,height,length,//D
        	width,height,-length,//E
        	
        	5.0f/6.0f*width,4.0f/3.0f*height,0,//O
        	width,height,-length,//E
        	-width,height,-length,//B
        	
        	5.0f/6.0f*width,4.0f/3.0f*height,0,//O
        	-width,height,-length,//B
        	-5.0f/6.0f*width,4.0f/3.0f*height,0,//A
        	
        	-width,height,length,//C
        	-width,0,6.0f/5.0f*length,//F
        	width,0,6.0f/5.0f*length,//G
        	
        	-width,height,length,//C
        	width,0,6.0f/5.0f*length,//G
        	width,height,length,//D
        	
        	width,height,length,//D
        	width,0,6.0f/5.0f*length,//G
        	width,0,-6.0f/5.0f*length,//H
        	
        	width,height,length,//D
        	width,0,-6.0f/5.0f*length,//H
        	width,height,-length,//E
        	
        	width,height,-length,//E
        	width,0,-6.0f/5.0f*length,//H
        	-width,0,-6.0f/5.0f*length,//I
        	
        	width,height,-length,//E
        	-width,0,-6.0f/5.0f*length,//I
        	-width,height,-length,//B
        	
        	-width,height,-length,//B
        	-width,0,-6.0f/5.0f*length,//I
        	-width,0,6.0f/5.0f*length,//F
        	
        	-width,height,-length,//B
        	-width,0,6.0f/5.0f*length,//F
        	-width,height,length,//C
        };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        float textures[]=new float[]
        {
        	0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,
        	0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,
        	0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,
        	0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,
        	0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,
        	0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,
        	0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,
        	0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,0.133f,	0.211f,0.242f,0.492f,0.555f,0.289f,
        };
        //创建顶点纹理数据缓冲
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
    }
    //初始化着色器的initShader方法
    public void initShader()
    {
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
    }
    public void drawSelf(int texId)
    {        
    	MatrixState.pushMatrix();
    	MatrixState.rotate(mAngleX, 1, 0, 0);
    	MatrixState.rotate(mAngleY, 0, 1, 0);
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
        //传入顶点纹理坐标数据
        GLES20.glVertexAttribPointer  
        (
       		maTexCoorHandle, 
        		2, 
        		GLES20.GL_FLOAT, 
        		false,
               2*4,   
               mTextureBuffer
        );   
        //允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
        MatrixState.popMatrix();
    }
}

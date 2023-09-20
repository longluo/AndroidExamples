package com.bn.commonObject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;

import com.bn.core.MatrixState;
/**
 * 用triangle_fan方式绘制纹理圆面 此圆面是平行于XY平面的
 * 	用于绘制圆,圆面的中心位于原点
 * 在高射炮中用到
 */
public class CircleForDraw
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id     
    
	private  FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	private  FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    private  int vCount;
    float angleSpan=12;//切分角度
    public CircleForDraw
    (
            int mProgram,
    		float radius//圆半径
    )
    {
    	this.mProgram=mProgram;
    	initVertexData(radius);
    	initShader();
    }
    public  void initVertexData
    (
    		float radius//圆半径
    )
    {
    	//顶点纹理坐标数据的初始化================begin============================
    	vCount=1+(int)(360/angleSpan)+1;//顶点的个数
    	float[] vertices=new float[vCount*3];//初始化顶点数组
    	float[] textures=new float[vCount*2];
    	
    	//存放中心点坐标
    	vertices[0]=0;
    	vertices[1]=0;
    	vertices[2]=0;
    	
    	//存放中心点纹理
    	textures[0]=0.5f;
    	textures[1]=0.5f;
        
    	int vcount=3;//当前顶点坐标索引
    	int tcount=2;//当前纹理坐标索引
    	
    	for(float angle=0;angle<=360;angle=angle+angleSpan)
    	{
    		double angleRadian=Math.toRadians(angle);
    		//顶点坐标
    		vertices[vcount++]=radius*(float)Math.cos(angleRadian);
    		vertices[vcount++]=radius*(float)Math.sin(angleRadian);
    		vertices[vcount++]=0;
    		textures[tcount++]=textures[0]+0.5f*(float)Math.cos(angleRadian);
    		textures[tcount++]=textures[1]+0.5f*(float)Math.sin(angleRadian);
    	}  
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
                
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mTexCoorBuffer = tbb.asFloatBuffer();//转换为Float型缓冲
        mTexCoorBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTexCoorBuffer.position(0);//设置缓冲区起始位置
    }
    public void initShader()
    {
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //获取程序中顶点纹理坐标属性引用id  
        maTexCoorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
    }
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
        //允许顶点位置、纹理坐标数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //绘制图形
        GLES20.glDrawArrays
        (
        		GL10.GL_TRIANGLE_FAN, 		//以TRIANGLE_FAN方式填充
        		0,
        		vCount
        );
    }
}

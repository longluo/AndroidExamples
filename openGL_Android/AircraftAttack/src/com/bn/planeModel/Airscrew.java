package com.bn.planeModel;
import static com.bn.gameView.Constant.planezAngle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.bn.core.MatrixState;
/*
 * 绘制螺旋桨   顶点和纹理
 */
public class Airscrew 
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本
    
	private FloatBuffer mVertexBuffer;	//顶点坐标数据缓冲
	private FloatBuffer mTextureBuffer;	//顶点纹理数据缓冲
	
	int vCount=6;						//顶点数量
	final int angleSpan=8;				//每片螺旋桨叶片角度
	float scale;						//尺寸
	float zSpan=0;						//螺旋桨在z轴上的偏离
	float speed_Airscrew=50f;//螺旋桨旋转地角速度
	
	public Airscrew(float scale,int mProgram)
	{
		this.mProgram=mProgram;
		this.scale=scale;	
		zSpan=scale/12;		//螺旋桨在z轴上的偏离
		initVertex();		//初始化顶点坐标数据
		initTexture();		//初始化顶点纹理数据
		initShader();
	}
	//初始化顶点的信息
	public void initVertex()
	{   //构建立方体
		float x=(float) (this.scale*Math.cos(Math.toRadians(angleSpan)));//构建三角形顶点的x坐标的变量
		float y=(float) (this.scale*Math.sin(Math.toRadians(angleSpan)));//构建三角形顶点的y坐标的变量
		float z=zSpan;														 //构建三角形顶点的z坐标的变量
		//顶点坐标缓冲数组初始化
		float[] vertices=
		{				
			//构成外侧表面三角形的坐标
			0,0,0,
			x,y,0,
			x,-y,-z,
			
			//构成内侧侧表面三角形的坐标
			0,0,0,
			x,-y,-z,
			x,y,0,								
		};
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertices.length*4);	
		vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
		mVertexBuffer=vbb.asFloatBuffer();//转换为float型缓冲
		mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);//设置缓冲区起始位置
	}
	//初始化纹理的信息
	public void initTexture()
	{
		float[] textures=generateTextures();	//生成纹理坐标数组
		ByteBuffer tbb=ByteBuffer.allocateDirect(textures.length*4);
		tbb.order(ByteOrder.nativeOrder());		//设置字节顺序为本地操作系统顺序
		mTextureBuffer=tbb.asFloatBuffer();		//转换为float型缓冲
		mTextureBuffer.put(textures);			//向缓冲区中放入顶点坐标数据
		mTextureBuffer.position(0);				//设置缓冲区起始位置
	}
	//生成纹理
	public float[] generateTextures()
	{
		float[] textures=new float[]
        {//生成纹理坐标数组
			0,0,1,0,0,1,
			0,0,0,1,1,0,
	    };
		return textures;
	}
	//初始化shader
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
		drawOneAirscrew(texId);//绘制其中一个螺旋桨
		MatrixState.rotate(60, 0, 0, 1);
		drawOneAirscrew(texId);//绘制其中一个螺旋桨
		MatrixState.rotate(60, 0, 0, 1);
		drawOneAirscrew(texId);//绘制其中一个螺旋桨
		MatrixState.rotate(60, 0, 0, 1);
		drawOneAirscrew(texId);//绘制其中一个螺旋桨
		MatrixState.rotate(60, 0, 0, 1);
		drawOneAirscrew(texId);//绘制其中一个螺旋桨
		MatrixState.rotate(60, 0, 0, 1);
		drawOneAirscrew(texId);//绘制其中一个螺旋桨
		MatrixState.popMatrix();
	}
	public void drawOneAirscrew(int texId)//绘制其中一个螺旋桨
	{
		MatrixState.pushMatrix();
		MatrixState.rotate(planezAngle, 0, 0, 1);
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
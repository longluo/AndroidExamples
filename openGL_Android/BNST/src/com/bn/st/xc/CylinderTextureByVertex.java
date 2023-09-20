package com.bn.st.xc;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.bn.core.MatrixState;

import android.opengl.GLES20;
/*
 * 用于绘制圆柱,用光照和颜色
 */
public class CylinderTextureByVertex 
{	
	int mProgram;//自定义渲染管着色器程序线程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maNormalHandle; //顶点法向量属性引用id
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maLightLocationHandle;//光源位置属性引用id  
    int maCameraHandle; //摄像机位置属性引用id 
    
    int maColorR;	//颜色值的R分量引用id
    int maColorG;	//颜色值的G分量引用id
    int maColorB;	//颜色值的B分量引用id
    int maColorA;
    String mVertexShader;//顶点着色器    	 
    String mFragmentShader;//片元着色器
    private static FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private static FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    static int vCount=0;//顶点数量
	
	float r;	//颜色值的R分量
    float g;	//颜色值的G分量
    float b;	//颜色值的B分量
   
    public CylinderTextureByVertex
    (
    	int mProgramIn,
    	float radius,//圆柱半径
    	float length,//圆柱长度
    	float aspan,//切分角度
    	float lspan,//切分长度  
    	float[]color
    )
    {    	
    	this.r=color[0];
    	this.g=color[1];
    	this.b=color[2];
    	//初始化着色器程序的方法        
    	initShader(mProgramIn);
    }
    //初始化顶点坐标数据的方法
    public static void initVertexData
    (
		float r,//圆柱半径
    	float length,//圆柱长度
    	float aspan,//切分角度
    	float lspan//切分长度  	
    )
    {
    	//顶点坐标数据的初始化================begin============================
    	//获取切分整图的纹理数组    	
    	ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标的ArrayList
        for(float tempY=length/2;tempY>-length/2;tempY=tempY-lspan)//垂直方向lspan长度一份
        {
        	for(float hAngle=360;hAngle>0;hAngle=hAngle-aspan)//水平方向angleSpan度一份
        	{
        		//纵向横向各到一个角度后计算对应的此点在球面上的四边形顶点坐标
        		//并构建两个组成四边形的三角形
        		
        		float x1=(float)(r*Math.cos(Math.toRadians(hAngle)));
        		float z1=(float)(r*Math.sin(Math.toRadians(hAngle)));
        		float y1=tempY;
        		float x2=(float)(r*Math.cos(Math.toRadians(hAngle)));
        		float z2=(float)(r*Math.sin(Math.toRadians(hAngle)));
        		float y2=tempY-lspan;
        		
        		float x3=(float)(r*Math.cos(Math.toRadians(hAngle-aspan)));
        		float z3=(float)(r*Math.sin(Math.toRadians(hAngle-aspan)));
        		float y3=tempY-lspan;
        		
        		float x4=(float)(r*Math.cos(Math.toRadians(hAngle-aspan)));
        		float z4=(float)(r*Math.sin(Math.toRadians(hAngle-aspan)));
        		float y4=tempY;   
        		
        		//构建第一三角形
        		alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);        		
        		//构建第二三角形
        		alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
        		alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
        		alVertix.add(x3);alVertix.add(y3);alVertix.add(z3); 
        	}
        } 	
        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标
        //将alVertix中的坐标值转存到一个int数组中
        float vertices[]=new float[vCount*3];
    	for(int i=0;i<alVertix.size();i++)
    	{
    		vertices[i]=alVertix.get(i);
    	}
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        //创建顶点法向量坐标缓冲
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(vertices);//向缓冲区中放入顶点着色数据
        mNormalBuffer.position(0);//设置缓冲区起始位置

    }
    public void initShader(int mProgramIn)
    {
        mProgram = mProgramIn;
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id  
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
        //获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix"); 
        //获取程序中光源位置引用id
        maLightLocationHandle=GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用id
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 
        
        maColorR=GLES20.glGetUniformLocation(mProgram, "colorR");
        maColorG=GLES20.glGetUniformLocation(mProgram, "colorG");
        maColorB=GLES20.glGetUniformLocation(mProgram, "colorB");
        maColorA=GLES20.glGetUniformLocation(mProgram, "colorA");
    }
    public void drawSelf(float alpha)
    {        
    	//制定使用某套shader程序
   	 	GLES20.glUseProgram(mProgram); 
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //将位置、旋转变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);  
        //将光源位置传入shader程序   
        GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //将摄像机位置传入shader程序   
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
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
         //传入顶点法向量数据
         GLES20.glVertexAttribPointer  
         (
         		maNormalHandle, 
         		3,   
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mNormalBuffer
         );  
         //允许顶点位置、法向量数据数组
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maNormalHandle);  
         GLES20.glUniform1f(maColorR , r); 
         GLES20.glUniform1f(maColorG , g); 
         GLES20.glUniform1f(maColorB , b);
         GLES20.glUniform1f(maColorA , alpha);
         //绘制整个圆柱
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
}

package com.bn.planeModel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;

import com.bn.core.MatrixState;
/*
 * 用于绘制圆柱    顶点和纹理
 */
public class Column
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本
	
	private FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mTextureBuffer;//顶点纹理数据缓冲
    private int vCount;    //顶点数量

    public float mAngleX;//绕x轴旋转角度
    public float mAngleY;//绕y轴旋转角度
    public float mAngleZ;//绕z轴旋转角度
    
    float mOffsetX;//沿x轴平移距离
    float mOffsetY;//沿y轴平移距离
    float mOffsetZ;//沿z轴平移距离
    
    float height;//设置高
    float radius;//设置半径
    
    private float heightSpan=0.05f; //高度切分单元
    private float angleSpan=30;		//角度切分大小
	
    //圆柱的切分行数和列数
    int col=(int) (360/angleSpan);//圆柱的切分列数
    int row=10;					  //圆柱的切分行数和列数
    
    public Column(float height,float radius,int mProgram)
    {
    	this.mProgram=mProgram;
    	this.height=height;
    	this.radius=radius;
    	heightSpan=height/row;		//高度切分单元
    	initVertex();				//初始化顶点坐标数据
    	initTexture();				//初始化顶点纹理数据
    	initShader();
    }
    //初始化顶点信息
    public void initVertex()
	{    	
    	ArrayList<Float> alVertex=new ArrayList<Float>();//存放顶点
		for(int i=0;i<=row;i++)
		{
			float y=(i-row/2.0f)*heightSpan;//构建三角形顶点的y坐标的变量
			float hAngle=0;					//水平面角度
			for(int j=0;j<=col;j++)
			{
				hAngle=j*angleSpan;			//水平面上的角度
				
				float x=(float) (radius*Math.cos(Math.toRadians(hAngle)));//构建三角形顶点的x坐标的变量
				float z=(float) (radius*Math.sin(Math.toRadians(hAngle)));//构建三角形顶点的z坐标的变量
				alVertex.add(x);			//添加x坐标
				alVertex.add(y);			//添加y坐标
				alVertex.add(z);			//添加z坐标
			}
		}
		ArrayList<Integer> alIndex=new ArrayList<Integer>();		//创建索引
		int ncol=col+1;					//每行实际顶点数量
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				int k=i*ncol+j;			//三角形索引
				alIndex.add(k);			//上三角形
				alIndex.add(k+ncol);
				alIndex.add(k+1);
				alIndex.add(k+ncol);	//下三角形
				alIndex.add(k+ncol+1);
				alIndex.add(k+1);
			}
		}
  		vCount=alIndex.size();			//顶点数量
  		float vertices[]=new float[alIndex.size()*3];//顶点坐标数组
		for(int i=0;i<vCount;i++)				//向数组中添加顶点
		{
			int k=alIndex.get(i);				//取出三角形索引
			vertices[i*3]=alVertex.get(k*3);	//添加三角形x坐标
			vertices[i*3+1]=alVertex.get(k*3+1);//添加三角形y坐标
			vertices[i*3+2]=alVertex.get(k*3+2);//添加三角形z坐标
		}
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertices.length*4);//一个float型4个byte
		vbb.order(ByteOrder.nativeOrder());							//设置字节顺序为本地操作系统顺序
		mVertexBuffer=vbb.asFloatBuffer();							//装换为float缓冲
		mVertexBuffer.put(vertices);								//放入顶点数组
		mVertexBuffer.position(0);									//设置起始位置 
	}
    //初始化纹理信息
    public void initTexture()
	{
		int tCount=row*col*2*3*2;					//顶点数量
		float[] textures=new float[tCount];			//纹理坐标数组
		float sizew=1.0f/col;						//横向纹理坐标单元
		float sizeh=1.0f/row;
		for(int i=0,temp=0;i<row;i++)
		{
			float t=i*sizeh;
			for(int j=0;j<col;j++) 
			{
				float s=j*sizew;
				//左上三角形
				textures[temp++]=s;
				textures[temp++]=t;
				
				textures[temp++]=s;
				textures[temp++]=t+sizeh;
				
				textures[temp++]=s+sizew;
				textures[temp++]=t;
				
				//右下三角形
				textures[temp++]=s;
				textures[temp++]=t+sizeh;
				
				textures[temp++]=s+sizew;
				textures[temp++]=t+sizeh;
				
				textures[temp++]=s+sizew;
				textures[temp++]=t;
			}
		}
		ByteBuffer tbb=ByteBuffer.allocateDirect(textures.length*4);//一个float型4个byte
		tbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
		mTextureBuffer=tbb.asFloatBuffer();//转换为FLOAT型缓冲
		mTextureBuffer.put(textures);//向缓冲区中放入顶点坐标数据
		mTextureBuffer.position(0);//缓冲区的起始位置		
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
		MatrixState.translate(mOffsetX, mOffsetY, mOffsetZ);
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
	 public float[] getLengthWidthHeight()
	 {
		float[] lwh=
		{
			this.radius*2,//长
			this.radius*2,//宽
			this.height	//高
		};			
		return lwh;
	}
}
package com.bn.commonObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.bn.core.MatrixState;

import android.opengl.GLES20;

import static com.bn.gameView.Constant.*;
/*
 * 绘制大坝
 */
public class DamForDraw 
{
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id  
    FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount;
    public boolean isShaderOk;
	public DamForDraw(float height,float length1,float length2,float length3,int mProgram)
	{
		this.mProgram=mProgram;
		initData(height,length1,length2,length3);
	}
	//初始化顶点的信息
	public void initData(float height,float length1,float length2,float length3)
	{
		float vertex[]=new float[(ArchieArray[mapId][5].length/2-1)*6*9*2];
		float texture[]=new float[(ArchieArray[mapId][5].length/2-1)*6*6*2];
		vCount=vertex.length/3;
		int t=0;
		int d=0;
		for(int i=0;i<ArchieArray[mapId][5].length/2-1;i++)
		{
			float x1,z1;
			float x2,z2;
			
			float texX,texY1,texY2,texY3,texY4;
			float spanx=1.0f/(ArchieArray[mapId][5].length/2-1);
			
			texX=spanx*i; 
			texY1=0;
			texY2=0.4f;
			texY3=0.6f;
			texY4=1;
			
			x1=ArchieArray[mapId][5][2*i]*WIDTH_LALNDFORM;
			x2=ArchieArray[mapId][5][2*(1+i)]*WIDTH_LALNDFORM;
			
			z1=ArchieArray[mapId][5][2*i+1]*WIDTH_LALNDFORM;
			z2=ArchieArray[mapId][5][2*(1+i)+1]*WIDTH_LALNDFORM;
			vertex[d++]=x1;//第一个三角形
			vertex[d++]=0;
			vertex[d++]=z1-length1;
			
			vertex[d++]=x1;
			vertex[d++]=height;
			vertex[d++]=z1;
			
			vertex[d++]=x2;
			vertex[d++]=height;
			vertex[d++]=z2;
			
			texture[t++]=texX;
			texture[t++]=texY1;
			
			texture[t++]=texX;
			texture[t++]=texY2;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY2;
			
			vertex[d++]=x1;//第一个三角形反面
			vertex[d++]=0;
			vertex[d++]=z1-length1;
					
			vertex[d++]=x2;
			vertex[d++]=-height;
			vertex[d++]=z2;
			
			vertex[d++]=x1;
			vertex[d++]=-height;
			vertex[d++]=z1;
			
			texture[t++]=texX;
			texture[t++]=texY1;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY2;
			
			texture[t++]=texX;
			texture[t++]=texY2;
			
			vertex[d++]=x1;//第二个三角形
			vertex[d++]=0;
			vertex[d++]=z1-length1;
			
			vertex[d++]=x2;
			vertex[d++]=height;
			vertex[d++]=z2;
			
			vertex[d++]=x2;
			vertex[d++]=0;
			vertex[d++]=z2-length1;
			
			texture[t++]=texX;
			texture[t++]=texY1;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY2;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY1;
			
			vertex[d++]=x1;//第二个三角形反面
			vertex[d++]=0;
			vertex[d++]=z1-length1;
			
			vertex[d++]=x2;
			vertex[d++]=0;
			vertex[d++]=z2-length1;
			
			vertex[d++]=x2;
			vertex[d++]=-height;
			vertex[d++]=z2;
			
			texture[t++]=texX;
			texture[t++]=texY1;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY1;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY2;
			
			vertex[d++]=x1;//第三个三角形
			vertex[d++]=height;
			vertex[d++]=z1;
			
			vertex[d++]=x1;
			vertex[d++]=height;
			vertex[d++]=z1+length2;
			
			vertex[d++]=x2;
			vertex[d++]=height;
			vertex[d++]=z2+length2;
			
			texture[t++]=texX;
			texture[t++]=texY2;
			
			texture[t++]=texX;
			texture[t++]=texY3;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY3;
			
			vertex[d++]=x1;//第三个三角形fanm反面
			vertex[d++]=-height;
			vertex[d++]=z1;
			
			vertex[d++]=x2;
			vertex[d++]=-height;
			vertex[d++]=z2+length2;
			
			vertex[d++]=x1;
			vertex[d++]=-height;
			vertex[d++]=z1+length2;
			
			texture[t++]=texX;
			texture[t++]=texY2;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY3;
			
			texture[t++]=texX;
			texture[t++]=texY3;
			
			vertex[d++]=x1;//第四个三角形
			vertex[d++]=height;
			vertex[d++]=z1;
			
			vertex[d++]=x2;
			vertex[d++]=height;
			vertex[d++]=z2+length2;
			
			vertex[d++]=x2;
			vertex[d++]=height;
			vertex[d++]=z2;
			
			texture[t++]=texX;
			texture[t++]=texY2;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY3;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY2;
			
			vertex[d++]=x1;//第四个三角形反面
			vertex[d++]=-height;
			vertex[d++]=z1;
			
			vertex[d++]=x2;
			vertex[d++]=-height;
			vertex[d++]=z2;
			
			vertex[d++]=x2;
			vertex[d++]=-height;
			vertex[d++]=z2+length2;
			
			texture[t++]=texX;
			texture[t++]=texY2;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY2;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY3;
			
			vertex[d++]=x1;//第五个三角形
			vertex[d++]=height;
			vertex[d++]=z1+length2;
			
			vertex[d++]=x1;
			vertex[d++]=0;
			vertex[d++]=z1+length2+length3;
			
			vertex[d++]=x2;
			vertex[d++]=0;
			vertex[d++]=z2+length2+length3;
			
			texture[t++]=texX;
			texture[t++]=texY3;
			
			texture[t++]=texX;
			texture[t++]=texY4;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY4;
			
			vertex[d++]=x1;//第五个三角形反面
			vertex[d++]=-height;
			vertex[d++]=z1+length2;
			
			vertex[d++]=x2;
			vertex[d++]=0;
			vertex[d++]=z2+length2+length3;
			
			vertex[d++]=x1;
			vertex[d++]=0;
			vertex[d++]=z1+length2+length3;
			
			texture[t++]=texX;
			texture[t++]=texY3;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY4;
			
			texture[t++]=texX;
			texture[t++]=texY4;
			
			vertex[d++]=x1;//第六个三角形
			vertex[d++]=height;
			vertex[d++]=z1+length2;
			
			vertex[d++]=x2;
			vertex[d++]=0;
			vertex[d++]=z2+length2+length3;
			
			vertex[d++]=x2;
			vertex[d++]=height;
			vertex[d++]=z2+length2;
			
			texture[t++]=texX;
			texture[t++]=texY3;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY4;
			
			
			texture[t++]=texX+spanx;
			texture[t++]=texY3;
			
			vertex[d++]=x1;//第六个三角形反面
			vertex[d++]=-height;
			vertex[d++]=z1+length2;
			
			vertex[d++]=x2;
			vertex[d++]=-height;
			vertex[d++]=z2+length2;
			
			vertex[d++]=x2;
			vertex[d++]=0;
			vertex[d++]=z2+length2+length3;

			texture[t++]=texX;
			texture[t++]=texY3;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY3;
			
			texture[t++]=texX+spanx;
			texture[t++]=texY4;
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertex.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序为本地操作系统顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertex);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);
       
        ByteBuffer vbt=ByteBuffer.allocateDirect(texture.length*4);
        vbt.order(ByteOrder.nativeOrder());
        mTexCoorBuffer=vbt.asFloatBuffer();
        mTexCoorBuffer.put(texture);
        mTexCoorBuffer.position(0);
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
	//绘制方法
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
        //绘制加载的物体
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);  
	}
}

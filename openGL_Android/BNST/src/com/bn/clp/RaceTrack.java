package com.bn.clp;
import static com.bn.clp.Constant.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import com.bn.core.MatrixState;
import android.opengl.GLES20;

//代表赛艇赛道中竖直的赛道类
public class RaceTrack
{
	//自定义渲染管线着色器程序的id
	private int mProgram;
	//总变化矩阵引用的id
	private int muMVPMatrixHandle;
	//位置、旋转变换矩阵
	int muMMatrixHandle;
	//顶点位置属性的id
	private int maPositionHandle;
	//顶点纹理属性引用的id
	private int maTexCoorHandle;
	//新添加的==========================================================================
	//草地的id
	int sTextureGrassHandle;
	//石头的id
	int sTextureRockHandle;
	//起始x值
	int b_YZ_StartYHandle;
	//长度
	int b_YZ_YSpanHandle;
	//是否为隧道山的标志位的引用id
	int sdflagHandle;
	//此处flag值为0表示隧道山，值为1表示为普通山
	private int flag=1;
	//新添加的=============================================================================
	
	//顶点坐标缓冲
	private FloatBuffer mVertexBuffer;
	//片元纹理缓冲
	private FloatBuffer mTexCoorBuffer;
	//顶点的数量
	private int vCount=0;
	
	//是否为直道标记
	boolean isZD;
	
	/*
	 * 本类的有参构造器，其中mv为MyGLSurfaceView类的引用，
	 * yArray为相应灰度图顶点的y坐标，rows为该灰度图的行数，cols为该灰度图的列数
	*/
	public RaceTrack(int programId,float[][] yArray,int rows,int cols,boolean isZD)
	{
		this.isZD=isZD;
		//初始化顶点数据
		initVertexData(yArray,rows,cols,isZD);
		//初始化Shader
		initShader(programId);
	}
	
	//初始化顶点数据的initVertexData方法
	public void initVertexData(float[][] yArray,int rows,int cols,boolean isZD)
	{
		if(isZD)
		{
			initVertexDataZD(yArray,rows,cols);
		}
		else
		{
			initVertexDataFZD(yArray,rows,cols);
		}
	}
	
	//初始化普通直道顶点数据的方法
	public void initVertexDataZD(float[][] yArray,int rows,int cols)
	{
		float width=UNIT_SIZE/cols;//分成格子的宽度
		float height=UNIT_SIZE/rows;//分成格子的宽高度
		
		vCount=rows*cols*2*3;//顶点的数量
		float[] vertex=new float[vCount*3];
		float[] texture=new float[vCount*2];
		
		float tempWidth=2.0f/cols;//分成格子的纹理宽度
		float tempHeight=2.0f/rows;//分成格子的纹理高度		
		int countv=0;
		int countt=0;
		
		int state=0;//0--一开始  1--高度为0  2--恢复
		
		for(int j=0;j<cols;j++)
		{
			state=0;
			for(int i=0;i<rows+1;i++)
			{
            	if(j!=0&&i==0)
            	{
            		//本列右面点
    				vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
    				vertex[countv++]=yArray[i][j+1];
    				vertex[countv++]=height*i-UNIT_SIZE/2;
    				
    				//纹理的S、T坐标
    				texture[countt++]=tempWidth*(j+1)-width/2;
    				texture[countt++]=tempHeight*i-height/2; 
    				
    				//本列右面点
    				vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
    				vertex[countv++]=yArray[i][j+1];
    				vertex[countv++]=height*i-UNIT_SIZE/2;
    				
    				//纹理的S、T坐标
    				texture[countt++]=tempWidth*(j+1)-width/2;
    				texture[countt++]=tempHeight*i-height/2; 
            	}  
				if(state==0)
				{
					//本列右面点
					vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
					vertex[countv++]=yArray[i][j+1];
					vertex[countv++]=height*i-UNIT_SIZE/2;
					
					//纹理的S、T坐标
					texture[countt++]=tempWidth*(j+1)-width/2;
					texture[countt++]=tempHeight*i-height/2; 
	            	
	            	//本行左面点
					vertex[countv++]=width*j-UNIT_SIZE/2;
					vertex[countv++]=yArray[i][j];
					vertex[countv++]=height*i-UNIT_SIZE/2;
					
					//纹理的S、T坐标
					texture[countt++]=tempWidth*j-width/2;
					texture[countt++]=tempHeight*i-height/2;  
					
					if(yArray[i][j]==0&&yArray[i][j+1]==0&&yArray[i+1][j]==0&&yArray[i+1][j+1]==0)
					{
						state=1;
						//本行左面点
						vertex[countv++]=width*j-UNIT_SIZE/2;
						vertex[countv++]=yArray[i][j];
						vertex[countv++]=height*i-UNIT_SIZE/2;
						
						//纹理的S、T坐标
						texture[countt++]=tempWidth*j-width/2;
						texture[countt++]=tempHeight*i-height/2;
						
						//本行左面点
						vertex[countv++]=width*j-UNIT_SIZE/2;
						vertex[countv++]=yArray[i][j];
						vertex[countv++]=height*i-UNIT_SIZE/2;
						
						//纹理的S、T坐标
						texture[countt++]=tempWidth*j-width/2;
						texture[countt++]=tempHeight*i-height/2;
					}
				}
				else if(state==1)
				{
					if(!(yArray[i+1][j]==0&&yArray[i+1][j+1]==0))
					{
						//本列右面点
						vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
						vertex[countv++]=yArray[i][j+1];
						vertex[countv++]=height*i-UNIT_SIZE/2;
						
						//纹理的S、T坐标
						texture[countt++]=tempWidth*(j+1)-width/2;
						texture[countt++]=tempHeight*i-height/2; 
						
						//本列右面点
						vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
						vertex[countv++]=yArray[i][j+1];
						vertex[countv++]=height*i-UNIT_SIZE/2;
						
						//纹理的S、T坐标
						texture[countt++]=tempWidth*(j+1)-width/2;
						texture[countt++]=tempHeight*i-height/2; 
						
						//本列右面点
						vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
						vertex[countv++]=yArray[i][j+1];
						vertex[countv++]=height*i-UNIT_SIZE/2;
						
						//纹理的S、T坐标
						texture[countt++]=tempWidth*(j+1)-width/2;
						texture[countt++]=tempHeight*i-height/2; 
		            	
		            	//本行左面点
						vertex[countv++]=width*j-UNIT_SIZE/2;
						vertex[countv++]=yArray[i][j];
						vertex[countv++]=height*i-UNIT_SIZE/2;
						
						//纹理的S、T坐标
						texture[countt++]=tempWidth*j-width/2;
						texture[countt++]=tempHeight*i-height/2;  
						state=2;
					}
				}
				else if(state==2)
				{
					//本列右面点
					vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
					vertex[countv++]=yArray[i][j+1];
					vertex[countv++]=height*i-UNIT_SIZE/2;
					
					//纹理的S、T坐标
					texture[countt++]=tempWidth*(j+1)-width/2;
					texture[countt++]=tempHeight*i-height/2; 
	            	
	            	//本行左面点
					vertex[countv++]=width*j-UNIT_SIZE/2;
					vertex[countv++]=yArray[i][j];
					vertex[countv++]=height*i-UNIT_SIZE/2;
					
					//纹理的S、T坐标
					texture[countt++]=tempWidth*j-width/2;
					texture[countt++]=tempHeight*i-height/2;  
				}
            	
				
				if(i==rows&&j!=cols-1)
				{
					//本行左面点
					vertex[countv++]=width*j-UNIT_SIZE/2;
					vertex[countv++]=yArray[i][j];
					vertex[countv++]=height*i-UNIT_SIZE/2;
					
					//纹理的S、T坐标
					texture[countt++]=tempWidth*j-width/2;
					texture[countt++]=tempHeight*i-height/2;   
					   
					//本行左面点
					vertex[countv++]=width*j-UNIT_SIZE/2;  
					vertex[countv++]=yArray[i][j];  
					vertex[countv++]=height*i-UNIT_SIZE/2;
					
					//纹理的S、T坐标
					texture[countt++]=tempWidth*j-width/2;
					texture[countt++]=tempHeight*i-height/2;   
				}                     
			}
		}
		vCount=countt/2;
		
		//产生包括倒影的多加两个顶点的数组
		float[] vertexL=new float[vCount*3];
		for(int i=0;i<vertexL.length;i++)
		{
			vertexL[i]=vertex[i];
		}
		vertex=vertexL;
		
		vertexL=new float[(vCount*2+2)*3];
		int cTemp=0;
		for(int i=0;i<vertex.length;i++)
		{
			vertexL[cTemp++]=vertex[i];
		}
		vertexL[cTemp++]=vertex[vertex.length-3];
		vertexL[cTemp++]=vertex[vertex.length-2];
		vertexL[cTemp++]=vertex[vertex.length-1];
		
		vertexL[cTemp++]=vertex[0];
		vertexL[cTemp++]=-vertex[1];
		vertexL[cTemp++]=vertex[2];
		
		for(int i=0;i<vertex.length;i++)
		{
			if(i%3==1)
			{
				vertexL[cTemp++]=-vertex[i];
			}
			else
			{
				vertexL[cTemp++]=vertex[i];
			}
		}
		
		
		float[] textureL=new float[vCount*2];
		for(int i=0;i<textureL.length;i++)
		{
			textureL[i]=texture[i];
		}
		texture=textureL;
		
		textureL=new float[(vCount*2+2)*2];
		cTemp=0;
		for(int i=0;i<texture.length;i++)
		{
			textureL[cTemp++]=texture[i];
		}
		textureL[cTemp++]=texture[texture.length-2];
		textureL[cTemp++]=texture[texture.length-1];
		
		textureL[cTemp++]=texture[0];
		textureL[cTemp++]=texture[1];
		
		for(int i=0;i<texture.length;i++)
		{
			textureL[cTemp++]=texture[i];
		}
		
		vCount=vCount*2+2;
		
		//创建顶点缓冲区坐标
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertexL.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer=vbb.asFloatBuffer();
		mVertexBuffer.put(vertexL);
		mVertexBuffer.position(0);
		
		//切分纹理图片的方法
		ByteBuffer cbb=ByteBuffer.allocateDirect(textureL.length*4);
		cbb.order(ByteOrder.nativeOrder());
		mTexCoorBuffer=cbb.asFloatBuffer();
		mTexCoorBuffer.put(textureL);
		mTexCoorBuffer.position(0);
	}
	
	//初始化非普通直道顶点数据的方法
	public void initVertexDataFZD(float[][] yArray,int rows,int cols)
	{
		float width=UNIT_SIZE/cols;//分成格子的宽度
		float height=UNIT_SIZE/rows;//分成格子的宽高度
		
		vCount=rows*cols*2*3;//顶点的数量
		float[] vertex=new float[vCount*3];
		float[] texture=new float[vCount*2];
		
		float tempWidth=2.0f/cols;//分成格子的宽度
		float tempHeight=2.0f/rows;//分成格子的宽高度
		
		int countv=0;
		int countt=0;
		//循环遍历顶点，行->列
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<cols;j++)
			{				
				if(yArray[i][j]!=0||yArray[i+1][j]!=0||yArray[i][j+1]!=0)
				{
					//逆时针卷绕第一个三角形的左上角的顶点
					vertex[countv++]=width*j-UNIT_SIZE/2;
					vertex[countv++]=yArray[i][j];
					vertex[countv++]=height*i-UNIT_SIZE/2;
					//逆时针卷绕第一个三角形的左下角的顶点
					vertex[countv++]=width*j-UNIT_SIZE/2;
					vertex[countv++]=yArray[i+1][j];
					vertex[countv++]=height*(i+1)-UNIT_SIZE/2;
					//逆时针卷绕第一个三角形的右上角的顶点
					vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
					vertex[countv++]=yArray[i][j+1];
					vertex[countv++]=height*i-UNIT_SIZE/2;
					
					//纹理图形中第一个三角形左上角的S、T坐标
					texture[countt++]=tempWidth*i-width/2;
					texture[countt++]=tempHeight*j-height/2;
					//纹理图形中第一个三角形左下角的S、T坐标
					texture[countt++]=tempWidth*(i+1)-width/2;
					texture[countt++]=tempHeight*j-height/2;
					//纹理图形中第一个三角形右上角的S、T坐标
					texture[countt++]=tempWidth*i-width/2;
					texture[countt++]=tempHeight*(j+1)-height/2;
				}
				
				if(yArray[i][j+1]!=0||yArray[i+1][j]!=0||yArray[i+1][j+1]!=0)
				{
					//逆时针卷绕第二个三角形的右上角的顶点
					vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
					vertex[countv++]=yArray[i][j+1];
					vertex[countv++]=height*i-UNIT_SIZE/2;
					//逆时针卷绕第二个三角形的左下角的顶点
					vertex[countv++]=width*j-UNIT_SIZE/2;
					vertex[countv++]=yArray[i+1][j];
					vertex[countv++]=height*(i+1)-UNIT_SIZE/2;
					//逆时针卷绕第二个三角形的右下角的顶点
					vertex[countv++]=width*(j+1)-UNIT_SIZE/2;
					vertex[countv++]=yArray[i+1][j+1];
					vertex[countv++]=height*(i+1)-UNIT_SIZE/2;
					
					//纹理图形中第二个三角形右上角的S、T坐标
					texture[countt++]=tempWidth*i-width/2;
					texture[countt++]=tempHeight*(j+1)-height/2;
					//纹理图形中第二个三角形左下角的S、T坐标
					texture[countt++]=tempWidth*(i+1)-width/2;
					texture[countt++]=tempHeight*j-height/2;
					//纹理图形中第二个三角形右上角的S、T坐标
					texture[countt++]=tempWidth*(i+1)-width/2;
					texture[countt++]=tempHeight*(j+1)-height/2;
				}
			}
		}
		vCount=countt/2;
		
		//产生包括倒影的多加两个顶点的数组
		float[] vertexL=new float[vCount*3];
		for(int i=0;i<vertexL.length;i++)
		{
			vertexL[i]=vertex[i];
		}
		vertex=vertexL;
		
		vertexL=new float[(vCount*2)*3];
		int cTemp=0;
		for(int i=0;i<vertex.length;i++)
		{
			vertexL[cTemp++]=vertex[i];
		}		
		
		for(int i=0;i<vertex.length;i++)
		{
			if(i%3==1)
			{
				vertexL[cTemp++]=-vertex[i];
			}
			else
			{
				vertexL[cTemp++]=vertex[i];
			}
		}
		
		
		float[] textureL=new float[vCount*2];
		for(int i=0;i<textureL.length;i++)
		{
			textureL[i]=texture[i];
		}
		texture=textureL;
		
		textureL=new float[(vCount*2)*2];
		cTemp=0;
		for(int i=0;i<texture.length;i++)
		{
			textureL[cTemp++]=texture[i];
		}		
		
		for(int i=0;i<texture.length;i++)
		{
			textureL[cTemp++]=texture[i];
		}
		
		vCount=vCount*2;
		
		//创建顶点缓冲区坐标
		ByteBuffer vbb=ByteBuffer.allocateDirect(vertexL.length*4);
		vbb.order(ByteOrder.nativeOrder());
		mVertexBuffer=vbb.asFloatBuffer();
		mVertexBuffer.put(vertexL);
		mVertexBuffer.position(0);
		
		//切分纹理图片的方法
		ByteBuffer cbb=ByteBuffer.allocateDirect(textureL.length*4);
		cbb.order(ByteOrder.nativeOrder());
		mTexCoorBuffer=cbb.asFloatBuffer();
		mTexCoorBuffer.put(textureL);
		mTexCoorBuffer.position(0);
	}
	
	//初始化着色器的方法
	public void initShader(int programId)
	{
		//基于顶点着色器与片元着色器创建程序
		mProgram=programId;  
		//获取程序中顶点位置属性引用id
		maPositionHandle=GLES20.glGetAttribLocation(mProgram, "aPosition");
		//获取程序中片元属性引用id
		maTexCoorHandle=GLES20.glGetAttribLocation(mProgram, "aTexCoor");
		//获取程序中总变换矩阵引用id
		muMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		//获取位置、旋转变换矩阵引用id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
        
        //新添加的=========================================================================
        //纹理
		//草地
		sTextureGrassHandle=GLES20.glGetUniformLocation(mProgram, "sTextureGrass");
		//石头
		sTextureRockHandle=GLES20.glGetUniformLocation(mProgram, "sTextureRock");
		//x位置
		b_YZ_StartYHandle=GLES20.glGetUniformLocation(mProgram, "b_YZ_StartY");
		//x最大
		b_YZ_YSpanHandle=GLES20.glGetUniformLocation(mProgram, "b_YZ_YSpan");
    	sdflagHandle=GLES20.glGetUniformLocation(mProgram, "sdflag");
    	//新添加的=========================================================================
	}   
		  
	//绘制图形的方法
	public void drawSelf(int rock_textId,int textureId)
	{  
		realDrawTask(textureId,rock_textId);  
	}
	
	//真正的绘制任务
	public void realDrawTask(int textureId,int rock_textId)
	{
		//制定使用某套shader程序
		GLES20.glUseProgram(mProgram);
		//将最终变换矩阵传入Shader程序   
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //将位置、旋转变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); 
        
        //新添加的============================================================
        //将是否为普通山的标志位传到Shader程序中
        GLES20.glUniform1i(sdflagHandle, flag);
        //新添加的============================================================
        
        //传入顶点位置数据
        GLES20.glVertexAttribPointer
        (
        	maPositionHandle, 
    		3, 
    		GLES20.GL_FLOAT, 
    		false,				//这个参数的具体意义？？？？？？？ 
    		3*4,				//3是顶点的坐标数量，但是4到底是什么？？？？？
    		mVertexBuffer		//顶点坐标缓冲数据
        );
        //传入顶点纹理坐标数据
        GLES20.glVertexAttribPointer
        (
        	maTexCoorHandle, 
    		2, 
    		GLES20.GL_FLOAT, 
    		false,				//这个参数的具体意义？？？？？？？ 
    		2*4,				//3是顶点的坐标数量，但是4到底是什么？？？？？
    		mTexCoorBuffer		//顶点坐标缓冲数据
        );
        //允许顶点位置、纹理坐标数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);//顶点坐标
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);//纹理坐标
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        //新添加的===============================================================
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, rock_textId);
		GLES20.glUniform1i(sTextureGrassHandle, 0);//使用0号纹理
        GLES20.glUniform1i(sTextureRockHandle, 1); //使用1号纹理
        
        //传送相应的x参数
        GLES20.glUniform1f(b_YZ_StartYHandle, 0);
        
        GLES20.glUniform1f(b_YZ_YSpanHandle, LAND_MAX_HIGHEST); 
        //新添加的===============================================================
        if(isZD)
        {
        	//绘制纹理矩形
    		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vCount);
        }
        else
        {
        	//绘制纹理矩形
    		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
        }
	}
}
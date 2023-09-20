package com.bn.clp;
import static com.bn.clp.Constant.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import android.opengl.GLES20;
import com.bn.core.MatrixState;

public class Tunnel extends BNDrawer
{
	//单位长度
	float UNIT_SIZE=15f;
	PipeLine ppl;//隧道下方的通道
	Mountion mountion;
	public Tunnel(int mProgramId0,int mProgramId1,float[][] yArray,int rows,int cols)
	{
		ppl=new PipeLine(mProgramId0);
		mountion=new Mountion(mProgramId1,yArray,rows,cols);
	}
	public void drawSelf(int[] texId,int udyflag)
	{
		//texId[0]为隧道的纹理Id，texId[1]表示小山的纹理id，texId[2]表示小山上方岩石的纹理id
		ppl.drawSelf(texId[0]);//绘制下方通道
		mountion.drawSelf(texId[1],texId[2]);
	}
	
	//隧道下方的通道
	private class PipeLine
	{
		//自定义渲染管线着色器的id
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
		
		final float R=18f;
		float height=UNIT_SIZE*3f;
		final float ANGLE_SPAN=18;//分割的度数
		public PipeLine(int programId) 
		{
			initVertexData();
			initShader(programId);
		}
		//初始化顶点数据的方法
		public void initVertexData()
		{
			List<Float> tempList=new ArrayList<Float>();
			for(float vAngle=180;vAngle>0;vAngle=vAngle-ANGLE_SPAN)
			{
				float x0=(float) (R*Math.cos(Math.toRadians(vAngle)));
				float y0=(float) (R*Math.sin(Math.toRadians(vAngle)));
				float z0=height;
				
				float x1=(float) (R*Math.cos(Math.toRadians(vAngle)));
				float y1=(float) (R*Math.sin(Math.toRadians(vAngle)));
				float z1=-height;
				
				float x2=(float) (R*Math.cos(Math.toRadians(vAngle-ANGLE_SPAN)));
				float y2=(float) (R*Math.sin(Math.toRadians(vAngle-ANGLE_SPAN)));
				float z2=-height;
				
				float x3=(float) (R*Math.cos(Math.toRadians(vAngle-ANGLE_SPAN)));
				float y3=(float) (R*Math.sin(Math.toRadians(vAngle-ANGLE_SPAN)));
				float z3=height;
				
				tempList.add(x0);tempList.add(y0);tempList.add(z0);
				tempList.add(x1);tempList.add(y1);tempList.add(z1);
				tempList.add(x3);tempList.add(y3);tempList.add(z3);
				
				tempList.add(x3);tempList.add(y3);tempList.add(z3);
				tempList.add(x1);tempList.add(y1);tempList.add(z1);
				tempList.add(x2);tempList.add(y2);tempList.add(z2);				
			}
			float[] vertex=new float[tempList.size()];
			for(int i=0;i<tempList.size();i++)
			{
				vertex[i]=tempList.get(i);
			}
			vCount=tempList.size()/3;
			ByteBuffer vbb=ByteBuffer.allocateDirect(vertex.length*4);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer=vbb.asFloatBuffer();
			mVertexBuffer.put(vertex);
			mVertexBuffer.position(0);
			
			float[] texcoor=generateTexCoor((int)(180/ANGLE_SPAN),1,3,3);
			ByteBuffer tbb=ByteBuffer.allocateDirect(texcoor.length*4);
			tbb.order(ByteOrder.nativeOrder());
			mTexCoorBuffer=tbb.asFloatBuffer();
			mTexCoorBuffer.put(texcoor);
			mTexCoorBuffer.position(0);
		}
		
		//初始化着色器的initShader方法
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
	
	//隧道上的小山
	private class Mountion
	{
		//自定义渲染管线的id
		int mProgram;
		//总变化矩阵引用的id
		int muMVPMatrixHandle;
		//顶点位置属性引用id
		int maPositionHandle;
		//顶点纹理坐标属性引用id
		int maTexCoorHandle;
		
		//草地的id
		int sTextureGrassHandle;
		//石头的id
		int sTextureRockHandle;
		//起始x值
		int b_YZ_StartYHandle;
		//长度
		int b_YZ_YSpanHandle;
		//位置、旋转变换矩阵
		int muMMatrixHandle;
		//是否为隧道山的标志位的引用id
		int sdflagHandle;
		//此处flag值为0表示隧道山，值为1表示为普通山
		private int flag=0;
		
		
		//顶点数据缓冲和纹理坐标数据缓冲
		FloatBuffer mVertexBuffer;
		FloatBuffer mTexCoorBuffer; 
		//顶点数量
		int vCount=0;
		final float TEMP_UNIT_SIZE_X=6*UNIT_SIZE/15;
		final float TEMP_UNIT_SIZE_Z=6*UNIT_SIZE/15;
		
		public Mountion(int programId,float[][] yArray,int rows,int cols)
		{
			initVertexData(yArray,rows,cols);
			initShader(programId);
		}
		
		//初始化顶点坐标数据的initVertexData方法
	    public void initVertexData(float[][] yArray,int rows,int cols)
	    {
	    	//顶点坐标数据的初始化================begin============================
	    	vCount=cols*rows*2*3;//每个格子两个三角形，每个三角形3个顶点   
	        float vertices[]=new float[vCount*3];//每个顶点xyz三个坐标
	        int count=0;//顶点计数器
	        for(int j=0;j<rows;j++)
	        {
	        	for(int i=0;i<cols;i++) 
	        	{        		
	        		//计算当前格子左上侧点坐标 
	        		float zsx=-TEMP_UNIT_SIZE_X*cols/2+i*TEMP_UNIT_SIZE_X;
	        		float zsz=-TEMP_UNIT_SIZE_Z*rows/2+j*TEMP_UNIT_SIZE_Z;
	        		
	        		vertices[count++]=zsx;
	        		vertices[count++]=yArray[j][i];
	        		vertices[count++]=zsz;
	        		
	        		vertices[count++]=zsx;
	        		vertices[count++]=yArray[j+1][i];
	        		vertices[count++]=zsz+TEMP_UNIT_SIZE_Z;
	        		
	        		vertices[count++]=zsx+TEMP_UNIT_SIZE_X;
	        		vertices[count++]=yArray[j][i+1];
	        		vertices[count++]=zsz;
	        		
	        		vertices[count++]=zsx+TEMP_UNIT_SIZE_X;
	        		vertices[count++]=yArray[j][i+1];
	        		vertices[count++]=zsz;
	        		
	        		vertices[count++]=zsx;
	        		vertices[count++]=yArray[j+1][i];
	        		vertices[count++]=zsz+TEMP_UNIT_SIZE_Z;
	        		
	        		vertices[count++]=zsx+TEMP_UNIT_SIZE_X;
	        		vertices[count++]=yArray[j+1][i+1];
	        		vertices[count++]=zsz+TEMP_UNIT_SIZE_Z;
	        	}
	        }
			
	        //创建顶点坐标数据缓冲
	        //vertices.length*4是因为一个整数四个字节
	        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
	        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
	        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
	        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
	        mVertexBuffer.position(0);//设置缓冲区起始位置

	        
	        //顶点纹理坐标数据的初始化================begin============================
	        float[] texCoor=generateTexCoor(cols,rows,8,8);
	        //创建顶点纹理坐标数据缓冲
	        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
	        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
	        mTexCoorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
	        mTexCoorBuffer.put(texCoor);//向缓冲区中放入顶点着色数据
	        mTexCoorBuffer.position(0);//设置缓冲区起始位置
	    }
		
		//初始化着色器的initShader方法
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
	        
	        //纹理
			//草地
			sTextureGrassHandle=GLES20.glGetUniformLocation(mProgram, "sTextureGrass");
			//石头
			sTextureRockHandle=GLES20.glGetUniformLocation(mProgram, "sTextureRock");
			//x位置
			b_YZ_StartYHandle=GLES20.glGetUniformLocation(mProgram, "b_YZ_StartY");
			//x最大
			b_YZ_YSpanHandle=GLES20.glGetUniformLocation(mProgram, "b_YZ_YSpan");
	    	//位置、旋转变换矩阵的引用id
	    	muMMatrixHandle=GLES20.glGetUniformLocation(mProgram, "uMMatrix");
	    	sdflagHandle=GLES20.glGetUniformLocation(mProgram, "sdflag");
		}        
		
		//实际的绘制方法
		public void drawSelf(int texId,int rock_textId)
		{
			//制定使用某套shader程序
	   	 	GLES20.glUseProgram(mProgram); 
	        //将最终变换矩阵传入shader程序
	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
	        //将位置、旋转变换矩阵传入到Shader程序中
	        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
	        GLES20.glUniform1i(sdflagHandle, flag);
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
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, rock_textId);
			GLES20.glUniform1i(sTextureGrassHandle, 0);//使用0号纹理
	        GLES20.glUniform1i(sTextureRockHandle, 1); //使用1号纹理
	        
	        //传送相应的x参数
	        GLES20.glUniform1f(b_YZ_StartYHandle, 0);
	        GLES20.glUniform1f(b_YZ_YSpanHandle, SD_HEIGHT);
	        
	        //开启混合
            GLES20.glEnable(GLES20.GL_BLEND);  
            //设置混合因子
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	        //绘制纹理矩形
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
	        //关闭混合
            GLES20.glDisable(GLES20.GL_BLEND); 
		} 
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
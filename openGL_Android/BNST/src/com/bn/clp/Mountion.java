package com.bn.clp;
import static com.bn.clp.Constant.*; 
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import android.opengl.GLES20;
import com.bn.core.MatrixState;

public class Mountion extends BNDrawer
{
	Mountion_In mountion_in;
	public Mountion(int programId,float[][] yArray,int rows,int cols)
	{
		mountion_in=new Mountion_In(programId,yArray,rows,cols);
	}
	
	@Override
	public void drawSelf(int[] texId,int dyFlag)
	{
		//texId[0]为草皮纹理id，texId[1]为石头纹理id
		mountion_in.drawSelf(texId[0], texId[1]);
	}
	
	//真正小山的内部类
	private class Mountion_In
	{
		//单位长度
		float UNIT_SIZE=Constant.UNIT_SIZE/15;
		
		//自定义渲染管线着色器程序的id
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
		//是否为隧道山的标志位的引用id   
		int sdflagHandle;
		//此处flag值为0表示隧道山，值为1表示为普通山
		private int flag=1;
		
		//顶点数据缓冲和纹理坐标数据缓冲
		FloatBuffer mVertexBuffer;
		FloatBuffer mTexCoorBuffer; 
		//顶点数量
		int vCount=0;
		
		public Mountion_In(int programId,float[][] yArray,int rows,int cols)
		{
			initVertexData(yArray,rows,cols);
			initShader(programId);
		}
		//初始化顶点数据的initVertexData方法
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
	        		float zsx=-UNIT_SIZE*cols/2+i*UNIT_SIZE;
	        		float zsz=-UNIT_SIZE*rows/2+j*UNIT_SIZE;
	        		
	        		vertices[count++]=zsx;
	        		vertices[count++]=yArray[j][i];
	        		vertices[count++]=zsz;
	        		
	        		vertices[count++]=zsx;
	        		vertices[count++]=yArray[j+1][i];
	        		vertices[count++]=zsz+UNIT_SIZE;
	        		
	        		vertices[count++]=zsx+UNIT_SIZE;
	        		vertices[count++]=yArray[j][i+1];
	        		vertices[count++]=zsz;
	        		
	        		vertices[count++]=zsx+UNIT_SIZE;
	        		vertices[count++]=yArray[j][i+1];
	        		vertices[count++]=zsz;
	        		
	        		vertices[count++]=zsx;
	        		vertices[count++]=yArray[j+1][i];
	        		vertices[count++]=zsz+UNIT_SIZE;
	        		
	        		vertices[count++]=zsx+UNIT_SIZE;
	        		vertices[count++]=yArray[j+1][i+1];
	        		vertices[count++]=zsz+UNIT_SIZE;
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
	        float[] texCoor=generateTexCoor(cols,rows);
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
	    	sdflagHandle=GLES20.glGetUniformLocation(mProgram, "sdflag");
		}
		
		//自定义的绘制方法drawSelf
		public void drawSelf(int texId,int rock_textId)
		{
			//制定使用某套shader程序
	   	 	GLES20.glUseProgram(mProgram); 
	        //将最终变换矩阵传入shader程序
	        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
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
	        
	        //绘制纹理矩形
	        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
		}
		//自动切分纹理产生纹理数组的方法
	    public float[] generateTexCoor(int bw,int bh)
	    {
	    	float[] result=new float[bw*bh*6*2]; 
	    	float sizew=8.0f/bw;//列数
	    	float sizeh=8.0f/bh;//行数
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
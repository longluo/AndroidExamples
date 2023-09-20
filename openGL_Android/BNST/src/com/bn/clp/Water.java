package com.bn.clp;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import com.bn.core.MatrixState;
import android.opengl.GLES20;
import static com.bn.clp.Constant.*;

//有波浪效果的水面
public class Water 
{	
	int mPrograms;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id  
    int maTexCoorHandle; //顶点纹理坐标属性引用id 
    
    int maSTOffset;	//水面纹理图的偏移量引用id

    static float[] mMMatrix = new float[16];//具体物体的移动旋转矩阵
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mTexCoorBuffer;//顶点纹理坐标数据缓冲
    int vCount=0;   //顶点数量
    float currStartST=0;	//水面纹理坐标的当前起始坐标0~1
    
    public Water(int programId,int rows,int cols)
    {    	
    	//初始化顶点坐标的initVertexData方法
    	initVertexData(rows,cols);
    	//初始化着色器的方法        
    	initShader(programId);
    	//启动一个线程定时换帧
    	new Thread()
    	{
    		public void run()
    		{
    			while(Constant.threadFlag)
    			{
    				//所谓水面定时换帧只是修改每帧起始角度即可，
    				//水面顶点Y坐标的变化由顶点着色单元完成
    				currStartST=(currStartST+0.004f)%1;
        			try 
        			{
    					Thread.sleep(100);  
    				} catch (InterruptedException e) 
    				{
    					e.printStackTrace();
    				}
    			}     
    		}    
    	}.start();  
    }
    
    //初始化顶点坐标的initVertexData方法
    public void initVertexData(int rows,int cols)
    {
    	final float pre_Size=UNIT_SIZE/rows;
    	
    	//顶点坐标数据的初始化================begin============================
    	vCount=cols*rows*2*3;//每个格子两个三角形，每个三角形3个顶点        
        float vertices[]=new float[vCount*3];//每个顶点xyz三个坐标
        
        int count=0;//顶点计数器
        for(int j=0;j<rows;j++)
        {
        	for(int i=0;i<cols;i++)
        	{        		
        		//计算当前格子左上侧点坐标 
        		float zsx=-UNIT_SIZE/2+i*pre_Size;
        		float zsy=WATER_HIGH_ADJUST;
        		float zsz=-UNIT_SIZE/2+j*pre_Size;
        		
        		vertices[count++]=zsx;
        		vertices[count++]=zsy;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx;
        		vertices[count++]=zsy;
        		vertices[count++]=zsz+pre_Size;
        		
        		vertices[count++]=zsx+pre_Size;
        		vertices[count++]=zsy;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx+pre_Size;
        		vertices[count++]=zsy;
        		vertices[count++]=zsz;
        		
        		vertices[count++]=zsx;
        		vertices[count++]=zsy;
        		vertices[count++]=zsz+pre_Size;
        		        		
        		vertices[count++]=zsx+pre_Size;
        		vertices[count++]=zsy;
        		vertices[count++]=zsz+pre_Size;   
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
        float texCoor[]=generateTexCoor(cols,rows);     
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
        mPrograms =programId;
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mPrograms, "aPosition");
        //获取程序中顶点纹理坐标属性引用id   
        maTexCoorHandle= GLES20.glGetAttribLocation(mPrograms, "aTexCoor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mPrograms, "uMVPMatrix");  
        //获取水面纹理图偏移量的引用id
        maSTOffset=GLES20.glGetUniformLocation(mPrograms, "stK");  
    }
    
    public void drawSelf(int texId,float startST)
    {   	    	
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mPrograms); 
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
         //将水面纹理图的st偏移量传入shader程序
         GLES20.glUniform1f(maSTOffset, startST);
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
         
         //绘制纹理矩形
         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount); 
    }
     
    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)
    {
    	float[] result=new float[bw*bh*6*2]; 
    	float sizew=1.0f/bw;//列数
    	float sizeh=1.0f/bh;//行数
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
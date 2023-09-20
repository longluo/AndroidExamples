package com.bn.clp;
import static com.bn.clp.Constant.*;
import java.util.ArrayList;

public class PathUtil 
{
    //产生标准路径
    public static ArrayList<float[]> generatePath()
    {
    	ArrayList<float[]> path=new ArrayList<float[]>();
    	
    	for(int[] grid:PATH)
    	{//加入一个格子的路径
    		//获取此格子的类型编号
    		int type=MAP_ARRAY[grid[0]][grid[1]];
    		float[][] pathTemp=getSubPath(grid[0],grid[1],type);
    		for(float[] fa:pathTemp)
    		{
    			path.add(fa);
    		}
    	}
    	
    	return path;
    }
    
    
    //获取指定行列指定类型的赛道的路线
    public static float[][] getSubPath(int row,int col,int type)
    {
    	float[][] result=null;
    	float startX=col*UNIT_SIZE-UNIT_SIZE/2;
    	float startZ=row*UNIT_SIZE-UNIT_SIZE/2;
    	
    	switch(type)
    	{
    	   case 0:
    		  result=new float[2][2];
    		  
    		  result[0][0]=0;
    		  result[0][1]=UNIT_SIZE/2+3;
    		  
    		  result[1][0]=UNIT_SIZE;
    		  result[1][1]=UNIT_SIZE/2+3;
    	   break;
    	   
    	   case 1:
     		  result=new float[2][2];
      		  
      		  result[0][0]=UNIT_SIZE/2+3;
      		  result[0][1]=UNIT_SIZE;
      		  
      		  result[1][0]=UNIT_SIZE/2+3;
      		  result[1][1]=0;      		   
      	   break;
      	   
    	   case 2:
    		  result=new float[4][2];
     		  
     		  result[0][0]=0;
     		  result[0][1]=UNIT_SIZE/2+3;
     		  
     		  result[1][0]=UNIT_SIZE/3;
     		  result[1][1]=UNIT_SIZE/2-13;
     		  
     		  result[2][0]=(UNIT_SIZE/3)*2;
    		  result[2][1]=UNIT_SIZE/2-13;
    		  
    		  result[3][0]=UNIT_SIZE;
    		  result[3][1]=UNIT_SIZE/2+3;
    	   break;
    	   
    	   case 3:
    		  result=new float[4][2];
       		  
       		  result[0][0]=UNIT_SIZE/2+3;
       		  result[0][1]=UNIT_SIZE;
       		  
       		  result[1][0]=UNIT_SIZE/2-13;
       		  result[1][1]=(UNIT_SIZE/3)*2;
       		  
       		  result[2][0]=UNIT_SIZE/2-13;
     		  result[2][1]=UNIT_SIZE/3;
     		  
     		  result[3][0]=UNIT_SIZE/2+3;
     		  result[3][1]=0;
           break;
    	   
    	   case 4:
    		  result=new float[3][2];
    		   
    		  result[0][0]=UNIT_SIZE/2+3;
     		  result[0][1]=UNIT_SIZE;
     		  
     		  result[1][0]=UNIT_SIZE/2+10;
     		  result[1][1]=UNIT_SIZE/2+10;
     		  
     		  result[2][0]=UNIT_SIZE;
    		  result[2][1]=UNIT_SIZE/2+3;
    		   
    	   break;
    	   
    	   case 5:
    		  result=new float[3][2];
    		   
     		  result[0][0]=0;
      		  result[0][1]=UNIT_SIZE/2+3;
      		  
      		  result[1][0]=UNIT_SIZE/2-10;
      		  result[1][1]=UNIT_SIZE/2+10;
      		  
      		  result[2][0]=UNIT_SIZE/2-3;
     		  result[2][1]=UNIT_SIZE;
           break;
        	   
           case 6:
        	  result=new float[3][2];
    		   
      		  result[0][0]=UNIT_SIZE/2-3;
       		  result[0][1]=0;
       		  
       		  result[1][0]=UNIT_SIZE/2+10;
       		  result[1][1]=UNIT_SIZE/2-10;
       		  
       		  result[2][0]=UNIT_SIZE;
      		  result[2][1]=UNIT_SIZE/2+3;	   
           break;
           
           case 7:
        	  result=new float[3][2];
    		   
       		  result[0][0]=0;
        	  result[0][1]=UNIT_SIZE/2+3;
        		  
        	  result[1][0]=UNIT_SIZE/2-10;
        	  result[1][1]=UNIT_SIZE/2-10;
        		  
        	  result[2][0]=UNIT_SIZE/2+3;
       		  result[2][1]=0;
           break;
            	   
           case 9:
        	  result=new float[2][2];
     		  
     		  result[0][0]=UNIT_SIZE;
     		  result[0][1]=UNIT_SIZE/2-3;
     		  
     		  result[1][0]=0;
     		  result[1][1]=UNIT_SIZE/2-3;	   
           break;
           
           case 10:
        	  result=new float[2][2];
       		  
       		  result[0][0]=UNIT_SIZE/2-3;
       		  result[0][1]=0;
       		  
       		  result[1][0]=UNIT_SIZE/2-3;
       		  result[1][1]=UNIT_SIZE; 	   
           break;
                	   
           case 11:
        	  result=new float[4][2];
      		  
      		  result[0][0]=UNIT_SIZE;
      		  result[0][1]=UNIT_SIZE/2-3;
      		  
      		  result[1][0]=(UNIT_SIZE/3)*2;
      		  result[1][1]=UNIT_SIZE/2+13;
      		  
      		  result[2][0]=UNIT_SIZE/3;
     		  result[2][1]=UNIT_SIZE/2+13;
     		  
     		  result[3][0]=0;
     		  result[3][1]=UNIT_SIZE/2-3;	   
           break;
           
           case 12:
        	  result=new float[4][2];
        		  
        	  result[0][0]=UNIT_SIZE/2-3;
        	  result[0][1]=0;
        		  
        	  result[1][0]=UNIT_SIZE/2+13;
        	  result[1][1]=UNIT_SIZE/3;
        		  
        	  result[2][0]=UNIT_SIZE/2+13; 
      		  result[2][1]=(UNIT_SIZE/3)*2;
      		  
      		  result[3][0]=UNIT_SIZE/2-3;
      		  result[3][1]=UNIT_SIZE;
           break;
                    	   
           case 13:
        	  result=new float[3][2];
    		   
     		  result[0][0]=UNIT_SIZE;
      		  result[0][1]=UNIT_SIZE/2-3;
      		  
      		  result[1][0]=UNIT_SIZE/2+10;
      		  result[1][1]=UNIT_SIZE/2+10;
      		  
      		  result[2][0]=UNIT_SIZE/2-3;
     		  result[2][1]=UNIT_SIZE;		   
           break;
           
           case 14:
        	  result=new float[3][2];
    		   
      		  result[0][0]=UNIT_SIZE/2+3;
       		  result[0][1]=UNIT_SIZE;
       		  
       		  result[1][0]=UNIT_SIZE/2-10;
       		  result[1][1]=UNIT_SIZE/2+10;
       		  
       		  result[2][0]=0;
      		  result[2][1]=UNIT_SIZE/2-3;	   
           break;
                        	   
           case 15:
        	  result=new float[3][2];
    		   
       		  result[0][0]=UNIT_SIZE;
        	  result[0][1]=UNIT_SIZE/2-3;
        		  
        	  result[1][0]=UNIT_SIZE/2+10;
        	  result[1][1]=UNIT_SIZE/2-10;
        		  
        	  result[2][0]=UNIT_SIZE/2+3;
       		  result[2][1]=0;	     		   
           break;
           
           case 16:
        	  result=new float[3][2];
    		   
        	  result[0][0]=UNIT_SIZE/2-3;
         	  result[0][1]=0;
         		  
         	  result[1][0]=UNIT_SIZE/2-10;
         	  result[1][1]=UNIT_SIZE/2-10;
         		  
         	  result[2][0]=0;
        	  result[2][1]=UNIT_SIZE/2-3;
           break;    	   
    	}
    	
    	for(int i=0;i<result.length;i++)
    	{
    		result[i][0]=result[i][0]+startX;
    		result[i][1]=result[i][1]+startZ;
    	}
    	
    	return result;
    }
}

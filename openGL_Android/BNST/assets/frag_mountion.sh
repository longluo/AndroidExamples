precision mediump float;
varying vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据

varying float currY;
uniform sampler2D sTextureGrass;//纹理内容数据（草皮）
uniform sampler2D sTextureRock;//纹理内容数据（岩石）
varying float rTemp;
uniform float b_YZ_StartY;//陆地起始Y
uniform float b_YZ_YSpan;//陆地Y偏移量
uniform int sdflag;//是否为隧道山的标志位

void main()                         
{      
       if(sdflag==0)//表示为隧道山
       {  
           float min=0.4;
			   float max=0.8;
			   
			   float currYRatio=(currY-b_YZ_StartY)/b_YZ_YSpan;
			   
			   vec4 gColor=texture2D(sTextureGrass, vTextureCoord); 
			   vec4 rColor=texture2D(sTextureRock, vTextureCoord); 
			   
			   vec4 finalColor;
			   
			   if(currYRatio<min)
			   {
			      finalColor=gColor;
			   }
			   else if(currYRatio>max)
			   {
			      finalColor=rColor;
			   }
			   else
			   {
			      float rockBL=(currYRatio-min)/(max-min);
			      finalColor=rockBL*rColor+(1.0-rockBL)*gColor;
			   }
            	   
		   if(rTemp<330.0)//距离小于400的不绘制 
		   {
		   		finalColor.a=0.0;
		   }
		   else
		   {
			   //给此片元从纹理中采样出颜色值            
			   
		   }
		   gl_FragColor = finalColor; 
       }
       else if(sdflag==1)//普通
       {
       	   float min=0.3;
		   float max=0.7;
		   
		   float currYRatio=(currY-b_YZ_StartY)/b_YZ_YSpan;
		   
		   vec4 gColor=texture2D(sTextureGrass, vTextureCoord); 
		   vec4 rColor=texture2D(sTextureRock, vTextureCoord); 
		   
		   vec4 finalColor;
		   
		   if(currYRatio<min)
		   {
		      finalColor=gColor;
		   }
		   else if(currYRatio>max)
		   {
		      finalColor=rColor;
		   }
		   else
		   {
		      float rockBL=(currYRatio-min)/(max-min);
		      finalColor=rockBL*rColor+(1.0-rockBL)*gColor;
		   }
		   
		   //给此片元从纹理中采样出颜色值            
		   gl_FragColor = finalColor; 
       }
       
       
   	   
}              
precision mediump float;
varying vec2 vTextureCoord; //接收纹理坐标参数
varying float vertexHeight;//接受顶点的高度值
uniform sampler2D usTextureTuCeng;//纹理内容数据   ----土层
uniform sampler2D usTextureCaoDi;//纹理内容数据-----草地
uniform sampler2D usTextureShiTou;//纹理内容数据-----石头
uniform sampler2D usTextureShanDing;//纹理内容数据-----山顶
uniform float uheight;//最低点高度
uniform float uheight_span;
uniform int uland_flag;//山的标志1为地面上的高山
void main()                         
{           
   //第一块地图的高度
 	float height1=30.0;//第一个的渐变高度
 	float height2=30.0;
 	float height3=10.0;
 	
 	float land1=250.0;//陆地山的过度
	float land2=400.0;

 	vec4 finalColor0=vec4(0.3,0.3,0.3,0.5);//黑色条
   vec4 finalColor1=texture2D(usTextureTuCeng, vTextureCoord);//土层
   vec4 finalColor2=texture2D(usTextureCaoDi, vTextureCoord);   //草地
   vec4 finalColor3=texture2D(usTextureShiTou, vTextureCoord);//石头
   vec4 finalColor4=texture2D(usTextureShanDing, vTextureCoord);//山顶纹理
   if(uland_flag==0)
   {
	   if(abs(vertexHeight)<uheight)
	   {
	      float ratio=abs(vertexHeight)/uheight;
	      finalColor3 *=(1.0-ratio); 
	   	  finalColor0 *=ratio;
	      gl_FragColor =finalColor3+ finalColor0;
	   }
	   else if(abs(vertexHeight)>=uheight&&abs(vertexHeight)<=uheight+height1)//第一个渐变高度
	   {
	   		float ratio=(abs(vertexHeight)-uheight)/height1;
	   		finalColor0 *=(1.0-ratio); 
	   		finalColor1 *=ratio;
	   		gl_FragColor =finalColor1 + finalColor0; 
	   }
	   else if(abs(vertexHeight)>uheight+height1&&abs(vertexHeight)<=uheight_span-height2)
	   {
	   		gl_FragColor =finalColor1;
	   }
	   else if(abs(vertexHeight)>=uheight_span-height2&&abs(vertexHeight)<=uheight_span)
	   {
	   		float ratio=(abs(vertexHeight)-uheight_span+height2)/height2;
	   		finalColor1 *=(1.0-ratio); 
	   		finalColor0 *=ratio;
	   		gl_FragColor =finalColor1 + finalColor0; 
	   }
	   else if(abs(vertexHeight)>=uheight_span&&abs(vertexHeight)<=uheight_span+height3)
	   {
	   		float ratio=(abs(vertexHeight)-uheight_span)/height3;
	   		finalColor0 *=(1.0-ratio); 
	   		finalColor2 *=ratio;
	   		finalColor0.a=0.2;
	   		gl_FragColor =finalColor2 + finalColor0; 
	   }   
	   else
	   {
	    	gl_FragColor = finalColor2; 
	   }  
	}
	else
	{
		
		if(abs(vertexHeight)<land1)
		{
			gl_FragColor = finalColor2; 
		}
		else if(abs(vertexHeight)>=land1&&abs(vertexHeight)<=land2)
		{
			float ratio=(abs(vertexHeight)-land1)/(land2-land1);
	   		finalColor2 *=(1.0-ratio); 
	   		finalColor4 *=ratio;
	   		gl_FragColor =finalColor2 + finalColor4; 
		}
		else
		{
			gl_FragColor = finalColor4; 
		}
	} 
}              
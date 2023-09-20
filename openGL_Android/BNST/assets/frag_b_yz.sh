//具有纹理功能的片元着色器
precision mediump float;
varying vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTextureGrass;//纹理内容数据（草皮）
uniform sampler2D sTextureRock;//纹理内容数据（岩石）
uniform float b_YZ_StartX;//陆地起始X
uniform float b_YZ_XSpan;//陆地X偏移量
varying float currX;//当前片元的陆地X
void main()                         
{           
   float min=0.2;
   float max=0.6;
   
   float currXRatio=(currX-b_YZ_StartX)/b_YZ_XSpan;
   
   vec4 gColor=texture2D(sTextureGrass, vTextureCoord); 
   vec4 rColor=texture2D(sTextureRock, vTextureCoord); 
   
   vec4 finalColor;
   
   if(currXRatio<min)
   {
      finalColor=gColor;
   }
   else if(currXRatio>max)
   {
      finalColor=rColor;
   }
   else
   {
      float rockBL=(currXRatio-min)/(max-min);
      finalColor=rockBL*rColor+(1.0-rockBL)*gColor;
   }  
   
   //给此片元从纹理中采样出颜色值            
   gl_FragColor = finalColor; 
}              
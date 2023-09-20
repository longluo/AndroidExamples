precision mediump float;
varying vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
uniform int uisButtonDown;//按钮是否按下,如果按下,改变其不透明度
void main()                         
{           
   //给此片元从纹理中采样出颜色值            
   vec4 finalColor = texture2D(sTexture, vTextureCoord); 
   if(uisButtonDown==1)//当前按钮按下
   {
   	  gl_FragColor=finalColor*0.5;
   }
   else
   {
   	  gl_FragColor=finalColor;
   }
   
}              
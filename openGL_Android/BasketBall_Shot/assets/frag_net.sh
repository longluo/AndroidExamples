precision mediump float;
varying vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
void main()                         
{           
   //给此片元从纹理中采样出颜色值            
   vec4 cTemp=texture2D(sTexture, vTextureCoord); 
   
   if(cTemp.r==0.0&&cTemp.g==0.0&&cTemp.b==0.0||cTemp.a==0.0)
   {
   		gl_FragColor=vec4(0.3,0.3,0.3,0.0);
   }
   else
   {     
    gl_FragColor = cTemp;
   }
}              
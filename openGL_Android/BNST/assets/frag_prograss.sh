precision mediump float;
varying vec2 vTextureCoord; //接收从顶点着色器过来的参数
uniform sampler2D sTexture;//纹理内容数据
uniform float aPrograss;
varying vec3 vPosition;
void main()                         
{
	if(vPosition.x<aPrograss)
	{
		//给此片元从纹理中采样出颜色值            
   		gl_FragColor = texture2D(sTexture, vTextureCoord); 
	}
	else
	{
		discard;
	}
   
}              
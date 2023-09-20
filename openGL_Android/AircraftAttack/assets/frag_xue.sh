precision mediump float;
varying vec2 vTextureCoord; //接收纹理坐标参数
varying float vertexHeight;//接受顶点的高度值
varying float vertexwhidth;//接受血的最左边位置
uniform sampler2D sTexture;//纹理内容数据
uniform float ublood;
void main()                         
{
  vec4 finalColor=texture2D(sTexture, vTextureCoord);
  if(vertexwhidth<ublood&&vertexwhidth>-97.0&&vertexwhidth<97.0&&vertexHeight>-6.5&&vertexHeight<6.5)
  {
  	gl_FragColor = vec4(0.5,0.17,0.04,1.0);
  }
  else
  {
  	gl_FragColor=finalColor;
  }
}              
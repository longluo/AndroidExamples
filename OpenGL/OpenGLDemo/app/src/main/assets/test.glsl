#version 300 es
precision mediump float;
out vec4 FragColor;
in vec2 vTexture;
uniform samplerExternalOES ourTexture;
void main()
{
    vec4 temColor = texture(ourTexture,vTexture);
    float r = 1.0- temColor.r;
    float g = 1.0- temColor.g;
    float b = 1.0- temColor.b;
    FragColor = vec4(r,g,b,1.0);
}
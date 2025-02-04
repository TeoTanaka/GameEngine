#version 330 core
out vec4 FragColor;
in vec2 texcoord;
void main()
{
    FragColor = vec4(texcoord.x,texcoord.y,0.0,1.0);//vec4(1.0f, 0.5f, 0.2f, 1.0f);
}
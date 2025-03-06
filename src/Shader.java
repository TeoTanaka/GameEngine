import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glCompileShader;

public class Shader {
    private String vertPath, fragPath, vShaderCode, fShaderCode;
    private int vertexShader, fragmentShader, ID;
    public Shader(String vertPath, String fragPath){
        this.vertPath = vertPath;
        this.fragPath = fragPath;
        this.vShaderCode = FileLoader.readFile(vertPath);
        this.fShaderCode = FileLoader.readFile(fragPath);

        vertexShader= glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader,vShaderCode);
        glCompileShader(vertexShader);

        //init fragment shader

        fragmentShader= glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader,fShaderCode);
        glCompileShader(fragmentShader);

        ID = glCreateProgram();
        glAttachShader(ID, vertexShader);
        glAttachShader(ID, fragmentShader);
        glLinkProgram(ID);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void use()
    {
        glUseProgram(ID);
    }
    public void setBool(String name, boolean value)
    {
        //glUniform1i(glGetUniformLocation(ID, name), (int)value);//1 if true
    }
    public void setInt(String name, int value)
    {
        glUniform1i(glGetUniformLocation(ID, name), value);
    }
    public void setFloat(String name, float value)
    {
        glUniform1f(glGetUniformLocation(ID, name), value);
    }

    public void setVec3(String name, Vector3f value)
    {
        glUniform3f(glGetUniformLocation(ID, name),value.x,value.y,value.z);
    }
}


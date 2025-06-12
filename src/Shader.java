import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glCompileShader;

public class Shader {
    private String vertPath, fragPath, vShaderCode, fShaderCode;
    private int vertexShader, fragmentShader, shaderProgram;
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

        shaderProgram = glCreateProgram();// is this the shader program.
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void use()
    {
        glUseProgram(shaderProgram);
    }
    public void setBool(String name, boolean value)
    {
        //glUniform1i(glGetUniformLocation(ID, name), (int)value);//1 if true
    }
    public void setInt(String name, int value)
    {
        glUniform1i(glGetUniformLocation(shaderProgram, name), value);
    }
    public void setFloat(String name, float value)
    {
        glUniform1f(glGetUniformLocation(shaderProgram, name), value);
    }

    public void setVec3(String name, Vector3f value)
    {
        glUniform3f(glGetUniformLocation(shaderProgram, name),value.x,value.y,value.z);
    }

    public void sendLocs(Matrix4f model, Matrix4f view, Matrix4f projection){

        int modelLoc = glGetUniformLocation(shaderProgram, "model");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matrixBuffer = stack.mallocFloat(16); // Allocate temporary buffer
            model.get(matrixBuffer); // Fill buffer with matrix data
            glUniformMatrix4fv(modelLoc, false, matrixBuffer); // Pass to OpenGL
        }
        //sending the view
        int viewLoc = glGetUniformLocation(shaderProgram, "view");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matrixBuffer = stack.mallocFloat(16); // Allocate temporary buffer
            view.get(matrixBuffer); // Fill buffer with matrix data
            glUniformMatrix4fv(viewLoc, false, matrixBuffer); // Pass to OpenGL
        }
        //sending the projection
        int projectionLoc = glGetUniformLocation(shaderProgram, "projection");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matrixBuffer = stack.mallocFloat(16); // Allocate temporary buffer
            projection.get(matrixBuffer); // Fill buffer with matrix data
            glUniformMatrix4fv(projectionLoc, false, matrixBuffer); // Pass to OpenGL
        }

    }
    public void sendModelLoc(Matrix4f model){
        int modelLoc = glGetUniformLocation(shaderProgram, "model");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matrixBuffer = stack.mallocFloat(16); // Allocate temporary buffer
            model.get(matrixBuffer); // Fill buffer with matrix data
            glUniformMatrix4fv(modelLoc, false, matrixBuffer); // Pass to OpenGL
        }
    }
}


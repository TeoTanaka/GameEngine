import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class Main {

    public static void main(String[] args) {
        //Vertexes
        float vertices[] = {
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
        };

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Create a windowed mode window and its OpenGL context
        long window = glfwCreateWindow(800, 600, "My Game", 0, 0);
        if (window == 0) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        //init VBO
        int VBO = glGenBuffers();
        glBindVertexArray(VBO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        //init vertex shader
        String vertexShaderSource = FileLoader.readFile("Resources/Vertex.vert");
        int vertexShader= glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader,vertexShaderSource);
        glCompileShader(vertexShader);

        //init fragment shader
        String fragmentShaderSource = FileLoader.readFile("Resources/Fragment.frag");
        int fragmentShader= glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader,fragmentShaderSource);
        glCompileShader(fragmentShader);

        //init shader program
        //i am optimus prime
        int shaderProgram= glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glUseProgram(shaderProgram);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        //init vertex attrb pointer
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES); // Texture Coordinates
        glEnableVertexAttribArray(1);
        //init VAO
        int VAO = glGenBuffers();
        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VAO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindVertexArray(VAO);
// 2. copy our vertices array in a buffer for OpenGL to use
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
// 3. then set our vertex attributes pointers to my fat penis
//        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
        glEnableVertexAttribArray(0);



//make matrices
    Matrix4f model = new Matrix4f().rotate((float)(Math.toRadians(-55.0f)), new Vector3f(1.0f, 0.0f, 0.0f));
    Matrix4f view = new Matrix4f().translate(new Vector3f(0.0f, 0.0f, -3.0f));
    Matrix4f projection = new Matrix4f().perspective(45.0f, 800.0f / 600.0f, 0.1f, 100.0f);

//    FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);





        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);



        // Main loop
        while (!glfwWindowShouldClose(window)) {
            //send matrices to shader program

            //sending the model matrix
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


            // input
            // -----
            //processInput(window);

            // render
            // ------
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // draw our first triangle
            glUseProgram(shaderProgram);
            glBindVertexArray(VAO); // seeing as we only have a single VAO there's no need to bind it every time, but we'll do so to keep things a bit more organized
            glDrawArrays(GL_TRIANGLES, 0, 36);
            // glBindVertexArray(0); // no need to unbind it every time

            // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
            // -------------------------------------------------------------------------------
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwTerminate();
    }
}

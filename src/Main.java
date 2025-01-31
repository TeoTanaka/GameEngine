import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;

public class Main {

    public static void main(String[] args) {
        //Vertexes
        float vertices[] = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f
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
        int shaderProgram= glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glUseProgram(shaderProgram);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        //init vertex attrb pointer
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        //init VAO
        int VAO = glGenBuffers();
        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindVertexArray(VAO);
// 2. copy our vertices array in a buffer for OpenGL to use
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
// 3. then set our vertex attributes pointers
//        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
        glEnableVertexAttribArray(0);











        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Main loop
        while (!glfwWindowShouldClose(window)) {
            // input
            // -----
            //processInput(window);

            // render
            // ------
            glClear(GL_COLOR_BUFFER_BIT);

            // draw our first triangle
            glUseProgram(shaderProgram);
            glBindVertexArray(VAO); // seeing as we only have a single VAO there's no need to bind it every time, but we'll do so to keep things a bit more organized
            glDrawArrays(GL_TRIANGLES, 0, 3);
            // glBindVertexArray(0); // no need to unbind it every time

            // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
            // -------------------------------------------------------------------------------
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        // Terminate GLFW
        glfwTerminate();
    }
}

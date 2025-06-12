import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.joml.Vector3fKt.cross;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;


public class Main {
    private static boolean firstMouse = true;

    public static Shader lightingShader;
    public static ArrayList<Point> points = new ArrayList<Point>();
    public static ArrayList<Stick> sticks = new ArrayList<Stick>();

    public static ArrayList<Box> boxes = new ArrayList<Box>();

    public static Vector3f lightPos = new Vector3f(1f, 1f, 0f);

    public static float GRAVITY = 10, AIR_RESIST = 1f;



    private static float yaw   = -90.0f;	// yaw is initialized to -90.0 degrees since a yaw of 0.0 results in a direction vector pointing to the right so we initially rotate a bit to the left.
    private static float pitch =  0.0f;

    private static Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);




    public static void main(String[] args) {


        //Vertexes
        float vertices[] = {//draw quadrilaterals (one face at a time)
                //vertex position  |       uv    |   normals
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f,

                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,

                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 1.0f, 1.0f, -1.0f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f,

                0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,

                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f,

                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f
        };
        //camera stuff


        Vector3f cameraPos = new Vector3f(0.0f, 0.0f, 3.0f);

        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);





        float lastX = (float) ( 800.0f / 2.0);
        float lastY = (float) (600.0 / 2.0);
        float fov   =  1.07f;

        Vector3f direction = new Vector3f();
        direction.x = (float) (cos(Math.toRadians(yaw)) * cos(Math.toRadians(pitch)));
        direction.y = (float) sin(Math.toRadians(pitch));
        direction.z = (float) (sin(Math.toRadians(yaw)) * cos(Math.toRadians(pitch)));

        float deltaTime = 0;
        float lastFrame = 0;
        float currentFrame = 0;


        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        long primaryMonitor = GLFW.glfwGetPrimaryMonitor();
        if (primaryMonitor == 0) {
            throw new RuntimeException("Failed to get the primary monitor");
        }

        // Get video mode (contains resolution info)
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(primaryMonitor);
        if (vidmode == null) {
            throw new RuntimeException("Failed to get video mode");
        }

        int screenWidth = vidmode.width();
        int screenHeight = vidmode.height();

        // Create a windowed mode window and its OpenGL context
        long window = glfwCreateWindow(screenWidth, screenHeight, "My Game", glfwGetPrimaryMonitor(), 0);
        if (window == 0) {
            glfwTerminate();
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        //init VBO
        int VBO = glGenBuffers();
        glBindVertexArray(VBO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);


        lightingShader = new Shader("Resources/Cube.vert", "Resources/Cube.frag");
        Shader lightCubeShader = new Shader("Resources/Light_cube.vert", "Resources/light_cube.frag");

        Box box = new Box(2,1,1,1,1,1,new Vector3f(28, 98, 212));
//        for (int i = 0; i < Math.random()*20; i++){
//            boxes.add(new Box((float) (Math.random())*10, (float)(Math.random())*10,(float)(Math.random())*10,(float)(Math.random())*2,(float)(Math.random())*2,(float)(Math.random()*2),new Vector3f((float) (Math.random()*255),(float)(Math.random()*255),(float)(Math.random()*255))));
//        }

        //init vertex attrb pointer
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 5 * Float.BYTES); // normals
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES); // Texture Coordinates
        glEnableVertexAttribArray(2);
        //init VAO


        int VAO = glGenBuffers();
        glBindVertexArray(VAO);
        glBindBuffer(GL_ARRAY_BUFFER, VAO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindVertexArray(VAO);
// 2. copy our vertices array in a buffer for OpenGL to use
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
// 3. then set our vertex attributes pointers
//        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
        glEnableVertexAttribArray(0);

        // init lightCubeVAO (for the lighting box)

        int lightCubeVAO = glGenBuffers();
        glBindVertexArray(lightCubeVAO);
        glBindBuffer(GL_ARRAY_BUFFER, lightCubeVAO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindVertexArray(lightCubeVAO);
// 2. copy our vertices array in a buffer for OpenGL to use
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
// 3. then set our vertex attributes pointers
//        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(float), (void*)0);
        glEnableVertexAttribArray(0);


//make matrices
        Matrix4f model = new Matrix4f().rotate((float) (Math.toRadians(-55.0f)), new Vector3f(0.0f, 1.0f, 0.0f));
        Matrix4f view;
        Matrix4f projection = new Matrix4f().perspective(fov, (float) screenWidth / screenHeight, 0.1f, 100.0f);

//    FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);


        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);

        glfwSwapInterval(1); // turn on v sync to stop screen tearing


        // Main loop
        while (!glfwWindowShouldClose(window)) {
            //send matrices to shader program

            glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glfwSetCursorPosCallback(window, Main::mouse_callback);

            currentFrame = (float) glfwGetTime();
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;
//            System.out.println("deltaTime: " + deltaTime);
            processInput(window,deltaTime,cameraPos,cameraUp);
//            System.out.println("cameraPos: " + cameraPos);
            //setting view matrix
            view = new Matrix4f().lookAt(cameraPos, new Vector3f(cameraPos).add(cameraFront), cameraUp);
            lightingShader.use();
            lightingShader.setVec3("objectColor", new Vector3f(1.0f, 0.5f, 0.31f));
            lightingShader.setVec3("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
            lightingShader.setVec3("lightPos", lightPos);
            lightingShader.sendLocs(model, view, projection);

            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLES, 0, 36);

            for (Box b : boxes){
                b.update();
            }

            box.update();


            Matrix4f light_cube_model = new Matrix4f();
            light_cube_model.translate(lightPos);
            light_cube_model.scale(new Vector3f(.2f));


            lightCubeShader.use();
            lightCubeShader.sendLocs(light_cube_model, view, projection);

            glBindVertexArray(lightCubeVAO);
            glDrawArrays(GL_TRIANGLES, 0, 36);



            // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
            // -------------------------------------------------------------------------------
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwTerminate();
    }

    public static void processInput(long window, float deltaTime, Vector3f cameraPos, Vector3f cameraUp) {
        //glfwSetCursorPosCallback(window, mouse_callback);//make function
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }
        float cameraSpeed = (float) ( (deltaTime) * 1.1);

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            cameraPos.add(new Vector3f(cameraFront).mul(cameraSpeed));
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            cameraPos.add(new Vector3f(cameraUp).mul(cameraSpeed));
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            cameraPos.sub(new Vector3f(cameraUp).mul(cameraSpeed));
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            cameraPos.sub(new Vector3f(cameraFront).mul(cameraSpeed));
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            cameraPos.sub(new Vector3f(cameraFront).cross(cameraUp).normalize(cameraSpeed));
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            cameraPos.add(new Vector3f(cameraFront).cross(cameraUp).normalize(cameraSpeed));
        }
    }
    public static double last_xpos = 0;
    public static double last_ypos = 0;
    public static void mouse_callback(long window, double xpos, double ypos)
    {
        double dx = xpos - last_xpos;
        double dy = ypos - last_ypos;
        last_xpos = xpos;
        last_ypos = ypos;

        if (firstMouse)
        {
            last_xpos = xpos;
            last_ypos = ypos;
            dx = 0;
            dy = 0;
            firstMouse = false;
        }



        float sensitivity = 0.1f;
        dx *= sensitivity;
        dy *= sensitivity;

        yaw   += (float) dx;
        pitch += (float) dy;

        if(pitch > 89.0f)
            pitch = 89.0f;
        if(pitch < -89.0f)
            pitch = -89.0f;

        Vector3f direction = new Vector3f();
        direction.x = (float) (cos(Math.toRadians(yaw)) * cos(Math.toRadians(pitch)));
        direction.y = (float) sin(Math.toRadians(pitch) * -1);
        direction.z = (float) (sin(Math.toRadians(yaw)) * cos(Math.toRadians(pitch)));
        cameraFront = direction.normalize();
    }

    public void framebuffer_size_callback(long window, int width, int height)
    {

        glViewport(0, 0, width, height);
    }
}

//sending the model matrix
//            int modelLoc = glGetUniformLocation(shaderProgram, "model");
//            try (MemoryStack stack = MemoryStack.stackPush()) {
//                FloatBuffer matrixBuffer = stack.mallocFloat(16); // Allocate temporary buffer
//                model.get(matrixBuffer); // Fill buffer with matrix data
//                glUniformMatrix4fv(modelLoc, false, matrixBuffer); // Pass to OpenGL
//            }
//            //sending the view
//            int viewLoc = glGetUniformLocation(shaderProgram, "view");
//            try (MemoryStack stack = MemoryStack.stackPush()) {
//                FloatBuffer matrixBuffer = stack.mallocFloat(16); // Allocate temporary buffer
//                view.get(matrixBuffer); // Fill buffer with matrix data
//                glUniformMatrix4fv(viewLoc, false, matrixBuffer); // Pass to OpenGL
//            }
//            //sending the projection
//            int projectionLoc = glGetUniformLocation(shaderProgram, "projection");
//            try (MemoryStack stack = MemoryStack.stackPush()) {
//                FloatBuffer matrixBuffer = stack.mallocFloat(16); // Allocate temporary buffer
//                projection.get(matrixBuffer); // Fill buffer with matrix data
//                glUniformMatrix4fv(projectionLoc, false, matrixBuffer); // Pass to OpenGL
//            }
//
//
//            // input
//            // -----
//            //processInput(window);
//
//            // render
//            // ------
//            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//            // draw our first triangle
//            glUseProgram(shaderProgram);
//            glBindVertexArray(VAO); // seeing as we only have a single VAO there's no need to bind it every time, but we'll do so to keep things a bit more organized
//            glDrawArrays(GL_TRIANGLES, 0, 36);
//            // glBindVertexArray(0); // no need to unbind it every time


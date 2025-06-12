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
import java.util.Arrays;


public class Box {

    private float length, width, height;

    private Point reference;
    private final Vector3f pos = new Vector3f();
    private final Vector3f color;

    private Point[] points;
    private Stick[] sticks;
    private float[] vertices;


    private float halfL;
    private float halfW;
    private float halfH;

    private int VAO;
    private int VBO;

    private boolean initialized = false;

    private final int[][] facePoints = {
            {4, 5, 6, 7}, // top
            {0, 1, 2, 3}, // bottom
            {3, 2, 6, 7}, // front
            {0, 1, 5, 4}, // back
            {0, 3, 7, 4}, // left
            {1, 2, 6, 5}  // right
    };

    public Box(float x, float y, float z, float length, float width, float height, Vector3f color) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.color = color;
        color.x/=255;
        color.y/=255;
        color.z/=255;
        pos.set(x, y, z);

        halfL = length / 2f;
        halfW = width / 2f;
        halfH = height / 2f;

        genPoints();
        genSticks();
        generateVertices();
        sendPhysicsObjs();
    }

    public void update() {
        render();
//        pos.x = reference.getPos().x+halfL;
//        pos.y = reference.getPos().y+halfH;
//        pos.z = reference.getPos().z+halfW;
    }

    public void render() {
        if (!initialized) {
            initBufferObjs();
            initialized = true;
        }

        generateVertices(); // if the box is dynamic

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);



        Matrix4f model = new Matrix4f()
                .translate(new Vector3f(pos).mul(.01f))
                .scale(.1f);

        //Main.lightingShader.setVec3("objectColor", new Vector3f(52, 235, 235));
        Main.lightingShader.setVec3("lightColor", color);
        Main.lightingShader.setVec3("lightPos", Main.lightPos);
        Main.lightingShader.sendModelLoc(model);

        glBindVertexArray(VAO);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);
    }

    private void genPoints() {


        reference = new Point(pos.x-halfL, pos.y-halfH, pos.z-halfW);

        points = new Point[]{
                reference,                         // 0 bottom back  left
                new Point( +halfL+pos.x, -halfH+pos.y, -halfW+pos.z), // 1 bottom back  right
                new Point( +halfL+pos.x, -halfH+pos.y,  +halfW+pos.z), // 2 bottom front right
                new Point(-halfL+pos.x, -halfH+pos.y,  +halfW+pos.z), // 3 bottom front left
                new Point(-halfL+pos.x, +halfH+pos.y, -halfW+pos.z), // 4 top    back  left
                new Point(+halfL+pos.x,  +halfH+pos.y, -halfW+pos.z), // 5 top    back  right
                new Point(+halfL+pos.x,  +halfH+pos.y, +halfW+pos.z), // 6 top    front right
                new Point(-halfL+pos.x,  +halfH+pos.y,  +halfW+pos.z)
        };

        System.out.println(points[3].getPos().y);
    }

//    points = new Point[]{
//        reference,                         // 0 bottom back  left
//                new Point( +halfL, -halfH, -halfW), // 1 bottom back  right
//                new Point( +halfL, -halfH,  +halfW), // 2 bottom front right
//                new Point(-halfL, -halfH,  +halfW), // 3 bottom front left
//                new Point(-halfL, +halfH, -halfW), // 4 top    back  left
//                new Point(+halfL,  +halfH, -halfW), // 5 top    back  right
//                new Point(+halfL,  +halfH, +halfW), // 6 top    front right
//                new Point(-halfL,  +halfH,  +halfW)
//    };



    private void generateVertices() {
        vertices = new float[36 * 6]; // 6 floats per vertex, 36 vertices
        int index = 0;
        for (int[] face : facePoints) {
            float[] faceData = generateFace(
                    points[face[0]].getPos(),
                    points[face[1]].getPos(),
                    points[face[2]].getPos(),
                    points[face[3]].getPos()
            );
            System.arraycopy(faceData, 0, vertices, index, faceData.length);
            index += faceData.length;
        }
    }

    private void genSticks(){
        sticks = new Stick[]{
                // === 12 Edges of the cube ===
                new Stick(points[0], points[1]),
                new Stick(points[1], points[2]),
                new Stick(points[2], points[3]),
                new Stick(points[3], points[0]),

                new Stick(points[4], points[5]),
                new Stick(points[5], points[6]),
                new Stick(points[6], points[7]),
                new Stick(points[7], points[4]),

                new Stick(points[0], points[4]),
                new Stick(points[1], points[5]),
                new Stick(points[2], points[6]),
                new Stick(points[3], points[7]),

//                // === 2 Diagonals per face ===
//                // Bottom face (0,1,2,3)
//                new Stick(points[0], points[2]),
//                new Stick(points[1], points[3]),
//
//                // Top face (4,5,6,7)
//                new Stick(points[4], points[6]),
//                new Stick(points[5], points[7]),
//
//                // Front face (3,2,6,7)
//                new Stick(points[3], points[6]),
//                new Stick(points[2], points[7]),
//
//                // Back face (0,1,5,4)
//                new Stick(points[0], points[5]),
//                new Stick(points[1], points[4]),
//
//                // Left face (0,3,7,4)
//                new Stick(points[0], points[7]),
//                new Stick(points[3], points[4]),
//
//                // Right face (1,2,6,5)
//                new Stick(points[1], points[6]),
//                new Stick(points[2], points[5]),

                // === 4 Internal diagonal corner-to-corner sticks ===
                new Stick(points[0], points[6]),
                new Stick(points[1], points[7]),
                new Stick(points[2], points[4]),
                new Stick(points[3], points[5])
        };
    }

    private void sendPhysicsObjs(){
        Main.points.addAll(Arrays.asList(points));
        Main.sticks.addAll(Arrays.asList(sticks));
    }

    private float[] generateFace(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4) {
        Vector3f edge1 = new Vector3f(p2).sub(p1);
        Vector3f edge2 = new Vector3f(p4).sub(p1);
        Vector3f normal = edge1.cross(edge2).normalize();

        return new float[]{
                // Triangle 1
                p1.x, p1.y, p1.z, normal.x, normal.y, normal.z,
                p2.x, p2.y, p2.z, normal.x, normal.y, normal.z,
                p3.x, p3.y, p3.z, normal.x, normal.y, normal.z,
                // Triangle 2
                p1.x, p1.y, p1.z, normal.x, normal.y, normal.z,
                p3.x, p3.y, p3.z, normal.x, normal.y, normal.z,
                p4.x, p4.y, p4.z, normal.x, normal.y, normal.z
        };
    }

    private void initBufferObjs() {
        if (vertices == null) {
            throw new IllegalStateException("Vertices must be generated before initializing buffer objects.");
        }

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0L);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
}
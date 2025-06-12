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


public class Box {

    private float length, width, height;
    private final Vector3f pos = new Vector3f();
    private final Vector3f color;

    private Point[] points;
    private float[] vertices;

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

        genPoints();
        generateVertices();
    }

    public void update() {
        render();
    }

    public void render() {
        if (!initialized) {
            initBufferObjs();
            initialized = true;
        }

        generateVertices(); // if the box is dynamic

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        Matrix4f model = new Matrix4f().translate(pos);
        //Main.lightingShader.setVec3("objectColor", new Vector3f(52, 235, 235));
        Main.lightingShader.setVec3("lightColor", color);
        Main.lightingShader.setVec3("lightPos", Main.lightPos);
        Main.lightingShader.sendModelLoc(model);

        glBindVertexArray(VAO);
        glDrawArrays(GL_TRIANGLES, 0, 36);
        glBindVertexArray(0);
    }

    private void genPoints() {
        float halfL = length / 2f;
        float halfW = width / 2f;
        float halfH = height / 2f;

        points = new Point[]{
                new Point(-halfL, -halfH, -halfW), // 0
                new Point( halfL, -halfH, -halfW), // 1
                new Point( halfL, -halfH,  halfW), // 2
                new Point(-halfL, -halfH,  halfW), // 3
                new Point(-halfL,  halfH, -halfW), // 4
                new Point( halfL,  halfH, -halfW), // 5
                new Point( halfL,  halfH,  halfW), // 6
                new Point(-halfL,  halfH,  halfW)  // 7
        };
    }

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
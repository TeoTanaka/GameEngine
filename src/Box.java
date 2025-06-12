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
    private Vector3f pos;

    private Vector3f[] points;
    private float[] vertices;
    private final int[][] facePoints = new int[][]{
            {0,1,2,3},//top
            {4,5,6,7},//bottom
            {2,3,6,7},//front
            {0,1,4,5},//back
            {0,3,4,7},//left
            {1,2,5,6}//right
    };//  which points make up each face


    public Box(Vector3f pos, float length, float width, float height){
        this.length = length;
        this.width = width;
        this.height = height;
        this.pos = pos;
        genPoints();
    }

    public void update(){}

    public void render(){
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0); // vertices
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 5 * Float.BYTES); // normals
        glEnableVertexAttribArray(1);

        generateVertices();//sets the vertices to the shape of the cube based on the points


        int VBO = glGenBuffers();
        glBindVertexArray(VBO);
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);


    }
    public void genPoints(){
        float halfL = length / 2f;
        float halfW = width / 2f;
        float halfH = height / 2f;


        points = new Vector3f[] {
                new Vector3f(-halfL, -halfH, -halfW), // 0: bottom-back-left
                new Vector3f( halfL, -halfH, -halfW), // 1: bottom-back-right
                new Vector3f( halfL, -halfH,  halfW), // 2: bottom-front-right
                new Vector3f(-halfL, -halfH,  halfW), // 3: bottom-front-left
                new Vector3f(-halfL,  halfH, -halfW), // 4: top-back-left
                new Vector3f( halfL,  halfH, -halfW), // 5: top-back-right
                new Vector3f( halfL,  halfH,  halfW), // 6: top-front-right
                new Vector3f(-halfL,  halfH,  halfW)  // 7: top-front-left
        };
    }

    public void generateVertices(){//generates the mesh for the whole shape
        vertices = new float[216];
        int index = 0;
        for (int i = 0; i < 6;i++){


            float[] face = generateFace(
                    points[facePoints[i][0]],//getting the index of each point that makes up
                    points[facePoints[i][1]],//the face and then taking the point at that index from
                    points[facePoints[i][2]],//points.
                    points[facePoints[i][3]]
            );

            System.arraycopy(face, 0, vertices, index, 36);
            index += 36;
        }
    }

    public float[] generateFace(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4){
        // Two edges
        Vector3f edge1 = new Vector3f(p2).sub(p1);//issue may be choosing the wrong points
        Vector3f edge2 = new Vector3f(p4).sub(p1);

// Normal using cross product (right-hand rule)
        Vector3f normal = edge1.cross(edge2).normalize();

        return new float[] {
                // Triangle 1
                p1.x, p1.y, p1.z,  normal.x, normal.y, normal.z,
                p2.x, p2.y, p2.z,  normal.x, normal.y, normal.z,
                p3.x, p3.y, p3.z,  normal.x, normal.y, normal.z,

                // Triangle 2
                p1.x, p1.y, p1.z,  normal.x, normal.y, normal.z,
                p3.x, p3.y, p3.z,  normal.x, normal.y, normal.z,
                p4.x, p4.y, p4.z,  normal.x, normal.y, normal.z
        };
    }



}

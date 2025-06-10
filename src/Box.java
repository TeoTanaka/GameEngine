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


public class Box {

    private float length, width, height;
    private Vector3f pos;

    private Vector3f[] points;



    public Box(Vector3f pos, float length, float width, float height){
        this.length = length;
        this.width = width;
        this.height = height;
        this.pos = pos;
        genPoints();
    }

    public void update(){}

    public void render(){

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

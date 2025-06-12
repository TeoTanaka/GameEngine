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


public class Stick {

    private Point p1,p2;
    private float length,elasticity,mass;


    public Stick(Point p1, Point p2){
        this.p1 = p1;
        this.p2 = p2;
        this.length = p1.getPos().distance(p2.getPos());
        this.elasticity = 1;
        this.mass = 1;

    }

    public void update(){
        restrainPoints();
    }
    
    public void restrainPoints(){
        Vector3f pos1 = new Vector3f(p1.getPos());  // safe copies
        Vector3f pos2 = new Vector3f(p2.getPos());



        Vector3f delta = new Vector3f(pos2).sub(pos1);     // vector from p1 to p2
        float dist = new Vector3f(delta).length();     // current distance
        float diff = (dist - length) / dist; // normalized difference
        System.out.println("delta "+pos1);

        Vector3f offset = new Vector3f(delta).mul(0.5f * diff); // move each point half the correction

        p1.setPos(pos1.add(offset));  // p1 moves toward p2
        p2.setPos(pos2.sub(offset));  // p2 moves toward p1
        //System.out.println();

    }

    public void addSelf(){
        Main.sticks.add(this);
    }

}


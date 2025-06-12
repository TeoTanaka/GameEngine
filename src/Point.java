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


public class Point {
    private Vector3f pos = new Vector3f(), oldPos = new Vector3f();
    private Vector3f force = new Vector3f();
    private float mass,elasticity;

    public Point(Vector3f pos, Vector3f oldPos, float mass, float elasticity){
        this.pos = pos;
        this.oldPos = oldPos;
        this.mass = mass;
        this.elasticity = elasticity;
        Main.points.add(this);
    }
    public Point(Vector3f pos, Vector3f oldPos){
        this.pos = pos;
        this.oldPos = oldPos;
        this.mass = 1;
        this.elasticity = 1;

    }
    public Point(Vector3f pos){
        this.pos = pos;
        this.oldPos = new Vector3f(pos);
        this.mass = 1;
        this.elasticity = 1;

    }
    public Point(float x, float y, float z){
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        this.oldPos = new Vector3f(pos);
        this.mass = 1;
        this.elasticity = 1;

    }

    public void update(float dt){
//        constrain();
        updatePos(dt);

        //System.out.println("x:"+ pos.x+" y:"+pos.y);

    }


    public void updatePos(float dt){
        Vector3f acc = new Vector3f();
        acc.x = force.x / mass * Main.AIR_RESIST;
        acc.y = force.y / mass * Main.AIR_RESIST;
        acc.z = force.z / mass * Main.AIR_RESIST;

        Vector3f vel = new Vector3f();
        vel.x = pos.x- oldPos.x;
        vel.y = pos.y- oldPos.y ;
        vel.z = pos.z- oldPos.z ;

        force.x = 0;
        force.y = Main.GRAVITY;//apply forces
        force.z = 0;


        oldPos.x = pos.x;
        oldPos.y = pos.y;
        oldPos.z = pos.z;

        pos.x += vel.x + acc.x *dt*dt;
        pos.y += vel.y + acc.y *dt*dt;
        pos.z += vel.z + acc.z *dt*dt;
//        constrain();
    }
//    public void constrain(){
//        Vector3f vel = new Vector3f();
//        vel.x = pos.x- oldPos.x;
//        vel.y = pos.y- oldPos.y;
//
//        if (pos.y > Main.app.screenH){
//
//            pos.y = Main.app.screenH;
//            oldPos.y = pos.y+vel.y*elasticity;
//
//        }else if ( pos.y < 0){
//            pos.y = 0;
//            oldPos.y = pos.y+vel.y*elasticity;
//        }
//        if (pos.x > Main.app.screenW){
//            pos.x = Main.app.screenW;
//            oldPos.x = pos.x+vel.x*elasticity;
//
//        }else if (pos.x < 0){
//            pos.x = 0;
//            oldPos.x = pos.x+vel.x*elasticity;
//        }
//    }
//    public void boxCollision(){
//        for (Box b : Main.boxes){
//            Vector3f boxPos = b.getPos();
//            Vector3f boxDim = b.getDim();
//            if (pos.x >= boxPos.x &&         // right of the left edge AND
//                    pos.x <= boxDim.x + boxDim.x &&    // left of the right edge AND
//                    pos.y >= boxDim.y &&         // below the top AND
//                    pos.y <= boxDim.y + boxDim.y){  // above the bottom
//                System.out.println("colliding");
//            }
//
//        }
//    }

    public Vector3f getPos(){
        return pos;
    }

    public void setPos(Vector3f p){
        pos = p;
    }

    public void addSelf(){
        Main.points.add(this);
    }

    public void addPos(Vector3f n){
        pos.add(n);
    }


}

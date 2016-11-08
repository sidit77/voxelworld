package com.github.sidit77.voxelworld;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

//A class that represents a Camera and is capable a creating a various transform matrices based on this camera

public class Camera {

    private static final Vector3f up = new Vector3f(0,1,0);

    private Matrix4f camera;

    private float aspect;
    private float fov;

    private Vector3f position;
    private Quaternionf rotation;

    public Camera(float fov, float aspect) {
        position = new Vector3f();
        camera = new Matrix4f();
        rotation = new Quaternionf().lookRotate(new Vector3f(0,0,1f), up);
        this.fov = fov;
        this.aspect = aspect;
    }

    public void update(){

    }

    public Matrix4f getCameraMatrix(){
        camera.identity();
        camera.perspective((float)Math.toRadians(fov), aspect, 0.01f, 100000.0f);
        camera.rotate(rotation);
        camera.translate(new Vector3f(position).negate());

        return camera;
    }

    public Matrix4f getViewMatrix(){
        camera.identity();
        camera.perspective((float)Math.toRadians(fov), aspect, 0.01f, 100000.0f);
        camera.rotate(rotation);
        return camera;
    }

    public float getFOV(){
        return fov;
    }

    public void setFOV(float fov){
        this.fov = fov;
    }

    public float getAspect(){
        return aspect;
    }

    public void setAspect(float aspect){
        this.aspect = aspect;
    }

    public Vector3f getPosition(){
        return position;
    }

    public void setPosition(Vector3f pos){
        pos.negate(this.position);
    }

    public void setPosition(float x, float y, float z){
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void addPosition(Vector3f pos){
        this.position.add(pos);
    }

    public void addPosition(float x, float y, float z){
        this.position.x += x;
        this.position.y += y;
        this.position.z += z;
    }

    public Vector3f getForward(){
        return new Vector3f(0,0,-1).rotate(new Quaternionf(rotation).conjugate());
    }

    public Vector3f getBack(){
        return new Vector3f(0,0, 1).rotate(new Quaternionf(rotation).conjugate());
    }

    public Vector3f getRight(){
        return new Vector3f(1,0,0).rotate(new Quaternionf(rotation).conjugate());
    }

    public Vector3f getLeft(){
        return new Vector3f(-1,0,0).rotate(new Quaternionf(rotation).conjugate());
    }

    public void setRotation(float yaw, float pitch){
        rotation = new Quaternionf().lookRotate(new Vector3f(0,0,1f), up);
        addRotation(yaw, pitch);
    }

    public void addRotation(float yaw, float pitch){
        rotation.rotateY(yaw);
        rotation.rotateAxis(pitch, getLeft());
    }

}


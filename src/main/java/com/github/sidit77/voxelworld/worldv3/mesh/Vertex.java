package com.github.sidit77.voxelworld.worldv3.mesh;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Objects;

public class Vertex {

    public static final int size = 6;

    private Vector3f position;
    private Vector2f tc;
    private int textureid;

    public Vertex(Vertex base, Vector3f newPos){
        this(newPos, base.tc, base.textureid);
        this.getPosition().add(base.getPosition());
    }

    public Vertex(Vector3f position, Vector2f tc, int textureid){
        this.position = position;
        this.tc = tc;
        this.textureid = textureid;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getTextureCoordinates(){
        return tc;
    }

    public int getTextureID(){
        return textureid;
    }

    public float[] getElements(){
        return new float[]{position.x,position.y,position.z,tc.x,tc.y,(float)textureid};
    }

    public static FloatBuffer store(Vertex[] vertices, FloatBuffer dest){
        if(dest == null || dest.capacity() < vertices.length * Vertex.size){
            dest = BufferUtils.createFloatBuffer(vertices.length * Vertex.size);
        }
        dest.clear();
        for(Vertex v : vertices){
            dest.put(v.getElements());
        }
        dest.flip();
        return dest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return textureid == vertex.textureid &&
                Objects.equals(position, vertex.position) &&
                Objects.equals(tc, vertex.tc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, tc, textureid);
    }
}

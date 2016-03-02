package com.github.sidit77.voxelworld.worldv3.mesh;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class ChunkMesh {

    private HashMap<Vertex, Integer> index;
    private ArrayList<Vertex> vertices;
    private ArrayList<Integer> indices;

    public ChunkMesh(){
        index = new HashMap<>();
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
    }

    public void addQuad(Vertex v1, Vertex v2, Vertex v3, Vertex v4){
        addVertex(v1);
        addVertex(v2);
        addVertex(v3);

        addVertex(v3);
        addVertex(v4);
        addVertex(v1);
    }

    public void addTriangle(Vertex v1, Vertex v2, Vertex v3){
        addVertex(v1);
        addVertex(v2);
        addVertex(v3);
    }

    private void addVertex(Vertex v){
        if(!index.containsKey(v)){
            indices.add(vertices.size());
            index.put(v, vertices.size());
            vertices.add(v);
        }else{
            indices.add(index.get(v));
        }
    }

    public FloatBuffer getVertexBuffer(){
        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.size() * Vertex.size);
        vertices.forEach(vertex -> fb.put(vertex.getElements()));
        fb.flip();
        return fb;
    }

    public IntBuffer getIndexBuffer(){
        IntBuffer ib = BufferUtils.createIntBuffer(indices.size());
        indices.forEach(ib::put);
        ib.flip();
        return ib;
    }
}

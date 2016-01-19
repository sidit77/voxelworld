package com.github.sidit77.voxelworld.world.dualcontouring;

import com.github.sidit77.voxelworld.world.Mesh;
import com.github.sidit77.voxelworld.world.Sampler;
import com.github.sidit77.voxelworld.world.dualcontouring.nodes.OctreeEndNode;
import com.github.sidit77.voxelworld.world.dualcontouring.nodes.OctreeInternalNode;
import com.github.sidit77.voxelworld.world.dualcontouring.nodes.OctreeLeafNode;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Octree {

    private final OctreeNode root;

    private static long time;
    private static long time2;

    public Octree(OctreeNode root){
        this.root = root;
    }

    public static Octree createOctree(Vector3f pos, int size){
        long t = System.nanoTime();
        OctreeNode node = createOctreeNode(pos, size);
        t = System.nanoTime() - t;
        System.out.println((float)time /t);
        System.out.println((float)time2/t);
        time = 0;
        time2 = 0;
        return new Octree(node);
    }

    private static OctreeNode createOctreeNode(Vector3f pos, int size){
        if(size == 1){
            return createOctreeLeaf(pos);
        }

        OctreeInternalNode node = new OctreeInternalNode(size, pos);
        int childSize = size/2;

        for(int i = 0; i < 8; i++){
            node.setChild(i, createOctreeNode(new Vector3f(vertexPositions[i]).mul(childSize).add(pos), childSize));
        }


        long t = System.nanoTime();
        OctreeNode on = node.isEmpty() ? new OctreeEndNode(size, pos) : node;
        time2 += (System.nanoTime() - t);
        return on;
    }

    private static OctreeNode createOctreeLeaf(Vector3f pos){

        long t = System.nanoTime();

        int corners = 0;
        for (int i = 0; i < 8; i++){
            Vector3f cornerPos = new Vector3f(pos).add(vertexPositions[i]);
            float density = Sampler.sample(cornerPos);
            int material = density > 0.f ? 1 : 0;
            corners |= (material << i);
        }

        OctreeNode on;
        if (corners == 0 || corners == 255){
            on = new OctreeEndNode(1, pos);
        }else{
            on = new OctreeLeafNode(pos, corners, 1);
        }

        time += (System.nanoTime() - t);

        return on;
    }
//
//    public ArrayList<Vertex> getVertices(){
//        ArrayList<Vertex> vertices = new ArrayList<>();
//        root.addVertexToList(vertices);
//        return vertices;
//    }


    public Mesh getMesh(){
        ArrayList<Vector3f[]> tris = new ArrayList<>();
        root.addTrianglesToList(tris);
        System.out.println(tris.size());
        return new Mesh(tris.toArray(new Vector3f[tris.size()][3]));
    }

    public Octree simplify(float threshold){
        root.simplify(threshold, 0);
        return this;
    }

    private static final Vector3f[] vertexPositions = {
            new Vector3f( -0.5f, -0.5f, -0.5f ),
            new Vector3f( -0.5f, -0.5f,  0.5f ),
            new Vector3f( -0.5f,  0.5f, -0.5f ),
            new Vector3f( -0.5f,  0.5f,  0.5f ),
            new Vector3f(  0.5f, -0.5f, -0.5f ),
            new Vector3f(  0.5f, -0.5f,  0.5f ),
            new Vector3f(  0.5f,  0.5f, -0.5f ),
            new Vector3f(  0.5f,  0.5f,  0.5f ),
    };

}

package com.github.sidit77.voxelworld.world.dualcontouring;


import org.joml.Vector3f;

import java.util.ArrayList;

public abstract class OctreeNode {

    private int size;
    private Vector3f pos;

    public OctreeNode(int size, Vector3f pos){
        this.size = size;
        this.pos = pos;
    }

    public int getSize() {
        return size;
    }

    public Vector3f getPosition(){
        return pos;
    }

    public abstract void addTrianglesToList(ArrayList<Vector3f[]> tris);

    public abstract boolean isEmpty();

    public abstract boolean isCollapsible();

    public abstract OctreeNode simplify(float threshold, int dir);

}

package com.github.sidit77.voxelworld.world.dualcontouring.nodes;

import com.github.sidit77.voxelworld.world.dualcontouring.OctreeNode;
import org.joml.Vector3f;

import java.util.ArrayList;

public class OctreeEndNode extends OctreeNode {

    public OctreeEndNode(int size,Vector3f pos) {
        super(size, pos);
    }

    @Override
    public void addTrianglesToList(ArrayList<Vector3f[]> tris) {

    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public OctreeNode simplify(float threshold, int dir) {
        return this;
    }

    @Override
    public boolean isCollapsible() {
        return true;
    }

}

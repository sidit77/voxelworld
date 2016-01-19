package com.github.sidit77.voxelworld.world.dualcontouring.nodes;

import com.github.sidit77.voxelworld.world.Sampler;
import com.github.sidit77.voxelworld.world.dualcontouring.OctreeNode;
import com.github.sidit77.voxelworld.world.dualcontouring.QEF;
import org.joml.Vector3f;

import java.util.ArrayList;

public class OctreeLeafNode extends OctreeNode {

    //private Vertex vertex;
    private int index;
    private int corners;

    private Vector3f position;
    private Vector3f normal;

    public OctreeLeafNode(Vector3f pos, Vector3f normal, int corners, int size){
        super(size, pos);
        this.index = -1;
        this.corners = corners;
        //vertex = new Vertex(pos, normal);
    }

    public OctreeLeafNode(Vector3f pos, int corners, int size) {
        super(size, pos);
        index = -1;
        this.corners = corners;

        QEF qef = new QEF();

        for (int i = 0; i < 12; i++){
            int c1 = edgevmap[i][0];
            int c2 = edgevmap[i][1];

            int m1 = (corners >> c1) & 1;
            int m2 = (corners >> c2) & 1;
            if (m1 == m2)
                continue;

            Vector3f v1 = new Vector3f(vertexPositions[c1]).mul(size).add(pos);
            Vector3f v2 = new Vector3f(vertexPositions[c2]).mul(size).add(pos);

            float d1 = Sampler.sample(new Vector3f(v1));
            float d2 = Sampler.sample(new Vector3f(v2));

            Vector3f intersection = v1.lerp(v2, (-d1) / (d2 - d1));
            Vector3f normal = getNormal(intersection);

            qef.Add(intersection, normal);
        }

        position = qef.Solve();
        normal = qef.Normal();
        //vertex = new Vertex(qef.Solve(), qef.Normal());
    }

    public int getCorners() {
        return corners;
    }

    public Vector3f getVertexPosition() {
        return position;
    }

    public Vector3f getVertexNormal() {
        return normal;
    }

    public int getIndex(){
        return index;
    }


    @Override
    public void addTrianglesToList(ArrayList<Vector3f[]> tris) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }


    private Vector3f getNormal(Vector3f pos){
        float H = 0.001f;
        float dx = Sampler.sample(new Vector3f(H, 0.f, 0.f).add(pos)) - Sampler.sample(new Vector3f(-H, 0.f, 0.f).add(pos));
        float dy = Sampler.sample(new Vector3f(0.f, H, 0.f).add(pos)) - Sampler.sample(new Vector3f(0.f, -H, 0.f).add(pos));
        float dz = Sampler.sample(new Vector3f(0.f, 0.f, H).add(pos)) - Sampler.sample(new Vector3f(0.f, 0.f, -H).add(pos));

        return new Vector3f(dx, dy, dz).normalize().negate();
    }

    private static final int[][] edgevmap = {
            {0,4},{1,5},{2,6},{3,7},	// x-axis
            {0,2},{1,3},{4,6},{5,7},	// y-axis
            {0,1},{2,3},{4,5},{6,7}		// z-axis
    };

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

    @Override
    public OctreeNode simplify(float threshold, int dir) {
        return this;
    }

    @Override
    public boolean isCollapsible() {
        return true;
    }

}

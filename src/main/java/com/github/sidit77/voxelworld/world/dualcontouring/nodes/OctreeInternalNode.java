package com.github.sidit77.voxelworld.world.dualcontouring.nodes;

import com.github.sidit77.voxelworld.world.Sampler;
import com.github.sidit77.voxelworld.world.dualcontouring.OctreeNode;
import com.github.sidit77.voxelworld.world.dualcontouring.QEF;
import org.joml.Vector3f;

import java.util.ArrayList;

public class OctreeInternalNode extends OctreeNode {
    private final OctreeNode[] children;

    public OctreeInternalNode(int size, Vector3f pos) {
        super(size, pos);
        children = new OctreeNode[8];
    }

    @Override
    public void addTrianglesToList(ArrayList<Vector3f[]> tris) {
        for(OctreeNode on : children){
            on.addTrianglesToList(tris);
        }

        for (int i = 0; i < 12; i++){
            OctreeNode[] faceNodes = new OctreeNode[2];
            int c[] = { cellProcFaceMask[i][0], cellProcFaceMask[i][1] };

            faceNodes[0] = getChild(c[0]);
            faceNodes[1] = getChild(c[1]);

            ContourFaceProc(faceNodes, cellProcFaceMask[i][2], tris);
        }

        for (int i = 0; i < 6; i++){
            OctreeNode[] edgeNodes = new OctreeNode[4];
            int[] c = {
                    cellProcEdgeMask[i][0],
                    cellProcEdgeMask[i][1],
                    cellProcEdgeMask[i][2],
                    cellProcEdgeMask[i][3],
            };

            for (int j = 0; j < 4; j++){
                edgeNodes[j] = getChild(c[j]);
            }

            ContourEdgeProc(edgeNodes, cellProcEdgeMask[i][4], tris);
        }
    }

    public void setChild(int nr, OctreeNode child){
        children[nr] = child;
    }

    public OctreeNode getChild(int nr){
        return children[nr];
    }

    @Override
    public boolean isCollapsible() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        for(OctreeNode oe : children){
            if(!oe.isEmpty()){
                return false;
            }
        }
        return true;
    }


    @Override
    public OctreeNode simplify(float threshold, int dir) {

        for(int i = 0; i < 8; i++){
            children[i] = children[i].simplify(threshold, i);
        }

        int[] signs = { -1, -1, -1, -1, -1, -1, -1, -1 };
        int mid_sign = -1;
        QEF qef = new QEF();

        for(int i = 0; i < 8; i++){
            if(children[i].isEmpty())
                continue;

            if(!children[i].isCollapsible())
                return this;

            OctreeLeafNode ol = ((OctreeLeafNode)children[i]);

            qef.Add(ol.getVertexPosition(), ol.getVertexNormal());
            mid_sign = (ol.getCorners() >> (7 - i)) & 1;
            signs[i] = (ol.getCorners() >> i) & 1;
        }

        Vector3f pos = qef.Solve();

        if(qef.error > threshold)
            return this;

//        int corners = 0;
//        for(int i = 0; i < 8; i++){
//            if (signs[i] == -1)
//		corners |= mid_sign << i;
//            else
//		corners |= signs[i] << i;
//	}

        int corners = 0;
        for (int i = 0; i < 8; i++){
            Vector3f cornerPos = new Vector3f(vertexPositions[i]).mul(getSize()).add(getPosition());
            float density = Sampler.sample(cornerPos);
            int material = density > 0.f ? 1 : 0;
            corners |= (material << i);
        }

        return ((corners == 0 || corners == 255) ? new OctreeEndNode(getSize(), getPosition()) : new OctreeLeafNode(getPosition(), corners, getSize()));
    }

    private void ContourFaceProc(OctreeNode[] node, int dir,ArrayList<Vector3f[]> indexBuffer){
        if (node[0].isEmpty() || node[1].isEmpty()){
            return;
        }

        if (node[0] instanceof OctreeInternalNode || node[1] instanceof OctreeInternalNode){
            for (int i = 0; i < 4; i++){
                OctreeNode[] faceNodes = new OctreeNode[2];
                int[] c = {
                        faceProcFaceMask[dir][i][0],
                        faceProcFaceMask[dir][i][1],
                };

                for (int j = 0; j < 2; j++){
                    if(node[j] instanceof OctreeInternalNode){
                        faceNodes[j] = ((OctreeInternalNode)node[j]).getChild(c[j]);
                    }else{
                        faceNodes[j] = node[j];
                    }
                }

                ContourFaceProc(faceNodes, faceProcFaceMask[dir][i][2], indexBuffer);
            }

            int[][] orders ={
                    { 0, 0, 1, 1 },
                    { 0, 1, 0, 1 },
            };
            for (int i = 0; i < 4; i++){
                OctreeNode[] edgeNodes = new OctreeNode[4];
                int[] c = {
                        faceProcEdgeMask[dir][i][1],
                        faceProcEdgeMask[dir][i][2],
                        faceProcEdgeMask[dir][i][3],
                        faceProcEdgeMask[dir][i][4],
                };

                int[] order = orders[faceProcEdgeMask[dir][i][0]];
                for (int j = 0; j < 4; j++){
                    if (node[order[j]] instanceof OctreeLeafNode){
                        edgeNodes[j] = node[order[j]];
                    }else{
                        edgeNodes[j] = ((OctreeInternalNode)node[order[j]]).getChild(c[j]);
                    }
                }

                ContourEdgeProc(edgeNodes, faceProcEdgeMask[dir][i][5], indexBuffer);
            }
        }
    }

    private void ContourEdgeProc(OctreeNode[] node, int dir, ArrayList<Vector3f[]> indexBuffer){
        if(node[0].isEmpty() || node[1].isEmpty() || node[2].isEmpty() || node[3].isEmpty()){
            return;
        }

        if( node[0] instanceof OctreeInternalNode ||
                node[1] instanceof OctreeInternalNode ||
                node[2] instanceof OctreeInternalNode ||
                node[3] instanceof OctreeInternalNode){

            for (int i = 0; i < 2; i++){
                OctreeNode[] edgeNodes = new OctreeNode[4];
                int[] c = {
                        edgeProcEdgeMask[dir][i][0],
                        edgeProcEdgeMask[dir][i][1],
                        edgeProcEdgeMask[dir][i][2],
                        edgeProcEdgeMask[dir][i][3],
                };

                for (int j = 0; j < 4; j++){
                    if (node[j] instanceof OctreeLeafNode){
                        edgeNodes[j] = node[j];
                    }else{
                        edgeNodes[j] = ((OctreeInternalNode)node[j]).getChild(c[j]);
                    }
                }

                ContourEdgeProc(edgeNodes, edgeProcEdgeMask[dir][i][4], indexBuffer);
            }

        }else{
            ContourProcessEdge(node, dir, indexBuffer);
        }
    }

    private void ContourProcessEdge(OctreeNode[] node, int dir, ArrayList<Vector3f[]> indexBuffer){
        int minSize = 1000000;		// arbitrary big number
        int minIndex = 0;
        Vector3f[] indices = { null, null, null, null };
        boolean flip = false;
        boolean[] signChange = { false, false, false, false };

        for (int i = 0; i < 4; i++){
            int edge = processEdgeMask[dir][i];
            int c1 = edgevmap[edge][0];
            int c2 = edgevmap[edge][1];

            int m1 = (((OctreeLeafNode)node[i]).getCorners() >> c1) & 1;
            int m2 = (((OctreeLeafNode)node[i]).getCorners() >> c2) & 1;

            if (node[i].getSize() < minSize){
                minSize = node[i].getSize();
                minIndex = i;
                flip = m1 != 0;
            }

            indices[i] = ((OctreeLeafNode)node[i]).getVertexPosition();

            signChange[i] = (m1 == 0 && m2 != 0) || (m1 != 0 && m2 == 0);
        }

        if (signChange[minIndex]){
            if(flip){
                indexBuffer.add(new Vector3f[]{indices[0],indices[1],indices[3]});
                indexBuffer.add(new Vector3f[]{indices[0],indices[3],indices[2]});
            }else{
                indexBuffer.add(new Vector3f[]{indices[3],indices[1],indices[0]});
                indexBuffer.add(new Vector3f[]{indices[3],indices[0],indices[2]});
            }
        }
    }

    private static final int[][] edgevmap = {
            {0,4},{1,5},{2,6},{3,7},	// x-axis
            {0,2},{1,3},{4,6},{5,7},	// y-axis
            {0,1},{2,3},{4,5},{6,7}		// z-axis
    };

    private static final int edgemask[] = { 5, 3, 6 };

    private static final int faceMap[][] = {{4, 8, 5, 9}, {6, 10, 7, 11},{0, 8, 1, 10},{2, 9, 3, 11},{0, 4, 2, 6},{1, 5, 3, 7}} ;
    private static final int cellProcFaceMask[][] = {{0,4,0},{1,5,0},{2,6,0},{3,7,0},{0,2,1},{4,6,1},{1,3,1},{5,7,1},{0,1,2},{2,3,2},{4,5,2},{6,7,2}} ;
    private static final int cellProcEdgeMask[][] = {{0,1,2,3,0},{4,5,6,7,0},{0,4,1,5,1},{2,6,3,7,1},{0,2,4,6,2},{1,3,5,7,2}} ;

    private static final int faceProcFaceMask[][][] = {
            {{4,0,0},{5,1,0},{6,2,0},{7,3,0}},
            {{2,0,1},{6,4,1},{3,1,1},{7,5,1}},
            {{1,0,2},{3,2,2},{5,4,2},{7,6,2}}
    };

    private static final int faceProcEdgeMask[][][] = {
            {{1,4,0,5,1,1},{1,6,2,7,3,1},{0,4,6,0,2,2},{0,5,7,1,3,2}},
            {{0,2,3,0,1,0},{0,6,7,4,5,0},{1,2,0,6,4,2},{1,3,1,7,5,2}},
            {{1,1,0,3,2,0},{1,5,4,7,6,0},{0,1,5,0,4,1},{0,3,7,2,6,1}}
    };

    private static final int edgeProcEdgeMask[][][] = {
            {{3,2,1,0,0},{7,6,5,4,0}},
            {{5,1,4,0,1},{7,3,6,2,1}},
            {{6,4,2,0,2},{7,5,3,1,2}},
    };

    private static final int processEdgeMask[][] = {{3,2,1,0},{7,5,6,4},{11,10,9,8}} ;

    private static final Vector3f[] vertexPositions = {
            new Vector3f( 0, 0, 0 ).sub(0.5f, 0.5f, 0.5f),
            new Vector3f( 0, 0, 1 ).sub(0.5f, 0.5f, 0.5f),
            new Vector3f( 0, 1, 0 ).sub(0.5f, 0.5f, 0.5f),
            new Vector3f( 0, 1, 1 ).sub(0.5f, 0.5f, 0.5f),
            new Vector3f( 1, 0, 0 ).sub(0.5f, 0.5f, 0.5f),
            new Vector3f( 1, 0, 1 ).sub(0.5f, 0.5f, 0.5f),
            new Vector3f( 1, 1, 0 ).sub(0.5f, 0.5f, 0.5f),
            new Vector3f( 1, 1, 1 ).sub(0.5f, 0.5f, 0.5f),
    };

}

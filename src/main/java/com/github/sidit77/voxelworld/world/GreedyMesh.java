package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GreedyMesh {

    private int materialid;

    private ArrayList<Face> faces;

    public GreedyMesh(int materialid){
        this.materialid = materialid;
        faces = new ArrayList<>();
    }

    public void addFace(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3, boolean backface){
        Face f = null;
        if(!backface){
            f = new Face(v0,v1,v2,v3);
        }else{
            f = new Face(v2,v1,v0,v3);
        }

        faces.add(f);
    }

    public void addDataToList(List<Float> vertices, List<Integer> indices){
        HashMap<Vector3f, Integer> indexes = new HashMap<>();
        int index = vertices.size() / 4;
        for(Face f : faces){
            for(Vector3f v : f.getVertices()){
                if(indexes.containsKey(v)){
                    indices.add(indexes.get(v));
                }else{
                    indexes.put(v, index);
                    vertices.add(v.x);
                    vertices.add(v.y);
                    vertices.add(v.z);
                    vertices.add((float)materialid);
                    indices.add(index);
                    index++;
                }
            }
        }
    }

    private class Face{
        private Vector3f[] v;
        public Face(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3){
            v = new Vector3f[]{v0,v1,v2,v3};
        }
        public Vector3f[] getVertices(){
            return v;
        }
    }



}

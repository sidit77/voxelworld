package com.github.sidit77.voxelworld.world.dualcontouring;

import org.joml.Vector3f;

import java.util.ArrayList;

public class QEF {

    private final ArrayList<Vector3f> intersections;
    private final ArrayList<Vector3f> normals;
    private final Vector3f mass_point;
    private final Vector3f averageNormal;
    public float error;

    public QEF(){
        intersections = new ArrayList<>();
        normals = new ArrayList<>();
        mass_point = new Vector3f(0,0,0);
        averageNormal = new Vector3f(0,0,0);
    }

    public void Add(Vector3f p, Vector3f n){
        intersections.add(p);
        normals.add(n);

        mass_point.add(p);
        averageNormal.add(n);
    }

    private float GetDistanceSquared(Vector3f x){
        float total = 0;
        for (int i = 0; i < intersections.size(); i++){
            Vector3f d = new Vector3f(x).sub(intersections.get(i));//#################################### new ###########
            float dot = normals.get(i).x * d.x + normals.get(i).y * d.y + normals.get(i).z * d.z;
            total += dot * dot;
        }
        return total;
    }

    public Vector3f Solve(){
        if (intersections.isEmpty()){
            this.error = 100000;
            return new Vector3f(0,0,0);
        }
        Vector3f x = new Vector3f(mass_point).div(intersections.size());
        error = GetDistanceSquared(x);
        return x;
    }

    public Vector3f Normal(){
        if (normals.isEmpty()){
            return new Vector3f(0,0,0);
        }
        return new Vector3f(averageNormal).normalize();
    }

}

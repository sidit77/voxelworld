package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;

public class Chunk {

    public static final int size = 64;

    private float[][][] density;
    private Vector3f position;

    public Chunk(Vector3f position){
        this.position = position;
        density = new float[size+1][size+1][size+1];
        for(int x = 0; x <= size; x++){
            for(int y = 0; y <= size; y++){
                for(int z = 0; z <= size; z++){
                    density[x][y][z] = Sampler.sample(new Vector3f(x,y,z));
                }
            }
        }
    }

    public float getDensity(int x, int y, int z){
        return density[x][y][z];
    }

    public Vector3f getPosition() {
        return position;
    }
}

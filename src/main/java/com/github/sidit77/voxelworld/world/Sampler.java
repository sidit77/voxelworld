package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;

public class Sampler {

    //private static final HashMap<Vector3f, Float> cache = new HashMap<>();

    public static float sample(Vector3f pos){
        return -(pos.length() - 20);

//        float result;
//
//        result = (float)SimplexNoise.noise(pos.x/100, pos.z/100) / 1.5f;
//
//        pos.add(new Vector3f((float)SimplexNoise.noise(pos.x/20, pos.y/20, pos.z/20)*8));
//
//        pos.add(0,-50,0);
//        pos.div(100);
//        pos.div(1, 0.5f, 1);
//
//        result -= -pos.y;
//        result += (float)SimplexNoise.noise(pos.x*2+5,pos.y*2+3,pos.z*2+0.6) * 0.20f;
//        result *= -1;
//
//        return result;

        //return -pos.y;

        //if(pos.y > -30 && pos.y < 30 && pos.z > -30 && pos.z < 30 && pos.x > -30 && pos.x < 30){//(pos.x > -10 && pos.x < 10 && pos.y > -10 && pos.y < 10 && pos.z > -10 && pos.z < 10){
        //    return (float)SimplexNoise.noise(pos.x/20,pos.y/20,pos.z/20);
        //}
        //return -1;

        //return -pos.y+ ((float)Math.sin(pos.x/20)+1)*10;// + 20; //
        //return -pos.y + (float)SimplexNoise.noise(pos.x/20, pos.z/20)*10;
    }

    private static float[][][] cache;

    public static float sample2(Vector3f pos){

        if(cache == null){
            cache = new float[130][130][130];
            for(int x = 0; x < cache.length; x++){
                for(int y = 0; y < cache[0].length; y++){
                    for(int z = 0; z < cache[0][0].length; z++){
                        cache[x][y][z] = sample(new Vector3f(x,y,z));
                    }
                }
            }
        }

        return cache[(int)pos.x][(int)pos.y][(int)pos.z];
    }
}

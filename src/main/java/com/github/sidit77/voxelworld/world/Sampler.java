package com.github.sidit77.voxelworld.world;

import com.github.sidit77.voxelworld.SimplexNoise;
import org.joml.Vector3f;

public class Sampler {

    public static int sample(Vector3f pos){
        //return -(pos.sub(4,4,4).length() - 30);

        Vector3f pos1 = new Vector3f(pos);
        float result = (float) SimplexNoise.noise(pos1.x/100, pos1.z/100) / 1.5f;

        pos1.add(new Vector3f((float)SimplexNoise.noise(pos1.x/20, pos1.y/20, pos1.z/20)*8));

        pos1.add(0,-50,0);
        pos1.div(100);
        pos1.div(1, 0.5f, 1);

        result -= -pos1.y;
        result += (float)SimplexNoise.noise(pos1.x*2+5,pos1.y*2+3,pos1.z*2+0.6) * 0.20f;


        Vector3f pos2 = new Vector3f(pos);
        float result2 = (float) SimplexNoise.noise(pos2.x/50, pos2.z/50)/2;

        ///pos2.add(new Vector3f((float)SimplexNoise.noise(pos2.x/20, pos2.y/20, pos2.z/20)*8));

        pos2.add(0,-50,0);
        pos2.div(75);
        pos2.div(1, 0.2f, 1);

        result2 -= -pos2.y;
        result2 += (float)SimplexNoise.noise(pos2.x,pos2.y,pos2.z) * 0.20f;
        //result *= -1;

        //int material = pos.y > 40 ? 0 : 1;
        //return new Material(-Math.min(result,), material);//;//;// );
        return union(id(result2, 1), id(result, 2));//union(union(result < 0 ? 1 : 0, (pos.sub(4,40,4).length() - 30) < 0 ? 2 : 0), (pos.sub(20,0,20).length() - 30) < 0 ? 3 : 0);//Material.union(new Material(-result, 0), new Material(-(pos.sub(4,40,4).length() - 30),1));

        //return -pos.y;

        //if(pos.y > -30 && pos.y < 30 && pos.z > -30 && pos.z < 30 && pos.x > -30 && pos.x < 30){//(pos.x > -10 && pos.x < 10 && pos.y > -10 && pos.y < 10 && pos.z > -10 && pos.z < 10){
        //    return (float)SimplexNoise.noise(pos.x/20,pos.y/20,pos.z/20);
        //}
        //return -1;

        //return -pos.y+ ((float)Math.sin(pos.x/20)+1)*10;// + 20; //
        //return -pos.y + (float)SimplexNoise.noise(pos.x/20, pos.z/20)*10;
    }


    private static int union(int m1, int m2){
        if(m2 != 0){
            return m2;
        }
        return m1;
    }

    private static int id(float f, int id){
        return f < 0 ? id : 0;
    }

}

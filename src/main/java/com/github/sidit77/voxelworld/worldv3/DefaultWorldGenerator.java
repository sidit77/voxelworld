package com.github.sidit77.voxelworld.worldv3;

import com.github.sidit77.voxelworld.SimplexNoise;
import com.github.sidit77.voxelworld.worldv3.blocks.Blocks;

import java.util.Random;

public class DefaultWorldGenerator implements IWorldGenerator{

    Random random = new Random();

    @Override
    public Block generate(int x, int y, int z, World world) {
        if(2 + SimplexNoise.noise((float)x / 100, (float)y / 100, (float)z / 100) - (float)(y) / 30 > 0){
            return Blocks.STONE;
        }
        return Blocks.AIR;
    }

    @Override
    public void postgenerate(int x, int y, int z, Block current, World world) {

        if(current == Blocks.AIR && world.getBlock(x, y - 1, z) == Blocks.STONE){
            if(random.nextFloat() < 0.005f){
                int h =  2 + random.nextInt(3);
                for(int i = 0; i < h; i++){
                    world.setBlock(x, y + 1 + i, z, Blocks.WOOD);
                }
                for(int i = -2; i <= 2; i++){
                    for(int j = -2; j <= 2; j++){
                        if(j != 0 || i != 0){
                            world.setBlock(x + i, y + h, z + j, Blocks.LEAF);
                        }
                    }
                }
                for(int i = -1; i <= 1; i++){
                    for(int j = -1; j <= 1; j++){
                        world.setBlock(x + i, y + h + 1, z + j, Blocks.LEAF);
                    }
                }
                world.setBlock(x, y + h + 2, z, Blocks.LEAF);

            }
            world.setBlock(x,y,z,Blocks.GRASS);
        }

        //if(world.getBlock(x,y,z) == Blocks.AIR && world.getBlock(x,y-1,z) == Blocks.STONE){
        //    world.setBlock(x,y,z, Blocks.GRASS);
        //    if(random.nextInt(40) == 0){
        //        for(int i = 0; i < 2+random.nextInt(3); i++){
        //            world.setBlock(x,y+1+i,z, random.nextInt(2) == 0 ? Blocks.BRICKS : Blocks.STONEBRICKS);
        //        }
        //    }else if(random.nextInt(250) == 0){
        //        //chunk.setBlock(x,y+1,z, Blocks.TORCH);
        //    }
        //}
    }
}

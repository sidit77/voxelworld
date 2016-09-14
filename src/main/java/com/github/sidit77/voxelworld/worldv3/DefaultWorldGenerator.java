package com.github.sidit77.voxelworld.worldv3;

import com.github.sidit77.voxelworld.worldv3.blocks.Blocks;

import java.util.Random;

public class DefaultWorldGenerator implements IWorldGenerator{

    Random random = new Random();

    @Override
    public void pregenerate(int x, int y, int z, Chunk chunk) {

    }

    @Override
    public void generate(int x, int y, int z, Chunk chunk) {
        if(y < 6+(Math.sin((float)x/3) + Math.sin((float)z/3)+2) * 2.5f){//(x*y*z-1000)
            chunk.setBlock(x,y,z, Blocks.STONE);
        }
    }

    @Override
    public void postgenerate(int x, int y, int z, Chunk chunk) {
        if(chunk.getBlock(x,y,z) == Blocks.AIR && chunk.getBlock(x,y-1,z) == Blocks.STONE){
            chunk.setBlock(x,y,z, Blocks.GRASS);
            if(random.nextInt(40) == 0){
                for(int i = 0; i < 2+random.nextInt(3); i++){
                    chunk.setBlock(x,y+1+i,z, random.nextInt(2) == 0 ? Blocks.BRICKS : Blocks.STONEBRICKS);
                }
            }else if(random.nextInt(250) == 0){
                chunk.setBlock(x,y+1,z, Blocks.TORCH);
            }
        }
    }
}

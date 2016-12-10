package com.github.sidit77.voxelworld.world;

import com.github.sidit77.voxelworld.SimplexNoise;
import com.github.sidit77.voxelworld.world.blocks.Blocks;

import java.util.Random;

public class DefaultWorldGenerator implements IWorldGenerator{

    Random random;
    SimplexNoise simplex;

    public DefaultWorldGenerator(long seed){
        random = new Random(seed);
        simplex = new SimplexNoise(random);
    }

    @Override
    public Block generate(int x, int y, int z, World world) {

        if(32 + 50 * simplex.noise((float)x / 300, (float)y / 80, (float)z / 300, simplex.noise((float) x / 30, (float)z / 30)/ 10) + 30 * simplex.noise((float)x / 100, (float)y / 40, (float)z / 100) - (y-16) > 0){
            return random.nextFloat() < 0.8f ? Blocks.STONE : Blocks.COBBLESTONE;
        }

        if(40 + 20 * simplex.noise((float)x / 300, (float)y / 300, (float)z / 300) - (y-16) > 0){
            return Blocks.DIRT;
        }

        return Blocks.AIR;
    }

    @Override
    public void postgenerate(int x, int y, int z, Block current, World world) {

        if(current == Blocks.DIRT && world.getBlock(x, y + 1, z) == Blocks.AIR){
            world.setBlock(x,y,z, Blocks.GRASS);

            if(random.nextFloat() < 0.005f){

                generateTree(x,y+1,z, world);

            }else if(random.nextFloat() < 0.004f){
                world.setBlock(x,y+1,z, Blocks.PINEAPPLE);
            }

        }

    }

    private void generateTree(int x, int y, int z, World world){

        int h = 3 + random.nextInt(2);
        int hh = 0;

        for(int h1 = 0; h1 < h; h1++){
            world.setBlock(x, y + h1, z, Blocks.WOOD);
        }

        for(int i = 0; i <= random.nextInt(4); i++){
            float dx = (random.nextFloat() - 0.5f);
            float dz = (random.nextFloat() - 0.5f);
            for(int h1 = 0; h1 < 3 + random.nextInt(3); h1++){
                world.setBlock(x + Math.round(dx * h1), y + h + h1, z + Math.round(dz * h1), Blocks.WOOD);
                hh = Math.max(hh, h1+2);
            }
        }

        hh += h;

        int rx = 3 + random.nextInt(3);
        int rz = 3 + random.nextInt(3);

        while(h < hh){

            for(int i = 0; i < 20; i++){
                double angle = 2 * Math.PI * random.nextFloat();
                double dist = 4 * random.nextFloat();
                if(world.getBlock(x + (int)Math.round(Math.sin(angle) * dist), y + h, z + (int)Math.round(Math.cos(angle) * dist)) == Blocks.AIR){
                    world.setBlock(x + (int)Math.round(Math.sin(angle) * dist), y + h, z + (int)Math.round(Math.cos(angle) * dist), Blocks.LEAF);
                }
            }

            for(int _rx = -rx; _rx <= rx; _rx++){
                for(int _rz = -rz; _rz <= rz; _rz++){
                    if(world.getBlock(x + _rx, y + h, z + _rz) == Blocks.AIR && 3 < random.nextInt(Math.abs(_rx) + 1)&& 3 > random.nextInt(Math.abs(_rz) + 1)){
                        world.setBlock(x + _rx, y + h, z + _rz, Blocks.LEAF);
                    }
                }
            }
            h++;
        }

    }

}


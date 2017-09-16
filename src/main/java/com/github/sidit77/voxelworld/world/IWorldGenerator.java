package com.github.sidit77.voxelworld.world;

public interface IWorldGenerator {

    Block generate(int x, int y, int z, World world);

    void postgenerate(int x, int y, int z, Block current, World world);

}

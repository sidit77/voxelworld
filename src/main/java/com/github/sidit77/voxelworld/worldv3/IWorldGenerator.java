package com.github.sidit77.voxelworld.worldv3;

public interface IWorldGenerator {

    void pregenerate(int x, int y, int z, Chunk chunk);
    void generate(int x, int y, int z, Chunk chunk);
    void postgenerate(int x, int y, int z, Chunk chunk);

}

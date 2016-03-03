package com.github.sidit77.voxelworld.worldv3.blocks;

import com.github.sidit77.voxelworld.worldv3.Block;
import com.github.sidit77.voxelworld.worldv3.Direction;
import com.github.sidit77.voxelworld.worldv3.mesh.ChunkMesh;

public class BlockAir extends Block {

    public BlockAir() {
        super(-1, "Air");
    }

    @Override
    public boolean isSolid(Direction direction) {
        return false;
    }

    @Override
    public void addToChunkMesh(ChunkMesh mesh, int x, int y, int z, Block[] neightbors, int[] lightlevels) {

    }

    @Override
    public boolean hasHitbox() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}

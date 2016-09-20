package com.github.sidit77.voxelworld.worldv3.blocks;

import com.github.sidit77.voxelworld.worldv3.Block;
import com.github.sidit77.voxelworld.worldv3.Direction;

public class BlockGrass extends Block {
    public BlockGrass() {
        super(3, "Grass");
        setTexture(Direction.UP, 2);
        setTexture(Direction.DOWN, 1);
    }
}

package com.github.sidit77.voxelworld.worldv3.blocks;

import com.github.sidit77.voxelworld.worldv3.Block;
import com.github.sidit77.voxelworld.worldv3.Direction;

public class BlockLog extends Block{

    public BlockLog() {
        super(8, "Wood");
        setTexture(Direction.UP, 9);
        setTexture(Direction.DOWN, 9);
    }

}

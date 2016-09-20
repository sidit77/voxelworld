package com.github.sidit77.voxelworld.worldv3.blocks;

import com.github.sidit77.voxelworld.worldv3.Block;
import com.github.sidit77.voxelworld.worldv3.Direction;

public class BlockAir extends Block {

    public BlockAir() {
        super(-1, "Air");
    }

    @Override
    public boolean isSolid(Direction direction) {
        return false;
    }

    @Override
    public boolean hasHitbox() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isUnrendered(){
        return true;
    }
}

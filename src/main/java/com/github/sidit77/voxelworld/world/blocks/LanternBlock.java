package com.github.sidit77.voxelworld.world.blocks;

import com.github.sidit77.voxelworld.world.Block;
import com.github.sidit77.voxelworld.world.ILightSource;

public class LanternBlock extends Block implements ILightSource{

    public LanternBlock() {
        super(14, "Lantern");
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    public byte getLightLevel() {
        return 10;
    }
}

package com.github.sidit77.voxelworld.world.blocks;

import com.github.sidit77.voxelworld.world.Direction;
import com.github.sidit77.voxelworld.world.ILightSource;

public class TorchBlock extends ObjBlock implements ILightSource{

    public TorchBlock() {
        super(11, "Torch", "assets/model/torch.obj");
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
    public byte getLightLevel() {
        return 16;
    }
}

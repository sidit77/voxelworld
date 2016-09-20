package com.github.sidit77.voxelworld.worldv3.blocks;

import com.github.sidit77.voxelworld.worldv3.Block;
import com.github.sidit77.voxelworld.worldv3.Direction;
import com.github.sidit77.voxelworld.worldv3.ISpecialRenderer;

import java.util.ArrayList;

public class BlockTorch extends Block implements ISpecialRenderer {

    public BlockTorch() {
        super(0, "Torch");

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
    public void addMeshToList(int x, int y, int z, ArrayList<Float> list) {
        for(int i = 0; i < faces.length; i += 8){
            list.add(x + faces[i + 0]);
            list.add(y + faces[i + 1]);
            list.add(z + faces[i + 2]);

            list.add(faces[i + 3]);
            list.add(faces[i + 4]);

            list.add(faces[i + 5]);
            list.add(faces[i + 6]);
            list.add(faces[i + 7]);

        }
    }

    float[] faces = {
         -0.25f, -0.25f,  0.25f, 0.00f, 0.25f, 0, 0, 1,
          0.25f, -0.25f,  0.25f, 0.25f, 0.25f, 0, 0, 1,
          0.25f,  0.25f,  0.25f, 0.25f, 0.00f, 0, 0, 1,
          0.25f,  0.25f,  0.25f, 0.25f, 0.00f, 0, 0, 1,
         -0.25f,  0.25f,  0.25f, 0.00f, 0.00f, 0, 0, 1,
         -0.25f, -0.25f,  0.25f, 0.00f, 0.25f, 0, 0, 1,

          0.25f, -0.25f,  0.25f, 0.00f, 0.25f, 1, 0, 0,
          0.25f, -0.25f, -0.25f, 0.25f, 0.25f, 1, 0, 0,
          0.25f,  0.25f, -0.25f, 0.25f, 0.00f, 1, 0, 0,
          0.25f,  0.25f, -0.25f, 0.25f, 0.00f, 1, 0, 0,
          0.25f,  0.25f,  0.25f, 0.00f, 0.00f, 1, 0, 0,
          0.25f, -0.25f,  0.25f, 0.00f, 0.25f, 1, 0, 0,

          0.25f, -0.25f, -0.25f, 0.00f, 0.25f, 0, 0,-1,
         -0.25f, -0.25f, -0.25f, 0.25f, 0.25f, 0, 0,-1,
         -0.25f,  0.25f, -0.25f, 0.25f, 0.00f, 0, 0,-1,
         -0.25f,  0.25f, -0.25f, 0.25f, 0.00f, 0, 0,-1,
          0.25f,  0.25f, -0.25f, 0.00f, 0.00f, 0, 0,-1,
          0.25f, -0.25f, -0.25f, 0.00f, 0.25f, 0, 0,-1,

         -0.25f, -0.25f, -0.25f, 0.00f, 0.25f,-1, 0, 0,
         -0.25f, -0.25f,  0.25f, 0.25f, 0.25f,-1, 0, 0,
         -0.25f,  0.25f,  0.25f, 0.25f, 0.00f,-1, 0, 0,
         -0.25f,  0.25f,  0.25f, 0.25f, 0.00f,-1, 0, 0,
         -0.25f,  0.25f, -0.25f, 0.00f, 0.00f,-1, 0, 0,
         -0.25f, -0.25f, -0.25f, 0.00f, 0.25f,-1, 0, 0,

         -0.25f,  0.25f,  0.25f, 0.00f, 0.25f, 0, 1, 0,
          0.25f,  0.25f,  0.25f, 0.25f, 0.25f, 0, 1, 0,
          0.25f,  0.25f, -0.25f, 0.25f, 0.00f, 0, 1, 0,
          0.25f,  0.25f, -0.25f, 0.25f, 0.00f, 0, 1, 0,
         -0.25f,  0.25f, -0.25f, 0.00f, 0.00f, 0, 1, 0,
         -0.25f,  0.25f,  0.25f, 0.00f, 0.25f, 0, 1, 0,

          0.25f, -0.25f,  0.25f, 0.00f, 0.25f, 0,-1, 0,
         -0.25f, -0.25f,  0.25f, 0.25f, 0.25f, 0,-1, 0,
         -0.25f, -0.25f, -0.25f, 0.25f, 0.00f, 0,-1, 0,
         -0.25f, -0.25f, -0.25f, 0.25f, 0.00f, 0,-1, 0,
          0.25f, -0.25f, -0.25f, 0.00f, 0.00f, 0,-1, 0,
          0.25f, -0.25f,  0.25f, 0.00f, 0.25f, 0,-1, 0

    };

}

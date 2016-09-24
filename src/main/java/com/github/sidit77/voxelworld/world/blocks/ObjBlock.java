package com.github.sidit77.voxelworld.world.blocks;

import com.github.sidit77.voxelworld.ObjLoader;
import com.github.sidit77.voxelworld.world.Block;
import com.github.sidit77.voxelworld.world.Direction;
import com.github.sidit77.voxelworld.world.ISpecialRenderer;

import java.util.ArrayList;

public class ObjBlock extends Block implements ISpecialRenderer {

    private float[] faces;

    public ObjBlock(int texture, String name, String path) {
        super(texture, name);

       faces = ObjLoader.loadMesh(path);
    }

    @Override
    public void addMeshToList(int x, int y, int z, ArrayList<Float> list) {
        for(int i = 0; i < faces.length; i += 8){
            list.add(x + faces[i + 0]);
            list.add(y + faces[i + 1]);
            list.add(z + faces[i + 2]);

            list.add(getTextureID(Direction.UP) % 4 * 0.25f + faces[i + 3] * 0.25f);
            list.add(getTextureID(Direction.UP) / 4 * 0.25f + faces[i + 4] * 0.25f);

            list.add(faces[i + 5]);
            list.add(faces[i + 6]);
            list.add(faces[i + 7]);

            list.add(1.0f); //TODO CHANGE
            list.add(1.0f);
        }
    }

}

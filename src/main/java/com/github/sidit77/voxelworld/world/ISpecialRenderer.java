package com.github.sidit77.voxelworld.world;

import java.util.ArrayList;

public interface ISpecialRenderer {

    void addMeshToList(int x, int y, int z, byte brightness, ArrayList<Float> list);

}

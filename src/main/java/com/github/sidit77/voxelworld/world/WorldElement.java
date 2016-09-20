package com.github.sidit77.voxelworld.world;


public abstract class WorldElement {

    public abstract void setBlock(int x, int y, int z, Block b);

    public abstract Block getBlock(int x, int y, int z);

    public abstract void setTop(WorldElement top);

    public abstract void setBottom(WorldElement bottom);

    public abstract void setLeft(WorldElement left);

    public abstract void setRight(WorldElement right);

    public abstract void setFront(WorldElement front);

    public abstract void setBack(WorldElement back);

    public abstract float[] getMesh();

    public abstract void generateMesh(int x, int y, int z);

    public abstract void needUpdate();

    public abstract boolean updateRequired();

}

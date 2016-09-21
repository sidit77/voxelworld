package com.github.sidit77.voxelworld.world;


public abstract class WorldElement {

    private static int nextId = 0;

    private int id;

    public WorldElement(){
        id = nextId;
        nextId++;
    }

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

    public abstract byte getLightLevel(int x, int y, int z);

    public abstract void setLightLevel(int x, int y, int z, byte lvl);

    public abstract void setLighting(boolean enabled);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldElement that = (WorldElement) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

}

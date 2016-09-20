package com.github.sidit77.voxelworld.worldv3;

public class BorderChunk extends WorldElement {

    private BorderBlock borderBlock = new BorderBlock();

    @Override
    public void setBlock(int x, int y, int z, Block b) {

    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return borderBlock;
    }

    @Override
    public void setTop(WorldElement top) {

    }

    @Override
    public void setBottom(WorldElement bottom) {

    }

    @Override
    public void setLeft(WorldElement left) {

    }

    @Override
    public void setRight(WorldElement right) {

    }

    @Override
    public void setFront(WorldElement front) {

    }

    @Override
    public void setBack(WorldElement back) {

    }

    private float[] vertices = {};

    @Override
    public float[] getMesh() {
        return vertices;
    }

    @Override
    public void generateMesh(int x, int y, int z) {

    }

    @Override
    public void needUpdate() {

    }

    @Override
    public boolean updateRequired() {
        return false;
    }

    private class BorderBlock extends Block{

        public BorderBlock() {
            super(-1, "WorldBorder");
        }

        @Override
        public boolean isSolid(Direction direction) {
            return true;
        }

        @Override
        public boolean isUnrendered() {
            return true;
        }
    }

}

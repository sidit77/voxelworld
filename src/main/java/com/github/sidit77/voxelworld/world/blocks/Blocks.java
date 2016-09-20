package com.github.sidit77.voxelworld.world.blocks;

import com.github.sidit77.voxelworld.world.Block;
import com.github.sidit77.voxelworld.world.Direction;

public class Blocks {

    public static final Block AIR = new Block(-1, "Air"){

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

    };
    public static final Block STONE = new Block(4, "Stone");
    public static final Block TORCH = new BlockTorch();
    public static final Block GRASS = new Block(3, "Grass").setTexture(Direction.UP, 2).setTexture(Direction.DOWN, 1);
    public static final Block WOOL = new Block(6, "Wool");
    public static final Block BRICKS = new Block(0, "Bricks");
    public static final Block STONEBRICKS = new Block(5, "Stonebricks");
    public static final Block WOOD = new Block(8, "Wood").setTexture(Direction.UP, 9).setTexture(Direction.DOWN, 9);
    public static final Block LEAF = new Block(7, "Leaf");
    public static final Block GLASS = new Block(10, "Glass"){

        @Override
        public boolean isSolid(Direction direction) {
            return false;
        }

        @Override
        public boolean isOpaque() {
            return false;
        }

    };

}

package com.github.sidit77.voxelworld.world;


public class Block {

    private int[] textureids;
    private String name;

    public Block(int texture, String name){
        textureids = new int[]{texture,texture,texture,texture,texture,texture};
        this.name = name;
    }

    public boolean isSolid(Direction direction){
        return true;
    }

    public int getTextureID(Direction d){
        return textureids[d.getID()];
    }

    public boolean hasHitbox(){
        return true;
    }

    public boolean isOpaque(){
        return true;
    }

    public Block setTexture(Direction d, int id){
        textureids[d.getID()] = id;
        return this;
    }

    public String getName(){
        return name;
    }

    public boolean isUnrendered(){
        return false;
    }

}

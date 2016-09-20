package com.github.sidit77.voxelworld.worldv3;


public abstract class Block {

    public int[] textureids;
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

    public boolean isLightSource(){
        return false;
    }

    public int getLightLevel(){
        return 15;
    }

    public void setTexture(Direction d, int id){
        textureids[d.getID()] = id;
    }

    public String getName(){
        return name;
    }

    public boolean isUnrendered(){
        return false;
    }

}

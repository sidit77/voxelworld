package com.github.sidit77.voxelworld.opengl.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

public abstract class Texture {

    private int id;
    private int type;

    public Texture(int type){
        id = GL11.glGenTextures();
        this.type = type;
        GL11.glBindTexture(type, id);
    }

    public int getID(){
        return id;
    }

    public void bind(int slot){
        if(slot > 31)throw new IllegalArgumentException("slot can not be greater than 31");
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + slot);
        GL11.glBindTexture(type, id);
    }

    public void setWarpMode(int mode){
        GL11.glBindTexture(type, id);
        GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_S, mode);
        GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_T, mode);
        GL11.glTexParameteri(type, GL12.GL_TEXTURE_WRAP_R, mode);
    }

    public void delete(){
        GL11.glDeleteTextures(id);
    }

}

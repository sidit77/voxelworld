package com.github.sidit77.voxelworld.opengl.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;

public class EmptyTexture2D extends Texture {

    /**
     * A simple wrapper around an OpenGL Texture object, that's used as framebuffer attachment.
     */

    private int mode;
    private int internalmode;

    public EmptyTexture2D(int width, int height){
        this(width, height, GL11.GL_RGBA);
    }

    public EmptyTexture2D(int width, int height, int mode) {
        this(width, height, mode, mode);
    }

    public EmptyTexture2D(int width, int height, int mode, int internalmode) {
        super(GL11.GL_TEXTURE_2D);
        this.mode = mode;
        this.internalmode = internalmode;
        setFiltering(GL11.GL_LINEAR, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        //GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_LOD_BIAS, -1);
        resize(width, height);
    }

    public void resize(int width, int height){
        bind(0);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalmode, width, height, 0, mode, GL11.GL_FLOAT, (ByteBuffer) null);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, getID());
        //GL42.glTexStorage2D(GL11.GL_TEXTURE_2D, 1, mode, width, height);
        //System.out.println(GL11.glGetError() == GL11.GL_NO_ERROR);
    }

}

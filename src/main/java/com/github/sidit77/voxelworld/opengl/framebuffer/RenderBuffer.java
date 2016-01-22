package com.github.sidit77.voxelworld.opengl.framebuffer;

import org.lwjgl.opengl.GL30;

public class RenderBuffer {

    private int id;
    private int mode;

    public RenderBuffer(int width, int height, int mode){
        id = GL30.glGenRenderbuffers();
        this.mode = mode;
        resize(width, height);
    }

    public void resize(int width, int height){
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, id);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, mode, width, height);
    }

    public int getID(){
        return id;
    }

    public void delete(){
        GL30.glDeleteRenderbuffers(id);
    }

}

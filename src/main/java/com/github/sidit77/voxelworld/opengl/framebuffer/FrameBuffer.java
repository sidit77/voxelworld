package com.github.sidit77.voxelworld.opengl.framebuffer;

import com.github.sidit77.voxelworld.opengl.texture.EmptyTexture2D;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class FrameBuffer {

    /**
     * A simple wrapper around an OpenGL Framebuffer object.
     */ 

    public int id;

    public FrameBuffer(){
        id = GL30.glGenFramebuffers();
        bind();
    }

    public int getID(){
        return id;
    }

    public boolean isOK(){
        return GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
    }

    public FrameBuffer attachTexture(EmptyTexture2D texture, int mode){
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, mode, GL11.GL_TEXTURE_2D, texture.getID(), 0);
        return this;
    }

    public FrameBuffer attachRenderBuffer(RenderBuffer renderBuffer, int mode){
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, mode, GL30.GL_RENDERBUFFER, renderBuffer.getID());
        return this;
    }

    public void bind(){
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
    }

    public void unbind(){
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void delete(){
        GL30.glDeleteFramebuffers(id);
    }

}
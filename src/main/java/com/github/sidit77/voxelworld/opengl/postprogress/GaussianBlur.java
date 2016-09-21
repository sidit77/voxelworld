package com.github.sidit77.voxelworld.opengl.postprogress;

import com.github.sidit77.voxelworld.opengl.framebuffer.FrameBuffer;
import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.EmptyTexture2D;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GaussianBlur {

    private EmptyTexture2D origin;
    private EmptyTexture2D target;
    private EmptyTexture2D temp;
    private FrameBuffer targetFramebuffer;
    private FrameBuffer tempFrameBuffer;

    private GLSLProgram shader;

    public GaussianBlur(EmptyTexture2D origin, int width, int height){
        this.origin = origin;

        target = new EmptyTexture2D(width, height);
        temp = new EmptyTexture2D(width, height);

        targetFramebuffer = new FrameBuffer().attachTexture(target, GL30.GL_COLOR_ATTACHMENT0);
        if(!targetFramebuffer.isOK())System.out.println("ERROR");
        targetFramebuffer.unbind();

        tempFrameBuffer = new FrameBuffer().attachTexture(temp, GL30.GL_COLOR_ATTACHMENT0);
        if(!tempFrameBuffer.isOK())System.out.println("ERROR");
        tempFrameBuffer.unbind();

        shader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/GaussianBlurVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/GaussianBlurFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();
        shader.bind();
        shader.setUniform("screen", width, height);
    }

    public void resize(int width, int height){
        target.resize(width, height);
        temp.resize(width, height);
        shader.bind();
        shader.setUniform("screen", width, height);
    }

    public void updateTargetTexture(){
        shader.bind();

        tempFrameBuffer.bind();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        origin.bind(0);
        shader.setUniform("direction", 0.0f, 1.0f);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        targetFramebuffer.bind();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        temp.bind(0);
        shader.setUniform("direction", 1.0f, 0.0f);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        targetFramebuffer.unbind();
    }

    public EmptyTexture2D getTargetTexture(){
        return target;
    }

    public void delete(){
        shader.delete();
        target.delete();
        temp.delete();
    }

}

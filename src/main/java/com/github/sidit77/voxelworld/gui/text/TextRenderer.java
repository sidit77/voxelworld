package com.github.sidit77.voxelworld.gui.text;

import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.Texture2D;
import javafx.scene.paint.Color;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

public class TextRenderer {

    private Texture2D fontTexture;
    private Font font;
    private GLSLProgram fontshader;
    private int uboid;

    public TextRenderer(Texture2D fontTexture, Font font, int width, int height){
        this.fontTexture = fontTexture;
        this.font = font;
        this.fontshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/FontVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/FontFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();
        resize(width, height);
        fontshader.bindUniformBlock("charinfo", 0);
        uboid = GL15.glGenBuffers();

    }

    public void render(String text, float xpos, float ypos, float size, Color color, float transparency){
        render(new Text(text, size, font), xpos, ypos, color, transparency);
    }

    public void render(Text text, float xpos, float ypos, Color color, float transparency){
        fontTexture.bind(0);
        fontshader.bind();
        fontshader.setUniform("color", (float)color.getRed(), (float)color.getGreen(), (float)color.getBlue());
        fontshader.setUniform("transparency", transparency);
        fontshader.setUniform("pos", xpos, ypos);

        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uboid);
        GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, text.getCharInfo(), GL15.GL_STREAM_DRAW);
        GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 0, uboid);

        GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, text.getLength());
    }

    public Text getText(String text, float size){
        return new Text(text, size, font);
    }

    public void resize(int width, int height){
        fontshader.bind();
        fontshader.setUniform("ortho", false, new Matrix4f().ortho2D(0, width, height, 0));
    }

    public void delete(){
        fontshader.delete();
        GL15.glDeleteBuffers(uboid);
    }

}

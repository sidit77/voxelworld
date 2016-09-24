package com.github.sidit77.voxelworld;

import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.EmptyTexture2D;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class CharacterModel {

    private GLSLProgram playershader;
    private GLSLProgram playershadowshader;

    private int vao;
    private int vbo;
    private int vl;

    public CharacterModel(){
        playershader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/PlayerVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/PlayerFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        playershadowshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/PlayerVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/shadow/Fragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        float[] faces = ObjLoader.loadMesh("assets/model/human.obj");
        vl = faces.length / 8;
        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(faces.length);
        verticesBuffer.put(faces);
        verticesBuffer.flip();

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 0 * Float.BYTES);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 5 * Float.BYTES);
        GL30.glBindVertexArray(0);


    }

    public void rendershadow(Vector3f playerpos, Matrix3f playerrot, Matrix4f lightMatrix){
        GL30.glBindVertexArray(vao);
        playershadowshader.bind();
        playershadowshader.setUniform("mvp", false, lightMatrix);
        playershadowshader.setUniform("pos", playerpos);
        playershadowshader.setUniform("rot", false, playerrot);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vl);
    }

    public void render(Vector3f playerpos, Matrix3f playerrot, Matrix4f matrix, Vector3f lightDir, float darkness, float torch, Matrix4f lightmatrix, EmptyTexture2D shadowmap){
        GL30.glBindVertexArray(vao);
        shadowmap.bind(1);
        playershader.bind();
        playershader.setUniform("mvp", false, matrix);
        playershader.setUniform("pos", playerpos);
        playershader.setUniform("rot", false, playerrot);
        playershader.setUniform("light", lightDir);
        playershader.setUniform("dark", darkness);
        playershader.setUniform("torch", torch);
        playershader.setUniform("lightmatrix", false, lightmatrix);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vl);
    }

    public void delete(){
        playershader.delete();
        playershadowshader.delete();
        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(vbo);
    }

}

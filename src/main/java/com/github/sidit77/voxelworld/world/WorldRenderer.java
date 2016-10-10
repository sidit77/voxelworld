package com.github.sidit77.voxelworld.world;

import com.github.sidit77.voxelworld.Camera;
import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.EmptyTexture2D;
import com.github.sidit77.voxelworld.opengl.texture.Texture2D;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;

public class WorldRenderer {

    private GLSLProgram shader;
    private GLSLProgram shadowshader;

    private Texture2D colortexture;

    private World world;

    public WorldRenderer(){
        shader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/Vertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/Fragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        shadowshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/shadow/Vertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/shadow/Fragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        colortexture = Texture2D.fromFile("assets/texture/Atlas2.png");
        colortexture.setFiltering(GL11.GL_NEAREST_MIPMAP_LINEAR, GL11.GL_NEAREST);
        colortexture.setWarpMode(GL12.GL_CLAMP_TO_EDGE);
        colortexture.setLODBias(-0.5f);


        world = new World(16,7,16, new DefaultWorldGenerator(System.nanoTime()));

    }

    public void update(){

    }

    public World getWorld(){
        return world;
    }

    public void render(Camera camera, float darkness, Vector3f lightDir, EmptyTexture2D shadowmap, Matrix4f lightmatrix){
        colortexture.bind(0);
        shadowmap.bind(1);
        shader.bind();
        shader.setUniform("mvp", false, camera.getCameraMatrix());
        shader.setUniform("lightmatrix", false, lightmatrix);
        shader.setUniform("pos", camera.getPosition());
        shader.setUniform("lightDir", lightDir);
        shader.setUniform("darkness", darkness);
        world.draw();
    }

    public void render(Matrix4f mvp){
        shadowshader.bind();
        shadowshader.setUniform("mvp", false, mvp);
        colortexture.bind(0);
        world.draw();
    }

    public void delete(){
        world.delete();
        shader.delete();
        colortexture.delete();
        shadowshader.delete();
    }

}

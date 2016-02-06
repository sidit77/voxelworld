package com.github.sidit77.voxelworld.world;

import com.github.sidit77.voxelworld.Camera;
import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.Texture2D;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.util.ArrayList;
import java.util.List;

//TODO save chunks to disk
//TODO make the terrain editable
//TODO speed up chunk loading

public class Terrain {

    private GLSLProgram shader;
    private boolean fog = false;

    private Texture2D colortexture;
    private Texture2D normaltexture;

    private ChunkMeshBuffer meshBuffer;

    private ChunkManager chunkManager;

    List<ChunkMesh> loadedMeshes;

    public Terrain(){

        shader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/Vertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/Fragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/Geometry.glsl", GL32.GL_GEOMETRY_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/SkyColor.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();


        colortexture = Texture2D.fromFile("assets/texture/dirt.png");
        normaltexture = Texture2D.fromFile("assets/texture/dirt_normal.png");

        loadedMeshes = new ArrayList<>();

        meshBuffer = new ChunkMeshBuffer();

        chunkManager = new ChunkManager(new Vector3f(0), 5, 5);
    }

    public void update(Vector3f pos){
        chunkManager.setPlayerPosition(pos);

        List<ChunkMesh> meshes = chunkManager.getViewMesh();
        if(loadedMeshes.size() != meshes.size() && !loadedMeshes.equals(meshes)){
            loadedMeshes = meshes;
            meshBuffer.clear();
            loadedMeshes.forEach((cm) -> meshBuffer.addChunkMesh(cm));

            //System.out.println("Vertex Buffer Usage: " + meshBuffer.getVertexBufferUsage() * 100 + "%");
            //System.out.println("Index Buffer Usage: " + meshBuffer.getIndexBufferUsage() * 100 + "%");
        }

    }

    public boolean isFogEnabled(){
        return fog;
    }

    public void setFog(boolean fog){
        this.fog = fog;
        shader.bind();
        shader.setUniform("fog", fog ? 1 : 0);
    }

    public void render(Camera camera, Vector3f lightDir, float darkness, Texture2D glowtexture){
        colortexture.bind(0);
        normaltexture.bind(1);
        glowtexture.bind(2);
        shader.bind();
        shader.setUniform("mvp", false, camera.getCameraMatrix());
        shader.setUniform("pos", camera.getPosition());
        shader.setUniform("lightDir", lightDir);
        shader.setUniform("lightPower", darkness);
        meshBuffer.render();
    }

    public void delete(){
        meshBuffer.delete();
        shader.delete();
        colortexture.delete();
        normaltexture.delete();
        chunkManager.shutdown();
    }

}

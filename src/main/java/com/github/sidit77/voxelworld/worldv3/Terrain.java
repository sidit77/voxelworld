package com.github.sidit77.voxelworld.worldv3;

import com.github.sidit77.voxelworld.Camera;
import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.EmptyTexture2D;
import com.github.sidit77.voxelworld.opengl.texture.Texture2DArray;
import com.github.sidit77.voxelworld.worldv3.mesh.ChunkMesh;
import com.github.sidit77.voxelworld.worldv3.mesh.ChunkMeshBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class Terrain {

    private GLSLProgram shader;
    private GLSLProgram shadowshader;
    private boolean fog = false;

    private Texture2DArray colortexture;

    private ChunkMeshBuffer meshBuffer;

    private IWorldGenerator generator = new DefaultWorldGenerator();

    //Block[][][] world;
    //int[][][] light;
    //boolean update = true;
    Chunk chunk;

    public Terrain(){
        shader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/Vertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/Fragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/Geometry.glsl", GL32.GL_GEOMETRY_SHADER))
                .link();

        shadowshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/shadow/Vertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/shadow/Fragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        colortexture = Texture2DArray.fromFile(new String[]{
                "assets/texture/worldv3/stone.png",
                "assets/texture/worldv3/dirt.png",
                "assets/texture/worldv3/grass_side.png",
                "assets/texture/worldv3/grass.png",
                "assets/texture/worldv3/brick.png",
                "assets/texture/worldv3/stonebrick.png",
                "assets/texture/worldv3/wool.png",
                "assets/texture/worldv3/torch.png"
        });
        colortexture.setWarpMode(GL12.GL_CLAMP_TO_EDGE);
        colortexture.setLODBias(-0.5f);
        //colortexture.setFiltering(GL11.GL_NEAREST_MIPMAP_LINEAR, GL11.GL_NEAREST);

        meshBuffer = new ChunkMeshBuffer();

        chunk = new Chunk(new ChunkIndex(0,0,0), generator);

        //world = new Block[32][32][32];
        //light = new int[32][32][32];
//
        //for(int x = 0; x < 32; x++){
        //    for(int y = 0; y < 32; y++){
        //        for(int z = 0; z < 32; z++){
        //            world[x][y][z] = (x*y*z-1000) > 0 ? Blocks.AIR : Blocks.STONE;
        //        }
        //    }
        //}
//
        //for(int x = 0; x < 32; x++){
        //    for(int y = 0; y < 32; y++){
        //        for(int z = 0; z < 32; z++){
        //            if(world[x][y][z] instanceof BlockAir && y - 1 > 0 && world[x][y-1][z] instanceof BlockStone)
        //            world[x][y][z] = Blocks.GRASS;
        //        }
        //    }
        //}
//
        //world[13][4][21] = Blocks.STONE;
        //world[14][4][21] = Blocks.STONE;
        //world[13][4][20] = Blocks.STONE;
        //world[14][4][20] = Blocks.STONE;
        //world[13][5][20] = Blocks.GRASS;
        //world[13][6][20] = Blocks.TORCH;
//
        //updateMesh();


    }

    public void update(Vector3f pos){
        if(chunk.updateRequired()){
            chunk.updateLight();
            ChunkMesh mesh = chunk.getMesh();
            meshBuffer.clear();
            meshBuffer.addChunkMesh(mesh.getVertexBuffer(true), mesh.getIndexBuffer());
            chunk.setUpdated();
        }
    }

    public Block getBlock(int x, int y, int z){
        return chunk.getBlock(x, y, z);
    }

    public  Block getBlock(Vector3f pos){
        return getBlock(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    }

    public int getLightLevel(int x, int y, int z){
        return chunk.getLightLevel(x, y, z);
    }

    public  int getLightLevel(Vector3f pos){
        return getLightLevel(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    }

    public void setBlock(int x, int y, int z, Block block){
        chunk.setBlock(x,y,z,block);
    }

    public void setBlock(Vector3f pos, Block block){
        setBlock(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z), block);
    }

    public boolean isFogEnabled(){
        return fog;
    }

    public void setFog(boolean fog){
        this.fog = fog;
        shader.bind();
        shader.setUniform("fog", fog ? 1 : 0);
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
        meshBuffer.render();
    }

    public void render(Matrix4f mvp){
        shadowshader.bind();
        shadowshader.setUniform("mvp", false, mvp);
        meshBuffer.render();
    }

    public void delete(){
        meshBuffer.delete();
        shader.delete();
        colortexture.delete();
        shadowshader.delete();
    }

}

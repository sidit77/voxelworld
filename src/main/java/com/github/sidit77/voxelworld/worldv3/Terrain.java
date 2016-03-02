package com.github.sidit77.voxelworld.worldv3;

import com.github.sidit77.voxelworld.Camera;
import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.Texture2DArray;
import com.github.sidit77.voxelworld.worldv3.blocks.BlockAir;
import com.github.sidit77.voxelworld.worldv3.blocks.BlockStone;
import com.github.sidit77.voxelworld.worldv3.blocks.Blocks;
import com.github.sidit77.voxelworld.worldv3.mesh.ChunkMesh;
import com.github.sidit77.voxelworld.worldv3.mesh.ChunkMeshBuffer;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class Terrain {

    private GLSLProgram shader;
    private boolean fog = false;

    private Texture2DArray colortexture;

    private ChunkMeshBuffer meshBuffer;

    Block[][][] world;
    boolean update = true;

    public Terrain(){
        shader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/Vertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/Fragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/worldv3/Geometry.glsl", GL32.GL_GEOMETRY_SHADER))
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

        world = new Block[32][32][32];
        for(int x = 0; x < 32; x++){
            for(int y = 0; y < 32; y++){
                for(int z = 0; z < 32; z++){
                    world[x][y][z] = (x*y*z-1000) > 0 ? Blocks.AIR : Blocks.STONE;
                }
            }
        }

        for(int x = 0; x < 32; x++){
            for(int y = 0; y < 32; y++){
                for(int z = 0; z < 32; z++){
                    if(world[x][y][z] instanceof BlockAir && y - 1 > 0 && world[x][y-1][z] instanceof BlockStone)
                    world[x][y][z] = Blocks.GRASS;
                }
            }
        }

        world[13][4][21] = Blocks.STONE;
        world[14][4][21] = Blocks.STONE;
        world[13][4][20] = Blocks.STONE;
        world[14][4][20] = Blocks.STONE;
        world[13][5][20] = Blocks.GRASS;
        world[13][6][20] = Blocks.TORCH;

        //updateMesh();


    }

    public void update(Vector3f pos){
        if(update){
            ChunkMesh mesh = new ChunkMesh();

            for(int x = 0; x < 32; x++){
                for(int y = 0; y < 32; y++){
                    for(int z = 0; z < 32; z++){

                        Block[] neighbors = new Block[6];
                        for(Direction d : Direction.values()){
                            if(x + d.getXOffset() >= 0 && x + d.getXOffset() < 32 &&
                                    y + d.getYOffset() >= 0 && y + d.getYOffset() < 32 &&
                                    z + d.getZOffset() >= 0 && z + d.getZOffset() < 32){

                                neighbors[d.getID()] = world[x + d.getXOffset()][y + d.getYOffset()][z + d.getZOffset()];

                            }else{
                                neighbors[d.getID()] = null;
                            }
                        }

                        world[x][y][z].addToChunkMesh(mesh, x,y,z, neighbors);
                    }
                }
            }

            meshBuffer.clear();
            meshBuffer.addChunkMesh(mesh.getVertexBuffer(), mesh.getIndexBuffer());
            update = false;
        }

    }

    public Block getBlock(int x, int y, int z){
        if(x >= 0 && x < 32 && y >= 0 && y < 32 && z >= 0 && z < 32){
            return world[x][y][z];
        }else{
            return Blocks.AIR;
        }
    }

    public  Block getBlock(Vector3f pos){
        return getBlock(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    }

    public void setBlock(int x, int y, int z, Block block){
        if(x >= 0 && x < 32 && y >= 0 && y < 32 && z >= 0 && z < 32){
            world[x][y][z] = block;
            update = true;
        }
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

    public void render(Camera camera){
        colortexture.bind(0);
        shader.bind();
        shader.setUniform("mvp", false, camera.getCameraMatrix());
        shader.setUniform("pos", camera.getPosition());
        meshBuffer.render();
    }

    public void delete(){
        meshBuffer.delete();
        shader.delete();
        colortexture.delete();
    }

}

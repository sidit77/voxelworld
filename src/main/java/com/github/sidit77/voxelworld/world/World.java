package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class World {

    private WorldElement[][][] chunks;
    private int vao, vbo, vs;
    private boolean update = true;

    public World(int dimX, int dimY, int dimZ, IWorldGenerator generator){
        chunks = new WorldElement[dimX + 2][dimY + 2][dimZ + 2];

        for(int x = 0; x < dimX + 2; x++){
            for(int y = 0; y < dimY + 2; y++){
                for(int z = 0; z < dimZ + 2; z++){
                    if(x == 0 || x == dimX + 1 || y == 0 || y == dimY + 1 || z == 0 || z == dimZ + 1){
                        chunks[x][y][z] = new BorderChunk();
                    }else{
                        chunks[x][y][z] = new Chunk();
                    }
                }
            }
        }

        for(int x = 1; x < dimX + 1; x++){
            for(int y = 1; y < dimY + 1; y++){
                for(int z = 1; z < dimZ + 1; z++){
                    chunks[x][y][z].setBack (chunks[x - 0][y - 0][z - 1]);
                    chunks[x][y][z].setFront(chunks[x + 0][y + 0][z + 1]);
                    chunks[x][y][z].setBottom(chunks[x - 0][y - 1][z - 0]);
                    chunks[x][y][z].setTop   (chunks[x + 0][y + 1][z + 0]);
                    chunks[x][y][z].setLeft  (chunks[x - 1][y - 0][z - 0]);
                    chunks[x][y][z].setRight (chunks[x + 1][y + 0][z + 0]);
                }
            }
        }

        for(int x = 1; x < dimX + 1; x++) {
            for (int y = 1; y < dimY + 1; y++) {
                for (int z = 1; z < dimZ + 1; z++) {

                    for(int x2 = 0; x2 < Chunk.size; x2++) {
                        for (int y2 = 0; y2 < Chunk.size; y2++) {
                            for (int z2 = 0; z2 < Chunk.size; z2++) {
                                chunks[x][y][z].setBlock(x2,y2,z2, generator.generate(x * Chunk.size + x2, y * Chunk.size + y2, z * Chunk.size + z2, this));
                            }
                        }
                    }

                }
            }
        }

        System.out.println("Finished generate");

        for(int x = 1; x < dimX + 1; x++) {
            for (int y = 1; y < dimY + 1; y++) {
                for (int z = 1; z < dimZ + 1; z++) {

                    for(int x2 = 0; x2 < Chunk.size; x2++) {
                        for (int y2 = 0; y2 < Chunk.size; y2++) {
                            for (int z2 = 0; z2 < Chunk.size; z2++) {
                                generator.postgenerate(x * Chunk.size + x2, y * Chunk.size + y2, z * Chunk.size + z2, chunks[x][y][z].getBlock(x2, y2,z2), this);
                            }
                        }
                    }

                }
            }
        }

        System.out.println("Finished postgenerate");

        for(int x = 1; x < dimX + 1; x++) {
            for (int y = 1; y < dimY + 1; y++) {
                for (int z = 1; z < dimZ + 1; z++) {
                    chunks[x][y][z].setLighting(true);
                }
            }
        }

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 9 * Float.BYTES, 0 * Float.BYTES);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 9 * Float.BYTES, 3 * Float.BYTES);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 9 * Float.BYTES, 5 * Float.BYTES);
        GL20.glVertexAttribPointer(3, 1, GL11.GL_FLOAT, false, 9 * Float.BYTES, 8 * Float.BYTES);
        GL30.glBindVertexArray(0);


        update();
    }

    public Block getBlock(int x, int y, int z){
        return getChunkAt(x,y,z).getBlock(((x % Chunk.size) + Chunk.size) % Chunk.size, ((y % Chunk.size) + Chunk.size) % Chunk.size, ((z % Chunk.size) + Chunk.size) % Chunk.size);
    }

    public  Block getBlock(Vector3f pos){
        return getBlock(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    }

    public int getLightLevel(int x, int y, int z){
        return 0;//chunk.getLightLevel(x, y, z);
    }

    public  int getLightLevel(Vector3f pos){
        return 0;//getLightLevel(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    }

    public void recalculateLighting(){
        for (int x = 0; x < chunks.length; x++) {
            for (int y = 0; y < chunks[0].length; y++) {
                for (int z = 0; z < chunks[0][0].length; z++) {
                    chunks[x][y][z].setLighting(false);
                }
            }
        }
        for (int x = 0; x < chunks.length; x++) {
            for (int y = 0; y < chunks[0].length; y++) {
                for (int z = 0; z < chunks[0][0].length; z++) {
                    chunks[x][y][z].setLighting(true);
                    chunks[x][y][z].needUpdate();
                }
            }
        }
        update = true;
    }

    public void setBlock(int x, int y, int z, Block block){
        getChunkAt(x,y,z).setBlock(((x % Chunk.size) + Chunk.size) % Chunk.size, ((y % Chunk.size) + Chunk.size) % Chunk.size, ((z % Chunk.size) + Chunk.size) % Chunk.size, block);
        update = true;
    }

    public void setBlock(Vector3f pos, Block block){
        setBlock(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z), block);
    }

    private WorldElement getChunkAt(int x, int y, int z) {
        return chunks
        [Math.min(Math.max((int)Math.floor(x / Chunk.size), 0), chunks.length - 1)]
        [Math.min(Math.max((int)Math.floor(y / Chunk.size), 0), chunks[0].length - 1)]
        [Math.min(Math.max((int)Math.floor(z / Chunk.size), 0), chunks[0][0].length -1)];

    }

    public void update(){
        if(update) {
            vs = 0;
            for (int x = 0; x < chunks.length; x++) {
                for (int y = 0; y < chunks[0].length; y++) {
                    for (int z = 0; z < chunks[0][0].length; z++) {
                        if (chunks[x][y][z].updateRequired()) {
                            chunks[x][y][z].generateMesh(x * Chunk.size, y * Chunk.size, z * Chunk.size);
                        }
                        vs += chunks[x][y][z].getMesh().length;
                    }
                }
            }

            FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vs);
            for (int x = 0; x < chunks.length; x++) {
                for (int y = 0; y < chunks[0].length; y++) {
                    for (int z = 0; z < chunks[0][0].length; z++) {
                        verticesBuffer.put(chunks[x][y][z].getMesh());
                    }
                }
            }
            verticesBuffer.flip();
            vs /= 9;

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_DYNAMIC_DRAW);
            update = false;
        }
    }

    public void draw(){
        update();
        GL30.glBindVertexArray(vao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vs);
    }

    public void delete(){
        GL30.glDeleteVertexArrays(vao);
        GL15.glDeleteBuffers(vbo);
    }

}

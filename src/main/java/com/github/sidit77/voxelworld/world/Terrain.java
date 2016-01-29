package com.github.sidit77.voxelworld.world;

import com.github.sidit77.voxelworld.Camera;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Terrain {

    private int vaoId;
    private int vboId;
    private int indexCount;
    private int iboId;

    private Map<ChunkIndex, Chunk> chunks;
    private Chunk currentChunk = null;

    public Terrain(){
        chunks = new HashMap<>();
        currentChunk = getChunkAt(new Vector3f(0,0,0));

        vaoId = GL30.glGenVertexArrays();
        vboId = GL15.glGenBuffers();
        iboId = GL15.glGenBuffers();
        GL30.glBindVertexArray(vaoId);
        updateWorld();
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
        GL30.glBindVertexArray(0);
    }

    private void updateWorld(){
        ChunkMesher.Mesh mcd = ChunkMesher.createMesh(currentChunk, 1);

        indexCount = mcd.indices.capacity();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mcd.vertices, GL15.GL_DYNAMIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, mcd.indices, GL15.GL_DYNAMIC_DRAW);
    }

    public void update(Vector3f pos){
        Chunk newChunk = getChunkAt(pos);
        if(currentChunk != newChunk){
            currentChunk = newChunk;
            updateWorld();
        }
    }

    public void render(Camera camera){
        GL30.glBindVertexArray(vaoId);
        GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT, 0);
    }

    public void delete(){
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(iboId);
        GL30.glDeleteVertexArrays(vaoId);
    }

    private Chunk getChunkAt(Vector3f pos){
        ChunkIndex ci = new ChunkIndex(pos);
        if(!chunks.containsKey(ci)){
            chunks.put(ci, new Chunk(ci.getChunkPosition()));
            System.out.println("Created Chunk: " + ci);
        }
        return chunks.get(ci);
    }

    private class ChunkIndex{
        private int x;
        private int y;
        private int z;

        public ChunkIndex(Vector3f pos){
            this.x = (int)Math.floor(pos.x / Chunk.size);
            this.y = (int)Math.floor(pos.y / Chunk.size);
            this.z = (int)Math.floor(pos.z / Chunk.size);
        }

        public Vector3f getChunkPosition(){
            return new Vector3f(x,y,z).mul(Chunk.size);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChunkIndex that = (ChunkIndex) o;
            return x == that.x &&
                    y == that.y &&
                    z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public String toString(){
            return x + "|" + y + "|" + z;
        }
    }
}

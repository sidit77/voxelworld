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

//TODO add shader to this class (maybe put all the rendering stuff into another class)
//TODO additional chunks into the buffer
//TODO draw additional chunks
//TODO reuse chunk mesh
//TODO multi thread chunk generation
//TODO save chunks to disk
//TODO keep the cache small
//TODO make the terrain editable

public class Terrain {

    public static final int viewdistance = 2;

    private int vaoId;
    private int vboId;
    private int indexCount;
    private int iboId;

    private Map<ChunkIndex, Chunk> chunks;
    private Chunk currentChunk = null;

    public Terrain(){
        chunks = new HashMap<>();

        vaoId = GL30.glGenVertexArrays();
        vboId = GL15.glGenBuffers();
        iboId = GL15.glGenBuffers();
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboId);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
        GL30.glBindVertexArray(0);
    }

    private void updateWorld(){
        long time = System.nanoTime();
        ChunkMesher.Mesh mcd = ChunkMesher.createMesh(currentChunk, 1);

        indexCount = mcd.indicesCount;
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mcd.vertices, GL15.GL_DYNAMIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, mcd.indices, GL15.GL_DYNAMIC_DRAW);
        System.out.println("Needed " + ((double)(System.nanoTime() - time))/1000000000 + " seconds to create the mesh");
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

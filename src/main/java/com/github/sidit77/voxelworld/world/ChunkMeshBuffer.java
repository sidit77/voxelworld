package com.github.sidit77.voxelworld.world;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

public class ChunkMeshBuffer {


    private static final int chunkmeshvertexnr = (Chunk.size + 1) * (Chunk.size + 1) * (Chunk.size + 1) / 5;
    private static final int chunkmeshvertexsize = chunkmeshvertexnr * 3 * 5;
    private static final int chunkmeshindexsize = Chunk.size * Chunk.size * Chunk.size * 5;

    private int vaoId;
    private int vboId;
    private int iboId;
    private int[] indexCounts;

    public ChunkMeshBuffer(int numberofslots){
        indexCounts = new int[numberofslots];
        vaoId = GL30.glGenVertexArrays();
        vboId = GL15.glGenBuffers();
        iboId = GL15.glGenBuffers();
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(chunkmeshvertexsize * indexCounts.length), GL15.GL_DYNAMIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createIntBuffer(chunkmeshindexsize * indexCounts.length), GL15.GL_DYNAMIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
        GL30.glBindVertexArray(0);
    }

    //TODO error spotting (offsets)

    public void setToChunk(int nr, Chunk chunk){
        assert(nr < indexCounts.length);

        long time = System.nanoTime();
        ChunkMesher.Mesh mcd = ChunkMesher.createMesh(chunk, 1);

        indexCounts[nr] = mcd.indicesCount;
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, chunkmeshvertexsize * nr * Float.BYTES, mcd.vertices);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboId);
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, chunkmeshindexsize * nr, mcd.indices);

        System.out.println("Needed " + ((double)(System.nanoTime() - time))/1000000000 + " seconds to create the mesh.");
        System.out.println("vertex buffer usage: " + ((float)mcd.vertices.capacity() / chunkmeshvertexsize) * 100 + "%");
        System.out.println("index buffer usage: " + ((float)mcd.indices.capacity() / chunkmeshindexsize) * 100 + "%");
    }

    public void render(){
        GL30.glBindVertexArray(vaoId);
        for(int i = 0; i < indexCounts.length; i++){
            GL32.glDrawElementsBaseVertex(GL11.GL_TRIANGLES, indexCounts[i], GL11.GL_UNSIGNED_INT, chunkmeshindexsize * i, chunkmeshvertexsize * i / 3);
        }
    }

    public void delete(){
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(iboId);
        GL30.glDeleteVertexArrays(vaoId);
    }
}

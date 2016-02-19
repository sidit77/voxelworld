package com.github.sidit77.voxelworld.world;

import org.lwjgl.opengl.*;

import java.util.ArrayList;

public class ChunkMeshBuffer {

    private int vaoId;
    private int vboId;
    private int iboId;

    private ArrayList<ChunkBufferIndex> bufferIndices;

    private static final int maxvsize = 20000000;
    private static final int maxisize = 20000000;

    private int vsize = 0;
    private int isize = 0;

    public ChunkMeshBuffer(){

        bufferIndices = new ArrayList<>();

        vaoId = GL30.glGenVertexArrays();
        vboId = GL15.glGenBuffers();
        iboId = GL15.glGenBuffers();
        GL30.glBindVertexArray(vaoId);
        clear();
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0 * Float.BYTES);
        GL20.glVertexAttribPointer(1, 1, GL11.GL_FLOAT, false, 4 * Float.BYTES, 3 * Float.BYTES);
        GL30.glBindVertexArray(0);

    }

    public void clear(){
        bufferIndices.clear();
        vsize = 0;
        isize = 0;
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, maxvsize * Float.BYTES, GL15.GL_DYNAMIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, maxisize * Integer.BYTES, GL15.GL_DYNAMIC_DRAW);
    }

    public void addChunkMesh(ChunkMesh cm){
        if(vsize + cm.vertices.capacity() >= maxvsize || isize + cm.indices.capacity() >= maxisize){
            System.out.println("Buffer too small");
            return;
        }
        bufferIndices.add(new ChunkBufferIndex(cm.indicesCount, isize, vsize));
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboId);
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, isize * Integer.BYTES, cm.indices);
        isize += cm.indices.capacity();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, vsize * Float.BYTES, cm.vertices);
        vsize += cm.vertices.capacity();
    }

    public void render(){
        GL30.glBindVertexArray(vaoId);
        bufferIndices.forEach(ChunkBufferIndex::draw);
    }

    public float getVertexBufferUsage(){
        return (float)vsize/(float)maxvsize;
    }

    public float getIndexBufferUsage(){
        return (float)isize/(float)maxisize;
    }

    public void delete(){
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(iboId);
        GL30.glDeleteVertexArrays(vaoId);
    }

    private class ChunkBufferIndex{
        private int offset;
        private int count;
        private int basevertex;

        public ChunkBufferIndex(int count, int offset, int basevertex){
            this.offset = offset;
            this.count = count;
            this.basevertex = basevertex / 4;
        }

        public void draw(){
            GL32.glDrawElementsBaseVertex(GL32.GL_LINES_ADJACENCY, count, GL11.GL_UNSIGNED_INT, offset * Integer.BYTES, basevertex);
        }

    }

}

package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;

import java.util.Objects;

public class ChunkIndex {

    private int x;
    private int y;
    private int z;

    public ChunkIndex(Vector3f pos){
        this.x = (int)Math.floor(pos.x / Chunk.size);
        this.y = (int)Math.floor(pos.y / Chunk.size);
        this.z = (int)Math.floor(pos.z / Chunk.size);
    }

    public ChunkIndex(Vector3f pos, int dx, int dy, int dz){
        this.x = (int)Math.floor(pos.x / Chunk.size) + dx;
        this.y = (int)Math.floor(pos.y / Chunk.size) + dy;
        this.z = (int)Math.floor(pos.z / Chunk.size) + dz;
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

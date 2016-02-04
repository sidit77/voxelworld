package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;


public class ChunkMesh {

    public FloatBuffer vertices;
    public IntBuffer indices;
    public int indicesCount;
    public Vector3f pos;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkMesh chunkMesh = (ChunkMesh) o;
        return indicesCount == chunkMesh.indicesCount &&
                Objects.equals(pos, chunkMesh.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indicesCount, pos);
    }
}

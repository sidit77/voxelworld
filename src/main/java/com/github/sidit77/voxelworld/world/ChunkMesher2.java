package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.util.*;

public class ChunkMesher2 {


    public static ChunkMesh createMesh(Chunk[][][] chunks){
        long t = System.currentTimeMillis();

        List<Integer> indices = new ArrayList<>();
        List<Vertex> vertices = new ArrayList<>();
        Map<Vertex, Integer> indicesMap = new HashMap<>();

        Vector3f[] p = {new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f()};
        int index = 0;

        for(int x = 0; x < Chunk.size; x += 1){
            for(int y = 0; y < Chunk.size; y += 1){
                for(int z = 0; z < Chunk.size; z += 1){
                    int material = chunks[1][1][1].getMaterial(x, y, z);
                    if(material != 0) {
                        for (int[][] face : faces) {
                            if (isAir(face[0][0] + x, face[0][1] + y, face[0][2] + z, chunks)) {
                                for (int i : face[1]) {
                                    Vertex vert = new Vertex(new Vector3f(vertex[i]).add(x,y,z).add(chunks[1][1][1].getPosition()), material);
                                    if (!indicesMap.containsKey(vert)) {
                                        indicesMap.put(vert, index);
                                        indices.add(index);
                                        index++;
                                        vertices.add(vert);
                                    } else {
                                        indices.add(indicesMap.get(vert));
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        ChunkMesh m = new ChunkMesh();
        m.vertices = BufferUtils.createFloatBuffer(vertices.size() * 4);
        vertices.forEach((v)->{
            m.vertices.put(v.getData());
        });
        m.vertices.flip();

        m.indices = BufferUtils.createIntBuffer(indices.size());
        indices.forEach((i)-> m.indices.put(i));
        m.indices.flip();

        m.indicesCount = indices.size();
        m.pos = chunks[1][1][1].getPosition();

        System.out.println(System.currentTimeMillis() - t);

        return m;
    }

    private static boolean isAir(int x, int y, int z, Chunk[][][] chunks){
        if(x >= 0 && x < Chunk.size && y >= 0 && y < Chunk.size && z >= 0 && z < Chunk.size){
            return chunks[1][1][1].getMaterial(x,y,z) == 0;
        }
        if(x >= Chunk.size || y >= Chunk.size || z >= Chunk.size){
            Chunk c2 = chunks[x >= Chunk.size ? 2 : 1][y >= Chunk.size ? 2 : 1][z >= Chunk.size ? 2 : 1];
            if(c2 != null){
                return c2.getMaterial(x % Chunk.size,y % Chunk.size,z % Chunk.size) == 0;
            }
        }
        if(x < 0 || y < 0 || z < 0){
            Chunk c2 = chunks[x < 0 ? 0 : 1][y < 0 ? 0 : 1][z < 0 ? 0 : 1];
            if(c2 != null){
                return c2.getMaterial((Chunk.size + x)%Chunk.size,(Chunk.size + y)%Chunk.size,(Chunk.size + z)%Chunk.size) == 0;
            }
        }
        return false;
    }

    private static class Vertex{

        private Vector3f position;
        private float material;

        public Vertex(Vector3f position, float material) {
            this.position = position;
            this.material = material;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return Float.compare(vertex.material, material) == 0 &&
                    Objects.equals(position, vertex.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, material);
        }

        public float[] getData(){
            return new float[]{position.x, position.y, position.z, material};
        }
    }

    private static final Vector3f[] vertex = {
            new Vector3f (-0.5f, -0.5f,  0.5f),
            new Vector3f ( 0.5f, -0.5f,  0.5f),
            new Vector3f ( 0.5f,  0.5f,  0.5f),
            new Vector3f (-0.5f,  0.5f,  0.5f),
            new Vector3f (-0.5f, -0.5f, -0.5f),
            new Vector3f ( 0.5f, -0.5f, -0.5f),
            new Vector3f ( 0.5f,  0.5f, -0.5f),
            new Vector3f (-0.5f,  0.5f, -0.5f)
    };

    private static final int[][][] faces = {
            {{ 0, 0, 1},{0,1,2,3}},
            {{ 0, 1, 0},{3,2,6,7}},
            {{ 0, 0,-1},{5,4,7,6}},
            {{-1, 0, 0},{4,0,3,7}},
            {{ 0,-1, 0},{1,0,4,5}},
            {{ 1, 0, 0},{1,5,6,2}}
    };

}

package com.github.sidit77.voxelworld.world;

import com.github.sidit77.voxelworld.Camera;
import org.joml.Vector3f;

import java.util.*;

//TODO add shader to this class (maybe put all the rendering stuff into another class)
//TODO reuse chunk mesh
//TODO multi thread chunk generation
//TODO save chunks to disk
//TODO keep the cache small
//TODO make the terrain editable

public class Terrain {

    public static final int viewdistance = 2;

    private ChunkMeshBuffer meshBuffer;

    private Map<ChunkIndex, Chunk> chunks;
    private Chunk currentChunk = null;
    private Map<ChunkIndex, Integer> loadedChunks;

    public Terrain(){
        chunks = new HashMap<>();
        loadedChunks = new HashMap<>();

        meshBuffer = new ChunkMeshBuffer(((viewdistance-1)*2+1)*((viewdistance-1)*2+1)*((viewdistance-1)*2+1));
    }

    public void update(Vector3f pos){
        Chunk newChunk = getChunkAt(new ChunkIndex(pos));
        if(currentChunk != newChunk){
            currentChunk = newChunk;
            System.out.println("###################");
            List<Integer> ids = new ArrayList<>();
            for(int i = 0; i < ((viewdistance-1)*2+1)*((viewdistance-1)*2+1)*((viewdistance-1)*2+1); i++){
                ids.add(i);
            }
            Set<ChunkIndex> unloadedChunks = new HashSet<>();
            for(int x = -viewdistance+1; x < viewdistance; x++){
                for(int y = -viewdistance+1; y < viewdistance; y++){
                    for(int z = -viewdistance+1; z < viewdistance; z++){
                        ChunkIndex ci = new ChunkIndex(pos, x,y,z);
                        if(loadedChunks.containsKey(ci)){
                            ids.remove(loadedChunks.get(ci));
                        }else{
                            unloadedChunks.add(ci);
                        }
                    }
                }
            }
            Set<ChunkIndex> unusedChunks = new HashSet<>();
            loadedChunks.forEach((ChunkIndex ci, Integer id)->{
                if(ids.contains(id)){
                    unusedChunks.add(ci);
                }
            });
            unusedChunks.forEach((ci)-> loadedChunks.remove(ci));

            for(ChunkIndex ci : unloadedChunks){
                if(!ids.isEmpty()){
                    int id = ids.get(0);
                    ids.remove(0);
                    meshBuffer.setToChunk(id, getChunkAt(ci));
                    loadedChunks.put(ci, id);
                }

            }

            System.out.println("######################");
            System.out.println("loaded " + unloadedChunks.size() + " new chunks!");

        }
    }

    public void render(Camera camera){
        meshBuffer.render();
    }

    public void delete(){
        meshBuffer.delete();
    }

    private Chunk getChunkAt(ChunkIndex ci){
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

    private class LoadedChunk{
        private Chunk chunk;
        private int id;
        public LoadedChunk(Chunk chunk, int id){
            this.chunk = chunk;
            this.id = id;
        }
        public int getID(){
            return id;
        }

    }
}

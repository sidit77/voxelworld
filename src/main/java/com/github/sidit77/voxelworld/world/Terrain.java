package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;

import java.util.*;

//TODO add shader to this class (maybe put all the rendering stuff into another class)
//TODO reuse chunk mesh
//TODO multi thread chunk generation
//TODO save chunks to disk
//TODO keep the cache small
//TODO make the terrain editable

public class Terrain {

    public static final int viewdistance = 3;

    private ChunkMeshBuffer meshBuffer;

    private Map<ChunkIndex, Chunk> chunks;
    private Chunk currentChunk = null;
    private Map<ChunkIndex, Integer> loadedChunks;
    private Set<ChunkIndex> chunksToRemesh;

    public Terrain(){
        chunks = new HashMap<>();
        loadedChunks = new HashMap<>();
        chunksToRemesh = new HashSet<>();
        meshBuffer = new ChunkMeshBuffer(getChunkIndices(new Vector3f(0)).size());
    }

    public void update(Vector3f pos){
        Chunk newChunk = getChunkAt(new ChunkIndex(pos));
        if(currentChunk != newChunk){
            currentChunk = newChunk;
            System.out.println("###################");
            List<Integer> ids = new ArrayList<>();
            for(int i = 0; i < meshBuffer.getMaxNumberOfSlots(); i++){
                ids.add(i);
            }
            Set<ChunkIndex> unloadedChunks = new HashSet<>();
            getChunkIndices(pos).forEach((ci)->{
                if(loadedChunks.containsKey(ci) && !chunksToRemesh.contains(ci)){
                    ids.remove(loadedChunks.get(ci));
                }else{
                    unloadedChunks.add(ci);
                }
            });
            Set<ChunkIndex> unusedChunks = new HashSet<>();
            loadedChunks.forEach((ChunkIndex ci, Integer id)->{
                if(ids.contains(id)){
                    unusedChunks.add(ci);
                }
            });
            unusedChunks.forEach((ci)-> loadedChunks.remove(ci));


            unloadedChunks.stream().forEach((ci)->{
                if(!ids.isEmpty()){
                    int id = ids.get(0);
                    ids.remove(0);
                    meshBuffer.setToChunk(id, getChunkAt(ci));
                    loadedChunks.put(ci, id);
                }
            });

            System.out.println("######################");
            System.out.println("loaded " + unloadedChunks.size() + " new chunks!");

        }else{
            chunksToRemesh.stream().forEach((ci) -> {
                if(loadedChunks.containsKey(ci)){
                    meshBuffer.setToChunk(loadedChunks.get(ci), getChunkAt(ci));
                }
            });
        }
    }

    public void render(){
        meshBuffer.render();
    }

    public void delete(){
        meshBuffer.delete();
    }

    public float getDensity(Vector3f pos){
        Chunk chunk = getChunkAt(new ChunkIndex(pos));
        Vector3f vec = pos.sub(chunk.getPosition());
        return chunk.getDensity((int)vec.x,(int)vec.y,(int)vec.z);
    }

    public void setDensity(Vector3f pos, float value){
        ChunkIndex ci = new ChunkIndex(pos);
        Chunk chunk = getChunkAt(ci);
        Vector3f vec = pos.sub(chunk.getPosition());
        chunk.setDensity((int)vec.x,(int)vec.y,(int)vec.z, value);
        if(!chunksToRemesh.contains(ci)) {
            chunksToRemesh.add(ci);
        }
    }

    private Chunk getChunkAt(ChunkIndex ci){
        if(!chunks.containsKey(ci)){
            chunks.put(ci, new Chunk(ci.getChunkPosition()));
            System.out.println("Created Chunk: " + ci);
        }
        return chunks.get(ci);
    }

    private Set<ChunkIndex> getChunkIndices(Vector3f pos){
        Set<ChunkIndex> result = new HashSet<>();
        for(int x = -viewdistance+1; x < viewdistance; x++) {
            for (int y = -viewdistance + 1; y < viewdistance; y++) {
                for (int z = -viewdistance + 1; z < viewdistance; z++) {
                    if((x*x)+(y*y)+(z*z) < viewdistance*viewdistance){
                        result.add(new ChunkIndex(pos, x, y, z));
                    }
                }
            }
        }
        return result;
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
}

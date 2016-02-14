package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;

import java.util.*;

public class ChunkManager extends Thread{

    private int unloaded = 0;
    private int loaded = 0;

    private boolean shouldEnd;
    private Vector3f playerPosition;
    private int loadDistance;
    private int viewDistance;

    private final Hashtable<ChunkIndex, LoadedChunk> chunks;

    public ChunkManager(Vector3f playerPosition, int viewDistance){
        this(playerPosition, viewDistance + 2, viewDistance);
    }

    public ChunkManager(Vector3f playerPosition, int loadDistance, int viewDistance){

        this.shouldEnd = false;
        this.loadDistance = loadDistance;
        this.viewDistance = viewDistance;
        this.playerPosition = playerPosition;
        this.chunks = new Hashtable<>();

        this.start();
    }

    public void setPlayerPosition(Vector3f playerPosition){
        this.playerPosition = playerPosition;
    }

    public void shutdown(){
        this.shouldEnd = true;
    }

    public List<ChunkMesh> getViewMesh(){
        Set<ChunkIndex> neededChunks = getChunkIndices(playerPosition, viewDistance);
        List<ChunkMesh> meshes = new ArrayList<>();
        neededChunks.forEach((ci)->{
            synchronized (chunks) {
                if (chunks.containsKey(ci)) {
                    LoadedChunk lc = chunks.get(ci);
                    if(lc.isMeshed()){
                        meshes.add(lc.getMesh());
                    }

                }
            }
        });
        return meshes;
    }

    @Override
    public void run() {
        while(!shouldEnd){
            long time = System.currentTimeMillis();
            unloaded = 0;
            loaded = 0;

            Vector3f position = new Vector3f(playerPosition);

            Set<ChunkIndex> neededChunks = getChunkIndices(position, loadDistance);
            chunks.keySet().removeIf((ci) -> {
                if(!neededChunks.contains(ci)){
                    unloaded++;
                    return true;
                }
                return false;
            });

            neededChunks.parallelStream().forEach((ci) -> {
                if (!chunks.containsKey(ci)) {
                    chunks.put(ci, new LoadedChunk(new Chunk(ci.getChunkPosition())));
                    loaded++;
                }
            });

            chunks.entrySet().parallelStream().forEach(e -> {
                if(!e.getValue().isMeshed()){
                    e.getValue().mesh();
                }
            });

            if(loaded != 0 && unloaded != 0){
                System.out.println("+" + loaded + " -" + unloaded + " in " + (System.currentTimeMillis() - time) + "ms");
            }

            try {
                Thread.sleep(Math.max(200 - (System.currentTimeMillis() - time), 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Set<ChunkIndex> getChunkIndices(Vector3f pos, int radius){
        Set<ChunkIndex> result = new HashSet<>();
        for(int x = -radius+1; x < radius; x++) {
            for (int y = -radius + 1; y < radius; y++) {
                for (int z = -radius + 1; z < radius; z++) {
                    if((x*x)+(y*y)+(z*z) < radius*radius){
                        result.add(new ChunkIndex(pos, x, y, z));
                    }
                }
            }
        }
        return result;
    }

    private class LoadedChunk{
        private Chunk chunk;
        private ChunkMesh mesh;
        private boolean meshed;

        public LoadedChunk(Chunk chunk){
            this.chunk = chunk;
            this.meshed = false;
        }

        public void mesh(){
            Chunk[][][] mchunks = new Chunk[3][3][3];
           // mchunks[0][0][0] = chunk;
            meshed = true;
            for(int x = -1; x < 2; x++){
                for(int y = -1; y < 2; y++){
                    for(int z = -1; z < 2; z++){
                        Chunk c;
                        if(x == 0 && y == 0 && z == 0){
                            c = chunk;
                        }else{
                            LoadedChunk lc = chunks.getOrDefault(new ChunkIndex(chunk.getPosition(), x,y,z),null);
                            c = lc != null ? lc.getChunk() : null;
                        }
                        if(c == null){
                            meshed = false;
                        }
                        mchunks[x+1][y+1][z+1] = c;
                    }
                }
            }

            this.mesh = ChunkMesher2.createMesh(mchunks);
        }

        public boolean isMeshed(){
            return meshed && mesh != null;
        }

        public Chunk getChunk() {
            return chunk;
        }

        public ChunkMesh getMesh() {
            return mesh;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LoadedChunk that = (LoadedChunk) o;
            return Objects.equals(chunk, that.chunk) &&
                    Objects.equals(mesh, that.mesh);
        }

        @Override
        public int hashCode() {
            return Objects.hash(chunk, mesh);
        }
    }

}

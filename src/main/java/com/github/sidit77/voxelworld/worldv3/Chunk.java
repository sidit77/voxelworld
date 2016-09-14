package com.github.sidit77.voxelworld.worldv3;

import com.github.sidit77.voxelworld.worldv3.blocks.Blocks;
import com.github.sidit77.voxelworld.worldv3.mesh.ChunkMesh;
import org.joml.Vector3f;

import java.util.ArrayDeque;

public class Chunk {

    public static final int size = 64;

    private Block[][][] blocks;
    private int[][][] light;
    private ChunkIndex index;
    private boolean update;

    public Chunk(ChunkIndex index, IWorldGenerator generator){
        this.index = index;
        blocks = new Block[size][size][size];
        light = new int[size][size][size];
        update = true;

        for(int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    blocks[x][y][z] = Blocks.AIR;
                }
            }
        }

        for(int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    generator.pregenerate((int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z, this);
                }
            }
        }

        for(int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    generator.generate((int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z, this);
                }
            }
        }

        for(int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    generator.postgenerate((int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z, this);
                }
            }
        }

    }

    public void clearLightInformation(){
        for(int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    light[x][y][z] = 0;
                }
            }
        }
    }

    public void updateLight(){
        clearLightInformation();
        for(int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    if(blocks[x][y][z].isLightSource()){
                        addTorchLight((int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z);
                    }
                }
            }
        }
    }

    private void addTorchLight(int x, int y, int z){
        ArrayDeque<Integer[]> lightQueue = new ArrayDeque<>();

        setLightLevel(x,y,z, getBlock(x,y,z).getLightLevel());
        lightQueue.addFirst(new Integer[]{x,y,z});

        while(!lightQueue.isEmpty()){
            Integer[] lightpos = lightQueue.removeFirst();
            int lightLevel = getLightLevel(lightpos[0], lightpos[1], lightpos[2]);
            for(Direction d : Direction.values()){
                int px = lightpos[0] + d.getXOffset();
                int py = lightpos[1] + d.getYOffset();
                int pz = lightpos[2] + d.getZOffset();
                if(!getBlock(px,py,pz).isOpaque() &&
                        getLightLevel(px,py,pz) + 2 <= lightLevel) {

                    setLightLevel(px,py,pz, lightLevel - 1);

                    lightQueue.addFirst(new Integer[]{px,py,pz});
                }
            }
        }
    }

    public void setUpdated(){
        update = false;
    }

    public boolean updateRequired(){
        return update;
    }

    public ChunkMesh getMesh(){
        ChunkMesh mesh = new ChunkMesh();

        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                for(int z = 0; z < size; z++){

                    Block[] neighbors = new Block[6];
                    int[] lightlevels = new int[6];
                    for(Direction d : Direction.values()){
                            neighbors[d.getID()] = getBlock(x + d.getXOffset(), y + d.getYOffset(), z + d.getZOffset());
                            lightlevels[d.getID()] = getLightLevel(x + d.getXOffset(), y + d.getYOffset(), z + d.getZOffset());
                    }

                    blocks[x][y][z].addToChunkMesh(mesh, (int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z, neighbors, lightlevels);
                }
            }
        }

        return mesh;
    }

    public Block getBlock(int x, int y, int z){
        x -= index.getChunkPosition().x;
        y -= index.getChunkPosition().y;
        z -= index.getChunkPosition().z;
        if(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size){
            return blocks[x][y][z];
        }else{
            return Blocks.AIR;
        }
    }

    private void setLightLevel(int x, int y, int z, int l){
        x -= index.getChunkPosition().x;
        y -= index.getChunkPosition().y;
        z -= index.getChunkPosition().z;
        if(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size){
            light[x][y][z] = l;
        }
    }

    public int getLightLevel(int x, int y, int z){
        x -= index.getChunkPosition().x;
        y -= index.getChunkPosition().y;
        z -= index.getChunkPosition().z;
        if(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size){
            return light[x][y][z];
        }else{
            return 0;
        }
    }

    public void setBlock(int x, int y, int z, Block block){
        x -= index.getChunkPosition().x;
        y -= index.getChunkPosition().y;
        z -= index.getChunkPosition().z;
        if(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size){
            blocks[x][y][z] = block;
            update = true;
        }
    }
    public  int getLightLevel(Vector3f pos){
        return getLightLevel(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    }
    public void setBlock(Vector3f pos, Block block){
        setBlock(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z), block);
    }
    public  Block getBlock(Vector3f pos){
        return getBlock(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    }
}

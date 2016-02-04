package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

//TODO add shader to this class (maybe put all the rendering stuff into another class)
//TODO save chunks to disk
//TODO keep the cache small
//TODO make the terrain editable
//TODO optimize terrain buffer
//TODO speed up chunk loading

public class Terrain {

    private ChunkMeshBuffer meshBuffer;

    private ChunkManager chunkManager;

    List<ChunkMesh> loadedMeshes;

    public Terrain(){

        loadedMeshes = new ArrayList<>();

        meshBuffer = new ChunkMeshBuffer(800);

        chunkManager = new ChunkManager(new Vector3f(0), 5, 5);
    }

    public void update(Vector3f pos){
        chunkManager.setPlayerPosition(pos);

        List<ChunkMesh> meshes = chunkManager.getViewMesh();
        if(loadedMeshes.size() != meshes.size() && !loadedMeshes.equals(meshes)){
            loadedMeshes = meshes;
            for(int i = 0; i < loadedMeshes.size(); i++){
                meshBuffer.setToChunk(i, loadedMeshes.get(i));
            }
        }


    }

    public void render(){
        meshBuffer.render();
    }

    public void delete(){
        meshBuffer.delete();
        chunkManager.shutdown();
    }

}

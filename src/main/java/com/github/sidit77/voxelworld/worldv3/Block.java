package com.github.sidit77.voxelworld.worldv3;


import com.github.sidit77.voxelworld.worldv3.mesh.ChunkMesh;
import com.github.sidit77.voxelworld.worldv3.mesh.Vertex;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class Block {

    public int[] textureids;
    private String name;

    public Block(int texture, String name){
        textureids = new int[]{texture,texture,texture,texture,texture,texture};
        this.name = name;
    }

    public boolean isSolid(Direction direction){
        return true;
    }

    public boolean hasHitbox(){
        return true;
    }

    public boolean isOpaque(){
        return true;
    }

    public boolean isLightSource(){
        return false;
    }

    public int getLightLevel(){
        return 15;
    }

    public void setTexture(Direction d, int id){
        textureids[d.getID()] = id;
    }

    public void addToChunkMesh(ChunkMesh mesh, int x, int y, int z, Block[] neightbors, int[] lightslevels){
        for(Direction dir : Direction.values()){
            if(neightbors[dir.getID()] == null || !neightbors[dir.getID()].isSolid(Direction.getOpposite(dir))){
                mesh.addQuad(
                        new Vertex(new Vector3f(x,y,z).add(vertex[faces[dir.getID()][0]]), new Vector2f(1,1), textureids[dir.getID()], lightslevels[dir.getID()]),
                        new Vertex(new Vector3f(x,y,z).add(vertex[faces[dir.getID()][1]]), new Vector2f(0,1), textureids[dir.getID()], lightslevels[dir.getID()]),
                        new Vertex(new Vector3f(x,y,z).add(vertex[faces[dir.getID()][2]]), new Vector2f(0,0), textureids[dir.getID()], lightslevels[dir.getID()]),
                        new Vertex(new Vector3f(x,y,z).add(vertex[faces[dir.getID()][3]]), new Vector2f(1,0), textureids[dir.getID()], lightslevels[dir.getID()]));
            }
        }
    }

    public String getName(){
        return name;
    }

    private static final Vector3f[] vertex = {
            new Vector3f(-0.5f, -0.5f,  0.5f),
            new Vector3f( 0.5f, -0.5f,  0.5f),
            new Vector3f( 0.5f,  0.5f,  0.5f),
            new Vector3f(-0.5f,  0.5f,  0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),
            new Vector3f( 0.5f, -0.5f, -0.5f),
            new Vector3f( 0.5f,  0.5f, -0.5f),
            new Vector3f(-0.5f,  0.5f, -0.5f)
    };

    private static final int[][] faces = {
            {0,1,2,3},
            {1,5,6,2},
            {5,4,7,6},
            {4,0,3,7},
            {3,2,6,7},
            {1,0,4,5}
    };

}

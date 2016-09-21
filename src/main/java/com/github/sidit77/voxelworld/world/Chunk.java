package com.github.sidit77.voxelworld.world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Chunk extends WorldElement{

    public final static int size = 16;

    private WorldElement top, bottom, left, right, front, back;

    private Block[][][] blocks;
    private byte[][][] lightmap;

    private float[] vertices = {};

    private boolean update = true;

    private boolean lighting = false;

    public Chunk() {
        blocks = new Block[size][size][size];
        lightmap = new byte[size][size][size];
    }

    @Override
    public float[] getMesh() {
        return vertices;
    }

    @Override
    public void generateMesh(int x, int y, int z) {

        ArrayList<Float> vlist = new ArrayList<>();

        for(int cx = 0; cx < size; cx++){
            for(int cy = 0; cy < size; cy++){
                for(int cz = 0; cz < size; cz++){

                    if(!blocks[cx][cy][cz].isUnrendered()){
                        if(blocks[cx][cy][cz] instanceof ISpecialRenderer){
                            ((ISpecialRenderer)blocks[cx][cy][cz]).addMeshToList(x + cx, y + cy, z + cz, vlist);
                        }else {
                            for (Direction d : Direction.values()) {
                                if (!getBlock(cx + d.getXOffset(), cy + d.getYOffset(), cz + d.getZOffset()).isSolid(Direction.getOpposite(d))) {
                                    float[] f = faces[d.getID()];
                                    for (int j = 0; j < f.length; j += 5) {
                                        vlist.add(f[j + 0] + x + cx);
                                        vlist.add(f[j + 1] + y + cy);
                                        vlist.add(f[j + 2] + z + cz);
                                        vlist.add(blocks[cx][cy][cz].getTextureID(d) % 4 * 0.25f + f[j + 3] * 0.25f);
                                        vlist.add(blocks[cx][cy][cz].getTextureID(d) / 4 * 0.25f + f[j + 4] * 0.25f);
                                        vlist.add(d.asVector().x);
                                        vlist.add(d.asVector().y);
                                        vlist.add(d.asVector().z);
                                        vlist.add((float)getLightLevel(cx + d.getXOffset(), cy + d.getYOffset(), cz + d.getZOffset()) / 16);
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        vertices = new float[vlist.size()];
        for(int i = 0; i < vertices.length; i++){
            vertices[i] = vlist.get(i);
        }
        update = false;
    }

    @Override
    public void needUpdate() {
        update = true;
    }

    @Override
    public boolean updateRequired(){
        return update;
    }

    @Override
    public byte getLightLevel(int x, int y, int z) {
        if(x < 0){
            return left.getLightLevel(x + size, y, z);
        }
        if(x >= size){
            return right.getLightLevel(x - size, y, z);
        }
        if(y < 0){
            return bottom.getLightLevel(x, y + size, z);
        }
        if(y >= size){
            return top.getLightLevel(x, y - size, z);
        }
        if(z < 0){
            return back.getLightLevel(x, y, z + size);
        }
        if(z >= size){
            return front.getLightLevel(x, y, z - size);
        }
        return lightmap[x][y][z];
    }

    @Override
    public void setLightLevel(int x, int y, int z, byte lvl) {
        if(x < 0){
            left.setLightLevel(x + size, y, z, lvl);
            return;
        }
        if(x >= size){
            right.setLightLevel(x - size, y, z, lvl);
            return;
        }
        if(y < 0){
            bottom.setLightLevel(x, y + size, z, lvl);
            return;
        }
        if(y >= size){
            top.setLightLevel(x, y - size, z, lvl);
            return;
        }
        if(z < 0){
            back.setLightLevel(x, y, z + size, lvl);
            return;
        }
        if(z >= size){
            front.setLightLevel(x, y, z - size, lvl);
            return;
        }
        lightmap[x][y][z] = lvl;
        this.needUpdate();
        if(x == 0)
            left.needUpdate();
        if(y == 0)
            bottom.needUpdate();
        if(z == 0)
            back.needUpdate();
        if(x == size - 1)
            right.needUpdate();
        if(y == size - 1)
            top.needUpdate();
        if(z == size - 1)
            front.needUpdate();
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        if(x < 0){
            return left.getBlock(x + size, y, z);
        }
        if(x >= size){
            return right.getBlock(x - size, y, z);
        }
        if(y < 0){
            return bottom.getBlock(x, y + size, z);
        }
        if(y >= size){
            return top.getBlock(x, y - size, z);
        }
        if(z < 0){
            return back.getBlock(x, y, z + size);
        }
        if(z >= size){
            return front.getBlock(x, y, z - size);
        }
        return blocks[x][y][z];
    }

    @Override
    public void setBlock(int x, int y, int z, Block b) {
        if(x < 0){
            left.setBlock(x + size, y, z, b);
            return;
        }
        if(x >= size){
            right.setBlock(x - size, y, z, b);
            return;
        }
        if(y < 0){
            bottom.setBlock(x, y + size, z, b);
            return;
        }
        if(y >= size){
            top.setBlock(x, y - size, z, b);
            return;
        }
        if(z < 0){
            back.setBlock(x, y, z + size, b);
            return;
        }
        if(z >= size){
            front.setBlock(x, y, z - size, b);
            return;
        }

        Block old = blocks[x][y][z];
        blocks[x][y][z] = b;

        if(lighting){
            if(old instanceof ILightSource){
                byte l = lightmap[x][y][z];
                lightmap[x][y][z] = 0;
                removeLight(x,y,z, l);
            }
            if(b instanceof ILightSource){
                lightmap[x][y][z] = (byte)Math.max(lightmap[x][y][z], ((ILightSource)b).getLightLevel());
                addLight(x,y,z);
            }
            if(b.isOpaque() && !old.isOpaque()){
                byte l = lightmap[x][y][z];
                lightmap[x][y][z] = 0;
                removeLight(x,y,z, l);
            }
            if(!b.isOpaque() && old.isOpaque()){
                byte biggestlevel = 0;
                for(Direction d : Direction.values()){
                    if(!getBlock(x + d.getXOffset(), y + d.getYOffset(), z + d.getZOffset()).isOpaque()){
                        biggestlevel = (byte)Math.max(biggestlevel, getLightLevel(x + d.getXOffset(), y + d.getYOffset(), z + d.getZOffset()) - 1);
                    }
                }
                lightmap[x][y][z] = biggestlevel;
                addLight(x,y,z);
            }
        }

        this.needUpdate();
        if(x == 0)
            left.needUpdate();
        if(y == 0)
            bottom.needUpdate();
        if(z == 0)
            back.needUpdate();
        if(x == size - 1)
            right.needUpdate();
        if(y == size - 1)
            top.needUpdate();
        if(z == size - 1)
            front.needUpdate();
    }

    @Override
    public void setTop(WorldElement top) {
        this.top = top;
    }

    @Override
    public void setBottom(WorldElement bottom) {
        this.bottom = bottom;
    }

    @Override
    public void setLeft(WorldElement left) {
        this.left = left;
    }

    @Override
    public void setRight(WorldElement right) {
        this.right = right;
    }

    @Override
    public void setFront(WorldElement front) {
        this.front = front;
    }

    @Override
    public void setBack(WorldElement back) {
        this.back = back;
    }

    @Override
    public void setLighting(boolean enabled) {
        lighting = enabled;
        if(lighting) {
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    for (int z = 0; z < size; z++) {
                        if (blocks[x][y][z] instanceof ILightSource) {
                            lightmap[x][y][z] = (byte) Math.max(lightmap[x][y][z], ((ILightSource) blocks[x][y][z]).getLightLevel());
                            addLight(x, y, z);
                        }
                    }
                }
            }
        }else{
            lightmap = new byte[size][size][size];
        }
    }


    private void addLight(int x, int y, int z){
        Queue<LightNode> lightQueue = new LinkedList<>();
        lightQueue.add(new LightNode(x,y,z));

        while(!lightQueue.isEmpty()){
            LightNode node = lightQueue.remove();

            byte lightlevel = getLightLevel(node.getX(),node.getY(),node.getZ());
            for(Direction d : Direction.values()){
                if(!getBlock(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset()).isOpaque() &&
                    getLightLevel(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset()) + 2 <= lightlevel){

                    setLightLevel(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset(), (byte)(lightlevel - 1));
                    lightQueue.add(new LightNode(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset()));
                }
            }

        }
    }

    private void removeLight(int x, int y, int z, byte lslvl){
        Queue<LightRemovalNode> lightRemoveQueue = new LinkedList<>();
        Queue<LightNode> lightQueue = new LinkedList<>();

        lightRemoveQueue.add(new LightRemovalNode(x,y,z, lslvl));

        while(!lightRemoveQueue.isEmpty()){
            LightRemovalNode node = lightRemoveQueue.remove();

            for(Direction d : Direction.values()){
                byte neighborlevel = getLightLevel(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset());

                if(neighborlevel != 0 && neighborlevel < node.getVal()){
                    setLightLevel(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset(), (byte)0);
                    lightRemoveQueue.add(new LightRemovalNode(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset(), neighborlevel));
                }else if(neighborlevel >= node.getVal()){
                    lightQueue.add(new LightNode(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset()));
                }
            }
        }

        while(!lightQueue.isEmpty()){
            LightNode node = lightQueue.remove();

            byte lightlevel = getLightLevel(node.getX(),node.getY(),node.getZ());
            for(Direction d : Direction.values()){
                if(!getBlock(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset()).isOpaque() &&
                    getLightLevel(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset()) + 2 <= lightlevel){

                    setLightLevel(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset(), (byte)(lightlevel - 1));
                    lightQueue.add(new LightNode(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset()));
                }
            }

        }
    }

    private class LightNode{

        private int x, y, z;

        public LightNode(int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

    }

    private class LightRemovalNode{

        private int x, y, z;
        private byte val;

        public LightRemovalNode(int x, int y, int z, byte val){
            this.x = x;
            this.y = y;
            this.z = z;
            this.val = val;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public byte getVal(){
            return val;
        }
    }

    float[][] faces = {
            {
                    -0.5f, -0.5f,  0.5f, 0, 1,
                    0.5f, -0.5f,  0.5f,  1, 1,
                    0.5f,  0.5f,  0.5f,  1, 0,
                    0.5f,  0.5f,  0.5f,  1, 0,
                    -0.5f,  0.5f,  0.5f, 0, 0,
                    -0.5f, -0.5f,  0.5f, 0, 1
            },
            {
                    0.5f, -0.5f,  0.5f,0, 1,
                    0.5f, -0.5f, -0.5f,1, 1,
                    0.5f,  0.5f, -0.5f,1, 0,
                    0.5f,  0.5f, -0.5f,1, 0,
                    0.5f,  0.5f,  0.5f,0, 0,
                    0.5f, -0.5f,  0.5f,0, 1
            },
            {
                    0.5f, -0.5f, -0.5f,0, 1,
                    -0.5f, -0.5f, -0.5f,1,1,
                    -0.5f,  0.5f, -0.5f,1,0,
                    -0.5f,  0.5f, -0.5f,1,0,
                    0.5f,  0.5f, -0.5f,0, 0,
                    0.5f, -0.5f, -0.5f,0, 1
            },
            {
                    -0.5f, -0.5f, -0.5f,0, 1,
                    -0.5f, -0.5f,  0.5f,1, 1,
                    -0.5f,  0.5f,  0.5f,1, 0,
                    -0.5f,  0.5f,  0.5f,1, 0,
                    -0.5f,  0.5f, -0.5f,0, 0,
                    -0.5f, -0.5f, -0.5f,0, 1
            },
            {
                    -0.5f,  0.5f,  0.5f,0,1,
                    0.5f,  0.5f,  0.5f,1, 1,
                    0.5f,  0.5f, -0.5f,1, 0,
                    0.5f,  0.5f, -0.5f,1, 0,
                    -0.5f,  0.5f, -0.5f,0,0,
                    -0.5f,  0.5f,  0.5f,0,1
            },
            {
                    0.5f, -0.5f,  0.5f,0, 1,
                    -0.5f, -0.5f,  0.5f,1,1,
                    -0.5f, -0.5f, -0.5f,1,0,
                    -0.5f, -0.5f, -0.5f,1,0,
                    0.5f, -0.5f, -0.5f,0, 0,
                    0.5f, -0.5f,  0.5f,0, 1
            }
    };

    //private Block[][][] blocks;
    //private int[][][] light;
    //private ChunkIndex index;
    //private boolean update;
//
    //public Chunk(ChunkIndex index, IWorldGenerator generator){
    //    this.index = index;
    //    blocks = new Block[size][size][size];
    //    light = new int[size][size][size];
    //    update = true;
//
    //    for(int x = 0; x < size; x++) {
    //        for (int y = 0; y < size; y++) {
    //            for (int z = 0; z < size; z++) {
    //                blocks[x][y][z] = Blocks.AIR;
    //            }
    //        }
    //    }
//
    //    for(int x = 0; x < size; x++) {
    //        for (int y = 0; y < size; y++) {
    //            for (int z = 0; z < size; z++) {
    //                generator.pregenerate((int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z, this);
    //            }
    //        }
    //    }
//
    //    for(int x = 0; x < size; x++) {
    //        for (int y = 0; y < size; y++) {
    //            for (int z = 0; z < size; z++) {
    //                generator.generate((int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z, this);
    //            }
    //        }
    //    }
//
    //    for(int x = 0; x < size; x++) {
    //        for (int y = 0; y < size; y++) {
    //            for (int z = 0; z < size; z++) {
    //                generator.postgenerate((int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z, this);
    //            }
    //        }
    //    }
//
    //}
//
    //public void clearLightInformation(){
    //    for(int x = 0; x < size; x++) {
    //        for (int y = 0; y < size; y++) {
    //            for (int z = 0; z < size; z++) {
    //                light[x][y][z] = 0;
    //            }
    //        }
    //    }
    //}
//
    //public void updateLight(){
    //    clearLightInformation();
    //    for(int x = 0; x < size; x++) {
    //        for (int y = 0; y < size; y++) {
    //            for (int z = 0; z < size; z++) {
    //                if(blocks[x][y][z].isLightSource()){
    //                    addTorchLight((int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z);
    //                }
    //            }
    //        }
    //    }
    //}
//
    //private void addTorchLight(int x, int y, int z){
    //    ArrayDeque<Integer[]> lightQueue = new ArrayDeque<>();
//
    //    setLightLevel(x,y,z, getBlock(x,y,z).getLightLevel());
    //    lightQueue.addFirst(new Integer[]{x,y,z});
//
    //    while(!lightQueue.isEmpty()){
    //        Integer[] lightpos = lightQueue.removeFirst();
    //        int lightLevel = getLightLevel(lightpos[0], lightpos[1], lightpos[2]);
    //        for(Direction d : Direction.values()){
    //            int px = lightpos[0] + d.getXOffset();
    //            int py = lightpos[1] + d.getYOffset();
    //            int pz = lightpos[2] + d.getZOffset();
    //            if(!getBlock(px,py,pz).isOpaque() &&
    //                    getLightLevel(px,py,pz) + 2 <= lightLevel) {
//
    //                setLightLevel(px,py,pz, lightLevel - 1);
//
    //                lightQueue.addFirst(new Integer[]{px,py,pz});
    //            }
    //        }
    //    }
    //}
//
    //public void setUpdated(){
    //    update = false;
    //}
//
    //public boolean updateRequired(){
    //    return update;
    //}
//
    //public ChunkMesh getMesh(){
    //    ChunkMesh mesh = new ChunkMesh();
//
    //    for(int x = 0; x < size; x++){
    //        for(int y = 0; y < size; y++){
    //            for(int z = 0; z < size; z++){
//
    //                Block[] neighbors = new Block[6];
    //                int[] lightlevels = new int[6];
    //                for(Direction d : Direction.values()){
    //                        neighbors[d.getID()] = getBlock(x + d.getXOffset(), y + d.getYOffset(), z + d.getZOffset());
    //                        lightlevels[d.getID()] = getLightLevel(x + d.getXOffset(), y + d.getYOffset(), z + d.getZOffset());
    //                }
//
    //                blocks[x][y][z].addToChunkMesh(mesh, (int)index.getChunkPosition().x + x, (int)index.getChunkPosition().y + y, (int)index.getChunkPosition().z + z, neighbors, lightlevels);
    //            }
    //        }
    //    }
//
    //    return mesh;
    //}
//
    //public Block getBlock(int x, int y, int z){
    //    x -= index.getChunkPosition().x;
    //    y -= index.getChunkPosition().y;
    //    z -= index.getChunkPosition().z;
    //    if(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size){
    //        return blocks[x][y][z];
    //    }else{
    //        return Blocks.AIR;
    //    }
    //}
//
    //private void setLightLevel(int x, int y, int z, int l){
    //    x -= index.getChunkPosition().x;
    //    y -= index.getChunkPosition().y;
    //    z -= index.getChunkPosition().z;
    //    if(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size){
    //        light[x][y][z] = l;
    //    }
    //}
//
    //public int getLightLevel(int x, int y, int z){
    //    x -= index.getChunkPosition().x;
    //    y -= index.getChunkPosition().y;
    //    z -= index.getChunkPosition().z;
    //    if(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size){
    //        return light[x][y][z];
    //    }else{
    //        return 0;
    //    }
    //}
//
    //public void setBlock(int x, int y, int z, Block block){
    //    x -= index.getChunkPosition().x;
    //    y -= index.getChunkPosition().y;
    //    z -= index.getChunkPosition().z;
    //    if(x >= 0 && x < size && y >= 0 && y < size && z >= 0 && z < size){
    //        blocks[x][y][z] = block;
    //        update = true;
    //    }
    //}
    //public  int getLightLevel(Vector3f pos){
    //    return getLightLevel(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    //}
    //public void setBlock(Vector3f pos, Block block){
    //    setBlock(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z), block);
    //}
    //public  Block getBlock(Vector3f pos){
    //    return getBlock(Math.round(pos.x), Math.round(pos.y), Math.round(pos.z));
    //}
}

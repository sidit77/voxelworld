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

    private Queue<LightRemovalNode> lightRemoveQueue = new LinkedList<>();
    private Queue<LightNode> lightQueue = new LinkedList<>();

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

        //for each block....
        for(int cx = 0; cx < size; cx++){
            for(int cy = 0; cy < size; cy++){
                for(int cz = 0; cz < size; cz++){

                    if(!blocks[cx][cy][cz].isUnrendered()){

                        //let the block generate his mesh or generate it for him
                        if(blocks[cx][cy][cz] instanceof ISpecialRenderer){
                            ((ISpecialRenderer)blocks[cx][cy][cz]).addMeshToList(x + cx, y + cy, z + cz, getLightLevel(cx, cy, cz), vlist);
                            System.out.println(getLightLevel(cx, cy, cz));
                        }else {
                            for (Direction d : Direction.values()) {
                                //if the face isn't occluded
                                if (!getBlock(cx + d.getXOffset(), cy + d.getYOffset(), cz + d.getZOffset()).isSolid(Direction.getOpposite(d))) {
                                    float[][] f = faces[d.getID()];

                                    //generate per vertex light values based on the 4 surrounding per block light values
                                    float[] ao = new float[4];
                                    float[] ll = new float[4];
                                    for(int j = 0; j < 4; j++){
                                        for(int i = 0; i < 4; i++){
                                            int[] offsets = {0,0,0};
                                            offsets[smoothlight[d.getID()][0]] = (i / 2) * (int)Math.signum(f[j][smoothlight[d.getID()][0]]);
                                            offsets[smoothlight[d.getID()][1]] = (i % 2) * (int)Math.signum(f[j][smoothlight[d.getID()][1]]);
                                            ao[j] += (getBlock(cx + d.getXOffset() + offsets[0], cy + d.getYOffset() + offsets[1], cz + d.getZOffset() + offsets[2]).isOpaque() ? 0.0f : 0.25f);
                                            ll[j] += (float)getLightLevel(cx + d.getXOffset() + offsets[0], cy + d.getYOffset() + offsets[1], cz + d.getZOffset() + offsets[2]) / (16 * 4);
                                        }
                                    }

                                    //add the face to the list
                                    for (int j : order[ao[1] + ao[3] > ao[0] + ao[2] ? 1 : 0]) {
                                        vlist.add(f[j][0] + x + cx);
                                        vlist.add(f[j][1] + y + cy);
                                        vlist.add(f[j][2] + z + cz);
                                        vlist.add(blocks[cx][cy][cz].getTextureID(d) % 4 * 0.25f + f[j][3] * 0.25f);
                                        vlist.add(blocks[cx][cy][cz].getTextureID(d) / 4 * 0.25f + f[j][4] * 0.25f);
                                        vlist.add(d.asVector().x);
                                        vlist.add(d.asVector().y);
                                        vlist.add(d.asVector().z);

                                        vlist.add(ll[j]);
                                        vlist.add(ao[j]);
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
        if(blocks[x][y][z] instanceof ILightSource){
            return (byte)Math.max(lightmap[x][y][z], ((ILightSource)blocks[x][y][z]).getLightLevel());
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

        //correct the light without recalculating it
        if(lighting){
            if(old instanceof ILightSource){
                byte l = lightmap[x][y][z];
                lightmap[x][y][z] = 0;
                lightRemoveQueue.add(new LightRemovalNode(x,y,z, l));
            }
            if(b instanceof ILightSource){
                byte ol = lightmap[x][y][z];
                byte nl = ((ILightSource)b).getLightLevel();
                lightmap[x][y][z] = b.isOpaque() ? nl : (byte)Math.max(nl, ol);
                lightRemoveQueue.add(new LightRemovalNode(x,y,z,ol));
                lightQueue.add(new LightNode(x,y,z));
            }
            if(b.isOpaque() && !old.isOpaque() && !(b instanceof ILightSource)){
                byte l = lightmap[x][y][z];
                lightmap[x][y][z] = 0;
                lightRemoveQueue.add(new LightRemovalNode(x,y,z, l));
            }
            if(!b.isOpaque() && old.isOpaque()){
                byte biggestlevel = 0;
                for(Direction d : Direction.values()){
                    if(!getBlock(x + d.getXOffset(), y + d.getYOffset(), z + d.getZOffset()).isOpaque()){
                        biggestlevel = (byte)Math.max(biggestlevel, getLightLevel(x + d.getXOffset(), y + d.getYOffset(), z + d.getZOffset()) - 1);
                    }
                }
                lightmap[x][y][z] = biggestlevel;
                lightQueue.add(new LightNode(x,y,z));
            }
            updateLight();
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
                            lightQueue.add(new LightNode(x, y, z));
                        }
                    }
                }
            }
            updateLight();
        }else{
            lightmap = new byte[size][size][size];
        }

    }

    private void updateLight(){

        //apply to changes to the lightmap

        while(!lightRemoveQueue.isEmpty()){
            LightRemovalNode node = lightRemoveQueue.remove();

            for(Direction d : Direction.values()){
                byte neighborlevel = getLightLevel(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset());
                boolean isls = getBlock(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset()) instanceof ILightSource;

                if(neighborlevel != 0 && neighborlevel < node.getVal() && !isls){
                    setLightLevel(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset(), (byte)0);
                    lightRemoveQueue.add(new LightRemovalNode(node.getX() + d.getXOffset(), node.getY() + d.getYOffset(), node.getZ() + d.getZOffset(), neighborlevel));
                }else if(neighborlevel >= node.getVal() || isls){
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

    //some lookup tables

    private static final int[][] order = {
            {0, 1, 2, 2, 3, 0},
            {1, 2, 3, 3, 0, 1}
    };

    private static final float[][][] faces = {
            {
                    {-0.5f, -0.5f,  0.5f, 0, 1},
                    {0.5f, -0.5f,  0.5f,  1, 1},
                    {0.5f,  0.5f,  0.5f,  1, 0},
                    {-0.5f,  0.5f,  0.5f, 0, 0}
            },
            {
                    {0.5f, -0.5f,  0.5f,0, 1},
                    {0.5f, -0.5f, -0.5f,1, 1},
                    {0.5f,  0.5f, -0.5f,1, 0},
                    {0.5f,  0.5f,  0.5f,0, 0}
            },
            {
                    {0.5f, -0.5f, -0.5f,0, 1},
                    {-0.5f, -0.5f, -0.5f,1,1},
                    {-0.5f,  0.5f, -0.5f,1,0},
                    {0.5f,  0.5f, -0.5f,0, 0}
            },
            {
                    {-0.5f, -0.5f, -0.5f,0, 1},
                    {-0.5f, -0.5f,  0.5f,1, 1},
                    {-0.5f,  0.5f,  0.5f,1, 0},
                    {-0.5f,  0.5f, -0.5f,0, 0}
            },
            {
                    {-0.5f,  0.5f,  0.5f,0,1},
                    {0.5f,  0.5f,  0.5f,1, 1},
                    {0.5f,  0.5f, -0.5f,1, 0},
                    {-0.5f,  0.5f, -0.5f,0,0}
            },
            {
                    {0.5f, -0.5f,  0.5f,0, 1},
                    {-0.5f, -0.5f,  0.5f,1,1},
                    {-0.5f, -0.5f, -0.5f,1,0},
                    {0.5f, -0.5f, -0.5f,0, 0}
            }
    };

    private static final int[][] smoothlight = {
        {0,1},
        {1,2},
        {0,1},
        {1,2},
        {0,2},
        {0,2},
    };

}

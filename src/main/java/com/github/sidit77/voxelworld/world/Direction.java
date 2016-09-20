package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;

public enum Direction {

    NORTH(new Vector3f( 0, 0, 1), 0),
    EAST (new Vector3f( 1, 0, 0), 1),
    SOUTH(new Vector3f( 0, 0,-1), 2),
    WEST (new Vector3f(-1, 0, 0), 3),
    UP   (new Vector3f( 0, 1, 0), 4),
    DOWN (new Vector3f( 0,-1, 0), 5);

    private final Vector3f dir;
    private final int id;

    Direction(Vector3f dir, int id){
        this.dir = dir;
        this.id = id;
    }

    public Vector3f asVector(){
        return dir;
    }

    public int getID(){
        return id;
    }

    public int getXOffset(){
        return (int)dir.x;
    }

    public int getYOffset(){
        return (int)dir.y;
    }

    public int getZOffset(){
        return (int)dir.z;
    }

    public static Direction getOpposite(Direction d){
        switch (d){
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case EAST:  return WEST;
            case WEST:  return EAST;
            case UP:    return DOWN;
            case DOWN:  return UP;
        }
        return null;
    }

    public static Direction getFromOffset(int x, int y, int z){
        for(Direction d : values()){
            if(d.getXOffset() == x && d.getYOffset() == y && d.getZOffset() == z)return d;
        }
        return null;
    }

    public static Direction getFromID(int id){
        for(Direction d : values()){
            if(d.getID() == id)return d;
        }
        return null;
    }

}

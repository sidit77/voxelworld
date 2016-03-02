package com.github.sidit77.voxelworld;

public class Main {

    public static void main(String[] args){
        boolean fullscreen = false, playmode = false;

        for(String s : args){
            if(s.equalsIgnoreCase("fullscreen"))fullscreen = true;
            if(s.equalsIgnoreCase("playmode"))playmode = true;
        }

        new VoxelGameWindow(fullscreen, playmode).run();
    }

}

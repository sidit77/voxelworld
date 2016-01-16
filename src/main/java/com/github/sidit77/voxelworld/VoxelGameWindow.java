package com.github.sidit77.voxelworld;

import com.github.sidit77.voxelworld.system.GameWindow;
import org.lwjgl.opengl.GL11;

public class VoxelGameWindow extends GameWindow{

    public VoxelGameWindow() {
        super("Voxel Game", 1280, 720);
    }

    @Override
    public void load() {
        System.out.println(GL11.glGetString(GL11.GL_VERSION));

        GL11.glClearColor(1,0,0,1);

    }

    @Override
    public void update(double time) {
        setTitle("Voxel Game (" + Math.round(1/time) + ")");
    }

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void resize(int width, int height) {
        System.out.println(width + "|" + height);
    }
}

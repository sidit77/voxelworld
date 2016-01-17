package com.github.sidit77.voxelworld;

import com.github.sidit77.voxelworld.system.GameWindow;
import com.github.sidit77.voxelworld.system.input.Action;
import com.github.sidit77.voxelworld.system.input.Key;
import com.github.sidit77.voxelworld.system.input.MouseButton;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class VoxelGameWindow extends GameWindow{

    public VoxelGameWindow() {
        super("Voxel Game", 1280, 720);
    }

    @Override
    public void load() {
        System.out.println(GL11.glGetString(GL11.GL_VERSION));

        GL11.glClearColor(0.5f, 0.5f, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);


        getKeyboard().addKeyEvent((Key key, Action action) -> {
            if(action == Action.Press && key == Key.Escape){
                exit();
            }
        });

    }

    @Override
    public void update(double time) {
        setTitle("Voxel Game (" + Math.round(1/time) + ")");

        if(getMouse().isButtonDown(MouseButton.Left) && getMouse().isCursorEnabled()){
            getMouse().setCursor(false);
        }
        if(getMouse().isButtonDown(MouseButton.Right) && !getMouse().isCursorEnabled()){
            getMouse().setCursor(true);
        }

        if(getKeyboard().isKeyDown(Key.Space)){
            System.out.println(getMouse().getDeltaMouseX() + "|" + getMouse().getDeltaMouseY());
        }
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
        GL11.glViewport(0, 0, width, height);
    }
}

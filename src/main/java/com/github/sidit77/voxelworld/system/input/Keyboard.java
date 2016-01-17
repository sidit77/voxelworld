package com.github.sidit77.voxelworld.system.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.HashSet;
import java.util.Set;

public class Keyboard {

    private long windowid;
    private Set<KeyEvent> keyEvents;

    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int keycode, int scancode, int actioncode, int mods) {
            Key key = Key.getKeyFromKeyCode(keycode);
            Action action = (actioncode == GLFW.GLFW_PRESS ? Action.Press : actioncode == GLFW.GLFW_RELEASE ? Action.Release : Action.Repeat);
            keyEvents.forEach((keyEvent) -> {
                keyEvent.invoke(key, action);
            });
        }
    };

    public Keyboard(long window){
        this.windowid = window;
        this.keyEvents = new HashSet<>();
        GLFW.glfwSetKeyCallback(window, keyCallback);
    }

    public void destroy(){
        keyCallback.release();
    }

    public boolean isKeyDown(Key key){
        return GLFW.glfwGetKey(windowid, key.getKeyCode()) == GLFW.GLFW_PRESS;
    }

    public void addKeyEvent(KeyEvent k){
        keyEvents.add(k);
    }

    public void removeKeyEvent(KeyEvent k){
        keyEvents.remove(k);
    }

    public interface KeyEvent{
        public void invoke(Key key, Action action);
    }

}

package com.github.sidit77.voxelworld.system.input;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.DoubleBuffer;
import java.util.HashSet;
import java.util.Set;

public class Mouse {

    private DoubleBuffer buffer;

    private long windowid;
    private Set<ButtonEvent> buttonEvents;

    private float scrolllevel = 0;

    private float oldscrolllevel = 0;
    private float oldx = 0;
    private float oldy = 0;

    private GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int buttoncode, int actioncode, int mods) {
            MouseButton button = MouseButton.getMouseButtonFromMouseButtonCode(buttoncode);
            Action action = (actioncode == GLFW.GLFW_PRESS ? Action.Press : actioncode == GLFW.GLFW_RELEASE ? Action.Release : Action.Repeat);
            buttonEvents.forEach((buttonEvent) -> {
                buttonEvent.invoke(button, action);
            });
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            scrolllevel += yoffset;
        }
    };

    public Mouse(long window){
        windowid = window;
        buttonEvents = new HashSet<>();
        buffer = BufferUtils.createDoubleBuffer(1);
        GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback);
        GLFW.glfwSetScrollCallback(window, scrollCallback);
    }

    public void destroy(){
        mouseButtonCallback.release();
        scrollCallback.release();
    }

    public void update(){
        oldscrolllevel = getScrollLevel();
        oldx = getMouseX();
        oldy = getMouseY();
    }

    public boolean isButtonDown(MouseButton button){
        return GLFW.glfwGetMouseButton(windowid, button.getMouseButtonCode()) == GLFW.GLFW_PRESS;
    }

    public void addButtonEvent(ButtonEvent e){
        buttonEvents.add(e);
    }

    public void removeButttonEvent(ButtonEvent e){
        buttonEvents.remove(e);
    }

    public interface ButtonEvent{
        void invoke(MouseButton button, Action action);
    }

    public float getScrollLevel(){
        return scrolllevel;
    }

    public float getDeltaScrollLevel(){
        return scrolllevel - oldscrolllevel;
    }

    public void resetScrollLevel(){
        scrolllevel = 0;
    }

    public float getMouseX(){
        buffer.clear();
        GLFW.glfwGetCursorPos(windowid, buffer, null);
        return (float)buffer.get(0);
    }

    public float getMouseY(){
        buffer.clear();
        GLFW.glfwGetCursorPos(windowid, null, buffer);
        return (float)buffer.get(0);
    }

    public float getDeltaMouseX(){
        return getMouseX() - oldx;
    }

    public float getDeltaMouseY(){
        return getMouseY() - oldy;
    }

    public void setCursor(boolean enabled){
        GLFW.glfwSetInputMode(windowid, GLFW.GLFW_CURSOR, enabled ? GLFW.GLFW_CURSOR_NORMAL : GLFW.GLFW_CURSOR_DISABLED);
    }

    public boolean isCursorEnabled(){
        return GLFW.glfwGetInputMode(windowid, GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_NORMAL;
    }

}

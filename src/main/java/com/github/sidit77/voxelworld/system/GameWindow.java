package com.github.sidit77.voxelworld.system;

import com.github.sidit77.voxelworld.system.input.Keyboard;
import com.github.sidit77.voxelworld.system.input.Mouse;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public abstract class GameWindow {

    private static int numberofwindows = 0;
    private static GLFWErrorCallback errorCallback;

    private GLFWFramebufferSizeCallback sizeCallback = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long window, int nwidth, int nheight) {
            width = nwidth;
            height = nheight;
            resized = true;
        }
    };

    private GLFWWindowPosCallback posCallback = new GLFWWindowPosCallback() {
        @Override
        public void invoke(long window, int xpos, int ypos) {
            x = xpos;
            y = ypos;
        }
    };

    private long windowid;
    private int width;
    private int height;
    private int x;
    private int y;
    private boolean resized;
    private String title;
    private Keyboard keyboard;
    private Mouse mouse;
    //TODO add Gamepad input

    public GameWindow(){
        this("Game Window");
    }

    public GameWindow(String title){
        this(title, 1280, 720);
    }

    public GameWindow(String title, int width, int height){
        this(title, width, height, 4, 3);
    }

    public GameWindow(String title, int width, int height, int major, int minor){
        this.width = width;
        this.height = height;
        this.resized = false;
        this.title = title;

        if(numberofwindows <= 0){
            GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
            if (GLFW.glfwInit() != GL11.GL_TRUE)
                throw new IllegalStateException("Unable to initialize GLFW");
        }
        numberofwindows++;

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        windowid = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        GLFW.glfwSetFramebufferSizeCallback(windowid, sizeCallback);
        GLFW.glfwSetWindowPosCallback(windowid, posCallback);
        if (windowid == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(
                windowid,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        keyboard = new Keyboard(windowid);
        mouse = new Mouse(windowid);

    }

    public void run(){
        GLFW.glfwMakeContextCurrent(windowid);
        GLFW.glfwShowWindow(windowid);
        GL.createCapabilities();
        GLFW.glfwSetTime(0);
        load();
        while (GLFW.glfwWindowShouldClose(windowid) == GLFW.GLFW_FALSE) {
            double time = GLFW.glfwGetTime();
            GLFW.glfwSetTime(0);

            GL.createCapabilities();

            if(resized){
                resized = false;
                resize(width, height);
            }

            update(time);
            mouse.update();
            render();

            GLFW.glfwSwapBuffers(windowid);
            GLFW.glfwPollEvents();
        }
        destroy();

        sizeCallback.release();
        posCallback.release();

        keyboard.destroy();
        mouse.destroy();

        GLFW.glfwDestroyWindow(windowid);

        numberofwindows--;
        if(numberofwindows <= 0){
            GLFW.glfwTerminate();
            errorCallback.release();
        }
    }

    public void exit(){
        GLFW.glfwSetWindowShouldClose(windowid, GLFW.GLFW_TRUE);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height){
        GLFW.glfwSetWindowSize(windowid, width, height);
    }

    public void setWidth(int width){
        GLFW.glfwSetWindowSize(windowid, width, height);
    }

    public void setSize(int width, int height){
        GLFW.glfwSetWindowSize(windowid, width, height);
    }

    public void setTitle(String title){
        GLFW.glfwSetWindowTitle(windowid, title);
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setVsync(boolean vsync){
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        GLFW.glfwSetWindowPos(windowid,x,y);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        GLFW.glfwSetWindowPos(windowid,x,y);
    }

    public void setPosition(int x, int y){
        GLFW.glfwSetWindowPos(windowid,x,y);
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public abstract void load();
    public abstract void update(double time);
    public abstract void render();
    public abstract void destroy();
    public abstract void resize(int width, int height);
}

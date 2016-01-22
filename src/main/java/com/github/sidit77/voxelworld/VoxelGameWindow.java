package com.github.sidit77.voxelworld;

import com.github.sidit77.voxelworld.opengl.framebuffer.FrameBuffer;
import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.EmptyTexture2D;
import com.github.sidit77.voxelworld.opengl.texture.Texture2D;
import com.github.sidit77.voxelworld.system.GameWindow;
import com.github.sidit77.voxelworld.system.input.Action;
import com.github.sidit77.voxelworld.system.input.Key;
import com.github.sidit77.voxelworld.system.input.MouseButton;
import com.github.sidit77.voxelworld.world.Mesh;
import com.github.sidit77.voxelworld.world.dualcontouring.Octree;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

public class VoxelGameWindow extends GameWindow{

    public VoxelGameWindow() {
        super("Voxel Game", 1280, 720, false);
    }

    private int vaoId;
    private int vboId;
    private int indexCount;

    private int iboId;

    private GLSLProgram shader;
    private GLSLProgram hudshader;
    private GLSLProgram ppshader;
    private Texture2D texture;
    private Camera camera;
    private boolean fog = false;

    EmptyTexture2D renderTexture;
    EmptyTexture2D depthTexture;
    //RenderBuffer renderBuffer;
    FrameBuffer framebuffer;

    @Override
    public void load() {
        System.out.println(GL11.glGetString(GL11.GL_VERSION));

        GL11.glClearColor(0.5f, 0.5f, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);

        getMouse().addButtonEvent((MouseButton button, Action action) -> {
            if(button == MouseButton.Left && action == Action.Press && getMouse().isCursorEnabled()){
                getMouse().setCursor(false);
            }
        });

        getKeyboard().addKeyEvent((Key key, Action action) -> {
            if(key == Key.Escape && action == Action.Press){
                if(!getMouse().isCursorEnabled()){
                    getMouse().setCursor(true);
                }else{
                    exit();
                }
            }
            if(key == Key.F10 && action == Action.Press){
                if(GL11.glIsEnabled(GL11.GL_CULL_FACE)){
                    GL11.glDisable(GL11.GL_CULL_FACE);
                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                }else{
                    GL11.glEnable(GL11.GL_CULL_FACE);
                    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                }
            }
            if(key == Key.F9 && action == Action.Press){
                fog = !fog;
                shader.bind();
                shader.setUniform("fog", fog ? 1 : 0);
            }
            if(key == Key.Tab && action == Action.Press){
                System.out.println(camera.getPosition());
            }
            if(key == Key.Space && action == Action.Press){
                updateWorld();
            }
        });

        shader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/Vertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/Fragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/Geometry.glsl", GL32.GL_GEOMETRY_SHADER))
                .link();

        hudshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/HUDVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/HUDFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        ppshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/FullscreenVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/FullscreenFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        texture = Texture2D.fromFile("assets/texture/grass.png");

        renderTexture = new EmptyTexture2D(getWidth(), getHeight());
        //renderBuffer = new RenderBuffer(getWidth(), getHeight(), GL30.GL_DEPTH_COMPONENT32F);
        depthTexture = new EmptyTexture2D(getWidth(), getHeight(), GL11.GL_DEPTH_COMPONENT);//GL30.GL_DEPTH_COMPONENT32F
        framebuffer = new FrameBuffer().attachTexture(renderTexture, GL30.GL_COLOR_ATTACHMENT0).attachTexture(depthTexture, GL30.GL_DEPTH_ATTACHMENT);//.attachRenderBuffer(renderBuffer, GL30.GL_DEPTH_ATTACHMENT);//
        if(!framebuffer.isOK())System.out.println("ERROR");
        framebuffer.unbind();

        camera = new Camera(75, (float)getWidth()/(float)getHeight());
        //camera.setPosition(60, 80, 60);
        camera.setPosition(5, 5, 15);

        vaoId = GL30.glGenVertexArrays();
        vboId = GL15.glGenBuffers();
        iboId = GL15.glGenBuffers();
        GL30.glBindVertexArray(vaoId);

        updateWorld();

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
        ///GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * 4, 3 * 4);

        GL30.glBindVertexArray(0);

    }

    private void updateWorld(){
        Mesh mc = Octree.createOctree(new Vector3f(32,32,0), 64).simplify(0.1f).getMesh();//MarchingCubes.createMesh(new Vector3f(camera.getPosition()).sub(64, 64, 64), 128, 1);//
        Mesh.MeshData mcd = mc.getData();



        indexCount = mcd.indices.capacity();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, mcd.vertices, GL15.GL_DYNAMIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, mcd.indices, GL15.GL_DYNAMIC_DRAW);
    }

    @Override
    public void update(double time) {
        setTitle("Voxel Game (" + Math.round(1/time) + ")");

        if(getKeyboard().isKeyDown(Key.W)){
            camera.addPosition(camera.getForward().mul(!getKeyboard().isKeyDown(Key.LeftShift)?1f:3));
        }
        if(getKeyboard().isKeyDown(Key.S)){
            camera.addPosition(camera.getBack()   .mul(!getKeyboard().isKeyDown(Key.LeftShift)?1f:3));
        }
        if(getKeyboard().isKeyDown(Key.A)){
            camera.addPosition(camera.getLeft()   .mul(!getKeyboard().isKeyDown(Key.LeftShift)?1f:3));
        }
        if(getKeyboard().isKeyDown(Key.D)){
            camera.addPosition(camera.getRight()  .mul(!getKeyboard().isKeyDown(Key.LeftShift)?1f:3));
        }
        if(!getMouse().isCursorEnabled()){
            camera.addRotation(getMouse().getDeltaMouseX()/(float)(getWidth()/2), -getMouse().getDeltaMouseY()/(float)(getHeight()/2));
        }
    }

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if(GL11.glIsEnabled(GL11.GL_CULL_FACE)) {
            framebuffer.bind();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        }
        shader.bind();
        texture.bind(0);
        shader.setUniform("mvp", false, camera.getCameraMatrix());
        shader.setUniform("pos", camera.getPosition());
        GL30.glBindVertexArray(vaoId);
        GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT ,0);//GL32.GL_LINES_ADJACENCY
        framebuffer.unbind();

        if(GL11.glIsEnabled(GL11.GL_CULL_FACE)) {
            ppshader.bind();
            renderTexture.bind(0);
            depthTexture.bind(1);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        }
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        hudshader.bind();
        GL11.glDrawArrays(GL11.GL_POINTS, 0, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    public void destroy() {
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(iboId);
        GL30.glDeleteVertexArrays(vaoId);
        shader.delete();
        hudshader.delete();
        ppshader.delete();
        texture.delete();

        renderTexture.delete();
        depthTexture.delete();
        //renderBuffer.delete();
        framebuffer.delete();
    }

    @Override
    public void resize(int width, int height) {
        GL11.glViewport(0, 0, width, height);
        camera.setAspect((float)width/(float)height);
        //renderBuffer.resize(width, height);
        renderTexture.resize(width, height);
        depthTexture.resize(width, height);
    }
}

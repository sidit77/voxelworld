package com.github.sidit77.voxelworld;

import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.system.GameWindow;
import com.github.sidit77.voxelworld.system.input.Action;
import com.github.sidit77.voxelworld.system.input.Key;
import com.github.sidit77.voxelworld.system.input.MouseButton;
import com.github.sidit77.voxelworld.world.MarchingCubes;
import com.github.sidit77.voxelworld.world.Mesh;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

public class VoxelGameWindow extends GameWindow{

    public VoxelGameWindow() {
        super("Voxel Game", 1280, 720);
    }

    private int vaoId;
    private int vboId;
    private int indexCount;

    private int iboId;

    private GLSLProgram shader;
    private GLSLProgram hudshader;
    private Camera camera;
    private boolean fog = false;

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
            if(key == Key.Escape && action == Action.Press && !getMouse().isCursorEnabled()){
                getMouse().setCursor(true);
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
        Mesh mc = MarchingCubes.createMesh(new Vector3f(camera.getPosition()).sub(64, 64, 64), 128, 2);
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

        shader.bind();
        shader.setUniform("mvp", false, camera.getCameraMatrix());
        shader.setUniform("pos", camera.getPosition());
        GL30.glBindVertexArray(vaoId);
        GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_INT ,0);//GL32.GL_LINES_ADJACENCY

        hudshader.bind();
        GL11.glDrawArrays(GL11.GL_POINTS, 0, 1);
    }

    @Override
    public void destroy() {
        GL15.glDeleteBuffers(vboId);
        GL15.glDeleteBuffers(iboId);
        GL30.glDeleteVertexArrays(vaoId);
        shader.delete();
        hudshader.delete();
    }

    @Override
    public void resize(int width, int height) {
        GL11.glViewport(0, 0, width, height);
        camera.setAspect((float)width/(float)height);
    }
}

package com.github.sidit77.voxelworld;

import com.github.sidit77.voxelworld.opengl.framebuffer.FrameBuffer;
import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.CubeMapTexture;
import com.github.sidit77.voxelworld.opengl.texture.EmptyTexture2D;
import com.github.sidit77.voxelworld.opengl.texture.Texture2D;
import com.github.sidit77.voxelworld.system.GameWindow;
import com.github.sidit77.voxelworld.system.input.Action;
import com.github.sidit77.voxelworld.system.input.Key;
import com.github.sidit77.voxelworld.system.input.MouseButton;
import com.github.sidit77.voxelworld.world.Terrain;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

public class VoxelGameWindow extends GameWindow{

    public VoxelGameWindow() {
        super("Voxel Game", 1280, 720, false, 4, 0);
    }

    private int emptyvao;

    private Terrain terrain;

    private GLSLProgram shader;
    private GLSLProgram hudshader;
    private GLSLProgram ppshader;
    private GLSLProgram skyboxShader;
    private Texture2D colortexture;
    private Texture2D normaltexture;
    private Texture2D glowtexture;
    private Texture2D moontexture;
    private Texture2D suntexture;
    private CubeMapTexture nighttexture;
    private CubeMapTexture daytexture;
    private Camera camera;
    private float time;
    private boolean fog = false;

    EmptyTexture2D renderTexture;
    EmptyTexture2D depthTexture;
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
        });

        shader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/Vertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/Fragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/Geometry.glsl", GL32.GL_GEOMETRY_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/SkyColor.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        hudshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/HUDVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/HUDFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        skyboxShader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/SkyboxVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/SkyboxFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/SkyColor.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        ppshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/FullscreenVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/FullscreenFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        colortexture = Texture2D.fromFile("assets/texture/dirt.png");
        normaltexture = Texture2D.fromFile("assets/texture/dirt_normal.png");
        glowtexture = Texture2D.fromFile("assets/texture/glow.png");
        glowtexture.setWarpMode(GL12.GL_CLAMP_TO_EDGE);
        moontexture = Texture2D.fromFile("assets/texture/moon.png");
        suntexture = Texture2D.fromFile("assets/texture/sun.png");
        nighttexture = CubeMapTexture.fromFile(new String[]{
                "assets/texture/skybox/night_top3.png",
                "assets/texture/skybox/night_bottom4.png",
                "assets/texture/skybox/night_left2.png",
                "assets/texture/skybox/night_right1.png",
                "assets/texture/skybox/night_back6.png",
                "assets/texture/skybox/night_front5.png"
        });
        daytexture = CubeMapTexture.fromFile(new String[]{
                "assets/texture/skybox/day_top3.png",
                "assets/texture/skybox/day_bottom4.png",
                "assets/texture/skybox/day_left2.png",
                "assets/texture/skybox/day_right1.png",
                "assets/texture/skybox/day_back6.png",
                "assets/texture/skybox/day_front5.png"
        });

        renderTexture = new EmptyTexture2D(getWidth(), getHeight());
        depthTexture = new EmptyTexture2D(getWidth(), getHeight(), GL11.GL_DEPTH_COMPONENT);
        framebuffer = new FrameBuffer().attachTexture(renderTexture, GL30.GL_COLOR_ATTACHMENT0).attachTexture(depthTexture, GL30.GL_DEPTH_ATTACHMENT);
        if(!framebuffer.isOK())System.out.println("ERROR");
        framebuffer.unbind();

        camera = new Camera(75, (float)getWidth()/(float)getHeight());
        camera.setPosition(60, 80, 60);
        //camera.setPosition(5, 5, 15);


        terrain = new Terrain();


        emptyvao = GL30.glGenVertexArrays();
    }

    @Override
    public void update(double time) {
        setTitle("Voxel Game (" + Math.round(1/time) + ")");

        if(getKeyboard().isKeyDown(Key.W)){
            camera.addPosition(camera.getForward().mul(!getKeyboard().isKeyDown(Key.LeftShift)?1f:0.3f));
        }
        if(getKeyboard().isKeyDown(Key.S)){
            camera.addPosition(camera.getBack()   .mul(!getKeyboard().isKeyDown(Key.LeftShift)?1f:0.3f));
        }
        if(getKeyboard().isKeyDown(Key.A)){
            camera.addPosition(camera.getLeft()   .mul(!getKeyboard().isKeyDown(Key.LeftShift)?1f:0.3f));
        }
        if(getKeyboard().isKeyDown(Key.D)){
            camera.addPosition(camera.getRight()  .mul(!getKeyboard().isKeyDown(Key.LeftShift)?1f:0.3f));
        }
        if(!getMouse().isCursorEnabled()){
            camera.addRotation(getMouse().getDeltaMouseX()/(float)(getWidth()/2), -getMouse().getDeltaMouseY()/(float)(getHeight()/2));
        }
        if(!getKeyboard().isKeyDown(Key.Space)){
            terrain.update(camera.getPosition());
            this.time += time * (getKeyboard().isKeyDown(Key.Q) ? 10 : 1);
        }
        if(getKeyboard().isKeyDown(Key.F)){
            camera.setPosition(0,0,0);
        }
        if(!getMouse().isCursorEnabled() && getMouse().isButtonDown(MouseButton.Left)){
            for(int x = -3; x <= 3; x++){
                for(int y = -3; y <= 3; y++){
                    for(int z = -3; z <= 3; z++){
                        //terrain.setDensity(new Vector3f(camera.getPosition()).add(x,y,z),1);
                    }
                }
            }
        }

    }

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        Vector3f lightDir = new Vector3f((float)Math.cos(time/20), (float)Math.sin(time/20),(float)Math.sin(time/20) * 0.5f);
        float darkness = (float)Math.max(0, Math.min(lightDir.y+0.5,1));

        if(GL11.glIsEnabled(GL11.GL_CULL_FACE)) {
            framebuffer.bind();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


        }
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glowtexture.bind(0);
        moontexture.bind(1);
        suntexture.bind(2);
        nighttexture.bind(3);
        daytexture.bind(4);
        skyboxShader.bind();
        skyboxShader.setUniform("view_matrix", false, camera.getViewMatrix());
        skyboxShader.setUniform("lightDir", lightDir);
        skyboxShader.setUniform("darkness", darkness);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 48);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        shader.bind();
        colortexture.bind(0);
        normaltexture.bind(1);
        glowtexture.bind(2);
        shader.setUniform("mvp", false, camera.getCameraMatrix());
        shader.setUniform("pos", camera.getPosition());
        shader.setUniform("lightDir", lightDir);
        shader.setUniform("lightPower", darkness);

        terrain.render();

        framebuffer.unbind();

        GL30.glBindVertexArray(emptyvao);
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
        terrain.delete();

        GL30.glDeleteVertexArrays(emptyvao);

        shader.delete();
        hudshader.delete();
        ppshader.delete();
        colortexture.delete();
        normaltexture.delete();
        glowtexture.delete();
        skyboxShader.delete();
        moontexture.delete();
        suntexture.delete();
        nighttexture.delete();
        daytexture.delete();

        renderTexture.delete();
        depthTexture.delete();
        framebuffer.delete();
    }

    @Override
    public void resize(int width, int height) {
        GL11.glViewport(0, 0, width, height);
        camera.setAspect((float)width/(float)height);
        renderTexture.resize(width, height);
        depthTexture.resize(width, height);
    }
}

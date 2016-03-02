package com.github.sidit77.voxelworld;

import com.github.sidit77.voxelworld.gui.text.Font;
import com.github.sidit77.voxelworld.gui.text.Text;
import com.github.sidit77.voxelworld.gui.text.TextRenderer;
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
import com.github.sidit77.voxelworld.worldv3.Block;
import com.github.sidit77.voxelworld.worldv3.Terrain;
import com.github.sidit77.voxelworld.worldv3.blocks.Blocks;
import javafx.scene.paint.Color;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

public class VoxelGameWindow extends GameWindow{

    public VoxelGameWindow(boolean fullscreen, boolean playmode) {
        super("Voxel Game", fullscreen ? -1 : 1280, fullscreen ? -1 : 720, fullscreen, 4, 0);
        if(fullscreen){
            this.getMouse().setCursor(false);
        }
        if(playmode){
            physics = true;
        }

    }

    private int emptyvao;

    private int inventorySlot = 0;
    private Block[] inventory = {Blocks.GRASS, Blocks.STONE, Blocks.TORCH, Blocks.WOOL, Blocks.STONEBRICKS, Blocks.BRICKS};

    private Terrain terrain;
    private Vector3f targetBlock;
    private Vector3f faceBlock;

    private GLSLProgram hudshader;
    private GLSLProgram ppshader;
    private GLSLProgram skyboxShader;
    private GLSLProgram pickingshader;
    private GLSLProgram playershader;
    private Texture2D glowtexture;
    private Texture2D moontexture;
    private Texture2D suntexture;
    private Texture2D fonttexture;
    private TextRenderer text;
    private CubeMapTexture nighttexture;
    private CubeMapTexture daytexture;
    private Camera camera;
    private float time;

    EmptyTexture2D renderTexture;
    EmptyTexture2D depthTexture;
    FrameBuffer framebuffer;

    private boolean physics = false;
    private boolean thirdperson = false;

    @Override
    public void load() {

        System.out.println(GL11.glGetString(GL11.GL_VERSION));

        GL11.glClearColor(0.5f, 0.5f, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL32.GL_DEPTH_CLAMP);

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
                terrain.setFog(!terrain.isFogEnabled());
            }
            if(key == Key.F8 && action == Action.Press){
                physics = !physics;
            }
            if(key == Key.F7 && action == Action.Press){
                thirdperson = !thirdperson;
            }
            if(key == Key.Tab && action == Action.Press){
                System.out.println(camera.getPosition());
            }
        });

        getMouse().addButtonEvent((MouseButton button, Action action)->{
            if(action != Action.Release && !getMouse().isCursorEnabled()){

                if(button == MouseButton.Middle && targetBlock != null) {
                    for(int i = 0; i < inventory.length; i++){
                        if(terrain.getBlock(targetBlock) == inventory[i])inventorySlot = i;
                    }
                }
                if(button == MouseButton.Left && targetBlock != null) {
                    terrain.setBlock(targetBlock, Blocks.AIR);
                }
                if(button == MouseButton.Right && faceBlock != null) {
                    terrain.setBlock(faceBlock, inventory[inventorySlot]);
                    if(physics && !canMoveTo(playerpos)){
                        terrain.setBlock(faceBlock, Blocks.AIR);
                    }
                }
            }

        });

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

        pickingshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/PickingVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/PickingFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        playershader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/PlayerVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/PlayerFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        fonttexture = Texture2D.fromFile("assets/texture/font/ComicSans.png");
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
        camera.setPosition(0, 80, 0);
        //camera.setPosition(5, 5, 15);


        terrain = new Terrain();


        emptyvao = GL30.glGenVertexArrays();


        text = new TextRenderer(fonttexture, Font.fromFile("assets/texture/font/ComicSans.fnt", 0, -14), getWidth(), getHeight());
    }

    private Vector3f playerpos = new Vector3f(2, 40, 2);
    private float velocity = 0;

    @Override
    public void update(double time) {
        setTitle("Voxel Game FPS: " + Math.round(1/time) + " | Physic: " + physics + " | Third Person: " + thirdperson);

        if(!physics) {
            float speed = 10 * (float)time * (!getKeyboard().isKeyDown(Key.LeftControl) ? 1 : 4);
            if(getKeyboard().isKeyDown(Key.W))        playerpos.add(camera.getForward().mul(1,0,1).normalize().mul(speed));
            if(getKeyboard().isKeyDown(Key.S))        playerpos.add(camera.getBack().mul(1,0,1).normalize().mul(speed));
            if(getKeyboard().isKeyDown(Key.A))        playerpos.add(camera.getLeft().mul(1,0,1).normalize().mul(speed));
            if(getKeyboard().isKeyDown(Key.D))        playerpos.add(camera.getRight().mul(1,0,1).normalize().mul(speed));
            if(getKeyboard().isKeyDown(Key.Space))    playerpos.add( 0, speed, 0);
            if(getKeyboard().isKeyDown(Key.LeftShift))playerpos.add( 0,-speed, 0);
        }else {
            //TODO check at higher velocitys
            velocity -= 0.01f * time * 60;
            Vector3f dir = new Vector3f();
            if(getKeyboard().isKeyDown(Key.W))        dir.add(camera.getForward().mul(1,0,1).normalize());
            if(getKeyboard().isKeyDown(Key.S))        dir.add(camera.getBack().mul(1,0,1).normalize());
            if(getKeyboard().isKeyDown(Key.A))        dir.add(camera.getLeft().mul(1,0,1).normalize());
            if(getKeyboard().isKeyDown(Key.D))        dir.add(camera.getRight().mul(1,0,1).normalize());
            if(getKeyboard().isKeyDown(Key.Space) && !canMoveTo(new Vector3f(playerpos).sub(0,0.1f,0))){
                velocity = 0.17f;
            }

            if(dir.length() != 0) dir.normalize();
            dir.mul(getKeyboard().isKeyDown(Key.LeftShift) ? 0.15f : 0.1f);
            dir.add(0, velocity, 0);
            dir.mul(60 * (float)time);

            if(canMoveTo(new Vector3f(playerpos).add(dir.x,0,0))) {
                playerpos.add(dir.x,0,0);
            }
            if(canMoveTo(new Vector3f(playerpos).add(0,dir.y,0))) {
                playerpos.add(0,dir.y,0);
            }else{
                velocity = 0;
            }
            if(canMoveTo(new Vector3f(playerpos).add(0,0,dir.z))) {
                playerpos.add(0,0,dir.z);
            }

        }

        if(!getMouse().isCursorEnabled()){
            camera.addRotation(getMouse().getDeltaMouseX()/(float)(getWidth()/2), -getMouse().getDeltaMouseY()/(float)(getHeight()/2));
        }
        if(getKeyboard().isKeyDown(Key.F)){
            playerpos.set(0,0,0);
        }
        inventorySlot = (inventory.length + inventorySlot + Math.round(getMouse().getDeltaScrollLevel()))%inventory.length;

        if(!thirdperson){
            camera.getPosition().set(playerpos);
        } else{
            camera.getPosition().set(new Vector3f(playerpos).add(camera.getBack().mul(5)));
        }

        terrain.update(camera.getPosition());
        this.time += time * (getKeyboard().isKeyDown(Key.Q) ? 10 : 1);


        Vector3f pos = new Vector3f(playerpos);
        Vector3f step = new Vector3f(camera.getForward()).mul(0.5f);
        targetBlock = null;
        faceBlock = null;
        for(float i = 0; i < 10; i += 0.5f){
            pos.add(step);
            if(terrain.getBlock(pos) != Blocks.AIR){
                int bx = Math.round(pos.x);
                int by = Math.round(pos.y);
                int bz = Math.round(pos.z);

                int bx2 = bx;
                int by2 = by;
                int bz2 = bz;

                while(Math.abs(bx - bx2)+Math.abs(by - by2)+Math.abs(bz - bz2) < 1){
                    pos.sub(step);
                    bx2 = Math.round(pos.x);
                    by2 = Math.round(pos.y);
                    bz2 = Math.round(pos.z);
                }

                targetBlock = new Vector3f(bx, by, bz);
                faceBlock = new Vector3f(bx2, by2, bz2);
                break;
            }
        }

    }

    private boolean isAir(Vector3f pos){
        return !terrain.getBlock(pos).hasHitbox();
    }

    public boolean canMoveTo(Vector3f pos){
        return  isAir(new Vector3f(pos).add(-0.4f,  0.4f, -0.4f)) &&
                isAir(new Vector3f(pos).add( 0.4f,  0.4f, -0.4f)) &&
                isAir(new Vector3f(pos).add( 0.4f,  0.4f,  0.4f)) &&
                isAir(new Vector3f(pos).add(-0.4f,  0.4f,  0.4f)) &&

                isAir(new Vector3f(pos).add(-0.4f, -1.5f, -0.4f)) &&
                isAir(new Vector3f(pos).add( 0.4f, -1.5f, -0.4f)) &&
                isAir(new Vector3f(pos).add( 0.4f, -1.5f,  0.4f)) &&
                isAir(new Vector3f(pos).add(-0.4f, -1.5f,  0.4f)) &&

                isAir(new Vector3f(pos).add(0, -1,  0)) &&
                isAir(new Vector3f(pos).add(0,  0,  0));
    }

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        Vector3f lightDir = new Vector3f((float) Math.cos(time / 20), (float) Math.sin(time / 20), (float) Math.sin(time / 20) * 0.5f);
        float darkness = (float) Math.max(0, Math.min(lightDir.y + 0.5, 1));

        if (GL11.glIsEnabled(GL11.GL_CULL_FACE)) {
            framebuffer.bind();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

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
        }

        terrain.render(camera);

        playershader.bind();
        playershader.setUniform("mvp", false, camera.getCameraMatrix());
        playershader.setUniform("pos", playerpos);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);

        if (targetBlock != null){
            GL30.glBindVertexArray(emptyvao);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            pickingshader.bind();
            pickingshader.setUniform("mvp", false, camera.getCameraMatrix());
            pickingshader.setUniform("pos", faceBlock);
            GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, 36, 1);

            GL11.glDepthFunc(GL11.GL_LESS);
            GL11.glDisable(GL11.GL_BLEND);
        }

        framebuffer.unbind();

        if(GL11.glIsEnabled(GL11.GL_CULL_FACE)) {
            ppshader.bind();
            renderTexture.bind(0);
            depthTexture.bind(1);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        }

        GL11.glDepthFunc(GL11.GL_ALWAYS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);


        if(!getKeyboard().isKeyDown(Key.F1)) {
            hudshader.bind();
            GL11.glDrawArrays(GL11.GL_POINTS, 0, 1);

            text.render(inventory[inventorySlot].getName(), 20, 10, 0.7f, Color.WHITE, 0.75f);
            text.render("F1: Keybindigs", 20, getHeight() - 40, 0.4f, Color.WHITE, 0.75f);
        }else{
            float size = 0.7f;
            Text[] lines = {
                    text.getText("W/S/A/D: Move", size),
                    text.getText("Space/Shift: Jump/Sprint", size),
                    text.getText("Left/Right: Destroy/Place a block", size),
                    text.getText("Scroll: Change block", size),
                    text.getText("Middle: Pick block from the world", size),
                    text.getText("F7/F8/F10: Toggle camera/physics/wireframe", size),
                    text.getText("Escape: Exit", size)
            };

            float height = (getHeight() - lines.length * 60)/2;
            for(Text line : lines){
                text.render(line, (getWidth()-line.getSize())/2, height, Color.WHITE, 0.60f);
                height += 60;
            }

        }

        GL11.glDepthFunc(GL11.GL_LESS);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void destroy() {
        terrain.delete();

        GL30.glDeleteVertexArrays(emptyvao);

        hudshader.delete();
        ppshader.delete();
        glowtexture.delete();
        skyboxShader.delete();
        moontexture.delete();
        suntexture.delete();
        nighttexture.delete();
        daytexture.delete();
        pickingshader.delete();
        playershader.delete();
        fonttexture.delete();
        text.delete();

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
        ppshader.bind();
        ppshader.setUniform("screen", (float)getWidth(), (float)getHeight());
        text.resize(width, height);
    }
}

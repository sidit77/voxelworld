package com.github.sidit77.voxelworld;

import com.github.sidit77.voxelworld.gui.text.Font;
import com.github.sidit77.voxelworld.gui.text.Text;
import com.github.sidit77.voxelworld.gui.text.TextRenderer;
import com.github.sidit77.voxelworld.opengl.framebuffer.FrameBuffer;
import com.github.sidit77.voxelworld.opengl.framebuffer.RenderBuffer;
import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import com.github.sidit77.voxelworld.opengl.texture.CubeMapTexture;
import com.github.sidit77.voxelworld.opengl.texture.EmptyTexture2D;
import com.github.sidit77.voxelworld.opengl.texture.Texture2D;
import com.github.sidit77.voxelworld.system.GameWindow;
import com.github.sidit77.voxelworld.system.input.Action;
import com.github.sidit77.voxelworld.system.input.Key;
import com.github.sidit77.voxelworld.system.input.MouseButton;
import com.github.sidit77.voxelworld.world.Block;
import com.github.sidit77.voxelworld.world.WorldRenderer;
import com.github.sidit77.voxelworld.world.blocks.Blocks;
import javafx.scene.paint.Color;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.IntBuffer;

public class VoxelGameWindow extends GameWindow{

    private static final int shadowres = 2048;
    private static final int shadowarea = 60;
    private static final int shadowdistance = 80;

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
    private Block[] inventory = {Blocks.GRASS, Blocks.STONE, Blocks.WOOL, Blocks.STONEBRICKS, Blocks.BRICKS, Blocks.WOOD, Blocks.LEAF, Blocks.TORCH, Blocks.GLASS};

    private WorldRenderer worldRenderer;
    private Vector3f targetBlock;
    private Vector3f faceBlock;

    private GLSLProgram ppshader;
    private GLSLProgram skyboxShader;
    private GLSLProgram pickingshader;
    private GLSLProgram radialblurshader;
    private Texture2D glowtexture;
    private Texture2D moontexture;
    private Texture2D suntexture;
    private Texture2D fonttexture;
    private TextRenderer text;
    private CubeMapTexture nighttexture;
    private CubeMapTexture daytexture;
    private Camera camera;
    private float time;

    private EmptyTexture2D renderTexture;
    private EmptyTexture2D renderTexture2;
    private EmptyTexture2D depthTexture;
    private FrameBuffer framebuffer;

    private FrameBuffer shadowmap;
    private EmptyTexture2D shadowtex;
    private RenderBuffer shadowdepth;

    private FrameBuffer radialblurframebuffer;
    private EmptyTexture2D radialblurtexture;

    private boolean physics = false;
    private boolean thirdperson = false;
    private boolean debug = false;
    private boolean wireframe = false;
    private int fps;

    private Vector3f playerpos;
    private float velocity = 0;

    private CharacterModel playermodel;

    @Override
    public void load() {

        System.out.println(GL11.glGetString(GL11.GL_VERSION));

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 1);
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
                wireframe = !wireframe;
            }
            if(key == Key.F8 && action == Action.Press){
                physics = !physics;
            }
            if(key == Key.F7 && action == Action.Press){
                thirdperson = !thirdperson;
            }
            if(key == Key.F6 && action == Action.Press){
                debug = !debug;
            }
            if(key == Key.F5 && action == Action.Press){
                worldRenderer.getWorld().recalculateLighting();
            }
            if(key == Key.Tab && action == Action.Press){
                System.out.println(camera.getPosition());
            }
            if(key == Key.G && action == Action.Press){
                worldRenderer.getWorld().setBlock(Math.round(camera.getPosition().x), Math.round(camera.getPosition().y), Math.round(camera.getPosition().z), Blocks.WOOL);
                worldRenderer.getWorld().update();
            }
        });

        getMouse().addButtonEvent((MouseButton button, Action action)->{
            if(action != Action.Release && !getMouse().isCursorEnabled()){

                if(button == MouseButton.Middle && targetBlock != null) {
                    for(int i = 0; i < inventory.length; i++){
                        if(worldRenderer.getWorld().getBlock(targetBlock) == inventory[i])inventorySlot = i;
                    }
                }
                if(button == MouseButton.Left && targetBlock != null) {
                    worldRenderer.getWorld().setBlock(targetBlock, Blocks.AIR);
                }
                if(button == MouseButton.Right && faceBlock != null) {
                    worldRenderer.getWorld().setBlock(faceBlock, inventory[inventorySlot]);
                    if(physics && !canMoveTo(playerpos)){
                        worldRenderer.getWorld().setBlock(faceBlock, Blocks.AIR);
                    }
                }
            }

        });

        skyboxShader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/SkyboxVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/SkyboxFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/SkyColor.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        ppshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/FullscreenVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/FullscreenFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        radialblurshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/FullscreenVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/RadialBlurFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        pickingshader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/PickingVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/PickingFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        playermodel = new CharacterModel();

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
        renderTexture2 = new EmptyTexture2D(getWidth(), getHeight());
        depthTexture = new EmptyTexture2D(getWidth(), getHeight(), GL11.GL_DEPTH_COMPONENT);
        framebuffer = new FrameBuffer().attachTexture(renderTexture, GL30.GL_COLOR_ATTACHMENT0).attachTexture(renderTexture2, GL30.GL_COLOR_ATTACHMENT1).attachTexture(depthTexture, GL30.GL_DEPTH_ATTACHMENT);
        IntBuffer db = BufferUtils.createIntBuffer(2).put(GL30.GL_COLOR_ATTACHMENT0).put(GL30.GL_COLOR_ATTACHMENT1);
        db.flip();
        GL20.glDrawBuffers(db);
        if(!framebuffer.isOK())System.out.println("ERROR");
        framebuffer.unbind();

        shadowtex = new EmptyTexture2D(shadowres, shadowres, GL30.GL_RG, GL30.GL_RG32F);
        shadowdepth = new RenderBuffer(shadowres, shadowres, GL11.GL_DEPTH_COMPONENT);
        //shadowtex.setFiltering(GL11.GL_NEAREST, GL11.GL_NEAREST);
        shadowtex.setWarpMode(GL13.GL_CLAMP_TO_BORDER);
        shadowmap = new FrameBuffer().attachTexture(shadowtex, GL30.GL_COLOR_ATTACHMENT0).attachRenderBuffer(shadowdepth, GL30.GL_DEPTH_ATTACHMENT);
        if(!shadowmap.isOK())System.out.println("ERROR");
        shadowmap.unbind();

        radialblurtexture = new EmptyTexture2D(getWidth()/8, getHeight()/8);
        radialblurframebuffer = new FrameBuffer().attachTexture(radialblurtexture, GL30.GL_COLOR_ATTACHMENT0);
        if(!radialblurframebuffer.isOK())System.out.println("ERROR");
        radialblurframebuffer.unbind();

        camera = new Camera(75, (float)getWidth()/(float)getHeight());
        //camera.setPosition(200, 80, 200);
        //camera.setPosition(5, 5, 15);


        worldRenderer = new WorldRenderer();


        emptyvao = GL30.glGenVertexArrays();


        text = new TextRenderer(fonttexture, Font.fromFile("assets/texture/font/ComicSans.fnt", 0, -14), getWidth(), getHeight());

        playerpos = new Vector3f(150, 100, 150);
        while(worldRenderer.getWorld().getBlock(playerpos) == Blocks.AIR) {
            playerpos.sub(0, 1, 0);
        }
        playerpos.add(0, 2.5f, 0);

    }

    @Override
    public void update(double time) {

        if(debug) fps = (int)Math.round(1/time);

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

        worldRenderer.update();
        this.time += time * (getKeyboard().isKeyDown(Key.Q) ? 10 : 0.3);


        Vector3f pos = new Vector3f(playerpos);
        Vector3f step = new Vector3f(camera.getForward()).mul(0.5f);
        targetBlock = null;
        faceBlock = null;
        for(float i = 0; i < 10; i += 0.5f){
            pos.add(step);
            if(worldRenderer.getWorld().getBlock(pos) != Blocks.AIR){
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
        return !worldRenderer.getWorld().getBlock(pos).hasHitbox();
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

        Matrix3f playerrot = new Matrix3f().lookAlong(camera.getForward().mul(1,0,1).normalize(), new Vector3f(0,1,0)).transpose().rotate((float)Math.PI/2, 0, 1, 0);

        if(wireframe) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

            GL11.glViewport(0, 0, getWidth(), getHeight());
            worldRenderer.render(camera.getCameraMatrix());

            if(thirdperson){
                playermodel.rendershadow(playerpos, playerrot, camera.getCameraMatrix());
            }

            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }else {

            Vector3f lightDir = new Vector3f((float) Math.cos(time / 20), (float) Math.sin(time / 20), (float) Math.sin(time / 20) * 0.5f);
            float darkness = (float) Math.max(0, Math.min(lightDir.y + 0.5, 1));

            GL11.glViewport(0, 0, shadowres, shadowres);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDepthFunc(GL11.GL_LESS);
            shadowmap.bind();
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
            Matrix4f lightMatrix = new Matrix4f();
            lightMatrix.ortho(-(shadowarea / 2), (shadowarea / 2), -(shadowarea / 2), (shadowarea / 2), 1, shadowdistance);
            lightMatrix.lookAt(new Vector3f(lightDir).normalize().mul(0.6f * shadowdistance).add(playerpos), new Vector3f(playerpos), new Vector3f(0, 1, 0));
            worldRenderer.render(lightMatrix);
            playermodel.rendershadow(playerpos, playerrot, lightMatrix);
            shadowmap.unbind();
            GL11.glEnable(GL11.GL_CULL_FACE);

            GL11.glViewport(0, 0, getWidth(), getHeight());

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
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 42);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);


            GL11.glDepthFunc(GL11.GL_LESS);
            worldRenderer.render(camera, darkness, lightDir, shadowtex, lightMatrix);

            if (thirdperson) {
                playermodel.render(playerpos, playerrot, camera.getCameraMatrix(), lightDir, darkness, (float)worldRenderer.getWorld().getLightLevel(playerpos) / 16, lightMatrix, shadowtex);
            }

            if (targetBlock != null) {
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


            GL11.glViewport(0, 0, getWidth() / 8, getHeight() / 8);
            radialblurframebuffer.bind();
            radialblurshader.bind();
            renderTexture2.bind(0);
            Vector4f lp = camera.getViewMatrix().transform(new Vector4f(new Vector3f(lightDir).normalize(), 1));
            lp.div(lp.w);
            radialblurshader.setUniform("lightpos", (lp.x + 1) / 2, (lp.y + 1) / 2);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
            radialblurframebuffer.unbind();
            GL11.glViewport(0, 0, getWidth(), getHeight());

            ppshader.bind();
            radialblurtexture.bind(1);
            renderTexture.bind(0);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        }

        GL11.glDepthFunc(GL11.GL_ALWAYS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (!getKeyboard().isKeyDown(Key.F1)) {

            if(!thirdperson) text.render("+", (getWidth()-30)/ 2, (getHeight() - 90) / 2, 1f, Color.WHITE, 0.75f);
            text.render(inventory[inventorySlot].getName(), 20, 10, 0.7f, Color.WHITE, 0.75f);
            text.render("F1: Keybindings", 20, getHeight() - 40, 0.4f, Color.WHITE, 0.75f);

            if (debug) {
                float size = 0.4f;
                Text[] lines = {
                        text.getText("Pos: [" + ((float) Math.round(playerpos.x * 10) / 10) + "," + ((float) Math.round(playerpos.y * 10) / 10) + "," + ((float) Math.round(playerpos.z * 10) / 10) + "]", size),
                        text.getText("Camera: " + (thirdperson ? "Third" : "First") + "person", size),
                        text.getText("Physics: " + physics, size),
                        text.getText("LookAt: " + (targetBlock != null ? worldRenderer.getWorld().getBlock(targetBlock).getName() : "-"), size),
                        text.getText("LightLevel: " + (faceBlock != null ? worldRenderer.getWorld().getLightLevel(faceBlock) : "-"), size),
                        text.getText("FPS: " + fps, size)
                };

                float height = 10;
                for (Text line : lines) {
                    text.render(line, getWidth() - 20 - line.getSize(), height, Color.WHITE, 0.75f);
                    height += 30;
                }
            }

        } else {
            float size = 0.7f;
            Text[] lines = {
                    text.getText("W/S/A/D: Move", size),
                    text.getText("Space/Shift: Jump/Sprint", size),
                    text.getText("Left/Right: Destroy/Place a block", size),
                    text.getText("Scroll: Change block", size),
                    text.getText("Middle: Pick block from the world", size),
                    text.getText("F6/F7/F8/F10: Toggle debug/camera/physics/wireframe", size),
                    text.getText("F5: Reload World", size),
                    text.getText("Escape: Exit", size)
            };

            float height = (getHeight() - lines.length * 60) / 2;
            for (Text line : lines) {
                text.render(line, (getWidth() - line.getSize()) / 2, height, Color.WHITE, 0.60f);
                height += 60;
            }


            GL11.glDepthFunc(GL11.GL_LESS);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
    @Override
    public void destroy() {
        worldRenderer.delete();

        GL30.glDeleteVertexArrays(emptyvao);

        ppshader.delete();
        glowtexture.delete();
        skyboxShader.delete();
        moontexture.delete();
        radialblurshader.delete();
        suntexture.delete();
        nighttexture.delete();
        daytexture.delete();
        pickingshader.delete();
        playermodel.delete();
        fonttexture.delete();
        text.delete();

        renderTexture.delete();
        renderTexture2.delete();
        depthTexture.delete();
        framebuffer.delete();

        shadowmap.delete();
        shadowtex.delete();
        shadowdepth.delete();

        radialblurframebuffer.delete();
        radialblurtexture.delete();
    }

    @Override
    public void resize(int width, int height) {
        camera.setAspect((float)width/(float)height);
        renderTexture.resize(width, height);
        depthTexture.resize(width, height);
        ppshader.bind();
        ppshader.setUniform("screen", (float)getWidth(), (float)getHeight());
        text.resize(width, height);
        radialblurtexture.resize(width/8, height/8);
        renderTexture2.resize(width, height);
    }
}

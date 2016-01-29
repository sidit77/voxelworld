package com.github.sidit77.voxelworld.world;

import com.github.sidit77.voxelworld.Camera;
import com.github.sidit77.voxelworld.opengl.shader.GLSLProgram;
import com.github.sidit77.voxelworld.opengl.shader.GLSLShader;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class LineRenderer {

    GLSLProgram shader;

    private int vaoid;
    private int vboid;
    private int linecount;

    private ArrayList<Vector3f[]> lines;

    public LineRenderer(){
        shader = new GLSLProgram()
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/LineVertex.glsl", GL20.GL_VERTEX_SHADER))
                .attachShaderAndDelete(GLSLShader.fromFile("assets/shader/LineFragment.glsl", GL20.GL_FRAGMENT_SHADER))
                .link();

        lines = new ArrayList<>();
        vaoid = GL30.glGenVertexArrays();
        vboid = GL15.glGenBuffers();
        linecount = 0;
        GL30.glBindVertexArray(vaoid);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
        GL30.glBindVertexArray(0);
    }

    public void addLine(Vector3f v1, Vector3f v2){
        lines.add(new Vector3f[]{v1,v2});
    }

    public void render(Camera camera){

        if(lines.size() != linecount){
            FloatBuffer fb = BufferUtils.createFloatBuffer(lines.size() * 2 * 3);
            lines.stream().forEach((v)->{
                fb.put(v[0].x);
                fb.put(v[0].y);
                fb.put(v[0].z);
                fb.put(v[1].x);
                fb.put(v[1].y);
                fb.put(v[1].z);
            });
            fb.flip();

            linecount = lines.size();

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboid * 2);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_DYNAMIC_DRAW);

        }

        shader.bind();
        shader.setUniform("mvp", false, camera.getCameraMatrix());
        GL30.glBindVertexArray(vaoid);
        GL11.glDrawArrays(GL11.GL_LINES, 0, linecount * 2);
        //System.out.println(linecount);
    }

    public void delete(){
        shader.delete();
        GL30.glDeleteVertexArrays(vaoid);
        GL15.glDeleteBuffers(vboid);
    }
}

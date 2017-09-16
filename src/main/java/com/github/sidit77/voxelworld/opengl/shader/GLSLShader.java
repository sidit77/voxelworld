package com.github.sidit77.voxelworld.opengl.shader;

import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GLSLShader{
    /**
     * A simple wrapper around an OpenGL Shader object.
     */
    private final int id;

    public GLSLShader(String source, int type){
        id = GL20.glCreateShader(type);

        GL20.glShaderSource(id, source);
        GL20.glCompileShader(id);

        int status = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS);

        if (status != 1) {
            String info = GL20.glGetShaderInfoLog(id, 2000);
            System.out.println(info);
            delete();
        }
    }

    public GLSLShader delete(){
        GL20.glDeleteShader(id);
        return this;
    }

    public int getID(){
        return id;
    }

    public static GLSLShader fromFile(String file, int type){
        StringBuilder result = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(GLSLShader.class.getClassLoader().getResourceAsStream(file)))) {
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (IOException ex) {
            System.out.println("Couldnt read file");
        }
        return new GLSLShader(result.toString(), type);
    }
}

package com.github.sidit77.voxelworld.opengl.shader;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import java.nio.FloatBuffer;
import java.util.HashMap;

public class GLSLProgram {

    /**
     * A simple wrapper around an OpenGL Program object.
     */

    private int id;
    private HashMap<String, Integer> uniforms;
    private FloatBuffer matrixbuffer;

    public GLSLProgram() {
        id = GL20.glCreateProgram();
        uniforms = new HashMap<>();
        matrixbuffer = BufferUtils.createFloatBuffer(16);
    }

    public GLSLProgram link(){
        GL20.glLinkProgram(id);

        int status = GL20.glGetProgrami(id, GL20.GL_LINK_STATUS);

        if (status != 1) {
            String info = GL20.glGetProgramInfoLog(id, 2000);
            System.out.println(info);
            delete();
        }

        return this;
    }

    public GLSLProgram bind(){
        GL20.glUseProgram(id);
        return this;
    }

    public GLSLProgram attachShader(GLSLShader shader){
        GL20.glAttachShader(id, shader.getID());
        return this;
    }

    public GLSLProgram attachShaderAndDelete(GLSLShader shader) {
        GL20.glAttachShader(id, shader.getID());
        shader.delete();
        return this;
    }

    //public GLSLProgram SetTransformFeedbackVaryings(string[] names) {
    //    GL.TransformFeedbackVaryings(id, names.Length, names, TransformFeedbackMode.InterleavedAttribs);
    //    return this;
    //}

    public GLSLProgram delete(){
        GL20.glDeleteProgram(id);
        return this;
    }

    public int getID(){
        return id;
    }

//    public void SetUBO(string name, IUBO ubo) {
//        GL.UniformBlockBinding(id, GL.GetUniformBlockIndex(id, name), ubo.GetID());
//    }

    public int getUniform(String name){
        if(!uniforms.containsKey(name)) {
            uniforms.put(name, GL20.glGetUniformLocation(id, name));
        }
        return uniforms.get(name);
    }

    public int getUniformBlock(String name){
        if(!uniforms.containsKey("ub_" + name)) {
            uniforms.put("ub_" + name, GL31.glGetUniformBlockIndex(id, name));
        }
        return uniforms.get("ub_" + name);
    }

    public void bindUniformBlock(String name, int slot){
        GL31.glUniformBlockBinding(id, getUniformBlock(name), slot);
    }

    public void bindUniformBlock(int location, int slot){
        GL31.glUniformBlockBinding(id, location, slot);
    }

    public void setUniform(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    public void setUniform(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    public void setUniform(int location, int xValue, int yValue) {
        GL20.glUniform2i(location, xValue, yValue);
    }

    public void setUniform(int location, float xValue, float yValue) {
        GL20.glUniform2f(location, xValue, yValue);
    }

    public void setUniform(int location, Vector2f vec) {
        GL20.glUniform2f(location, vec.x, vec.y);
    }

    public void setUniform(int location, int xValue, int yValue, int zValue) {
        GL20.glUniform3i(location, xValue, yValue, zValue);
    }

    public void setUniform(int location, float xValue, float yValue, float zValue) {
        GL20.glUniform3f(location, xValue, yValue, zValue);
    }

    public void setUniform(int location, Vector3f vec) {
        GL20.glUniform3f(location, vec.x, vec.y, vec.z);
    }

    public void setUniform(int location, float xValue, float yValue, float zValue, float wValue) {
        GL20.glUniform4f(location, xValue, yValue, zValue, wValue);
    }

    public void setUniform(int location, int xValue, int yValue, int zValue, int wValue) {
        GL20.glUniform4i(location, xValue, yValue, zValue, wValue);
    }

    public void setUniform(int location, Vector4f vec) {
        GL20.glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
    }

    public void setUniform(int location, boolean transpose, Matrix4f matrix) {
        matrix.get(matrixbuffer);
        GL20.glUniformMatrix4fv(location, transpose, matrixbuffer);
        matrixbuffer.rewind();
    }

    public void setUniform(String name, int value) {
        setUniform(getUniform(name), value);
    }

    public void setUniform(String name, float value) {
        setUniform(getUniform(name), value);
    }

    public void setUniform(String name, int xValue, int yValue) {
        setUniform(getUniform(name), xValue, yValue);
    }

    public void setUniform(String name, float xValue, float yValue) {
        setUniform(getUniform(name), xValue, yValue);
    }

    public void SetUniform(String name, Vector2f vec) {
        setUniform(getUniform(name), vec);
    }

    public void setUniform(String name, int xValue, int yValue, int zValue) {
        setUniform(getUniform(name), xValue, yValue, zValue);
    }

    public void setUniform(String name, float xValue, float yValue, float zValue) {
        setUniform(getUniform(name), xValue, yValue, zValue);
    }

    public void setUniform(String name, Vector3f vec) {
        setUniform(getUniform(name), vec);
    }

    public void setUniform(String name, float xValue, float yValue, float zValue, float wValue) {
        setUniform(getUniform(name), xValue, yValue, zValue, wValue);
    }

    public void setUniform(String name, int xValue, int yValue, int zValue, int wValue) {
        setUniform(getUniform(name), xValue, yValue, zValue, wValue);
    }

    public void setUniform(String name, Vector4f vec) {
        setUniform(getUniform(name), vec);
    }

    public void setUniform(String name, boolean transpose, Matrix4f matrix) {
        setUniform(getUniform(name), transpose, matrix);
    }


    public void setUniform(String name, boolean transpose, Matrix3f matrix) {
        setUniform(getUniform(name), transpose, matrix);
    }

    private void setUniform(int uniform, boolean transpose, Matrix3f matrix) {
        matrix.get(matrixbuffer);
        GL20.glUniformMatrix3fv(uniform, transpose, matrixbuffer);
        matrixbuffer.rewind();
    }
}


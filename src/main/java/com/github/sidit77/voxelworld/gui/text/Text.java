package com.github.sidit77.voxelworld.gui.text;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Text {

    private FloatBuffer charinfo;
    private float size;
    private int length;

    public Text(String text, float scale, Font font){
        char[] st = text.toCharArray();

        length = st.length;
        size = 0;

        charinfo = BufferUtils.createFloatBuffer(st.length * 8);

        for(char ch : st){
            Font.Char c = font.getCharInformation(ch);

            charinfo.put(c.getXOffset() * scale + size);
            charinfo.put(c.getYOffset() * scale);

            charinfo.put(c.getWidth() * scale);
            charinfo.put(c.getHeight() * scale);

            charinfo.put(c.getX());
            charinfo.put(c.getY());

            charinfo.put(c.getXTex());
            charinfo.put(c.getYTex());

            size += c.getXAdvance() * scale;
        }

        charinfo.flip();
    }

    public FloatBuffer getCharInfo() {
        return charinfo;
    }

    public float getSize() {
        return size;
    }

    public int getLength() {
        return length;
    }
}

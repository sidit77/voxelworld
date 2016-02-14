package com.github.sidit77.voxelworld.opengl.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Texture2D extends Texture{

    public Texture2D(int width, int height, ByteBuffer data) {
        super(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        //GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
    }

    public static Texture2D fromFile(String file){
        ByteBuffer buffer = null;
        BufferedImage image = null;
        try {
            image = ImageIO.read(Texture2D.class.getClassLoader().getResourceAsStream(file));
            int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

            buffer = ByteBuffer.allocateDirect(image.getHeight() * image.getWidth() * 4).order(ByteOrder.nativeOrder());
            boolean hasAlpha = image.getColorModel().hasAlpha();

            for(int y = 0; y < image.getHeight(); y++){
                for(int x = 0; x < image.getWidth(); x++){
                    int pixel = pixels[y * image.getWidth() + x];

                    buffer.put((byte)((pixel >> 16) & 0xFF));
                    buffer.put((byte)((pixel >> 8) & 0xFF));
                    buffer.put((byte)((pixel) & 0xFF));
                    if(hasAlpha)
                        buffer.put((byte)((pixel >> 24) & 0xFF));
                    else
                        buffer.put((byte)(0xFF));
                }
            }

            buffer.flip();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Texture2D(image.getWidth(), image.getHeight(), buffer);
    }
}

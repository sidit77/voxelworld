package com.github.sidit77.voxelworld.opengl.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class Texture2DArray extends Texture{

    public Texture2DArray(int width, int height, ByteBuffer[] data) {
        super(GL30.GL_TEXTURE_2D_ARRAY);


        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL12.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL11.GL_RGBA8, width, height, data.length, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        for(int i = 0; i < data.length; i++){
            GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, width, height, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data[i]);

        }

        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D_ARRAY);

    }


    public static Texture2DArray fromFile(String[] paths){
        int width = 0;
        int height = 0;
        ByteBuffer[] data = new ByteBuffer[paths.length];
        for(int i = 0; i < paths.length; i++){
            ByteBuffer buffer = null;
            BufferedImage image = null;
            try {
                image = ImageIO.read(Texture2D.class.getClassLoader().getResourceAsStream(paths[i]));
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
            data[i] = buffer;
            width = image.getWidth();
            height = image.getHeight();
        }
        return new Texture2DArray(width,height,data);
    }

}

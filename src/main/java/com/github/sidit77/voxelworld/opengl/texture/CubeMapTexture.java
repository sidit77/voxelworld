package com.github.sidit77.voxelworld.opengl.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CubeMapTexture extends Texture {

    public CubeMapTexture(int width, int height, ByteBuffer[] data) {
        super(GL13.GL_TEXTURE_CUBE_MAP);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        setWarpMode(GL12.GL_CLAMP_TO_EDGE);

        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data[0]);
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data[1]);
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data[2]);
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data[3]);
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data[4]);
        GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data[5]);

        GL30.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);
    }


    public static CubeMapTexture fromFile(String[] files){
        int width = 0;
        int height = 0;
        ByteBuffer[] data = new ByteBuffer[6];
        for(int i = 0; i < 6; i++){
            ByteBuffer buffer = null;
            BufferedImage image = null;
            try {
                image = ImageIO.read(Texture2D.class.getClassLoader().getResourceAsStream(files[i]));
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
        return new CubeMapTexture(width,height,data);
    }

}

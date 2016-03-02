package com.github.sidit77.voxelworld.gui.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Font {

    Map<Integer, Char> chars;

    public Font(Map<Integer, Char> chars){
        this.chars = chars;
    }

    public Char getCharInformation(char c){
        return chars.get((int)c);
    }

    public static Font fromFile(String path){
        return fromFile(path, 0, 0);
    }

    public static Font fromFile(String path, int extra, int space){

        String common = null;
        List<String> chardefs = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Font.class.getClassLoader().getResourceAsStream(path)))) {
            String line;
            while((line = reader.readLine()) != null) {
                if(line.startsWith("common"))common = line;
                if(line.startsWith("char"))chardefs.add(line);
            }
        } catch (IOException ex) {
            System.out.println("Couldnt read file");
        }

        int totalwidth = 512;
        int totalheight = 512;
        if(common == null){
            throw new IllegalArgumentException("bad font file");
        }else{
            for (String s : common.split(" ")){
                String[] tokens = s.split("=");
                if(tokens[0].equalsIgnoreCase("scaleW"))totalwidth = Integer.parseInt(tokens[1]);
                if(tokens[0].equalsIgnoreCase("scaleH"))totalheight = Integer.parseInt(tokens[1]);
            }
        }

        Map<Integer, Char> result = new HashMap<>();

        for(String chardef : chardefs){
            int id = -1;
            int x = -1;
            int y = -1;
            int width = -1;
            int height = -1;
            int xoffset = -1;
            int yoffset = -1;
            int xadvance = -1;

            for(String s : chardef.split(" ")){
                String[] tokens = s.split("=");

                if(tokens[0].equalsIgnoreCase("id"))id = Integer.parseInt(tokens[1]);

                if(tokens[0].equalsIgnoreCase("x"))x = Integer.parseInt(tokens[1]);
                if(tokens[0].equalsIgnoreCase("y"))y = Integer.parseInt(tokens[1]);

                if(tokens[0].equalsIgnoreCase("width"))width = Integer.parseInt(tokens[1]);
                if(tokens[0].equalsIgnoreCase("height"))height = Integer.parseInt(tokens[1]);

                if(tokens[0].equalsIgnoreCase("xoffset"))xoffset = Integer.parseInt(tokens[1]);
                if(tokens[0].equalsIgnoreCase("yoffset"))yoffset = Integer.parseInt(tokens[1]);

                if(tokens[0].equalsIgnoreCase("xadvance"))xadvance = Integer.parseInt(tokens[1]);
            }

            result.put(id, new Char(
                    (float)(x-extra)/totalwidth,
                    (float)(y-extra)/totalheight,
                    (float)(width+extra*2)/totalwidth,
                    (float)(height+extra*2)/totalheight,
                    (float)width,
                    (float)height,
                    (float)xoffset,
                    (float)yoffset,
                    (float)(xadvance+space)));
        }


        return new Font(result);
    }

    public static class Char{
        private float x;
        private float y;
        private float xtex;
        private float ytex;
        private float width;
        private float height;
        private float xoffset;
        private float yoffset;
        private float xadvance;

        public Char(float x, float y, float xtex, float ytex, float width, float height, float xoffset, float yoffset, float xadvance) {
            this.x = x;
            this.y = y;
            this.xtex = xtex;
            this.ytex = ytex;
            this.width = width;
            this.height = height;
            this.xoffset = xoffset;
            this.yoffset = yoffset;
            this.xadvance = xadvance;
        }

        public float getXTex() {
            return xtex;
        }

        public float getYTex() {
            return ytex;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public float getXOffset() {
            return xoffset;
        }

        public float getYOffset() {
            return yoffset;
        }

        public float getXAdvance() {
            return xadvance;
        }
    }
}

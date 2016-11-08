package com.github.sidit77.voxelworld;

import com.github.sidit77.voxelworld.world.blocks.ObjBlock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//This class can read a .obj file and return it as renderable float array.

public class ObjLoader {

    public static float[] loadMesh(String path){
        //Some lists for relevant vertex information from the file
        List<Float[]> positions = new ArrayList<>();
        List<Float[]> normals = new ArrayList<>();
        List<Float[]> uvs = new ArrayList<>();

        List<Integer[]> vertices = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(ObjBlock.class.getClassLoader().getResourceAsStream(path)))) {
            String line;
            //for each line in the file....
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");

                switch (tokens[0]){
                    case "v":
                        //if the line starts with a 'v' it contains a 3d position
                        positions.add(new Float[]{Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3])});
                        break;
                    case "vn":
                        //if the line starts with a 'vn' it contains a 3d normal
                        normals.add(new Float[]{Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3])});
                        break;
                    case "vt":
                        //if the line starts with a 'vt' it contains a 2d texture coordinate
                        uvs.add(new Float[]{Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])});
                        break;
                    case "f":
                        //if the line starts with a face and a position/texcoord/normal index
                        String[] v1 = tokens[1].split("/");
                        String[] v2 = tokens[2].split("/");
                        String[] v3 = tokens[3].split("/");
                        vertices.add(new Integer[]{Integer.parseInt(v1[0]), Integer.parseInt(v1[1]), Integer.parseInt(v1[2])});
                        vertices.add(new Integer[]{Integer.parseInt(v2[0]), Integer.parseInt(v2[1]), Integer.parseInt(v2[2])});
                        vertices.add(new Integer[]{Integer.parseInt(v3[0]), Integer.parseInt(v3[1]), Integer.parseInt(v3[2])});

                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Couldnt read file");
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Bad file");
        }

        float[] faces = new float[vertices.size() * 8];
        for(int i = 0; i < vertices.size(); i++){
            Integer[] vertex = vertices.get(i);
            Float[] position = positions.get(vertex[0] - 1);
            Float[] normal = normals.get(vertex[2] - 1);
            Float[] uv = uvs.get(vertex[1] - 1);

            faces[i * 8 + 0] = position[0];
            faces[i * 8 + 1] = position[1];
            faces[i * 8 + 2] = position[2];
            faces[i * 8 + 3] = uv[0];
            faces[i * 8 + 4] = uv[1];
            faces[i * 8 + 5] = normal[0];
            faces[i * 8 + 6] = normal[1];
            faces[i * 8 + 7] = normal[2];
        }

        return faces;
    }

}

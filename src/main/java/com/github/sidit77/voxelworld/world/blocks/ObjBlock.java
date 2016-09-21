package com.github.sidit77.voxelworld.world.blocks;

import com.github.sidit77.voxelworld.world.Block;
import com.github.sidit77.voxelworld.world.Direction;
import com.github.sidit77.voxelworld.world.ISpecialRenderer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjBlock extends Block implements ISpecialRenderer {

    private float[] faces;

    public ObjBlock(int texture, String name, String path) {
        super(texture, name);

        List<Float[]> positions = new ArrayList<>();
        List<Float[]> normals = new ArrayList<>();
        List<Float[]> uvs = new ArrayList<>();

        List<Integer[]> vertices = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(ObjBlock.class.getClassLoader().getResourceAsStream(path)))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");

                switch (tokens[0]){
                    case "v":
                        positions.add(new Float[]{Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3])});
                        break;
                    case "vn":
                        normals.add(new Float[]{Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3])});
                        break;
                    case "vt":
                        uvs.add(new Float[]{Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])});
                        break;
                    case "f":
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

        faces = new float[vertices.size() * 8];
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

    }

    @Override
    public void addMeshToList(int x, int y, int z, ArrayList<Float> list) {
        for(int i = 0; i < faces.length; i += 8){
            list.add(x + faces[i + 0]);
            list.add(y + faces[i + 1]);
            list.add(z + faces[i + 2]);

            list.add(getTextureID(Direction.UP) % 4 * 0.25f + faces[i + 3] * 0.25f);
            list.add(getTextureID(Direction.UP) / 4 * 0.25f + faces[i + 4] * 0.25f);

            list.add(faces[i + 5]);
            list.add(faces[i + 6]);
            list.add(faces[i + 7]);

            list.add(1.0f); //TODO CHANGE

        }
    }

}

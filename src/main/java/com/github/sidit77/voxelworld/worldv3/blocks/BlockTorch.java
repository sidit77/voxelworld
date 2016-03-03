package com.github.sidit77.voxelworld.worldv3.blocks;

import com.github.sidit77.voxelworld.worldv3.Block;
import com.github.sidit77.voxelworld.worldv3.Direction;
import com.github.sidit77.voxelworld.worldv3.mesh.ChunkMesh;
import com.github.sidit77.voxelworld.worldv3.mesh.Vertex;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BlockTorch extends Block {

    private Vector3f[] vertex;
    private Vector2f[] tex;
    private int[][][] faces;

    public BlockTorch() {
        super(0, "Torch");
        ArrayList<Vector3f> vertices = new ArrayList<>();
        ArrayList<Vector2f> texCoords = new ArrayList<>();
        ArrayList<Integer[][]> indices = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(BlockTorch.class.getClassLoader().getResourceAsStream("assets/texture/worldv3/torch.obj")))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if(tokens[0].equalsIgnoreCase("v")){
                    vertices.add(new Vector3f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2]),Float.parseFloat(tokens[3])));
                }
                if(tokens[0].equalsIgnoreCase("vt")){
                    texCoords.add(new Vector2f(Float.parseFloat(tokens[1]),Float.parseFloat(tokens[2])));
                }
                if(tokens[0].equalsIgnoreCase("f")){

                    Integer[][] face = new Integer[3][2];
                    for(int i = 1; i < 4; i++){
                        String[] tonkens2 = tokens[i].split("/");
                        face[i-1][0] = Integer.parseInt(tonkens2[0]);
                        face[i-1][1] = Integer.parseInt(tonkens2[1]);
                    }
                    indices.add(face);
                }
            }
        } catch (IOException ex) {
            System.out.println("Couldnt read file");
        }
        vertex = vertices.toArray(new Vector3f[vertices.size()]);
        tex = texCoords.toArray(new Vector2f[texCoords.size()]);
        faces = new int[indices.size()][3][2];
        for(int i = 0; i < indices.size(); i++){
            Integer[][] f = indices.get(i);
            faces[i][0][0] = f[0][0]-1;
            faces[i][0][1] = f[0][1]-1;

            faces[i][1][0] = f[1][0]-1;
            faces[i][1][1] = f[1][1]-1;

            faces[i][2][0] = f[2][0]-1;
            faces[i][2][1] = f[2][1]-1;
        }
    }

    @Override
    public boolean isSolid(Direction direction) {
        return false;
    }

    @Override
    public boolean hasHitbox() {
        return false;
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public boolean isLightSource() {
        return true;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public void addToChunkMesh(ChunkMesh mesh, int x, int y, int z, Block[] neightbors, int[] lightlevels) {
        for(int[][] face : faces){
            mesh.addTriangle(new Vertex(new Vector3f(x,y,z).add(vertex[face[0][0]]), tex[face[0][1]], 7, getLightLevel()),
                             new Vertex(new Vector3f(x,y,z).add(vertex[face[1][0]]), tex[face[1][1]], 7, getLightLevel()),
                             new Vertex(new Vector3f(x,y,z).add(vertex[face[2][0]]), tex[face[2][1]], 7, getLightLevel()));

        }

    }

}

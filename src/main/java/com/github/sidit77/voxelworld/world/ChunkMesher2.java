package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.util.*;

public class ChunkMesher2 {


    public static ChunkMesh createMesh(Chunk[][][] chunks){
        long t = System.currentTimeMillis();


        Map<Integer, GreedyMesh> meshMap = new HashMap<>();

       // Vector3f[] p = {new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f()};
       // //int index = 0;
//
       // //GreedyMesh greedyMesh = new GreedyMesh(1);
//
       // for(int x = 0; x < Chunk.size; x += 1){
       //     for(int y = 0; y < Chunk.size; y += 1){
       //         for(int z = 0; z < Chunk.size; z += 1){
       //             int material = chunks[1][1][1].getMaterial(x, y, z);
       //             if(material != 0) {
       //                 for (int[][] face : faces) {
       //                     if (isAir(face[0][0] + x, face[0][1] + y, face[0][2] + z, chunks)) {
       //                         //for (int i : face[1]) {
       //                         //    Vertex vert = new Vertex(new Vector3f(vertex[i]).add(x,y,z).add(chunks[1][1][1].getPosition()), material);
       //                         //    if (!indicesMap.containsKey(vert)) {
       //                         //        indicesMap.put(vert, index);
       //                         //        indices.add(index);
       //                         //        index++;
       //                         //        vertices.add(vert);
       //                         //    } else {
       //                         //        indices.add(indicesMap.get(vert));
       //                         //    }
       //                         //}
//
       //                         if(!meshMap.containsKey(material)){
       //                             meshMap.put(material, new GreedyMesh(material));
       //                         }
//
       //                         meshMap.get(material).addFace(
       //                                 new Vector3f(vertex[face[1][0]]).add(x,y,z).add(chunks[1][1][1].getPosition()),
       //                                 new Vector3f(vertex[face[1][1]]).add(x,y,z).add(chunks[1][1][1].getPosition()),
       //                                 new Vector3f(vertex[face[1][2]]).add(x,y,z).add(chunks[1][1][1].getPosition()),
       //                                 new Vector3f(vertex[face[1][3]]).add(x,y,z).add(chunks[1][1][1].getPosition()),
       //                                 new Vector3f((face[0][0]),(face[0][1]),(face[0][2])));
//
       //                     }
       //                 }
       //             }
       //         }
       //     }
       // }


          //  for (int d = 0; d < 3; d++) {
          //      int i, j, k, l, w, h, u = (d + 1) % 3, v = (d + 2) % 3;
          //      int[] x = new int[3];
          //      int[] q = new int[3];
          //      boolean[] mask = new boolean[32 * 32];
//
          //      q[d] = 1;
//
          //      for (x[d] = -1; x[d] < 32; ) {
          //          // Compute the mask
          //          int n = 0;
          //          for (x[v] = 0; x[v] < 32; ++x[v]) {
          //              for (x[u] = 0; x[u] < 32; ++x[u]) {
          //                  mask[n++] = (0 <= x[d] && !isAir(x[0], x[1], x[2], chunks)) != (x[d] < 32 - 1 && !isAir(x[0] + q[0], x[1] + q[1], x[2] + q[2], chunks));
          //              }
          //          }
//
          //          // Increment x[d]
          //          ++x[d];
//
          //          // Generate mesh for mask using lexicographic ordering
          //          n = 0;
          //          for (j = 0; j < 32; ++j) {
          //              for (i = 0; i < 32; ) {
          //                  if (mask[n]) {
          //                      // Compute width
          //                      for (w = 1; i + w < 32 && mask[n + w]; ++w) ;
//
          //                      // Compute height (this is slightly awkward
          //                      boolean done = false;
          //                      for (h = 1; j + h < 32; ++h) {
          //                          for (k = 0; k < w; ++k) {
          //                              if (!mask[n + k + h * 32]) {
          //                                  done = true;
          //                                  break;
          //                              }
          //                          }
          //                          if (done) break;
          //                      }
//
          //                      // Add quad
          //                      x[u] = i;
          //                      x[v] = j;
          //                      int[] du = new int[3];
          //                      int[] dv = new int[3];
          //                      du[u] = w;
          //                      dv[v] = h;
//
          //                      if (!meshMap.containsKey(1)) {
        //                          meshMap.put(1, new GreedyMesh(1));
        //                      }
//
          //                      meshMap.get(1).addFace(
          //                              new Vector3f(x[0], x[1], x[2]).add(chunks[1][1][1].getPosition()),
          //                              new Vector3f(x[0] + du[0], x[1] + du[1], x[2] + du[2]).add(chunks[1][1][1].getPosition()),
          //                              new Vector3f(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2]).add(chunks[1][1][1].getPosition()),
          //                              new Vector3f(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2]).add(chunks[1][1][1].getPosition()),
          //                              new Vector3f(0));
//
          //                      // Zero-out mask
          //                      for (l = 0; l < h; ++l) {
          //                          for (k = 0; k < w; ++k) {
          //                              mask[n + k + l * 32] = false;
          //                          }
          //                      }
//
          //                      // Increment counters and continue
          //                      i += w;
          //                      n += w;
          //                  } else {
          //                      ++i;
          //                      ++n;
          //                  }
          //              }
          //          }
          //      }
          //  }



        /*
         * These are just working variables for the algorithm - almost all taken
         * directly from Mikola Lysenko's javascript implementation.
         */
        int i, j, k, l, w, h, u, v, n, side = 0;

        final int[] x = new int []{0,0,0};
        final int[] q = new int []{0,0,0};
        final int[] du = new int[]{0,0,0};
        final int[] dv = new int[]{0,0,0};

        /*
         * We create a mask - this will contain the groups of matching voxel faces
         * as we proceed through the chunk in 6 directions - once for each face.
         */
        final VoxelFace[] mask = new VoxelFace[Chunk.size * Chunk.size];

        /*
         * These are just working variables to hold two faces during comparison.
         */
        VoxelFace voxelFace, voxelFace1;

        /**
         * We start with the lesser-spotted boolean for-loop (also known as the old flippy floppy).
         *
         * The variable backFace will be TRUE on the first iteration and FALSE on the second - this allows
         * us to track which direction the indices should run during creation of the quad.
         *
         * This loop runs twice, and the inner loop 3 times - totally 6 iterations - one for each
         * voxel face.
         */
        for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b) {

            /*
             * We sweep over the 3 dimensions - most of what follows is well described by Mikola Lysenko
             * in his post - and is ported from his Javascript implementation.  Where this implementation
             * diverges, I've added commentary.
             */
            for(int d = 0; d < 3; d++) {

                u = (d + 1) % 3;
                v = (d + 2) % 3;

                x[0] = 0;
                x[1] = 0;
                x[2] = 0;

                q[0] = 0;
                q[1] = 0;
                q[2] = 0;
                q[d] = 1;

                /*
                 * Here we're keeping track of the side that we're meshing.
                 */
                if (d == 0)      { side = backFace ? WEST   : EAST;  }
                else if (d == 1) { side = backFace ? BOTTOM : TOP;   }
                else if (d == 2) { side = backFace ? SOUTH  : NORTH; }

                /*
                 * We move through the dimension from front to back
                 */
                for(x[d] = -1; x[d] < Chunk.size;) {

                    /*
                     * -------------------------------------------------------------------
                     *   We compute the mask
                     * -------------------------------------------------------------------
                     */
                    n = 0;

                    for(x[v] = 0; x[v] < Chunk.size; x[v]++) {

                        for(x[u] = 0; x[u] < Chunk.size; x[u]++) {

                            /*
                             * Here we retrieve two voxel faces for comparison.
                             */
                            voxelFace  = getVoxelFace(x[0], x[1], x[2], side, chunks);
                            voxelFace1 = getVoxelFace(x[0] + q[0], x[1] + q[1], x[2] + q[2], side, chunks);

                            /*
                             * Note that we're using the equals function in the voxel face class here, which lets the faces
                             * be compared based on any number of attributes.
                             *
                             * Also, we choose the face to add to the mask depending on whether we're moving through on a backface or not.
                             */
                            mask[n++] = ((voxelFace != null && voxelFace1 != null))
                                    ? null
                                    : backFace ? voxelFace1 : voxelFace;
                        }
                    }

                    x[d]++;

                    /*
                     * Now we generate the mesh for the mask
                     */
                    n = 0;

                    for(j = 0; j < Chunk.size; j++) {

                        for(i = 0; i < Chunk.size;) {

                            if(mask[n] != null) {

                                /*
                                 * We compute the width
                                 */
                                for(w = 1; i + w < Chunk.size && mask[n + w] != null && mask[n + w].equals(mask[n]); w++) {}

                                /*
                                 * Then we compute height
                                 */
                                boolean done = false;

                                for(h = 1; j + h < Chunk.size; h++) {

                                    for(k = 0; k < w; k++) {

                                        if(mask[n + k + h * Chunk.size] == null || !mask[n + k + h * Chunk.size].equals(mask[n])) { done = true; break; }
                                    }

                                    if(done) { break; }
                                }

                                /*
                                 * Here we check the "transparent" attribute in the VoxelFace class to ensure that we don't mesh
                                 * any culled faces.
                                 */
                                if (!mask[n].transparent) {
                                    /*
                                     * Add quad
                                     */
                                    x[u] = i;
                                    x[v] = j;

                                    du[0] = 0;
                                    du[1] = 0;
                                    du[2] = 0;
                                    du[u] = w;

                                    dv[0] = 0;
                                    dv[1] = 0;
                                    dv[2] = 0;
                                    dv[v] = h;

                                    /*
                                     * And here we call the quad function in order to render a merged quad in the scene.
                                     *
                                     * We pass mask[n] to the function, which is an instance of the VoxelFace class containing
                                     * all the attributes of the face - which allows for variables to be passed to shaders - for
                                     * example lighting values used to create ambient occlusion.
                                     */
                                    //quad(new Vector3f(x[0],                 x[1],                   x[2]),
                                    //        new Vector3f(x[0] + du[0],         x[1] + du[1],           x[2] + du[2]),
                                    //        new Vector3f(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1],   x[2] + du[2] + dv[2]),
                                    //        new Vector3f(x[0] + dv[0],         x[1] + dv[1],           x[2] + dv[2]),
                                    //        w,
                                    //        h,
                                    //        mask[n],
                                    //        backFace);

                                    if (!meshMap.containsKey(mask[n].type)) {
                                        meshMap.put(mask[n].type, new GreedyMesh(mask[n].type));
                                    }

                                    meshMap.get(mask[n].type).addFace(
                                            new Vector3f(x[0],                 x[1],                   x[2]).add(chunks[1][1][1].getPosition()),
                                            new Vector3f(x[0] + du[0],         x[1] + du[1],           x[2] + du[2]).add(chunks[1][1][1].getPosition()),
                                            new Vector3f(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1],   x[2] + du[2] + dv[2]).add(chunks[1][1][1].getPosition()),
                                            new Vector3f(x[0] + dv[0],         x[1] + dv[1],           x[2] + dv[2]).add(chunks[1][1][1].getPosition()),
                                            backFace);

                                }

                                /*
                                 * We zero out the mask
                                 */
                                for(l = 0; l < h; ++l) {

                                    for(k = 0; k < w; ++k) { mask[n + k + l * Chunk.size] = null; }
                                }

                                /*
                                 * And then finally increment the counters and continue
                                 */
                                i += w;
                                n += w;

                            } else {

                                i++;
                                n++;
                            }
                        }
                    }
                }
            }
        }



        List<Integer> indices = new ArrayList<>();
        List<Float> vertices = new ArrayList<>();
        meshMap.forEach((integer, greedyMesh) -> {
            greedyMesh.addDataToList(vertices, indices);
        });


        ChunkMesh m = new ChunkMesh();
        m.vertices = BufferUtils.createFloatBuffer(vertices.size() * 4);
        vertices.forEach((vertex)->{
            m.vertices.put(vertex);//.getData()
        });
        m.vertices.flip();

        m.indices = BufferUtils.createIntBuffer(indices.size());
        indices.forEach((index)-> m.indices.put(index));
        m.indices.flip();

        m.indicesCount = indices.size();
        m.pos = chunks[1][1][1].getPosition();

        //System.out.println(System.currentTimeMillis() - t);

        return m;
    }

    static VoxelFace getVoxelFace(final int x, final int y, final int z, final int side, Chunk[][][] chunks) {
        int material = 0;
        if(x >= 0 && x < Chunk.size && y >= 0 && y < Chunk.size && z >= 0 && z < Chunk.size){
            material = chunks[1][1][1].getMaterial(x,y,z);
        }
        if(x >= Chunk.size || y >= Chunk.size || z >= Chunk.size){
            Chunk c2 = chunks[x >= Chunk.size ? 2 : 1][y >= Chunk.size ? 2 : 1][z >= Chunk.size ? 2 : 1];
            if(c2 != null){
                material = c2.getMaterial(x % Chunk.size,y % Chunk.size,z % Chunk.size);
            }
        }
        if(x < 0 || y < 0 || z < 0){
            Chunk c2 = chunks[x < 0 ? 0 : 1][y < 0 ? 0 : 1][z < 0 ? 0 : 1];
            if(c2 != null){
                material = c2.getMaterial((Chunk.size + x)%Chunk.size,(Chunk.size + y)%Chunk.size,(Chunk.size + z)%Chunk.size);
            }
        }

        VoxelFace voxelFace = new VoxelFace();

        voxelFace.type = material;

        voxelFace.side = side;

        return voxelFace.type == 0 ? null : voxelFace;
    }

    private static boolean isAir(int x, int y, int z, Chunk[][][] chunks){
        if(x >= 0 && x < Chunk.size && y >= 0 && y < Chunk.size && z >= 0 && z < Chunk.size){
            return chunks[1][1][1].getMaterial(x,y,z) == 0;
        }
        if(x >= Chunk.size || y >= Chunk.size || z >= Chunk.size){
            Chunk c2 = chunks[x >= Chunk.size ? 2 : 1][y >= Chunk.size ? 2 : 1][z >= Chunk.size ? 2 : 1];
            if(c2 != null){
                return c2.getMaterial(x % Chunk.size,y % Chunk.size,z % Chunk.size) == 0;
            }
        }
        if(x < 0 || y < 0 || z < 0){
            Chunk c2 = chunks[x < 0 ? 0 : 1][y < 0 ? 0 : 1][z < 0 ? 0 : 1];
            if(c2 != null){
                return c2.getMaterial((Chunk.size + x)%Chunk.size,(Chunk.size + y)%Chunk.size,(Chunk.size + z)%Chunk.size) == 0;
            }
        }
        return false;
    }

    private static class Vertex{

        private Vector3f position;
        private float material;

        public Vertex(Vector3f position, float material) {
            this.position = position;
            this.material = material;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return Float.compare(vertex.material, material) == 0 &&
                    Objects.equals(position, vertex.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, material);
        }

        public float[] getData(){
            return new float[]{position.x, position.y, position.z, material};
        }
    }

    /**
     * This class is used to encapsulate all information about a single voxel face.  Any number of attributes can be
     * included - and the equals function will be called in order to compare faces.  This is important because it
     * allows different faces of the same voxel to be merged based on varying attributes.
     *
     * Each face can contain vertex data - for example, int[] sunlight, in order to compare vertex attributes.
     *
     * Since it's optimal to combine greedy meshing with face culling, I have included a "transparent" attribute here
     * and the mesher skips transparent voxel faces.  The getVoxelData function below - or whatever it's equivalent
     * might be when this algorithm is used in a real engine - could set the transparent attribute on faces based
     * on whether they should be visible or not.
     */
    static class VoxelFace {

        public boolean transparent;
        public int type;
        public int side;

        public boolean equals(final VoxelFace face) { return face.transparent == this.transparent && face.type == this.type; }
    }


    /*
     * These are just constants to keep track of which face we're dealing with - their actual
     * values are unimportantly - only that they're constant.
     */
    private static final int SOUTH      = 0;
    private static final int NORTH      = 1;
    private static final int EAST       = 2;
    private static final int WEST       = 3;
    private static final int TOP        = 4;
    private static final int BOTTOM     = 5;

    private static final Vector3f[] vertex = {
            new Vector3f (-0.5f, -0.5f,  0.5f),
            new Vector3f ( 0.5f, -0.5f,  0.5f),
            new Vector3f ( 0.5f,  0.5f,  0.5f),
            new Vector3f (-0.5f,  0.5f,  0.5f),
            new Vector3f (-0.5f, -0.5f, -0.5f),
            new Vector3f ( 0.5f, -0.5f, -0.5f),
            new Vector3f ( 0.5f,  0.5f, -0.5f),
            new Vector3f (-0.5f,  0.5f, -0.5f)
    };

    private static final int[][][] faces = {
            {{ 0, 0, 1},{0,1,2,3}},
            {{ 0, 1, 0},{3,2,6,7}},
            {{ 0, 0,-1},{5,4,7,6}},
            {{-1, 0, 0},{4,0,3,7}},
            {{ 0,-1, 0},{1,0,4,5}},
            {{ 1, 0, 0},{1,5,6,2}}
    };

}

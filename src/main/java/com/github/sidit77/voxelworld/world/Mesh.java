package com.github.sidit77.voxelworld.world;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class Mesh {

    private ArrayList<Vertex> vertices;
    private ArrayList<Triangle> triangles;

    public Mesh(Vector3f[][] tris, Set<Vector3f> unremoveablepos){
        HashMap<Vector3f, Vertex> vertexBuffer = new HashMap<>();
        vertices = new ArrayList<>();
        triangles = new ArrayList<>();

        for(Vector3f[] v : tris){
            for(int i = 0; i < 3; i++){
                if(!vertexBuffer.containsKey(v[i])){
                    vertexBuffer.put(v[i], new Vertex(v[i]));
                    if(unremoveablepos.contains(v[i])){
                        vertexBuffer.get(v[i]).setUnremoveable();
                    }
                    vertices.add(vertexBuffer.get(v[i]));
                }
            }
            triangles.add(new Triangle(vertexBuffer.get(v[0]), vertexBuffer.get(v[1]), vertexBuffer.get(v[2])).register());
        }

        vertices.forEach((v)->{
            if(v.isRemoveable()){
                //v.delete(); Disable simplification because broken
            }
        });

        clean();
    }

    public Mesh(Vector3f[][] tris){
        this(tris, new HashSet<>());
    }

    private void clean(){
        triangles.removeIf(Triangle::isDeleted);
        vertices.removeIf(Vertex::isDeleted);
    }

    private class Edge{
        private Vertex to;
        private float cost;

        public Edge(Vertex u, Vertex v){
            cost = computeEdgeCollapseCost(u, v);
            to = v;
        }

        float computeEdgeCollapseCost(Vertex src, Vertex dest) {

            float t = 0.001f;

            for (Triangle triangle : src.getFaces()) {
                float mincurv = 1.0f;

                for (Triangle triangle2 : src.getFaces()) {
                    if (triangle2.hasVertex(dest)) {

                        float dotprod = triangle.getNormal().dot(triangle2.getNormal());
                        mincurv = Math.min(mincurv, (1.002f - dotprod) * 0.5f);
                    }
                }
                t = Math.max(t, mincurv);
            }

            return t * src.position.distanceSquared(dest.position);
        }

        public Vertex getTo() {
            return to;
        }

        public float getCost() {
            return cost;
        }

        public boolean isDeleted(){
            return to.isDeleted();
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 79 * hash + Objects.hashCode(this.to);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Edge other = (Edge) obj;
            return Objects.equals(this.to, other.to);
        }

    }

    private class Vertex{

        private Vector3f position;
        private ArrayList<Triangle> faces;
        private ArrayList<Edge> neighbors;
        private boolean deleted;
        private boolean removeable;

        public Vertex(Vector3f position){
            this.position = position;
            this.faces = new ArrayList<>();
            this.neighbors = new ArrayList<>();
            this.deleted = false;
            this.removeable = true;
        }

        public void registerFace(Triangle t){
            if(!faces.contains(t)){
                faces.add(t);
            }
            for(int i = 0; i < 3; i++){
                if(t.getVertex(i) != this){
                    Edge e = new Edge(this, t.getVertex(i));
                    if(!neighbors.contains(e)){
                        neighbors.add(e);
                    }
                }
            }

            neighbors.sort((e1,e2) -> Math.round(e1.getCost() - e2.getCost()));
        }

        public void setUnremoveable(){
            this.removeable = false;
        }

        public void clean(){
            faces.removeIf(Triangle::isDeleted);
            neighbors.removeIf(Edge::isDeleted);
        }

        public ArrayList<Triangle> getFaces(){
            return faces;
        }

        public ArrayList<Edge> getNeightbors(){
            return neighbors;
        }

        public Vector3f getNormal(){
            Vector3f vec = new Vector3f(0);
            faces.stream().forEach((t) -> vec.add(t.getNormal()));
            return vec.normalize();
        }

        public Vector3f getPosition(){
            return position;
        }

        public boolean isInside(){
            return faces.size() == neighbors.size();
        }

        public float getCost(){
            return neighbors.isEmpty() ? 0 : neighbors.get(0).getCost();
        }

        public boolean isRemoveable(){

            return removeable && getCost() < 0.01f;
        }

        public boolean isDeleted(){
            return deleted;
        }

        public void delete(){
            deleted = true;

            if(!neighbors.isEmpty()){
                for(Triangle t : faces){
                    t.setVertex(this, neighbors.get(0).getTo());
                }
            }

            neighbors.stream().forEach((v) -> v.getTo().clean());
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 31 * hash + Objects.hashCode(this.position);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Vertex other = (Vertex) obj;
            return Objects.equals(this.position, other.position);
        }
    }

    private class Triangle{
        private Vertex[] v;
        private Vector3f normal;
        public Triangle(Vertex v0, Vertex v1, Vertex v2){
            v = new Vertex[]{v0,v1,v2};
            cumputeNormal();
        }
        public Triangle register(){
            for(Vertex ve : v){
                ve.registerFace(this);
            }
            return this;
        }
        public Vector3f getNormal(){
            return normal;
        }
        public boolean isDeleted(){
            return v[0].isDeleted() || v[1].isDeleted() || v[2].isDeleted() || v[0] == v[1] || v[1] == v[2] || v[0] == v[2];
        }
        public Vertex getVertex(int i){
            return v[i];
        }
        public boolean hasVertex(Vertex vert){
            return vert == v[0] || vert == v[1] || vert == v[2];
        }
        public void setVertex(Vertex src, Vertex dest){
            if(v[0] == src){
                v[0] = dest;
            }
            if(v[1] == src){
                v[1] = dest;
            }
            if(v[2] == src){
                v[2] = dest;
            }
            dest.registerFace(this);
            cumputeNormal();
        }
        private void cumputeNormal(){
            Vector3f ab = new Vector3f(v[1].getPosition()).sub(v[0].getPosition());
            Vector3f ac = new Vector3f(v[2].getPosition()).sub(v[0].getPosition());
            normal = ab.cross(ac).normalize();
            if(Float.isNaN(normal.x) || Float.isNaN(normal.y) || Float.isNaN(normal.z))normal = new Vector3f(0);
        }
    }

    public MeshData getData(){
        MeshData data = new MeshData();
        data.indices = BufferUtils.createIntBuffer(triangles.size() * 3);
        data.vertices = BufferUtils.createFloatBuffer(vertices.size() * 3);

        HashMap<Vertex, Integer> index = new HashMap<>();

        vertices.stream().forEach((v) -> {
            index.put(v, index.size());
            data.vertices.put(v.getPosition().x);
            data.vertices.put(v.getPosition().y);
            data.vertices.put(v.getPosition().z);
        });

        triangles.stream().forEach((t) -> {
            data.indices.put(index.get(t.getVertex(0)));
            data.indices.put(index.get(t.getVertex(1)));
            data.indices.put(index.get(t.getVertex(2)));
        });

        data.indices.flip();
        data.vertices.flip();

        return data;
    }

    public class MeshData{
        public FloatBuffer vertices;
        public IntBuffer indices;
    }

}

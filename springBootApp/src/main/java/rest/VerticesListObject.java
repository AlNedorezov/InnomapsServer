package rest;

import java.util.List;
import pathfinding.LatLngGraphVertex;

/**
 * Created by alnedorezov on 6/7/16.
 */
public class VerticesListObject {

        private List<LatLngGraphVertex> vertices;

        public VerticesListObject(List<LatLngGraphVertex> vertices) {
            this.vertices = vertices;
        }

        public void addVertex(LatLngGraphVertex category) {
            this.vertices.add(category);
        }

        public void setVertex(int index, LatLngGraphVertex category) {
            this.vertices.set(index, category);
        }

        public void getVertex(int index) {
            this.vertices.get(index);
        }

        public void removeVertex(int index) {
            this.vertices.remove(index);
        }

        public List<LatLngGraphVertex> getVertices() { return vertices; }
}

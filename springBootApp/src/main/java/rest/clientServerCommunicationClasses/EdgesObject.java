package rest.clientServerCommunicationClasses;

import db.Edge;

import java.util.List;

/**
 * Created by alnedorezov on 6/21/16.
 */
public class EdgesObject {
    private List<Edge> edges;

    public EdgesObject(List<Edge> edges) {
        this.edges = edges;
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    public void setEdge(int index, Edge edge) {
        this.edges.set(index, edge);
    }

    public void getEdge(int index) {
        this.edges.get(index);
    }

    public void removeEdge(int index) {
        this.edges.remove(index);
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
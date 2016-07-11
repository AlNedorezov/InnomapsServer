package rest.clientservercommunicationclasses;

import db.EdgeType;

import java.util.List;

/**
 * Created by alnedorezov on 6/21/16.
 */

public class EdgeTypesObject {
    private List<EdgeType> edgeTypes;

    public EdgeTypesObject(List<EdgeType> edgeTypes) {
        this.edgeTypes = edgeTypes;
    }

    public void addEdgeType(EdgeType edgeType) {
        this.edgeTypes.add(edgeType);
    }

    public void setEdgeType(int index, EdgeType edgeType) {
        this.edgeTypes.set(index, edgeType);
    }

    public void getEdgeType(int index) {
        this.edgeTypes.get(index);
    }

    public void removeEdgeType(int index) {
        this.edgeTypes.remove(index);
    }

    public List<EdgeType> getEdgetypes() {
        return edgeTypes;
    }
}
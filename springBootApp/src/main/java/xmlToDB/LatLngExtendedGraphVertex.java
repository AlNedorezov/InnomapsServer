package xmlToDB;

import pathfinding.JGraphTWrapper;
import pathfinding.LatLng;
import pathfinding.LatLngGraphVertex;

/**
 * Extention of LatLngGraphVertex class, necessary for moving data from xml to db
 * Created by alnedorezov on 6/10/16.
 */
public class LatLngExtendedGraphVertex extends LatLngGraphVertex {

    private String name;
    private String description;
    private Integer number;

    public LatLngExtendedGraphVertex(LatLng vertex, int vertexId, JGraphTWrapper.GraphElementType graphVertexType,
                                     String name, String description, Integer number) {
        super(vertex, vertexId, graphVertexType);
        this.name = name;
        this.description = description;
        this.number = number;
    }

    public LatLng getVertex() {
        return super.getVertex();
    }

    public int getVertexId() {
        return super.getVertexId();
    }

    public JGraphTWrapper.GraphElementType getGraphVertexType() {
        return super.getGraphVertexType();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getNumber() {
        return number;
    }
}

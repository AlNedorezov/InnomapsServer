package xmlToDB;

import org.jgrapht.graph.SimpleWeightedGraph;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import pathfinding.*;
import rest.clientservercommunicationclasses.ClosestCoordinateWithDistance;

import java.io.*;
import java.util.HashMap;

/**
 * Created by alnedorezov on 6/10/16.
 * <p>
 * This class is intended to help transfer data from xml file to the database.
 */
public class ExtendedJGraphTWrapper extends JGraphTWrapper {

    // Graph with extra data from xml
    private SimpleWeightedGraph<LatLngExtendedGraphVertex, LatLngGraphEdge> graph;

    private void addExtendedVertex(LatLngFlr v, int id, JGraphTWrapper.GraphElementType graphVertexType,
                                   String name, String description, Integer number) {
        LatLngExtendedGraphVertex vTemp = new LatLngExtendedGraphVertex(v, id, graphVertexType, name, description, number);
        graph.addVertex(vTemp);
    }

    /**
     * Adds new edge of given type.
     *
     * @param v1               - vertex edge begins
     * @param v2               - vertex edge ends
     * @param v1Index          - v1 vertex index
     * @param v2Index          - v2 vertex index
     * @param graphElementType - edge type (see LatLngGraphEdge.GraphElementType)
     */

    public void addEdge(LatLng v1, LatLng v2, int v1Index, int v2Index, GraphElementType graphElementType) {
        LatLngExtendedGraphVertex[] verticesList = getVertices();
        LatLngExtendedGraphVertex gv1 = null;
        LatLngExtendedGraphVertex gv2 = null;
        for (int i = 0; i < verticesList.length; i++) {
            if (verticesList[i].getVertexId() == v1Index)
                gv1 = verticesList[i];
            if (verticesList[i].getVertexId() == v2Index)
                gv2 = verticesList[i];
            if (gv1 != null && gv2 != null)
                break;
        }

        graph.addEdge(gv1, gv2, new LatLngGraphEdge(graphElementType));
        LatLngGraphEdge e = graph.getEdge(gv1, gv2);
        double penaltyWeight = (graphElementType == GraphElementType.DEFAULT) ? 0.0 : 1.0;
        graph.setEdgeWeight(e, haversine(gv1.getVertex().getLatitude(), gv1.getVertex().getLongitude(),
                gv2.getVertex().getLatitude(), gv2.getVertex().getLongitude()) + penaltyWeight);
    }

    /**
     * Imports coordinates for the graph from the file of GraphML format. Doesn't return anything but if import was
     * successful internal graph object will be replaced by the imported one.
     *
     * @param inputStream - stream to read.
     */
    public void importGraphML(InputStream inputStream) throws XmlPullParserException, FileNotFoundException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(br);
        int eventType = xpp.getEventType();

        graph = new SimpleWeightedGraph<>(LatLngGraphEdge.class);
        HashMap<Integer, LatLng> verticesMap = new HashMap<>();
        int id = -1;
        GraphElementType vertexType = GraphElementType.DEFAULT;
        String name = "";
        Integer number = null;
        String description = "";
        boolean nodeDataFound = false;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = xpp.getName();
                switch (tagName) {
                    case "node":
                        id = Integer.valueOf(xpp.getAttributeValue(null, "id"));
                        name = xpp.getAttributeValue(null, "name");
                        String numberString = xpp.getAttributeValue(null, "number");
                        if (numberString == null)
                            number = null;
                        else
                            number = Integer.valueOf(numberString);
                        String vType = xpp.getAttributeValue(null, "type");
                        // Possible types up to 17.06.2015 are DEFAULT, ELEVATOR, STAIRS,
                        // ROOM, FOOD, WC, CLINIC, READING, DOOR, LIBRARY and EASTER_EGG
                        if (vType == null) vertexType = GraphElementType.DEFAULT;
                        else if (vType.equals("stairs")) vertexType = GraphElementType.STAIRS;
                        else if (vType.equals("elevator")) vertexType = GraphElementType.ELEVATOR;
                        else if (vType.equals("room")) vertexType = GraphElementType.ROOM;
                        else if (vType.equals("food")) vertexType = GraphElementType.FOOD;
                        else if (vType.equals("wc")) vertexType = GraphElementType.WC;
                        else if (vType.equals("clinic")) vertexType = GraphElementType.CLINIC;
                        else if (vType.equals("reading")) vertexType = GraphElementType.READING;
                        else if (vType.equals("door")) vertexType = GraphElementType.DOOR;
                        else if (vType.equals("library")) vertexType = GraphElementType.LIBRARY;
                        else if (vType.equals("easter egg")) vertexType = GraphElementType.EASTER_EGG;
                        else vertexType = GraphElementType.DEFAULT;
                        description = xpp.getAttributeValue(null, "attr");
                        break;
                    case "edge":
                        int from = Integer.valueOf(xpp.getAttributeValue(null, "source"));
                        int to = Integer.valueOf(xpp.getAttributeValue(null, "target"));
                        String eType = xpp.getAttributeValue(null, "id");
                        GraphElementType graphEdgeType = GraphElementType.DEFAULT;
                        switch (eType) {
                            case "ELEVATOR":
                                graphEdgeType = GraphElementType.ELEVATOR;
                                break;
                            case "STAIRS":
                                graphEdgeType = GraphElementType.STAIRS;
                                break;
                            case "DEFAULT":
                                graphEdgeType = GraphElementType.DEFAULT;
                                break;
                        }
                        System.out.println(from + ", " + to);
                        addEdge(verticesMap.get(from), verticesMap.get(to), from, to, graphEdgeType);
                        break;
                    case "data":
                        if (id != -1) {
                            nodeDataFound = true;
                        }
                        break;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                if (id != -1 && nodeDataFound) {
                    String[] coords = xpp.getText().split(" ");
                    LatLngFlr latLng = new LatLngFlr(Double.valueOf(coords[0]), Double.valueOf(coords[1]), (int) Math.floor(id / 1000));
                    addExtendedVertex(latLng, id, vertexType, name, description, number);
                    verticesMap.put(id, latLng);
                    id = -1;
                    vertexType = GraphElementType.DEFAULT;
                    nodeDataFound = false;
                }
            }

            try {
                eventType = xpp.next();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns all graph vertices as array (warning - this method complexity is O(n))
     *
     * @return array of extended graph vertices
     */
    @Override
    public LatLngExtendedGraphVertex[] getVertices() {
        LatLngExtendedGraphVertex[] v = new LatLngExtendedGraphVertex[graph.vertexSet().size()];
        v = graph.vertexSet().toArray(v);
        return v;
    }

    public LatLngGraphEdge[] getEdges() {
        LatLngGraphEdge[] v = new LatLngGraphEdge[graph.edgeSet().size()];
        v = graph.edgeSet().toArray(v);
        return v;
    }

    @Override
    public ClosestCoordinateWithDistance findClosestCoordinateToGiven(LatLngFlr v) {
        LatLngGraphVertex[] verticesList = new LatLngGraphVertex[graph.vertexSet().size()];
        verticesList = graph.vertexSet().toArray(verticesList);
        LatLngFlr closestCoordinate = null;
        double shortestDistance = Double.MAX_VALUE;
        for (int i = 0; i < verticesList.length; i++) {
            LatLng candidateCoordinate = verticesList[i].getVertex();
            double candidateDistance = haversine(v.getLatitude(), v.getLongitude(), candidateCoordinate.getLatitude(), candidateCoordinate.getLongitude());
            if (candidateDistance < shortestDistance && v.getFloor() == (int) Math.floor(verticesList[i].getVertexId() / 1000)) {
                closestCoordinate = new LatLngFlr(candidateCoordinate.getLatitude(), candidateCoordinate.getLongitude(), v.getFloor());
                shortestDistance = candidateDistance;
            }
        }

        return new ClosestCoordinateWithDistance(closestCoordinate, shortestDistance);
    }

}

package xmlToDB;

import org.jgrapht.graph.SimpleWeightedGraph;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import pathfinding.JGraphTWrapper;
import pathfinding.LatLng;
import pathfinding.LatLngGraphEdge;

import java.io.*;
import java.util.HashMap;

/**
 * Created by alnedorezov on 6/10/16.
 */
public class ExtendedJGraphTWrapper extends JGraphTWrapper {

    // Graph with extra data from xml
    private SimpleWeightedGraph<LatLngExtendedGraphVertex, LatLngGraphEdge> graph;

    public SimpleWeightedGraph<LatLngExtendedGraphVertex, LatLngGraphEdge> getGraph() {
        return graph;
    }

    private void addExtendedVertex(LatLng v, int id, JGraphTWrapper.GraphElementType graphVertexType,
                                   String name, String description, Integer number) {
        LatLngExtendedGraphVertex vTemp = new LatLngExtendedGraphVertex(v, id, graphVertexType, name, description, number);
        graph.addVertex(vTemp);
    }

    /**
     * Imports coordinates for the graph from the file of GraphML format. Doesn't return anything but if import was
     * successful internal graph object will be replaced by the imported one.
     *
     * @param inputStream - stream to read.
     */
    @Override
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
                    case "data":
                        if (id != -1) {
                            nodeDataFound = true;
                        }
                        break;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                if (id != -1 && nodeDataFound) {
                    String[] coords = xpp.getText().split(" ");
                    LatLng latLng = new LatLng(Double.valueOf(coords[0]), Double.valueOf(coords[1]));
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
}

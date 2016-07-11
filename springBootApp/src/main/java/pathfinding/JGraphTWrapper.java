package pathfinding;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import db.Coordinate;
import db.CoordinateType;
import db.Edge;
import db.EdgeType;
import org.jgrapht.Graph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.UndirectedWeightedSubgraph;
import rest.Application;
import rest.clientservercommunicationclasses.ClosestCoordinateWithDistance;

import java.sql.SQLException;
import java.util.*;

/**
 * Wrapper for JGraphT library. Creating graphs, adding vertices and edges, searching for
 * the shortest paths and so on.
 */
public class JGraphTWrapper {
    public enum GraphElementType {
        DEFAULT, ELEVATOR, STAIRS, ROOM, FOOD, WC, CLINIC, READING, DOOR, LIBRARY, EASTER_EGG
    }

    private SimpleWeightedGraph<LatLngGraphVertex, LatLngGraphEdge> graph;
    private int currentVertexId;


    public JGraphTWrapper() {
        graph = new SimpleWeightedGraph<>(LatLngGraphEdge.class);
        currentVertexId = 0;
    }

    /**
     * Adds new vertex.
     *
     * @param v - vertex to add
     * @return id of vertex added
     */
    public int addVertex(LatLngFlr v, GraphElementType graphVertexType) {
        addVertexWithId(v, currentVertexId, graphVertexType);
        return currentVertexId++;
    }

    private void addVertexWithId(LatLngFlr v, int id, GraphElementType graphVertexType) {
        LatLngGraphVertex vTemp = new LatLngGraphVertex(v, id, graphVertexType);
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
    public void addEdge(LatLngFlr v1, LatLngFlr v2, int v1Index, int v2Index, GraphElementType graphElementType) {
        LatLngGraphVertex gv1 = new LatLngGraphVertex(v1, v1Index, graphElementType);
        LatLngGraphVertex gv2 = new LatLngGraphVertex(v2, v2Index, graphElementType);

        graph.addEdge(gv1, gv2, new LatLngGraphEdge(graphElementType));
        LatLngGraphEdge e = graph.getEdge(gv1, gv2);
        double penaltyWeight = (graphElementType == GraphElementType.DEFAULT) ? 0.0 : 1.0;
        graph.setEdgeWeight(e, haversine(gv1.getVertex().getLatitude(), gv1.getVertex().getLongitude(),
                gv2.getVertex().getLatitude(), gv2.getVertex().getLongitude()) + penaltyWeight);
    }

    /**
     * Removes edge from the graph
     */
    public void removeEdge(LatLngGraphVertex sourceVertex, LatLngGraphVertex targetVertex) {
        graph.removeEdge(sourceVertex, targetVertex);
    }

    /**
     * Shortest path using all edges.
     *
     * @param v1 - start LatLngFlr
     * @param v2 - end LatLngFlr
     * @return sequential list of LatLngFlr objects
     */
    public ArrayList<LatLngGraphVertex> shortestPath(LatLngFlr v1, LatLngFlr v2) {
        return shortestPathForGraph(v1, v2, graph);
    }

    private ArrayList<LatLngGraphVertex> shortestPathForGraph(LatLngFlr v1, LatLngFlr v2, Graph<LatLngGraphVertex, LatLngGraphEdge> g) {
        ArrayList<LatLngGraphVertex> pointsList = new ArrayList<>();

        LatLngGraphVertex vTemp1 = new LatLngGraphVertex(v1, 0, GraphElementType.DEFAULT);
        LatLngGraphVertex vTemp2 = new LatLngGraphVertex(v2, 0, GraphElementType.DEFAULT);
        if (!g.containsVertex(vTemp1)) {
            pointsList.add(vTemp1);
            vTemp1 = new LatLngGraphVertex(findClosestCoordinateToGiven(v1).getCoordinate(), 0, GraphElementType.DEFAULT);
        }

        DijkstraShortestPath<LatLngGraphVertex, LatLngGraphEdge> dijkstraPathFinder = new DijkstraShortestPath<>(g, vTemp1, vTemp2);
        List<LatLngGraphEdge> foundPath = dijkstraPathFinder.getPathEdgeList();
        if (foundPath == null || foundPath.size() == 0) {
            return null;
        }

        LatLngGraphVertex testVertexFrom = foundPath.get(0).getV1();
        LatLngGraphVertex testVertexTo = foundPath.get(0).getV2();
        pointsList.add(testVertexFrom.equals(vTemp1) ? testVertexFrom : testVertexTo);
        for (LatLngGraphEdge aFoundPath : foundPath) {
            testVertexFrom = aFoundPath.getV1();
            testVertexTo = aFoundPath.getV2();
            pointsList.add(pointsList.get(pointsList.size() - 1).equals(testVertexFrom) ? testVertexTo : testVertexFrom);
        }
        return pointsList;
    }

    /**
     * Shortest path with only default edges.
     *
     * @param v1 - start LatLngFlr
     * @param v2 - end LatLngFlr
     * @return sequential list of LatLngFlr objects
     */
    public ArrayList<LatLngGraphVertex> defaultShortestPath(LatLngFlr v1, LatLngFlr v2) {
        Set<LatLngGraphEdge> oldEdges = graph.edgeSet();
        Set<LatLngGraphEdge> defaultEdges = new HashSet<>();
        for (LatLngGraphEdge edge : oldEdges) {
            if (edge.getGraphEdgeType() == GraphElementType.DEFAULT) {
                defaultEdges.add(edge);
            }
        }
        UndirectedWeightedSubgraph<LatLngGraphVertex, LatLngGraphEdge> defaultEdgesGraph =
                new UndirectedWeightedSubgraph<>(graph, null, defaultEdges);
        return shortestPathForGraph(v1, v2, defaultEdgesGraph);
    }

    /**
     * Imports graph from the database. Doesn't return anything but if import was
     * successful internal graph object will be replaced by the imported one.
     */
    public void importGraphFromDB(String dbUrl, String dbUsername, String dbPassword) throws SQLException {
        Application a = new Application();
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(dbUrl, dbUsername, dbPassword);
        a.setupDatabase(connectionSource, false);

        graph = new SimpleWeightedGraph<>(LatLngGraphEdge.class);
        HashMap<Integer, LatLngFlr> verticesMap = new HashMap<>();
        HashMap<Integer, String> coordinateTypesMap = new HashMap<>();
        HashMap<Integer, String> edgeTypesMap = new HashMap<>();
        GraphElementType vertexType;
        GraphElementType edgeType;

        List<Edge> edges = a.edgeDao.queryForAll();
        List<Coordinate> coordinates = a.coordinateDao.queryForAll();
        List<CoordinateType> coordinateTypes = a.coordinateTypeDao.queryForAll();
        List<EdgeType> edgeTypes = a.edgeTypeDao.queryForAll();
        int currentCoordinateId;
        LatLngFlr currentCoordinate;
        int source_id, target_id;

        for (CoordinateType coordinateType : coordinateTypes) {
            coordinateTypesMap.put(coordinateType.getId(), coordinateType.getName());
        }

        for (EdgeType edgeType1 : edgeTypes) {
            edgeTypesMap.put(edgeType1.getId(), edgeType1.getName());
        }

        for (Coordinate coordinate : coordinates) {
            currentCoordinateId = coordinate.getId();
            String coordinate_type = coordinateTypesMap.get(coordinate.getType_id());
            vertexType = determineVertexType(coordinate_type);
            currentCoordinate = new LatLngFlr(coordinate.getLatitude(), coordinate.getLongitude(), coordinate.getFloor());
            addVertexWithId(currentCoordinate, currentCoordinateId, vertexType);
            verticesMap.put(currentCoordinateId, currentCoordinate);
        }

        for (Edge edge : edges) {
            String edge_type = edgeTypesMap.get(edge.getType_id());
            edgeType = determineEdgeType(edge_type);
            source_id = edge.getSource_id();
            target_id = edge.getTarget_id();
            addEdge(verticesMap.get(source_id), verticesMap.get(target_id), source_id, target_id, edgeType);
        }

        connectionSource.close();
    }

    public GraphElementType determineVertexType(String typeStr) {
        switch (typeStr) {
            case "STAIRS":
                return GraphElementType.STAIRS;
            case "ELEVATOR":
                return GraphElementType.ELEVATOR;
            case "ROOM":
                return GraphElementType.ROOM;
            case "FOOD":
                return GraphElementType.FOOD;
            case "WC":
                return GraphElementType.WC;
            case "CLINIC":
                return GraphElementType.CLINIC;
            case "READING":
                return GraphElementType.READING;
            case "DOOR":
                return GraphElementType.DOOR;
            case "LIBRARY":
                return GraphElementType.LIBRARY;
            case "EASTER_EGG":
                return GraphElementType.EASTER_EGG;
            case "DEFAULT":
                return GraphElementType.DEFAULT;
            default:
                return GraphElementType.DEFAULT;
        }
    }

    protected GraphElementType determineEdgeType(String typeStr) {
        switch (typeStr) {
            case "STAIRS":
                return GraphElementType.STAIRS;
            case "ELEVATOR":
                return GraphElementType.ELEVATOR;
            case "DEFAULT":
                return GraphElementType.DEFAULT;
            default:
                return GraphElementType.DEFAULT;
        }
    }

    public boolean graphIsConnected() {
        ConnectivityInspector<LatLngGraphVertex, LatLngGraphEdge> connectivityInspector = new ConnectivityInspector<LatLngGraphVertex, LatLngGraphEdge>(graph);
        return connectivityInspector.isGraphConnected();
    }

    /**
     * Returns all graph vertices as array (warning - this method complexity is O(n))
     *
     * @return array of graph vertices
     */
    public LatLngGraphVertex[] getVertices() {
        LatLngGraphVertex[] v = new LatLngGraphVertex[graph.vertexSet().size()];
        v = graph.vertexSet().toArray(v);
        return v;
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6372.8 * c;
    }

    public ClosestCoordinateWithDistance findClosestCoordinateToGiven(LatLngFlr v) {
        LatLngGraphVertex[] verticesList = new LatLngGraphVertex[graph.vertexSet().size()];
        verticesList = graph.vertexSet().toArray(verticesList);
        LatLngFlr closestCoordinate = null;
        double shortestDistance = Double.MAX_VALUE;
        for (LatLngGraphVertex aVerticesList : verticesList) {
            LatLngFlr candidateCoordinate = aVerticesList.getVertex();
            double candidateDistance = haversine(v.getLatitude(), v.getLongitude(), candidateCoordinate.getLatitude(), candidateCoordinate.getLongitude());
            if (candidateDistance < shortestDistance && v.getFloor() == candidateCoordinate.getFloor()) {
                closestCoordinate = new LatLngFlr(candidateCoordinate.getLatitude(), candidateCoordinate.getLongitude(), candidateCoordinate.getFloor());
                shortestDistance = candidateDistance;
            }
        }

        return new ClosestCoordinateWithDistance(closestCoordinate, shortestDistance);
    }

    private double calculateDistance(LatLngFlr v1, LatLngFlr v2) {
        return Math.sqrt(Math.pow(v1.getLatitude() - v2.getLatitude(), 2) + Math.pow(v1.getLongitude() - v2.getLongitude(), 2));
    }

    public boolean graphContainsVertexWithCoordinates(LatLngFlr c) {
        LatLngGraphVertex vTemp = new LatLngGraphVertex(c, 0, GraphElementType.DEFAULT);
        return graph.containsVertex(vTemp);
    }
}
package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Coordinate;
import db.Edge;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pathfinding.JGraphTWrapper;
import pathfinding.LatLng;
import pathfinding.LatLngFlr;
import pathfinding.LatLngGraphVertex;
import rest.clientServerCommunicationClasses.EdgesObject;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by alnedorezov on 6/21/16.
 */

@RestController
public class EdgesController {
    Application a = new Application();

    @RequestMapping("/resources/edges")
    public EdgesObject edges() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        EdgesObject edges1 = new EdgesObject(a.edgeDao.queryForAll());
        connectionSource.close();
        return edges1;
    }

    @RequestMapping("/resources/edge")
    public Edge edge(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        Edge edge1 = a.edgeDao.queryForId(id);
        connectionSource.close();
        return edge1;
    }


    @RequestMapping(value = "/resources/edge", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id, @RequestParam(value = "typeid", defaultValue = "-2") int type_id,
                       @RequestParam(value = "sourceid", defaultValue = "-3") int source_id,
                       @RequestParam(value = "targetid", defaultValue = "-4") int target_id,
                       @RequestParam(value = "checkconnectivity", defaultValue = "true") String checkConnectivityStr) throws SQLException {
        boolean checkConnectivity = true;
        if (checkConnectivityStr.equals("false"))
            checkConnectivity = false;

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        if (type_id == -2 && source_id == -3 && target_id == -4) {
            // Deleting an edge
            System.out.println("Received POST request: delete edge with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.edgeDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such edge.\n";
            } else {
                String errorMessage = "";
                if(checkConnectivity)
                    errorMessage = checkIfEdgeCanBeDeleted(id);
                if (errorMessage.equals("")) {
                    a.edgeDao.deleteById(id);
                    connectionSource.close();
                    return "0. Edge with id=" + id + " was successfully deleted.\n";
                } else {
                    connectionSource.close();
                    return "-1. " + errorMessage;
                }
            }
        } else {
            if (id == -1) {
                // Creating an edge
                if (source_id == -3 || target_id == -4 || source_id < 0 || target_id < 0) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                } else {
                    System.out.println("Received POST request: create new edge");
                    type_id = checkTypeId(type_id);
                    String errorMessageOnCreate = checkIfEdgeCanBeCreated(source_id, target_id);
                    if (errorMessageOnCreate.equals("")) {
                        a.edgeDao.create(new Edge(id, type_id, source_id, target_id));
                        QueryBuilder<Edge, Integer> qBuilder = a.edgeDao.queryBuilder();
                        qBuilder.orderBy("id", false); // false for descending order
                        qBuilder.limit(1);
                        Edge createdEdge = a.edgeDao.queryForId(qBuilder.query().get(0).getId());
                        System.out.println(createdEdge.getId() + " | " + createdEdge.getType_id() + " | " +
                                createdEdge.getSource_id() + " | " + createdEdge.getTarget_id());
                        connectionSource.close();
                        return "0. Edge with id=" + createdEdge.getId() + " was successfully created.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + errorMessageOnCreate;
                    }
                }
            } else {
                // Updating an edge
                System.out.println("Received POST request: update edge with id=" + id);
                EdgeUpdateData updEdge = checkDataForUpdates(new EdgeUpdateData(type_id, source_id, target_id), a.edgeDao.queryForId(id));
                if (updEdge.getErrorMessage().equals("")) {
                    a.edgeDao.update(new Edge(id, updEdge.getType_id(), updEdge.getSource_id(), updEdge.getTarget_id()));
                    connectionSource.close();
                    return "0. Coordinate with id=" + id + " was successfully updated.\n";
                } else {
                    connectionSource.close();
                    return "-1. " + updEdge.getErrorMessage();
                }
            }
        }
    }

    private int checkTypeId(int type_id) {
        // if type_id was not specified on creation request then DEFAULT type assumed
        if (type_id == -2)
            return 1;
        else
            return type_id;
    }

    private EdgeUpdateData checkDataForUpdates(EdgeUpdateData checkedEdgeData, Edge edgeInDatabase) throws SQLException {
        if (checkedEdgeData.getType_id() == -2)
            checkedEdgeData.setType_id(edgeInDatabase.getType_id());

        if (checkedEdgeData.getSource_id() == -3)
            checkedEdgeData.setSource_id(edgeInDatabase.getSource_id());
        else
            checkedEdgeData.setErrorMessage(checkedEdgeData.getErrorMessage() +
                    checkIfCoordinateExist(checkedEdgeData.getSource_id(), "source one"));

        if (checkedEdgeData.getTarget_id() == -4)
            checkedEdgeData.setTarget_id(edgeInDatabase.getTarget_id());
        else
            checkedEdgeData.setErrorMessage(checkedEdgeData.getErrorMessage() +
                    checkIfCoordinateExist(checkedEdgeData.getTarget_id(), "target one"));

        return checkedEdgeData;
    }

    private class EdgeUpdateData {

        private int type_id;
        private int source_id;
        private int target_id;
        private String errorMessage;

        public EdgeUpdateData(int type_id, int source_id, int target_id) {
            this.type_id = type_id;
            this.source_id = source_id;
            this.target_id = target_id;
            this.errorMessage = "";
        }

        public int getType_id() {
            return type_id;
        }

        public void setType_id(int type_id) {
            this.type_id = type_id;
        }

        public int getSource_id() {
            return source_id;
        }

        public void setSource_id(int source_id) {
            this.source_id = source_id;
        }

        public int getTarget_id() {
            return target_id;
        }

        public void setTarget_id(int target_id) {
            this.target_id = target_id;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private String checkIfEdgeCanBeDeleted(int edge_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        Edge checkedEdge = a.edgeDao.queryForId(edge_id);
        int source_id = checkedEdge.getSource_id();
        int target_id = checkedEdge.getTarget_id();
        String errorMessage = "";

        JGraphTWrapper jGraphTWrapper = new JGraphTWrapper();
        jGraphTWrapper.importGraphFromDB(Application.DATABASE_URL, Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        LatLngGraphVertex sourceVertex, targetVertex;
        Coordinate sourceCoordinate = a.coordinateDao.queryForId(source_id);
        Coordinate targetCoordinate = a.coordinateDao.queryForId(target_id);
        sourceVertex = new LatLngGraphVertex(new LatLng(sourceCoordinate.getLatitude(), sourceCoordinate.getLongitude()),
                        sourceCoordinate.getId(), jGraphTWrapper.determineVertexType(a.coordinateTypeDao.queryForId(sourceCoordinate.getType_id()).getName()));
        targetVertex = new LatLngGraphVertex(new LatLng(targetCoordinate.getLatitude(), targetCoordinate.getLongitude()),
                targetCoordinate.getId(), jGraphTWrapper.determineVertexType(a.coordinateTypeDao.queryForId(targetCoordinate.getType_id()).getName()));
        jGraphTWrapper.removeEdge(sourceVertex, targetVertex);

        //Check that edge can be removed
        // (if the graph stay connected or maybe have isolated vertices, but no isolated subgraphs)

        // graphIsConnected function from JGraphTWrapper is
        // not used as it doesn't uphold isolated vertices
        // and therefore implies constraint that there shouldn't be
        // any isolated vertices.
        // Moreover if any of the edges vertices is an end vertex
        // implemented method will work faster than graphIsConnected()

        boolean edgeCanBeRemoved = false;
        QueryBuilder<Edge, Integer> qbEdge = a.edgeDao.queryBuilder();
        qbEdge.where().eq("source_id", source_id).or().eq("target_id", source_id);
        List<Edge> bufEdgesList = qbEdge.query();
        if(bufEdgesList.size() == 1 && bufEdgesList.get(0).getId() == edge_id)
            edgeCanBeRemoved = true;

        if(!edgeCanBeRemoved) {
            qbEdge.reset();
            qbEdge = a.edgeDao.queryBuilder();
            qbEdge.where().eq("source_id", target_id).or().eq("target_id", target_id);
            bufEdgesList = qbEdge.query();
            if(bufEdgesList.size() == 1 && bufEdgesList.get(0).getId() == edge_id)
                edgeCanBeRemoved = true;
        }

        if(!edgeCanBeRemoved) {
            List<LatLngGraphVertex> path = jGraphTWrapper.shortestPath(new LatLngFlr(sourceCoordinate.getLatitude(), sourceCoordinate.getLongitude(), sourceCoordinate.getFloor()),
                                        new LatLng(targetCoordinate.getLatitude(), targetCoordinate.getLongitude()));
            if(path != null)
                edgeCanBeRemoved = true;
        }

        if(!edgeCanBeRemoved)
            errorMessage += "Deletion of the edge (" + source_id + ", " + target_id + ") will violate graphs connectivity. ";

        connectionSource.close();
        return errorMessage;
    }

    private String checkIfEdgeCanBeCreated(int source_id, int target_id) throws SQLException {
        String errorMessage = "";

        errorMessage += checkIfCoordinateExist(source_id, "source one");
        errorMessage += checkIfCoordinateExist(target_id, "target one");

        QueryBuilder<Edge, Integer> qbEdge = a.edgeDao.queryBuilder();
        qbEdge.where().eq("source_id", target_id).and().eq("target_id", source_id);
        if (qbEdge.query().size() > 0)
            errorMessage += "Edge (" + source_id + ", " + target_id + ") cannot be created, as graph is undirected" +
                    " and the edge with swapped coordinates already exists. It's id=" + qbEdge.query().get(0).getId() + ". ";

        return errorMessage;
    }

    private String checkIfCoordinateExist(int coordinate_id, String coordinateName) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (!a.coordinateDao.idExists(coordinate_id))
            errorMessage += "Coordinate stated as the " + coordinateName + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }
}
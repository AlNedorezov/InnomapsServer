package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Edge;
import db.EdgeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.EdgeTypesObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/21/16.
 */

@RestController
public class EdgeTypesController {
    Application a = new Application();

    @RequestMapping("/resources/edgetypes")
    public EdgeTypesObject edgeTypes() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        EdgeTypesObject edgeTypes1 = new EdgeTypesObject(a.edgeTypeDao.queryForAll());
        connectionSource.close();
        return edgeTypes1;
    }

    @RequestMapping("/resources/edgetype")
    public EdgeType edgeType(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        EdgeType edgeType1 = a.edgeTypeDao.queryForId(id);
        connectionSource.close();
        return edgeType1;
    }

    @RequestMapping(value = "/resources/edgetype", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "name", defaultValue = "!~DELETE") String name) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        if (name.equals("!~DELETE")) {
            // Deleting an edge type
            System.out.println("Received POST request: delete edge type with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.edgeTypeDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such edge type.\n";
            } else {
                QueryBuilder<Edge, Integer> qb = a.edgeDao.queryBuilder();
                qb.where().eq("type_id", id);
                if (qb.query().size() > 0) {
                    connectionSource.close();
                    return "-1. Delete all edges with type " + a.edgeTypeDao.queryForId(id).getName() + " first.\n";
                } else {
                    a.edgeTypeDao.deleteById(id);
                    connectionSource.close();
                    return "0. Edge type with id=" + id + " was successfully deleted.\n";
                }
            }
        } else {
            if (id == -1) {
                // Creating an edge type
                System.out.println("Received POST request: create edge type with name=" + name);
                a.edgeTypeDao.create(new EdgeType(id, name, new Date()));
                QueryBuilder<EdgeType, Integer> qBuilder = a.edgeTypeDao.queryBuilder();
                qBuilder.orderBy("id", false); // false for descending order
                qBuilder.limit(1);
                EdgeType createdEdgeType = a.edgeTypeDao.queryForId(qBuilder.query().get(0).getId());
                System.out.println(createdEdgeType.getId() + " | " + createdEdgeType.getName() + " | " +
                                    createdEdgeType.getModified());
                connectionSource.close();
                return "0. Edge type with id=" + createdEdgeType.getId() + " was successfully created.\n";
            } else {
                // Updating an edge type
                System.out.println("Received POST request: update edge type with id=" + id);
                if (!a.edgeTypeDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such edge type.\n";
                } else {
                    EdgeType edgeType1 = a.edgeTypeDao.queryForId(id);
                    String updName;
                    if (!name.equals("!~DELETE"))
                        updName = name;
                    else
                        updName = edgeType1.getName();
                    a.edgeTypeDao.update(new EdgeType(id, updName, new Date()));
                    connectionSource.close();
                    return "0. Edge type with id=" + id + " was successfully updated.\n";
                }
            }
        }
    }
}
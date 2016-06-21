package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Coordinate;
import db.CoordinateType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.CoordinateTypesObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/21/16.
 */

@RestController
public class CoordinateTypesController {
    Application a = new Application();

    @RequestMapping("/resources/coordinatetypes")
    public CoordinateTypesObject coordinateTypes() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        CoordinateTypesObject coordinateTypes1 = new CoordinateTypesObject(a.coordinateTypeDao.queryForAll());
        connectionSource.close();
        return coordinateTypes1;
    }

    @RequestMapping("/resources/coordinatetype")
    public CoordinateType coordinateType(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        CoordinateType coordinateType1 = a.coordinateTypeDao.queryForId(id);
        connectionSource.close();
        return coordinateType1;
    }

    @RequestMapping(value = "/resources/coordinatetype", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "name", defaultValue = "!~DELETE") String name) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        if (name.equals("!~DELETE")) {
            // Deleting a coordinate type
            System.out.println("Received POST request: delete coordinate type with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.coordinateTypeDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such coordinate type.\n";
            } else {
                QueryBuilder<Coordinate, Integer> qb = a.coordinateDao.queryBuilder();
                qb.where().eq("type_id", id);
                if (qb.query().size() > 0) {
                    connectionSource.close();
                    return "-1. Delete all coordinates with type " + a.coordinateTypeDao.queryForId(id).getName() + " first.\n";
                } else {
                    a.coordinateTypeDao.deleteById(id);
                    connectionSource.close();
                    return "0. Coordinate type with id=" + id + " was successfully deleted.\n";
                }
            }
        } else {
            if (id == -1) {
                // Creating a coordinate type
                System.out.println("Received POST request: create coordinate type with name=" + name);
                a.coordinateTypeDao.create(new CoordinateType(id, name));
                QueryBuilder<CoordinateType, Integer> qBuilder = a.coordinateTypeDao.queryBuilder();
                qBuilder.orderBy("id", false); // false for descending order
                qBuilder.limit(1);
                CoordinateType createdCoordinateType = a.coordinateTypeDao.queryForId(qBuilder.query().get(0).getId());
                System.out.println(createdCoordinateType.getId() + " | " + createdCoordinateType.getName());
                connectionSource.close();
                return "0. Coordinate type with id=" + createdCoordinateType.getId() + " was successfully created.\n";
            } else {
                // Updating a coordinate type
                System.out.println("Received POST request: update coordinate type with id=" + id);
                if (!a.coordinateTypeDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such coordinate type.\n";
                } else {
                    CoordinateType coordinateType1 = a.coordinateTypeDao.queryForId(id);
                    String updName;
                    if (!name.equals("!~DELETE"))
                        updName = name;
                    else
                        updName = coordinateType1.getName();
                    a.coordinateTypeDao.update(new CoordinateType(id, updName));
                    connectionSource.close();
                    return "0. Coordinate type with id=" + id + " was successfully updated.\n";
                }
            }
        }
    }
}
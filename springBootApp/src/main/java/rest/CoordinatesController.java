package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import db.Coordinate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.CoordinatesObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/21/16.
 */

@RestController
public class CoordinatesController {
    Application a = new Application();

    @RequestMapping("/resources/coordinates")
    public CoordinatesObject roles() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);
        CoordinatesObject coordinates1 = new CoordinatesObject(a.coordinateDao.queryForAll());
        connectionSource.close();
        return coordinates1;
    }

    @RequestMapping("/resources/coordinate")
    public Coordinate coordinate(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);
        Coordinate coordinate1 = a.coordinateDao.queryForId(id);
        connectionSource.close();
        return coordinate1;
    }

    @RequestMapping(value = "/resources/coordinate", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id, @RequestParam(value = "latitude", defaultValue = "-2") double latitude,
                       @RequestParam(value = "longitude", defaultValue = "-3") double longitude, @RequestParam(value = "floor", defaultValue = "1") int floor,
                       @RequestParam(value = "typeid", defaultValue = "-4") int type_id, @RequestParam(value = "name", defaultValue = "!~NO_NAME") String name,
                       @RequestParam(value = "description", defaultValue = "!~NO_DESCRIPTION") String description) throws SQLException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "h2", "");
        a.setupDatabase(connectionSource, false);

        if (latitude == -2 && longitude == -3 && floor == 1 && type_id == -4 &&
                name.equals("!~NO_NAME") && description.equals("!~NO_DESCRIPTION")) {
            // Deleting a coordinate
            System.out.println("Received POST request: delete coordinate with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.coordinateDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such coordinate.\n";
            } else {
                a.coordinateDao.deleteById(id);
                connectionSource.close();
                return "0. Coordinate with id=" + id + " was successfully deleted.\n";
            }
        }
        else {
            if (id == -1) {
                // Creating a coordinate
            }
            else {
                // Updating a coordinate
            }
        }

        return "УБРАТЬ КОГДА ДОДЕЛАЮ!";
    }
}
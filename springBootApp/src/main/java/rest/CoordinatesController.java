package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
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
                       @RequestParam(value = "longitude", defaultValue = "-3") double longitude, @RequestParam(value = "floor", defaultValue = "-4") int floor,
                       @RequestParam(value = "typeid", defaultValue = "-5") int type_id, @RequestParam(value = "name", defaultValue = "!~NO_NAME") String name,
                       @RequestParam(value = "description", defaultValue = "!~NO_DESCRIPTION") String description) throws SQLException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);

        if (latitude == -2 && longitude == -3 && floor == -4 && type_id == -5 &&
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
                if (latitude == -2 || longitude == -3 || latitude > 90 || latitude < -90 || longitude > 180 || longitude < -180) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                }
                else
                {
                    System.out.println("Received POST request: create new coordinate");
                    CoordinateCreateData createData = checkCoordinateCreateData(new CoordinateCreateData(floor, type_id, name, description));
                    a.coordinateDao.create(new Coordinate(id, latitude, longitude, createData.getFloor(),
                                            createData.getType_id(), createData.getName(), createData.getDescription()));
                    QueryBuilder<Coordinate, Integer> qBuilder = a.coordinateDao.queryBuilder();
                    qBuilder.orderBy("id", false); // false for descending order
                    qBuilder.limit(1);
                    Coordinate createdCoordinate = a.coordinateDao.queryForId(qBuilder.query().get(0).getId());
                    System.out.println(createdCoordinate.getId() + " | " + createdCoordinate.getLatitude()  + " | " +
                                        createdCoordinate.getLongitude() + " | " + createdCoordinate.getFloor()  + " | " +
                                        createdCoordinate.getType_id() + " | " + createdCoordinate.getName() + " | " +
                                        createdCoordinate.getDescription());
                    connectionSource.close();
                    return "0. Coordinate with id=" + createdCoordinate.getId() + " was successfully created.\n";
                }
            }
            else {
                // Updating a coordinate
            }
        }

        return "УБРАТЬ КОГДА ДОДЕЛАЮ!";
    }

    private CoordinateCreateData checkCoordinateCreateData(CoordinateCreateData coordinateCreateData) {
        // if floor was not specified on creation request then 1 floor assumed
        if (coordinateCreateData.getFloor() == -4)
            coordinateCreateData.setFloor(1);

        // if type_id was not specified on creation request then DEFAULT type assumed
        if (coordinateCreateData.getType_id() == -5)
            coordinateCreateData.setType_id(2);

        if (coordinateCreateData.getName().equals("!~NO_NAME"))
            coordinateCreateData.setName("");

        if (coordinateCreateData.getDescription().equals("!~NO_DESCRIPTION"))
            coordinateCreateData.setDescription("");

        return coordinateCreateData;
    }

    private class CoordinateCreateData {
        private int floor;
        private int type_id;
        private String name;
        private String description;

        public CoordinateCreateData(int floor, int type_id, String name, String description) {
            this.floor = floor;
            this.type_id = type_id;
            this.name = name;
            this.description = description;
        }

        public int getFloor() {
            return floor;
        }

        public void setFloor(int floor) {
            this.floor = floor;
        }

        public int getType_id() {
            return type_id;
        }

        public void setType_id(int type_id) {
            this.type_id = type_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
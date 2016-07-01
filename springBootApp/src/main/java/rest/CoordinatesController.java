package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.CoordinatesObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/21/16.
 */

@RestController
public class CoordinatesController {
    Application a = new Application();

    @RequestMapping("/resources/coordinates")
    public CoordinatesObject coordinates() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        CoordinatesObject coordinates1 = new CoordinatesObject(a.coordinateDao.queryForAll());
        connectionSource.close();
        return coordinates1;
    }

    @RequestMapping("/resources/coordinate")
    public Coordinate coordinate(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
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

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        if (CommonFunctions.doubleValuesAreSimilarWithPrecision16(latitude, -2) &&
                CommonFunctions.doubleValuesAreSimilarWithPrecision16(longitude, -3) &&
                floor == -4 && type_id == -5 &&
                name.equals("!~NO_NAME") && description.equals("!~NO_DESCRIPTION")) {
            // Deleting a coordinate
            System.out.println("Received POST request: delete coordinate with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.coordinateDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such coordinate.\n";
            } else {
                String errorMessage = checkIfCoordinateCanBeDeleted(id);
                if (errorMessage.equals("")) {
                    a.coordinateDao.deleteById(id);
                    connectionSource.close();
                    return "0. Coordinate with id=" + id + " was successfully deleted.\n";
                } else {
                    connectionSource.close();
                    return "-1. " + errorMessage;
                }
            }
        } else {
            if (id == -1) {
                // Creating a coordinate
                if (CommonFunctions.doubleValuesAreSimilarWithPrecision16(latitude, -2) ||
                        CommonFunctions.doubleValuesAreSimilarWithPrecision16(longitude, -3) ||
                        CommonFunctions.latiduteAndLongitudeAreInBounds(latitude, longitude)) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                } else {
                    System.out.println("Received POST request: create new coordinate");
                    CoordinateCreateData createData = checkCoordinateCreateData(new CoordinateCreateData(floor, type_id, name, description));
                    if (createData.getErrorMessage().equals("")) {
                        a.coordinateDao.create(new Coordinate(id, latitude, longitude, createData.getFloor(),
                                createData.getType_id(), createData.getName(), createData.getDescription(), new Date()));
                        QueryBuilder<Coordinate, Integer> qBuilder = a.coordinateDao.queryBuilder();
                        qBuilder.orderBy("id", false); // false for descending order
                        qBuilder.limit(1);
                        Coordinate createdCoordinate = a.coordinateDao.queryForId(qBuilder.query().get(0).getId());
                        System.out.println(createdCoordinate.getId() + " | " + createdCoordinate.getLatitude() + " | " +
                                createdCoordinate.getLongitude() + " | " + createdCoordinate.getFloor() + " | " +
                                createdCoordinate.getType_id() + " | " + createdCoordinate.getName() + " | " +
                                createdCoordinate.getDescription() + " | " + createdCoordinate.getModified());
                        connectionSource.close();
                        return "0. Coordinate with id=" + createdCoordinate.getId() + " was successfully created.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + createData.getErrorMessage();
                    }
                }
            } else {
                // Updating a coordinate
                System.out.println("Received POST request: update coordinate with id=" + id);
                if (!a.coordinateDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such coordinate.\n";
                } else {
                    CoordinateUpdateData updCoordinate = checkDataForUpdates(new CoordinateUpdateData(latitude, longitude, floor, type_id, name, description),
                            a.coordinateDao.queryForId(id));
                    if (updCoordinate.getErrorMessage().equals("")) {
                        a.coordinateDao.update(new Coordinate(id, updCoordinate.getLatitude(), updCoordinate.getLongitude(), updCoordinate.getFloor(),
                                updCoordinate.getType_id(), updCoordinate.getName(), updCoordinate.getDescription(), new Date()));
                        connectionSource.close();
                        return "0. Coordinate with id=" + id + " was successfully updated.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + updCoordinate.getErrorMessage();
                    }
                }
            }
        }
    }

    private CoordinateCreateData checkCoordinateCreateData(CoordinateCreateData coordinateCreateData) throws SQLException {
        // if floor was not specified on creation request then 1 floor assumed
        if (coordinateCreateData.getFloor() == -4)
            coordinateCreateData.setFloor(1);

        // if type_id was not specified on creation request then DEFAULT type assumed
        if (coordinateCreateData.getType_id() == -5)
            coordinateCreateData.setType_id(2);
        else
            coordinateCreateData.setErrorMessage(coordinateCreateData.getErrorMessage() +
                    checkIfTypeExist(coordinateCreateData.getType_id()));

        if (coordinateCreateData.getName().equals("!~NO_NAME"))
            coordinateCreateData.setName("");

        if (coordinateCreateData.getDescription().equals("!~NO_DESCRIPTION"))
            coordinateCreateData.setDescription("");

        return coordinateCreateData;
    }

    private CoordinateUpdateData checkDataForUpdates(CoordinateUpdateData checkedCoordinateData, Coordinate coordinateInDatabase) throws SQLException {
        if (checkedCoordinateData.getLatitude() == -2)
            checkedCoordinateData.setLatitude(coordinateInDatabase.getLatitude());

        if (checkedCoordinateData.getLongitude() == -3)
            checkedCoordinateData.setLongitude(coordinateInDatabase.getLongitude());

        if (checkedCoordinateData.getFloor() == -4)
            checkedCoordinateData.setFloor(coordinateInDatabase.getFloor());

        if (checkedCoordinateData.getType_id() == -5)
            checkedCoordinateData.setType_id(coordinateInDatabase.getType_id());
        else
            checkedCoordinateData.setErrorMessage(checkedCoordinateData.getErrorMessage() +
                    checkIfTypeExist(checkedCoordinateData.getType_id()));

        if (checkedCoordinateData.getName().equals("!~NO_NAME"))
            checkedCoordinateData.setName(coordinateInDatabase.getName());

        if (checkedCoordinateData.getDescription().equals("!~NO_DESCRIPTION"))
            checkedCoordinateData.setDescription(coordinateInDatabase.getDescription());

        return checkedCoordinateData;
    }

    private class CoordinateUpdateData extends CoordinateCreateData {

        private double latitude;
        private double longitude;

        public CoordinateUpdateData(double latitude, double longitude, int floor, int type_id, String name, String description) {
            super(floor, type_id, name, description);
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public int getFloor() {
            return super.getFloor();
        }

        public void setFloor(int floor) {
            super.setFloor(floor);
        }

        public int getType_id() {
            return super.getType_id();
        }

        public void setType_id(int type_id) {
            super.setType_id(type_id);
        }

        public String getName() {
            return super.getName();
        }

        public void setName(String name) {
            super.setName(name);
        }

        public String getDescription() {
            return super.getDescription();
        }

        public void setDescription(String description) {
            super.setDescription(description);
        }

        public String getErrorMessage() {
            return super.getErrorMessage();
        }

        public void setErrorMessage(String errorMessage) {
            super.setErrorMessage(errorMessage);
        }
    }

    private class CoordinateCreateData {
        private int floor;
        private int type_id;
        private String name;
        private String description;
        private String errorMessage;

        public CoordinateCreateData(int floor, int type_id, String name, String description) {
            this.floor = floor;
            this.type_id = type_id;
            this.name = name;
            this.description = description;
            this.errorMessage = "";
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

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private String checkIfCoordinateCanBeDeleted(int coordinateId) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        QueryBuilder<Edge, Integer> qbEdge = a.edgeDao.queryBuilder();
        qbEdge.where().eq("source_id", coordinateId).or().eq("target_id", coordinateId);
        if (qbEdge.query().size() > 0)
            errorMessage += "Delete all edges with coordinate " + coordinateId + " first. ";

        QueryBuilder<EventSchedule, Integer> qbEventSchedule = a.eventScheduleDao.queryBuilder();
        qbEventSchedule.where().eq("location_id", coordinateId);
        if (qbEventSchedule.query().size() > 0)
            errorMessage += "Delete all event schedules with location_id=" + coordinateId + " first. ";

        QueryBuilder<Room, Integer> qbRoom = a.roomDao.queryBuilder();
        qbRoom.where().eq("coordinate_id", coordinateId);
        if (qbRoom.query().size() > 0)
            errorMessage += "Delete all rooms with coordinate_id=" + coordinateId + " first. ";

        QueryBuilder<Building, Integer> qbBuilding = a.buildingDao.queryBuilder();
        qbBuilding.where().eq("coordinate_id", coordinateId);
        if (qbBuilding.query().size() > 0)
            errorMessage += "Delete all buildings with coordinate_id=" + coordinateId + " first. ";

        connectionSource.close();
        return errorMessage;
    }

    private String checkIfTypeExist(int type_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (type_id < 0 || !a.coordinateTypeDao.idExists(type_id))
            errorMessage += "Coordinate type with id=" + type_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }
}
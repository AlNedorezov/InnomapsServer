package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Room;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.RoomsObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/23/16.
 */

@RestController
public class RoomsController {
    private Application a = new Application();

    @RequestMapping("/resources/rooms")
    public RoomsObject rooms() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        RoomsObject rooms1 = new RoomsObject(a.roomDao.queryForAll());
        connectionSource.close();
        return rooms1;
    }

    @RequestMapping("/resources/room")
    public Room room(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        Room room1 = a.roomDao.queryForId(id);
        connectionSource.close();
        return room1;
    }

    @RequestMapping(value = "/resources/room", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "number", defaultValue = "-2") String numberStr,
                       @RequestParam(value = "buildingid", defaultValue = "-3") int building_id,
                       @RequestParam(value = "coordinateid", defaultValue = "-4") int coordinate_id,
                       @RequestParam(value = "typeid", defaultValue = "-5") int type_id) throws SQLException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        if (numberStr.equals("-2") && building_id == -3 && coordinate_id == -4 && type_id == -5) {
            // Deleting a room
            System.out.println("Received POST request: delete room with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.roomDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such room.\n";
            } else {
                a.roomDao.deleteById(id);
                connectionSource.close();
                return "0. Room with id=" + id + " was successfully deleted.\n";
            }
        } else {
            if (id == -1) {
                // Creating a room
                if (building_id == -3 || coordinate_id == -4 || type_id == -5) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                } else {
                    System.out.println("Received POST request: create new room");
                    Integer number = checkRoomNumber(numberStr);
                    String errorMessageOnCreate = checkIfRoomCanBeCreated(building_id, coordinate_id, type_id);
                    if (errorMessageOnCreate.equals("")) {
                        a.roomDao.create(new Room(id, number, building_id, coordinate_id, type_id, new Date()));
                        QueryBuilder<Room, Integer> qBuilder = a.roomDao.queryBuilder();
                        qBuilder.orderBy("id", false); // false for descending order
                        qBuilder.limit(1);
                        Room createdRoom = a.roomDao.queryForId(qBuilder.query().get(0).getId());
                        System.out.println(createdRoom.getId() + " | " + createdRoom.getNumber() + " | " +
                                createdRoom.getBuilding_id() + " | " + createdRoom.getCoordinate_id() + " | " +
                                createdRoom.getType_id() + " | " + createdRoom.getModified());
                        connectionSource.close();
                        return "0. Room with id=" + createdRoom.getId() + " was successfully created.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + errorMessageOnCreate;
                    }
                }
            } else {
                // Updating a room
                System.out.println("Received POST request: update room with id=" + id);
                if (!a.roomDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such room.\n";
                } else {
                    RoomUpdateData updRoom = checkDataForUpdates(new RoomUpdateData(numberStr, building_id, coordinate_id, type_id), a.roomDao.queryForId(id));
                    if (updRoom.getErrorMessage().equals("")) {
                        a.roomDao.update(new Room(id, updRoom.getNumber(), updRoom.getBuilding_id(), updRoom.getCoordinate_id(), updRoom.getType_id(), new Date()));
                        connectionSource.close();
                        return "0. Room with id=" + id + " was successfully updated.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + updRoom.getErrorMessage();
                    }
                }
            }
        }
    }

    private Integer checkRoomNumber(String numberStr) {
        // if number was not specified on creation request then it is assumed that room doesn't have a number
        if (numberStr.equals("-2") || numberStr.equals("null"))
            return null;
        else
            return Integer.valueOf(numberStr);
    }

    private RoomUpdateData checkDataForUpdates(RoomUpdateData checkedRoomData, Room roomInDatabase) throws SQLException {
        if (checkedRoomData.getNumberStr().equals("-2"))
            checkedRoomData.setNumber(roomInDatabase.getNumber());

        if (checkedRoomData.getBuilding_id() == -3)
            checkedRoomData.setBuilding_id(roomInDatabase.getBuilding_id());
        else
            checkedRoomData.setErrorMessage(checkedRoomData.getErrorMessage() +
                    CommonFunctions.checkIfBuildingExist(checkedRoomData.getBuilding_id()));

        if (checkedRoomData.getCoordinate_id() == -4)
            checkedRoomData.setCoordinate_id(roomInDatabase.getCoordinate_id());
        else
            checkedRoomData.setErrorMessage(checkedRoomData.getErrorMessage() +
                    CommonFunctions.checkIfCoordinateExist(checkedRoomData.getCoordinate_id()));

        if (checkedRoomData.getType_id() == -5)
            checkedRoomData.setType_id(roomInDatabase.getType_id());
        else
            checkedRoomData.setErrorMessage(checkedRoomData.getErrorMessage() +
                    checkIfTypeExist(checkedRoomData.getType_id()));

        return checkedRoomData;
    }

    private class RoomUpdateData {
        private Integer number;
        private int building_id;
        private int coordinate_id;
        private int type_id;
        private String errorMessage;

        public RoomUpdateData(String numberStr, int building_id, int coordinate_id, int type_id) {
            this.number = checkRoomNumber(numberStr);
            this.building_id = building_id;
            this.coordinate_id = coordinate_id;
            this.type_id = type_id;
            this.errorMessage = "";
        }

        public Integer getNumber() {
            return number;
        }

        public String getNumberStr() {
            return String.valueOf(number);
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public int getBuilding_id() {
            return building_id;
        }

        public void setBuilding_id(int building_id) {
            this.building_id = building_id;
        }

        public int getCoordinate_id() {
            return coordinate_id;
        }

        public void setCoordinate_id(int coordinate_id) {
            this.coordinate_id = coordinate_id;
        }

        public int getType_id() {
            return type_id;
        }

        public void setType_id(int type_id) {
            this.type_id = type_id;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private String checkIfRoomCanBeCreated(int building_id, int coordinate_id, int type_id) throws SQLException {
        String errorMessage = "";

        errorMessage += CommonFunctions.checkIfBuildingExist(building_id);
        errorMessage += CommonFunctions.checkIfCoordinateExist(coordinate_id);
        errorMessage += checkIfTypeExist(type_id);

        return errorMessage;
    }

    private String checkIfTypeExist(int type_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (type_id < 0 || !a.roomTypeDao.idExists(type_id))
            errorMessage += "Room type with id=" + type_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }
}

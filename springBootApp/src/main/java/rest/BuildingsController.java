package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Building;
import db.BuildingPhoto;
import db.Room;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.BuildingsObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/23/16.
 */

@RestController
public class BuildingsController {
    Application a = new Application();

    @RequestMapping("/resources/buildings")
    public BuildingsObject buildings() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        BuildingsObject buildings1 = new BuildingsObject(a.buildingDao.queryForAll());
        connectionSource.close();
        return buildings1;
    }

    @RequestMapping("/resources/building")
    public Building building(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        Building building1 = a.buildingDao.queryForId(id);
        connectionSource.close();
        return building1;
    }

    @RequestMapping(value = "/resources/building", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id, @RequestParam(value = "number", defaultValue = "") String number,
                       @RequestParam(value = "block", defaultValue = "-2") String blockStr,
                       @RequestParam(value = "description", defaultValue = "!~NO_DESCRIPTION") String description,
                       @RequestParam(value = "coordinateid", defaultValue = "-3") int coordinate_id,
                       @RequestParam(value = "streetid", defaultValue = "-4") int street_id) throws SQLException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        if (number.equals("") && blockStr.equals("-2") && description.equals("!~NO_DESCRIPTION") &&
                coordinate_id == -3 && street_id == -4) {
            // Deleting a building
            System.out.println("Received POST request: delete building with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.buildingDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such building.\n";
            } else {
                String errorMessage = checkIfBuildingCanBeDeleted(id);
                if (errorMessage.equals("")) {
                    DeleteBuilder<BuildingPhoto, Integer> db = a.buildingPhotoDao.deleteBuilder();
                    db.where().eq("building_id", id);
                    PreparedDelete<BuildingPhoto> preparedDelete = db.prepare();
                    a.buildingPhotoDao.delete(preparedDelete);
                    a.buildingDao.deleteById(id);
                    connectionSource.close();
                    return "0. Building with id=" + id + " was successfully deleted.\n";
                } else {
                    connectionSource.close();
                    return "-1. " + errorMessage;
                }
            }
        } else {
            if (id == -1) {
                // Creating a building
                if (number.equals("") || coordinate_id == -3 || street_id == -4) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                } else {
                    System.out.println("Received POST request: create new building");
                    Integer block = checkBuildingBlock(blockStr);
                    description = checkBuildingDescription(description);
                    String errorMessageOnCreate = checkIfBuildingCanBeCreated(coordinate_id, street_id);
                    if (errorMessageOnCreate.equals("")) {
                        a.buildingDao.create(new Building(id, number, block, description, coordinate_id, street_id, new Date()));
                        QueryBuilder<Building, Integer> qBuilder = a.buildingDao.queryBuilder();
                        qBuilder.orderBy("id", false); // false for descending order
                        qBuilder.limit(1);
                        Building createdBuilding = a.buildingDao.queryForId(qBuilder.query().get(0).getId());
                        System.out.println(createdBuilding.getId() + " | " + createdBuilding.getNumber() + " | " +
                                createdBuilding.getBlock() + " | " + createdBuilding.getDescription() + " | " +
                                createdBuilding.getCoordinate_id() + " | " + createdBuilding.getStreet_id() + " | " +
                                createdBuilding.getModified());
                        connectionSource.close();
                        return "0. Building with id=" + createdBuilding.getId() + " was successfully created.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + errorMessageOnCreate;
                    }
                }
            } else {
                // Updating a building
                System.out.println("Received POST request: update building with id=" + id);
                if (!a.buildingDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such building.\n";
                } else {
                    BuildingUpdateData updBuilding = checkDataForUpdates(new BuildingUpdateData(number, blockStr, description, coordinate_id, street_id), a.buildingDao.queryForId(id));
                    if (updBuilding.getErrorMessage().equals("")) {
                        a.buildingDao.update(new Building(id, updBuilding.getNumber(), updBuilding.getBlock(), updBuilding.getDescription(),
                                updBuilding.getCoordinate_id(), updBuilding.getStreet_id(), new Date()));
                        connectionSource.close();
                        return "0. Building with id=" + id + " was successfully updated.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + updBuilding.getErrorMessage();
                    }
                }
            }
        }
    }

    private Integer checkBuildingBlock(String blockStr) {
        // if block was not specified on creation request then it is assumed that building doesn't have block number
        if (blockStr.equals("-2") || blockStr.equals("null"))
            return null;
        else
            return Integer.valueOf(blockStr);
    }

    private String checkBuildingDescription(String description) {
        if (description.equals("!~NO_DESCRIPTION"))
            description = "";

        return description;
    }

    private BuildingUpdateData checkDataForUpdates(BuildingUpdateData checkedBuildingData, Building buildingInDatabase) throws SQLException {
        if (checkedBuildingData.getNumber().equals(""))
            checkedBuildingData.setNumber(buildingInDatabase.getNumber());

        if (checkedBuildingData.getBlockStr().equals("-2"))
            checkedBuildingData.setBlock(buildingInDatabase.getBlock());

        if (checkedBuildingData.getDescription().equals("!~NO_DESCRIPTION"))
            checkedBuildingData.setDescription(buildingInDatabase.getDescription());

        if (checkedBuildingData.getCoordinate_id() == -3)
            checkedBuildingData.setCoordinate_id(buildingInDatabase.getCoordinate_id());
        else
            checkedBuildingData.setErrorMessage(checkedBuildingData.getErrorMessage() +
                    CommonFunctions.checkIfCoordinateExist(checkedBuildingData.getCoordinate_id()));

        if (checkedBuildingData.getStreet_id() == -4)
            checkedBuildingData.setStreet_id(buildingInDatabase.getStreet_id());
        else
            checkedBuildingData.setErrorMessage(checkedBuildingData.getErrorMessage() +
                    checkIfStreetExist(checkedBuildingData.getStreet_id()));

        return checkedBuildingData;
    }

    private class BuildingUpdateData {
        private String number;
        private Integer block;
        private String description;
        private int coordinate_id;
        private int street_id;
        private String errorMessage;

        public BuildingUpdateData(String number, String blockStr, String description, int coordinate_id, int street_id) {
            this.number = number;
            this.block = checkBuildingBlock(blockStr);
            this.description = description;
            this.coordinate_id = coordinate_id;
            this.street_id = street_id;
            this.errorMessage = "";
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public Integer getBlock() {
            return block;
        }

        public String getBlockStr() {
            return String.valueOf(block);
        }

        public void setBlock(Integer block) {
            this.block = block;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCoordinate_id() {
            return coordinate_id;
        }

        public void setCoordinate_id(int coordinate_id) {
            this.coordinate_id = coordinate_id;
        }

        public int getStreet_id() {
            return street_id;
        }

        public void setStreet_id(int street_id) {
            this.street_id = street_id;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private String checkIfBuildingCanBeDeleted(int buildingId) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        QueryBuilder<Room, Integer> qbRoom = a.roomDao.queryBuilder();
        qbRoom.where().eq("building_id", buildingId);
        if (qbRoom.query().size() > 0)
            errorMessage += "Delete all rooms within building with id=" + buildingId + " first. ";

        connectionSource.close();
        return errorMessage;
    }

    private String checkIfBuildingCanBeCreated(int coordinate_id, int street_id) throws SQLException {
        String errorMessage = "";

        errorMessage += CommonFunctions.checkIfCoordinateExist(coordinate_id);
        errorMessage += checkIfStreetExist(street_id);

        return errorMessage;
    }

    private String checkIfStreetExist(int street_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (street_id < 0 || !a.streetDao.idExists(street_id))
            errorMessage += "Street with id=" + street_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }
}

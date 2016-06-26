package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.BuildingFloorOverlay;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.BuildingFloorOverlaysObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/27/16.
 */

@RestController
public class BuildingFloorOverlaysController {
    Application a = new Application();

    @RequestMapping("/resources/buildingflooroverlays")
    public BuildingFloorOverlaysObject buildings() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        BuildingFloorOverlaysObject buildings1 = new BuildingFloorOverlaysObject(a.buildingFloorOverlayDao.queryForAll());
        connectionSource.close();
        return buildings1;
    }

    @RequestMapping("/resources/buildingflooroverlay")
    public BuildingFloorOverlay building(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        BuildingFloorOverlay buildingFloorOverlay1 = a.buildingFloorOverlayDao.queryForId(id);
        connectionSource.close();
        return buildingFloorOverlay1;
    }

    @RequestMapping(value = "/resources/buildingflooroverlay", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "buildingid", defaultValue = "-2") int building_id,
                       @RequestParam(value = "photoid", defaultValue = "-3") int photo_id,
                       @RequestParam(value = "floor", defaultValue = "-4") int floor) throws SQLException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        if (building_id == -2 && photo_id == -3 && floor == -4) {
            // Deleting a building floor overlay
            System.out.println("Received POST request: delete building floor overlay with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.buildingFloorOverlayDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such building floor overlay.\n";
            } else {
                a.buildingFloorOverlayDao.deleteById(id);
                connectionSource.close();
                return "0. Building floor overlay with id=" + id + " was successfully deleted.\n";
            }
        } else {
            if (id == -1) {
                // Creating a building floor overlay
                if (building_id == -2 || photo_id == -3 || floor == -4) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                } else {
                    System.out.println("Received POST request: create new building floor overlay");
                    String errorMessageOnCreate = checkIfBuildingFloorOverlayCanBeCreated(building_id, photo_id, floor);
                    if (errorMessageOnCreate.equals("")) {
                        a.buildingFloorOverlayDao.create(new BuildingFloorOverlay(id, building_id, photo_id, floor, new Date()));
                        QueryBuilder<BuildingFloorOverlay, Integer> qBuilder = a.buildingFloorOverlayDao.queryBuilder();
                        qBuilder.orderBy("id", false); // false for descending order
                        qBuilder.limit(1);
                        BuildingFloorOverlay createdBuildingFloorOverlay = a.buildingFloorOverlayDao.queryForId(qBuilder.query().get(0).getId());
                        System.out.println(createdBuildingFloorOverlay.getId() + " | " + createdBuildingFloorOverlay.getBuilding_id() + " | " +
                                createdBuildingFloorOverlay.getPhoto_id() + " | " + createdBuildingFloorOverlay.getFloor() + " | " +
                                createdBuildingFloorOverlay.getModified());
                        connectionSource.close();
                        return "0. Building floor overlay with id=" + createdBuildingFloorOverlay.getId() + " was successfully created.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + errorMessageOnCreate;
                    }
                }
            } else {
                // Updating a building
                System.out.println("Received POST request: update building floor overlay with id=" + id);
                if (!a.buildingFloorOverlayDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such building floor overlay.\n";
                } else {
                    BuildingFloorOverlayUpdateData updBFO =
                            checkDataForUpdates(new BuildingFloorOverlayUpdateData(building_id, photo_id, floor), a.buildingFloorOverlayDao.queryForId(id));
                    if (updBFO.getErrorMessage().equals("")) {
                        a.buildingFloorOverlayDao.update(new BuildingFloorOverlay(id, updBFO.getBuilding_id(), updBFO.getPhoto_id(), updBFO.getFloor(), new Date()));
                        connectionSource.close();
                        return "0. Building floor overlay with id=" + id + " was successfully updated.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + updBFO.getErrorMessage();
                    }
                }
            }
        }
    }

    private BuildingFloorOverlayUpdateData checkDataForUpdates(BuildingFloorOverlayUpdateData checkedBFOData, BuildingFloorOverlay BFOInDatabase) throws SQLException {
        if (checkedBFOData.getBuilding_id() == -2)
            checkedBFOData.setBuilding_id(BFOInDatabase.getBuilding_id());
        else
            checkedBFOData.setErrorMessage(checkedBFOData.getErrorMessage() +
                    CommonFunctions.checkIfBuildingExist(checkedBFOData.getBuilding_id()));

        boolean checkFloor = false;
        if(checkedBFOData.getErrorMessage().equals(""))
            checkFloor = true;

        if (checkedBFOData.getPhoto_id() == -3)
            checkedBFOData.setPhoto_id(BFOInDatabase.getPhoto_id());
        else
            checkedBFOData.setErrorMessage(checkedBFOData.getErrorMessage() +
                    CommonFunctions.checkIfPhotoExist(checkedBFOData.getPhoto_id()));

        if (checkedBFOData.getFloor() == -4)
            checkedBFOData.setFloor(BFOInDatabase.getFloor());
        else if(checkFloor)
            checkedBFOData.setErrorMessage(checkedBFOData.getErrorMessage() +
                    checkIfBuildingFloorOverlayExist(checkedBFOData.getBuilding_id(), checkedBFOData.getFloor()));


        return checkedBFOData;
    }

    private class BuildingFloorOverlayUpdateData {
        private int building_id;
        private int photo_id;
        private int floor;
        private String errorMessage;

        public BuildingFloorOverlayUpdateData(int building_id, int photo_id, int floor) {
            this.building_id = building_id;
            this.photo_id = photo_id;
            this.floor = floor;
            this.errorMessage = "";
        }

        public int getBuilding_id() {
            return building_id;
        }

        public void setBuilding_id(int building_id) {
            this.building_id = building_id;
        }

        public int getPhoto_id() {
            return photo_id;
        }

        public void setPhoto_id(int photo_id) {
            this.photo_id = photo_id;
        }

        public int getFloor() {
            return floor;
        }

        public void setFloor(int floor) {
            this.floor = floor;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private String checkIfBuildingFloorOverlayCanBeCreated(int building_id, int photo_id, int floor) throws SQLException {
        String errorMessage = "";

        errorMessage += CommonFunctions.checkIfBuildingExist(building_id);
        errorMessage += CommonFunctions.checkIfPhotoExist(photo_id);
        errorMessage += checkIfBuildingFloorOverlayExist(building_id, floor);

        return errorMessage;
    }

    private String checkIfBuildingFloorOverlayExist(int building_id, int floor) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        QueryBuilder<BuildingFloorOverlay, Integer> qbBFO = a.buildingFloorOverlayDao.queryBuilder();
        qbBFO.where().eq("building_id", building_id).and().eq("floor", floor);
        if (qbBFO.query().size() > 0)
            errorMessage += "Building floor overlay for " + floor + " floor of building with id=" + building_id +
                    " already exists. It's id=" + qbBFO.query().get(0).getId() + ". ";

        connectionSource.close();
        return errorMessage;
    }
}

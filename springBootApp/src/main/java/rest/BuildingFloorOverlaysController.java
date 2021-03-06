package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.BuildingFloorOverlay;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.BuildingFloorOverlaysObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/27/16.
 */

@RestController
public class BuildingFloorOverlaysController {
    private Application a = new Application();

    @RequestMapping("/resources/buildingflooroverlays")
    public BuildingFloorOverlaysObject buildings() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        BuildingFloorOverlaysObject buildings1 = new BuildingFloorOverlaysObject(a.buildingFloorOverlayDao.queryForAll());
        connectionSource.close();
        return buildings1;
    }

    @RequestMapping("/resources/buildingflooroverlay")
    public BuildingFloorOverlay building(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        BuildingFloorOverlay buildingFloorOverlay1 = a.buildingFloorOverlayDao.queryForId(id);
        connectionSource.close();
        return buildingFloorOverlay1;
    }

    @RequestMapping(value = "/resources/buildingflooroverlay", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "buildingid", defaultValue = "-2") int building_id,
                       @RequestParam(value = "photoid", defaultValue = "-3") int photo_id,
                       @RequestParam(value = "floor", defaultValue = "-4") int floor,
                       @RequestParam(value = "southwestlatitude", defaultValue = "-5") double southWestLatitude,
                       @RequestParam(value = "southwestlongitude", defaultValue = "-6") double southWestLongitude,
                       @RequestParam(value = "northeastlatitude", defaultValue = "-7") double northEastLatitude,
                       @RequestParam(value = "northeastlongitude", defaultValue = "-8") double northEastLongitude) throws SQLException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        boolean coordinatesAreDefault = CommonFunctions.doubleValuesAreSimilarWithPrecision16(southWestLatitude, -5) &&
                CommonFunctions.doubleValuesAreSimilarWithPrecision16(southWestLongitude, -6) &&
                CommonFunctions.doubleValuesAreSimilarWithPrecision16(northEastLatitude, -7) &&
                CommonFunctions.doubleValuesAreSimilarWithPrecision16(northEastLongitude, -8);
        boolean buildingIdAndPhotoIdAndFloorAreDefault = building_id == -2 && photo_id == -3 && floor == -4;
        if (buildingIdAndPhotoIdAndFloorAreDefault && coordinatesAreDefault) {
            // Deleting a building floor overlay
            return deleteABuildingFloorOverlay(id);
        } else {
            if (id == -1) {
                // Creating a building floor overlay
                return createABuildingFloorOverlay(new BuildingFloorOverlayUpdateData(id, building_id, photo_id,
                        floor, southWestLatitude, southWestLongitude, northEastLatitude, northEastLongitude));
            } else {
                // Updating a building floor overlay
                return updateABuildingFloorOverlay(new BuildingFloorOverlayUpdateData(id, building_id, photo_id,
                        floor, southWestLatitude, southWestLongitude, northEastLatitude, northEastLongitude));
            }
        }
    }

    private String deleteABuildingFloorOverlay(int buildingFloorOverlayId) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        System.out.println("Received POST request: delete building floor overlay with id=" + buildingFloorOverlayId);
        if (buildingFloorOverlayId == -1) {
            connectionSource.close();
            return "-1. Wrong parameters.\n";
        } else if (!a.buildingFloorOverlayDao.idExists(buildingFloorOverlayId)) {
            connectionSource.close();
            return "-1. There is no such building floor overlay.\n";
        } else {
            a.buildingFloorOverlayDao.deleteById(buildingFloorOverlayId);
            connectionSource.close();
            return "0. Building floor overlay with id=" + buildingFloorOverlayId + " was successfully deleted.\n";
        }
    }

    private String createABuildingFloorOverlay(BuildingFloorOverlayUpdateData overlayData) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        boolean someCoordinatesAreDefault = CommonFunctions.doubleValuesAreSimilarWithPrecision16(overlayData.getSouthWestLatitude(), -5) ||
                CommonFunctions.doubleValuesAreSimilarWithPrecision16(overlayData.getSouthWestLongitude(), -6) ||
                CommonFunctions.doubleValuesAreSimilarWithPrecision16(overlayData.getNorthEastLatitude(), -7) ||
                CommonFunctions.doubleValuesAreSimilarWithPrecision16(overlayData.getNorthEastLongitude(), -8);
        boolean buildingIdOrPhotoIdOrFloorAreDefault = overlayData.getBuilding_id() == -2 ||
                overlayData.getPhoto_id() == -3 || overlayData.getFloor() == -4;
        if (buildingIdOrPhotoIdOrFloorAreDefault || someCoordinatesAreDefault) {
            connectionSource.close();
            return "-1. Wrong parameters.\n";
        } else {
            System.out.println("Received POST request: create new building floor overlay");
            String errorMessageOnCreate = checkIfBuildingFloorOverlayCanBeCreated(overlayData.getBuilding_id(),
                    overlayData.getPhoto_id(), overlayData.getFloor(), overlayData.getId());
            if ("".equals(errorMessageOnCreate)) {
                a.buildingFloorOverlayDao.create(new BuildingFloorOverlay(overlayData.getId(), overlayData.getBuilding_id(),
                        overlayData.getPhoto_id(), overlayData.getFloor(), overlayData.getSouthWestLatitude(),
                        overlayData.getSouthWestLongitude(), overlayData.getNorthEastLatitude(), overlayData.getNorthEastLongitude(), new Date()));
                QueryBuilder<BuildingFloorOverlay, Integer> qBuilder = a.buildingFloorOverlayDao.queryBuilder();
                qBuilder.orderBy("id", false); // false for descending order
                qBuilder.limit(1);
                BuildingFloorOverlay createdBuildingFloorOverlay = a.buildingFloorOverlayDao.queryForId(qBuilder.query().get(0).getId());
                printBuildingFloorOverlayData(createdBuildingFloorOverlay);
                connectionSource.close();
                return "0. Building floor overlay with id=" + createdBuildingFloorOverlay.getId() + " was successfully created.\n";
            } else {
                connectionSource.close();
                return "-1. " + errorMessageOnCreate;
            }
        }
    }

    private void printBuildingFloorOverlayData(BuildingFloorOverlay buildingFloorOverlay) {
        System.out.println(buildingFloorOverlay.getId() + " | " + buildingFloorOverlay.getBuilding_id() + " | " +
                buildingFloorOverlay.getPhoto_id() + " | " + buildingFloorOverlay.getFloor() + " | " +
                buildingFloorOverlay.getSouthWestLatitude() + " | " + buildingFloorOverlay.getSouthWestLongitude() + " | " +
                buildingFloorOverlay.getNorthEastLatitude() + " | " + buildingFloorOverlay.getNorthEastLongitude() + " | " +
                buildingFloorOverlay.getModified());
    }

    private String updateABuildingFloorOverlay(BuildingFloorOverlayUpdateData overlayData) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        System.out.println("Received POST request: update building floor overlay with id=" + overlayData.getId());
        if (!a.buildingFloorOverlayDao.idExists(overlayData.getId())) {
            connectionSource.close();
            return "-1. There is no such building floor overlay.\n";
        } else {
            BuildingFloorOverlayUpdateData updatedOverlay =
                    checkDataForUpdates(overlayData, a.buildingFloorOverlayDao.queryForId(overlayData.getId()));
            if ("".equals(updatedOverlay.getErrorMessage())) {
                a.buildingFloorOverlayDao.update(new BuildingFloorOverlay(overlayData.getId(), updatedOverlay.getBuilding_id(), updatedOverlay.getPhoto_id(), updatedOverlay.getFloor(),
                        updatedOverlay.getSouthWestLatitude(), updatedOverlay.getSouthWestLongitude(), updatedOverlay.getNorthEastLatitude(), updatedOverlay.getNorthEastLongitude(), new Date()));
                connectionSource.close();
                return "0. Building floor overlay with id=" + overlayData.getId() + " was successfully updated.\n";
            } else {
                connectionSource.close();
                return "-1. " + updatedOverlay.getErrorMessage();
            }
        }
    }

    private BuildingFloorOverlayUpdateData checkDataForUpdates(BuildingFloorOverlayUpdateData checkedOverlayData, BuildingFloorOverlay overlayInDatabase) throws SQLException {
        if (checkedOverlayData.getBuilding_id() == -2)
            checkedOverlayData.setBuilding_id(overlayInDatabase.getBuilding_id());
        else
            checkedOverlayData.setErrorMessage(checkedOverlayData.getErrorMessage() +
                    CommonFunctions.checkIfBuildingExist(checkedOverlayData.getBuilding_id()));

        boolean checkFloor = false;
        if ("".equals(checkedOverlayData.getErrorMessage()))
            checkFloor = true;

        if (checkedOverlayData.getPhoto_id() == -3)
            checkedOverlayData.setPhoto_id(overlayInDatabase.getPhoto_id());
        else
            checkedOverlayData.setErrorMessage(checkedOverlayData.getErrorMessage() +
                    CommonFunctions.checkIfPhotoExist(checkedOverlayData.getPhoto_id()));

        if (checkedOverlayData.getFloor() == -4)
            checkedOverlayData.setFloor(overlayInDatabase.getFloor());
        else if (checkFloor)
            checkedOverlayData.setErrorMessage(checkedOverlayData.getErrorMessage() +
                    checkIfBuildingFloorOverlayExist(checkedOverlayData.getBuilding_id(), checkedOverlayData.getFloor(), checkedOverlayData.getId()));

        if (CommonFunctions.doubleValuesAreSimilarWithPrecision16(checkedOverlayData.getSouthWestLatitude(), -5))
            checkedOverlayData.setSouthWestLatitude(overlayInDatabase.getSouthWestLatitude());

        if (CommonFunctions.doubleValuesAreSimilarWithPrecision16(checkedOverlayData.getSouthWestLongitude(), -6))
            checkedOverlayData.setSouthWestLongitude(overlayInDatabase.getSouthWestLongitude());

        if (CommonFunctions.doubleValuesAreSimilarWithPrecision16(checkedOverlayData.getNorthEastLatitude(), -7))
            checkedOverlayData.setNorthEastLatitude(overlayInDatabase.getNorthEastLatitude());

        if (CommonFunctions.doubleValuesAreSimilarWithPrecision16(checkedOverlayData.getNorthEastLongitude(), -8))
            checkedOverlayData.setNorthEastLongitude(overlayInDatabase.getNorthEastLongitude());

        return checkedOverlayData;
    }

    private class BuildingFloorOverlayUpdateData {
        private int id;
        private int building_id;
        private int photo_id;
        private int floor;
        private double southWestLatitude;
        private double southWestLongitude;
        private double northEastLatitude;
        private double northEastLongitude;
        private String errorMessage;

        public BuildingFloorOverlayUpdateData(int id, int building_id, int photo_id, int floor, double southWestLatitude, double southWestLongitude, double northEastLatitude, double northEastLongitude) {
            this.id = id;
            this.building_id = building_id;
            this.photo_id = photo_id;
            this.floor = floor;
            this.southWestLatitude = southWestLatitude;
            this.southWestLongitude = southWestLongitude;
            this.northEastLatitude = northEastLatitude;
            this.northEastLongitude = northEastLongitude;
            this.errorMessage = "";
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public double getSouthWestLatitude() {
            return southWestLatitude;
        }

        public void setSouthWestLatitude(double southWestLatitude) {
            this.southWestLatitude = southWestLatitude;
        }

        public double getSouthWestLongitude() {
            return southWestLongitude;
        }

        public void setSouthWestLongitude(double southWestLongitude) {
            this.southWestLongitude = southWestLongitude;
        }

        public double getNorthEastLatitude() {
            return northEastLatitude;
        }

        public void setNorthEastLatitude(double northEastLatitude) {
            this.northEastLatitude = northEastLatitude;
        }

        public double getNorthEastLongitude() {
            return northEastLongitude;
        }

        public void setNorthEastLongitude(double northEastLongitude) {
            this.northEastLongitude = northEastLongitude;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private String checkIfBuildingFloorOverlayCanBeCreated(int building_id, int photo_id, int floor, int BFOid) throws SQLException {
        String errorMessage = "";

        errorMessage += CommonFunctions.checkIfBuildingExist(building_id);
        errorMessage += CommonFunctions.checkIfPhotoExist(photo_id);
        errorMessage += checkIfBuildingFloorOverlayExist(building_id, floor, BFOid);

        return errorMessage;
    }

    private String checkIfBuildingFloorOverlayExist(int building_id, int floor, int BFOid) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        QueryBuilder<BuildingFloorOverlay, Integer> qbBFO = a.buildingFloorOverlayDao.queryBuilder();
        qbBFO.where().eq("building_id", building_id).and().eq("floor", floor);
        if (qbBFO.query().size() > 0 && BFOid != -1 && qbBFO.query().get(0).getId() != BFOid) // it is acceptable to update data of the same overlay
            errorMessage += "Building floor overlay for " + floor + " floor of building with id=" + building_id +
                    " already exists. It's id=" + qbBFO.query().get(0).getId() + ". ";

        connectionSource.close();
        return errorMessage;
    }
}

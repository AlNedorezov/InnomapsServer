package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import db.BuildingPhoto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.BuildingPhotosObject;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by alnedorezov on 6/24/16.
 */

@RestController
public class BuildingPhotosController {
    Application a = new Application();

    @RequestMapping("/resources/buildingphotos")
    public BuildingPhotosObject buildingPhotos() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        BuildingPhotosObject buildingPhotos1 = new BuildingPhotosObject(a.buildingPhotoDao.queryForAll());
        connectionSource.close();
        return buildingPhotos1;
    }

    @RequestMapping("/resources/buildingphoto")
    public BuildingPhotosObject building(@RequestParam(value = "buildingid", defaultValue = "-1") int building_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        QueryBuilder<BuildingPhoto, Integer> qb = a.buildingPhotoDao.queryBuilder();
        qb.where().eq("building_id", building_id);
        BuildingPhotosObject buildingPhotos1 = new BuildingPhotosObject(qb.query());
        connectionSource.close();
        return buildingPhotos1;
    }

    @RequestMapping(value = "/resources/buildingphoto", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "buildingid", defaultValue = "-1") int building_id,
                       @RequestParam(value = "photoid", defaultValue = "-2") int photo_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        QueryBuilder<BuildingPhoto, Integer> qb = a.buildingPhotoDao.queryBuilder();
        qb.where().eq("building_id", building_id).and().eq("photo_id", photo_id);
        PreparedQuery<BuildingPhoto> pc = qb.prepare();
        List<BuildingPhoto> buildingPhotos = a.buildingPhotoDao.query(pc);
        String action;
        if (buildingPhotos.size() > 0)
            action = "delete";
        else
            action = "add";

        if (building_id == -1 || photo_id == -2) {
            connectionSource.close();
            return "-1. Wrong parameters.";
        } else {
            String errorMessageOnAdd = checkIfBuildingPhotoCanBeAdded(building_id, photo_id);
            if (!errorMessageOnAdd.equals("")) {
                connectionSource.close();
                return "-1. " + errorMessageOnAdd;
            } else if (action.equals("add")) {
                System.out.println("Received POST request: photo with id=" + photo_id + " was assigned to building with id=" + building_id);
                a.buildingPhotoDao.create(new BuildingPhoto(building_id, photo_id));
                connectionSource.close();
                return "0. Photo with id=" + photo_id + " was assigned to building with id=" + building_id + "\n";
            } else { // if action = delete
                System.out.println("Received POST request: photo with id=" + photo_id + " was unassigned from building with id=" + building_id);
                DeleteBuilder<BuildingPhoto, Integer> db = a.buildingPhotoDao.deleteBuilder();
                db.where().eq("building_id", building_id).and().eq("photo_id", photo_id);
                PreparedDelete<BuildingPhoto> preparedDelete = db.prepare();
                a.buildingPhotoDao.delete(preparedDelete);
                connectionSource.close();
                return "0. Photo with id=" + photo_id + " was unassigned from building with id=" + building_id + "\n";
            }
        }
    }

    private String checkIfBuildingPhotoCanBeAdded(int building_id, int photo_id) throws SQLException {
        String errorMessage = "";

        errorMessage += CommonFunctions.checkIfBuildingExist(building_id);
        errorMessage += CommonFunctions.checkIfPhotoExist(photo_id);

        return errorMessage;
    }
}
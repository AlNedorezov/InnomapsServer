package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.BuildingPhoto;
import db.Photo;
import db.RoomPhoto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.PhotosObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/23/16.
 */

@RestController
public class PhotosController {
    Application a = new Application();

    @RequestMapping("/resources/photos")
    public PhotosObject photos() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        PhotosObject photos1 = new PhotosObject(a.photoDao.queryForAll());
        connectionSource.close();
        return photos1;
    }

    @RequestMapping("/resources/photo")
    public Photo photo(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        Photo photo1 = a.photoDao.queryForId(id);
        connectionSource.close();
        return photo1;
    }

    @RequestMapping(value = "/resources/photo", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "url", defaultValue = "!~DELETE") String url) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        if (url.equals("!~DELETE")) {
            // Deleting a photo
            System.out.println("Received POST request: delete photo with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.photoDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such photo.\n";
            } else {
                String errorMessage = checkIfPhotoCanBeDeleted(id);
                if (errorMessage.equals("")) {
                    a.photoDao.deleteById(id);
                    connectionSource.close();
                    return "0. Photo with id=" + id + " was successfully deleted.\n";
                } else {
                    connectionSource.close();
                    return "-1. " + errorMessage;
                }
            }
        } else {
            if (id == -1) {
                // Creating a photo
                System.out.println("Received POST request: create photo with url=" + url);
                a.photoDao.create(new Photo(id, url, new Date()));
                QueryBuilder<Photo, Integer> qBuilder = a.photoDao.queryBuilder();
                qBuilder.orderBy("id", false); // false for descending order
                qBuilder.limit(1);
                Photo createdPhoto = a.photoDao.queryForId(qBuilder.query().get(0).getId());
                System.out.println(createdPhoto.getId() + " | " + createdPhoto.getUrl() + " | " +
                        createdPhoto.getModified());
                connectionSource.close();
                return "0. Photo with id=" + createdPhoto.getId() + " was successfully created.\n";
            } else {
                // Updating a photo
                System.out.println("Received POST request: update photo with id=" + id);
                if (!a.photoDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such photo.\n";
                } else {
                    Photo photo1 = a.photoDao.queryForId(id);
                    String updUrl;
                    if (!url.equals("!~DELETE"))
                        updUrl = url;
                    else
                        updUrl = photo1.getUrl();
                    a.photoDao.update(new Photo(id, updUrl, new Date()));
                    connectionSource.close();
                    return "0. Photo with id=" + id + " was successfully updated.\n";
                }
            }
        }
    }

    private String checkIfPhotoCanBeDeleted(int photoId) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        QueryBuilder<BuildingPhoto, Integer> qbBuildingPhoto = a.buildingPhotoDao.queryBuilder();
        qbBuildingPhoto.where().eq("photo_id", photoId);
        if (qbBuildingPhoto.query().size() > 0)
            errorMessage += "Delete all building photos with photo " + photoId + " first. ";

        QueryBuilder<RoomPhoto, Integer> qbRoomPhoto = a.roomPhotoDao.queryBuilder();
        qbRoomPhoto.where().eq("photo_id", photoId);
        if (qbRoomPhoto.query().size() > 0)
            errorMessage += "Delete all room photos with photo " + photoId + " first. ";

        connectionSource.close();
        return errorMessage;
    }
}

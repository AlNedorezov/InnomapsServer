package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import db.RoomPhoto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.RoomPhotosObject;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by alnedorezov on 6/24/16.
 */

@RestController
public class RoomPhotosController {
    Application a = new Application();

    @RequestMapping("/resources/roomphotos")
    public RoomPhotosObject roomPhotos() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        RoomPhotosObject roomPhotos1 = new RoomPhotosObject(a.roomPhotoDao.queryForAll());
        connectionSource.close();
        return roomPhotos1;
    }

    @RequestMapping("/resources/roomphoto")
    public RoomPhotosObject room(@RequestParam(value = "roomid", defaultValue = "-1") int room_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        QueryBuilder<RoomPhoto, Integer> qb = a.roomPhotoDao.queryBuilder();
        qb.where().eq("room_id", room_id);
        RoomPhotosObject roomPhotos1 = new RoomPhotosObject(qb.query());
        connectionSource.close();
        return roomPhotos1;
    }

    @RequestMapping(value = "/resources/roomphoto", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "roomid", defaultValue = "-1") int room_id,
                       @RequestParam(value = "photoid", defaultValue = "-2") int photo_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        QueryBuilder<RoomPhoto, Integer> qb = a.roomPhotoDao.queryBuilder();
        qb.where().eq("room_id", room_id).and().eq("photo_id", photo_id);
        PreparedQuery<RoomPhoto> pc = qb.prepare();
        List<RoomPhoto> roomPhotos = a.roomPhotoDao.query(pc);
        String action;
        if (roomPhotos.size() > 0)
            action = "delete";
        else
            action = "add";

        if (room_id == -1 || photo_id == -2) {
            connectionSource.close();
            return "-1. Wrong parameters.";
        } else {
            String errorMessageOnAdd = checkIfRoomPhotoCanBeAdded(room_id, photo_id);
            if (!errorMessageOnAdd.equals("")) {
                connectionSource.close();
                return "-1. " + errorMessageOnAdd;
            } else if (action.equals("add")) {
                System.out.println("Received POST request: photo with id=" + photo_id + " was assigned to room with id=" + room_id);
                a.roomPhotoDao.create(new RoomPhoto(room_id, photo_id));
                connectionSource.close();
                return "0. Photo with id=" + photo_id + " was assigned to room with id=" + room_id + "\n";
            } else { // if action = delete
                System.out.println("Received POST request: photo with id=" + photo_id + " was unassigned from room with id=" + room_id);
                DeleteBuilder<RoomPhoto, Integer> db = a.roomPhotoDao.deleteBuilder();
                db.where().eq("room_id", room_id).and().eq("photo_id", photo_id);
                PreparedDelete<RoomPhoto> preparedDelete = db.prepare();
                a.roomPhotoDao.delete(preparedDelete);
                connectionSource.close();
                return "0. Photo with id=" + photo_id + " was unassigned from room with id=" + room_id + "\n";
            }
        }
    }

    private String checkIfRoomPhotoCanBeAdded(int room_id, int photo_id) throws SQLException {
        String errorMessage = "";

        errorMessage += checkIfRoomExist(room_id);
        errorMessage += CommonFunctions.checkIfPhotoExist(photo_id);

        return errorMessage;
    }

    private String checkIfRoomExist(int room_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (room_id < 0 || !a.roomDao.idExists(room_id))
            errorMessage += "Room with id=" + room_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }

}

package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Photo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.PhotosObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/23/16.
 */

@RestController
public class PhotosController {
    Application a = new Application();

    @RequestMapping("/resources/photos")
    public PhotosObject photos() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        PhotosObject photos1 = new PhotosObject(a.photoDao.queryForAll());
        connectionSource.close();
        return photos1;
    }

    @RequestMapping("/resources/photo")
    public Photo photo(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        Photo photo1 = a.photoDao.queryForId(id);
        connectionSource.close();
        return photo1;
    }
}

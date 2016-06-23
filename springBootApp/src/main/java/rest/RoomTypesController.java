package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.RoomType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.RoomTypesObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/23/16.
 */

@RestController
public class RoomTypesController {
    Application a = new Application();

    @RequestMapping("/resources/roomtypes")
    public RoomTypesObject roomTypes() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        RoomTypesObject roomTypes1 = new RoomTypesObject(a.roomTypeDao.queryForAll());
        connectionSource.close();
        return roomTypes1;
    }

    @RequestMapping("/resources/roomtype")
    public RoomType roomType(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        RoomType roomType1 = a.roomTypeDao.queryForId(id);
        connectionSource.close();
        return roomType1;
    }
}
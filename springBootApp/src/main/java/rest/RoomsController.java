package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Room;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.RoomsObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/23/16.
 */

@RestController
public class RoomsController {
    Application a = new Application();

    @RequestMapping("/resources/rooms")
    public RoomsObject rooms() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        RoomsObject rooms1 = new RoomsObject(a.roomDao.queryForAll());
        connectionSource.close();
        return rooms1;
    }

    @RequestMapping("/resources/room")
    public Room room(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        Room room1 = a.roomDao.queryForId(id);
        connectionSource.close();
        return room1;
    }
}

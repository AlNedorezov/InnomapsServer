package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Street;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.StreetsObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/23/16.
 */

@RestController
public class StreetsController {
    Application a = new Application();

    @RequestMapping("/resources/streets")
    public StreetsObject streets() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        StreetsObject roomTypes1 = new StreetsObject(a.streetDao.queryForAll());
        connectionSource.close();
        return roomTypes1;
    }

    @RequestMapping("/resources/street")
    public Street street(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        Street street1 = a.streetDao.queryForId(id);
        connectionSource.close();
        return street1;
    }
}
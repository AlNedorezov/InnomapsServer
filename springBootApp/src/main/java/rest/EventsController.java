package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Event;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.EventsObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/24/16.
 */

@RestController
public class EventsController {
    Application a = new Application();

    @RequestMapping("/resources/events")
    public EventsObject events() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        EventsObject events1 = new EventsObject(a.eventDao.queryForAll());
        connectionSource.close();
        return events1;
    }

    @RequestMapping("/resources/event")
    public Event event(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        Event event1 = a.eventDao.queryForId(id);
        connectionSource.close();
        return event1;
    }
}

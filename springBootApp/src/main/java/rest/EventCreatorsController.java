package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import db.EventCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.EventCreatorsObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/24/16.
 */

@RestController
public class EventCreatorsController {
    Application a = new Application();

    @RequestMapping("/resources/eventcreators")
    public EventCreatorsObject eventCreators() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        EventCreatorsObject eventCreators1 = new EventCreatorsObject(a.eventCreatorDao.queryForAll());
        connectionSource.close();
        return eventCreators1;
    }

    @RequestMapping("/resources/eventcreator")
    public EventCreator eventCreator(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        EventCreator eventCreator1 = a.eventCreatorDao.queryForId(id);
        connectionSource.close();
        return eventCreator1;
    }
}

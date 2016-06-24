package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import db.EventSchedule;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.EventSchedulesObject;

import java.sql.SQLException;


/**
 * Created by alnedorezov on 6/24/16.
 */

@RestController
public class EventSchedulesController {
    Application a = new Application();

    @RequestMapping("/resources/eventschedules")
    public EventSchedulesObject eventSchedules() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        EventSchedulesObject eventSchedules1 = new EventSchedulesObject(a.eventScheduleDao.queryForAll());
        connectionSource.close();
        return eventSchedules1;
    }

    @RequestMapping("/resources/eventschedule")
    public EventSchedule eventSchedule(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        EventSchedule eventSchedule1 = a.eventScheduleDao.queryForId(id);
        connectionSource.close();
        return eventSchedule1;
    }
}

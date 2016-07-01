package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import db.EventCreatorAppointment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.EventCreatorAppointmentsObject;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by alnedorezov on 7/1/16.
 */

@RestController
public class EventCreatorAppointmentsController {
    Application a = new Application();

    @RequestMapping("/resources/eventcreatorappointments")
    public EventCreatorAppointmentsObject eventcreatorappointments() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        EventCreatorAppointmentsObject eventCreatorAppointments1 = new EventCreatorAppointmentsObject(a.eventCreatorAppointmentDao.queryForAll());
        connectionSource.close();
        return eventCreatorAppointments1;
    }

    @RequestMapping("/resources/eventcreatorappointment")
    public EventCreatorAppointment eventcreatorappointment(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        EventCreatorAppointment eventCreatorAppointment1 = a.eventCreatorAppointmentDao.queryForId(id);
        connectionSource.close();
        return eventCreatorAppointment1;
    }
}

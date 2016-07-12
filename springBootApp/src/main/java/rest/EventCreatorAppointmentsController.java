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
import rest.clientservercommunicationclasses.EventCreatorAppointmentsObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/1/16.
 */

@RestController
public class EventCreatorAppointmentsController {
    private Application a = new Application();

    @RequestMapping("/resources/eventcreatorappointments")
    public EventCreatorAppointmentsObject eventcreatorappointments(@RequestParam(value = "createdAfterDate", defaultValue = "-1") String date) throws SQLException, ParseException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        EventCreatorAppointmentsObject eventCreatorAppointments1;

        if ("-1".equals(date))
            // if created after date is not specified, return all event creator appointments
            eventCreatorAppointments1 = new EventCreatorAppointmentsObject(a.eventCreatorAppointmentDao.queryForAll());
        else {
            // if created after date is specified, return all event creator apointmenments that were
            // created after on on specified date
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(date);
            QueryBuilder<EventCreatorAppointment, Integer> qb = a.eventCreatorAppointmentDao.queryBuilder();
            qb.where().ge("created", createdDate);
            PreparedQuery<EventCreatorAppointment> pc = qb.prepare();
            eventCreatorAppointments1 = new EventCreatorAppointmentsObject(a.eventCreatorAppointmentDao.query(pc));
        }
        connectionSource.close();
        return eventCreatorAppointments1;
    }

    @RequestMapping("/resources/eventcreatorappointment")
    public EventCreatorAppointmentsObject room(@RequestParam(value = "eventid", defaultValue = "-1") int event_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        QueryBuilder<EventCreatorAppointment, Integer> qb = a.eventCreatorAppointmentDao.queryBuilder();
        qb.where().eq("event_id", event_id);
        EventCreatorAppointmentsObject eventCreatorAppointments1 = new EventCreatorAppointmentsObject(qb.query());
        connectionSource.close();
        return eventCreatorAppointments1;
    }

    @RequestMapping(value = "/resources/eventcreatorappointment", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "eventid", defaultValue = "-1") int event_id,
                       @RequestParam(value = "eventcreatorid", defaultValue = "-2") int event_creator_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        QueryBuilder<EventCreatorAppointment, Integer> qb = a.eventCreatorAppointmentDao.queryBuilder();
        qb.where().eq("event_id", event_id).and().eq("event_creator_id", event_creator_id);
        PreparedQuery<EventCreatorAppointment> pc = qb.prepare();
        List<EventCreatorAppointment> eventCreatorAppointments = a.eventCreatorAppointmentDao.query(pc);
        String action;
        if (eventCreatorAppointments.size() > 0)
            action = "delete";
        else
            action = "add";

        if (event_id == -1 || event_creator_id == -2) {
            connectionSource.close();
            return "-1. Wrong parameters.";
        } else {
            String errorMessageOnAddorDelete = checkIfEventCreatorAppointmentCanBeAdded(event_id, event_creator_id);
            if (!errorMessageOnAddorDelete.equals("")) {
                connectionSource.close();
                return "-1. " + errorMessageOnAddorDelete;
            } else if (action.equals("add")) {
                System.out.println("Received POST request: event creator with id=" + event_creator_id + " was assigned to event with id=" + event_id);
                a.eventCreatorAppointmentDao.create(new EventCreatorAppointment(event_id, event_creator_id, new Date()));
                connectionSource.close();
                return "0. Event creator with id=" + event_creator_id + " was assigned to event with id=" + event_id + "\n";
            } else { // if action = delete
                System.out.println("Received POST request: event creator with id=" + event_creator_id + " was unassigned from event with id=" + event_id);
                DeleteBuilder<EventCreatorAppointment, Integer> db = a.eventCreatorAppointmentDao.deleteBuilder();
                db.where().eq("event_id", event_id).and().eq("event_creator_id", event_creator_id);
                PreparedDelete<EventCreatorAppointment> preparedDelete = db.prepare();
                a.eventCreatorAppointmentDao.delete(preparedDelete);
                connectionSource.close();
                return "0. Event creator with id=" + event_creator_id + " was unassigned from event with id=" + event_id + "\n";
            }
        }
    }

    private String checkIfEventCreatorAppointmentCanBeAdded(int event_id, int event_creator_id) throws SQLException {
        String errorMessage = "";

        errorMessage += CommonFunctions.checkIfEventExist(event_id);
        errorMessage += CommonFunctions.checkIfEventCreatorExist(event_creator_id);

        return errorMessage;
    }
}

package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Event;
import db.EventCreatorAppointment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 6/23/16.
 */
public class CommonFunctions {
    protected static Application a = new Application();

    static String checkIfCoordinateExist(int coordinate_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (coordinate_id < 0 || !a.coordinateDao.idExists(coordinate_id))
            errorMessage += "Coordinate with id=" + coordinate_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }

    static String checkIfBuildingExist(int building_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (building_id < 0 || !a.buildingDao.idExists(building_id))
            errorMessage += "Building with id=" + building_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }

    static String checkIfPhotoExist(int photo_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (photo_id < 0 || !a.photoDao.idExists(photo_id))
            errorMessage += "Photo with id=" + photo_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }

    static String checkIfEventExist(int event_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (event_id < 0 || !a.eventDao.idExists(event_id))
            errorMessage += "Event with id=" + event_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }

    static String checkIfEventCreatorExist(int creator_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (creator_id < 0 || !a.eventCreatorDao.idExists(creator_id))
            errorMessage += "Event creator with id=" + creator_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }

    // At the moment
    // event creators can be assigned to multiple events with the same name, as, for example,
    // creator 1 can organize one of such events on his own and other with co-organizers
    static String checkIfEventCreatorAlreadyHasTheEventWithTheSameName(int event_id, int event_creator_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        QueryBuilder<EventCreatorAppointment, Integer> qbECA = a.eventCreatorAppointmentDao.queryBuilder();
        qbECA.where().eq("event_creator_id", event_creator_id);
        if (qbECA.query().size() > 0) {
            String eventName = a.eventDao.queryForId(event_id).getName();
            List<EventCreatorAppointment> eventCreatorAppointments = qbECA.query();
            List<Integer> creators_event_ids = new ArrayList<>();
            for (int i = 0; i < eventCreatorAppointments.size(); i++)
                creators_event_ids.add(eventCreatorAppointments.get(i).getEvent_id());

            QueryBuilder<Event, Integer> qbEvent = a.eventDao.queryBuilder();
            qbEvent.where().in("id", creators_event_ids);
            List<Event> creators_events = qbEvent.query();

            for (int i = 0; i < creators_events.size(); i++) {
                if (creators_events.get(i).getName().equals(eventName)) {
                    errorMessage += "Event creator with id=" + event_creator_id + " already assigned to event with the name=" + eventName +
                            ". It's id=" + creators_events.get(i).getId() + ". ";
                    break;
                }
            }
        }

        connectionSource.close();
        return errorMessage;
    }
}

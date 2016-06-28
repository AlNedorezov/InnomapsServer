package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.EventSchedule;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.EventSchedulesObject;

import java.sql.SQLException;
import java.util.Date;


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

    @RequestMapping(value = "/resources/eventschedule", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "startdatetime", defaultValue = "-2") String start_datetimeStr,
                       @RequestParam(value = "enddatetime", defaultValue = "-3") String end_datetimeStr,
                       @RequestParam(value = "locationid", defaultValue = "-4") int location_id,
                       @RequestParam(value = "comment", defaultValue = "!~NO_COMMENT") String comment,
                       @RequestParam(value = "eventid", defaultValue = "-5") int event_id) throws SQLException, java.text.ParseException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        if (start_datetimeStr.equals("-2") && end_datetimeStr.equals("-3") && location_id == -4 &&
                comment.equals("!~NO_COMMENT") && event_id == -5) {
            // Deleting an event schedule
            System.out.println("Received POST request: delete event schedule with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.eventScheduleDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such event schedule.\n";
            } else {
                a.eventScheduleDao.deleteById(id);
                connectionSource.close();
                return "0. Event schedule with id=" + id + " was successfully deleted.\n";
            }
        } else {
            if (id == -1) {
                // Creating an event schedule
                if (start_datetimeStr.equals("-2") || end_datetimeStr.equals("-3") || location_id == -4 || event_id == -5) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                } else {
                    System.out.println("Received POST request: create new event schedule");
                    comment = checkEventScheduleComment(comment);
                    String errorMessageOnCreate = checkIfEventScheduleCanBeCreated(location_id, event_id);
                    if (errorMessageOnCreate.equals("")) {
                        a.eventScheduleDao.create(new EventSchedule(id, start_datetimeStr, end_datetimeStr, location_id, comment, event_id, new Date()));
                        QueryBuilder<EventSchedule, Integer> qBuilder = a.eventScheduleDao.queryBuilder();
                        qBuilder.orderBy("id", false); // false for descending order
                        qBuilder.limit(1);
                        EventSchedule createdEventSchedule = a.eventScheduleDao.queryForId(qBuilder.query().get(0).getId());
                        System.out.println(createdEventSchedule.getId() + " | " + createdEventSchedule.getStart_datetime() + " | " +
                                createdEventSchedule.getEnd_datetime() + " | " + createdEventSchedule.getLocation_id() + " | " +
                                createdEventSchedule.getComment() + " | " + createdEventSchedule.getEvent_id() + " | " +
                                createdEventSchedule.getModified());
                        connectionSource.close();
                        return "0. Event schedule with id=" + createdEventSchedule.getId() + " was successfully created.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + errorMessageOnCreate;
                    }
                }
            } else {
                // Updating an event schedule
                System.out.println("Received POST request: update event schedule with id=" + id);
                if (!a.eventScheduleDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such event schedule.\n";
                } else {
                    EventScheduleUpdateData updEventSchedule = checkDataForUpdates(new EventScheduleUpdateData(start_datetimeStr, end_datetimeStr,
                            location_id, comment, event_id), a.eventScheduleDao.queryForId(id));
                    if (updEventSchedule.getErrorMessage().equals("")) {
                        a.eventScheduleDao.update(new EventSchedule(id, updEventSchedule.getStart_datetimeStr(), updEventSchedule.getEnd_datetimeStr(),
                                updEventSchedule.getLocation_id(), updEventSchedule.getComment(), updEventSchedule.getEvent_id(), new Date()));
                        connectionSource.close();
                        return "0. Event schedule with id=" + id + " was successfully updated.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + updEventSchedule.getErrorMessage();
                    }
                }
            }
        }
    }

    private String checkEventScheduleComment(String comment) {
        if (comment.equals("!~NO_COMMENT"))
            comment = "";

        return comment;
    }

    private EventScheduleUpdateData checkDataForUpdates(EventScheduleUpdateData checkedEventScheduleData, EventSchedule eventScheduleInDatabase) throws SQLException {
        if (checkedEventScheduleData.getStart_datetimeStr().equals("-2"))
            checkedEventScheduleData.setStart_datetimeStr(eventScheduleInDatabase.getStart_datetime());

        if (checkedEventScheduleData.getEnd_datetimeStr().equals("-3"))
            checkedEventScheduleData.setEnd_datetimeStr(eventScheduleInDatabase.getEnd_datetime());

        if (checkedEventScheduleData.getLocation_id() == -4)
            checkedEventScheduleData.setLocation_id(eventScheduleInDatabase.getLocation_id());
        else
            checkedEventScheduleData.setErrorMessage(checkedEventScheduleData.getErrorMessage() +
                    CommonFunctions.checkIfCoordinateExist(checkedEventScheduleData.getLocation_id()));

        if (checkedEventScheduleData.getComment().equals("!~NO_COMMENT"))
            checkedEventScheduleData.setComment(eventScheduleInDatabase.getComment());

        if (checkedEventScheduleData.getEvent_id() == -5)
            checkedEventScheduleData.setEvent_id(eventScheduleInDatabase.getEvent_id());
        else
            checkedEventScheduleData.setErrorMessage(checkedEventScheduleData.getErrorMessage() +
                    checkIfEventExist(checkedEventScheduleData.getEvent_id()));

        return checkedEventScheduleData;
    }

    private class EventScheduleUpdateData {
        private String start_datetimeStr = null;
        private String end_datetimeStr = null;
        private Integer location_id;
        private String comment;
        private int event_id;
        private String errorMessage;

        public EventScheduleUpdateData(String start_datetimeStr, String end_datetimeStr, Integer location_id, String comment, int event_id) {
            this.start_datetimeStr = start_datetimeStr;
            this.end_datetimeStr = end_datetimeStr;
            this.location_id = location_id;
            this.comment = comment;
            this.event_id = event_id;
            this.errorMessage = "";
        }

        public String getStart_datetimeStr() {
            return start_datetimeStr;
        }

        public void setStart_datetimeStr(String start_datetimeStr) {
            this.start_datetimeStr = start_datetimeStr;
        }

        public String getEnd_datetimeStr() {
            return end_datetimeStr;
        }

        public void setEnd_datetimeStr(String end_datetimeStr) {
            this.end_datetimeStr = end_datetimeStr;
        }

        public Integer getLocation_id() {
            return location_id;
        }

        public void setLocation_id(Integer location_id) {
            this.location_id = location_id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getEvent_id() {
            return event_id;
        }

        public void setEvent_id(int event_id) {
            this.event_id = event_id;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private String checkIfEventScheduleCanBeCreated(int location_id, int event_id) throws SQLException {
        String errorMessage = "";

        errorMessage += CommonFunctions.checkIfCoordinateExist(location_id);
        errorMessage += checkIfEventExist(event_id);

        return errorMessage;
    }

    private String checkIfEventExist(int event_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (event_id < 0 || !a.eventDao.idExists(event_id))
            errorMessage += "Event with id=" + event_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }
}

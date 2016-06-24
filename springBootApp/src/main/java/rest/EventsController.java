package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Event;
import db.EventSchedule;
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

    @RequestMapping(value = "/resources/event", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "name", defaultValue = "!~NO_NAME") String name,
                       @RequestParam(value = "description", defaultValue = "!~NO_DESCRIPTION") String description,
                       @RequestParam(value = "creatorid", defaultValue = "-2") int creator_id,
                       @RequestParam(value = "link", defaultValue = "!~NO_LINK") String link) throws SQLException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        if (name.equals("!~NO_NAME") && description.equals("!~NO_DESCRIPTION") &&
                creator_id == -2 && link.equals("!~NO_LINK")) {
            // Deleting an event
            System.out.println("Received POST request: delete event with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.eventDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such event.\n";
            } else {
                String errorMessage = checkIfEventCanBeDeleted(id);
                if (errorMessage.equals("")) {
                    a.eventDao.deleteById(id);
                    connectionSource.close();
                    return "0. Event with id=" + id + " was successfully deleted.\n";
                } else {
                    connectionSource.close();
                    return "-1. " + errorMessage;
                }
            }
        } else {
            if (id == -1) {
                // Creating an event
                if (name.equals("!~NO_NAME") || description.equals("!~NO_DESCRIPTION") || creator_id == -2) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                } else {
                    System.out.println("Received POST request: create new event");
                    link = checkEventsLink(link);
                    String errorMessageOnCreate = checkIfEventCreatorExist(creator_id);
                    if (errorMessageOnCreate.equals("")) {
                        a.eventDao.create(new Event(id, name, description, creator_id, link));
                        QueryBuilder<Event, Integer> qBuilder = a.eventDao.queryBuilder();
                        qBuilder.orderBy("id", false); // false for descending order
                        qBuilder.limit(1);
                        Event createdEvent = a.eventDao.queryForId(qBuilder.query().get(0).getId());
                        System.out.println(createdEvent.getId() + " | " + createdEvent.getName() + " | " +
                                createdEvent.getDescription() + " | " + createdEvent.getCreator_id() + " | " +
                                createdEvent.getLink());
                        connectionSource.close();
                        return "0. Event with id=" + createdEvent.getId() + " was successfully created.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + errorMessageOnCreate;
                    }
                }
            } else {
                // Updating an event
                System.out.println("Received POST request: update event with id=" + id);
                if (!a.eventDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such event.\n";
                } else {
                    EventUpdateData updEvent = checkDataForUpdates(new EventUpdateData(name, description, creator_id, link), a.eventDao.queryForId(id));
                    if (updEvent.getErrorMessage().equals("")) {
                        a.eventDao.update(new Event(id, updEvent.getName(), updEvent.getDescription(),
                                                    updEvent.getCreator_id(), updEvent.getLink()));
                        connectionSource.close();
                        return "0. Event with id=" + id + " was successfully updated.\n";
                    } else {
                        connectionSource.close();
                        return "-1. " + updEvent.getErrorMessage();
                    }
                }
            }
        }
    }

    private String checkEventsLink(String link) {
        if (link.equals("!~NO_LINK"))
            link = "";

        return link;
    }

    private EventUpdateData checkDataForUpdates(EventUpdateData checkedEventData, Event eventInDatabase) throws SQLException {
        if (checkedEventData.getName().equals("!~NO_NAME"))
            checkedEventData.setName(eventInDatabase.getName());

        if (checkedEventData.getDescription().equals("!~NO_DESCRIPTION"))
            checkedEventData.setDescription(eventInDatabase.getDescription());

        if (checkedEventData.getCreator_id() == -2)
            checkedEventData.setCreator_id(eventInDatabase.getCreator_id());
        else
            checkedEventData.setErrorMessage(checkedEventData.getErrorMessage() +
                    checkIfEventCreatorExist(checkedEventData.getCreator_id()));

        if (checkedEventData.getLink().equals("!~NO_LINK"))
            checkedEventData.setLink(eventInDatabase.getLink());

        return checkedEventData;
    }

    private class EventUpdateData {
        private String name;
        private String description;
        private int creator_id;
        private String link;
        private String errorMessage;

        public EventUpdateData(String name, String description, int creator_id, String link) {
            this.name = name;
            this.description = description;
            this.creator_id = creator_id;
            this.link = link;
            this.errorMessage = "";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCreator_id() {
            return creator_id;
        }

        public void setCreator_id(int creator_id) {
            this.creator_id = creator_id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    private String checkIfEventCanBeDeleted(int eventId) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        QueryBuilder<EventSchedule, Integer> qbEvent = a.eventScheduleDao.queryBuilder();
        qbEvent.where().eq("event_id", eventId);
        if (qbEvent.query().size() > 0)
            errorMessage += "Delete all schedules of event with id=" + eventId + " first. ";

        connectionSource.close();
        return errorMessage;
    }

    private String checkIfEventCreatorExist(int creator_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (creator_id < 0 || !a.eventCreatorDao.idExists(creator_id))
            errorMessage += "Event creator with id=" + creator_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }
}

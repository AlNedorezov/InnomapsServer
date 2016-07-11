package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Event;
import db.EventCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.EventCreatorsObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/24/16.
 */

@RestController
public class EventCreatorsController {
    Application a = new Application();

    @RequestMapping("/resources/eventcreators")
    public EventCreatorsObject eventCreators() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        EventCreatorsObject eventCreators1 = new EventCreatorsObject(a.eventCreatorDao.queryForAll());
        connectionSource.close();
        return eventCreators1;
    }

    @RequestMapping("/resources/eventcreator")
    public EventCreator eventCreator(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        EventCreator eventCreator1 = a.eventCreatorDao.queryForId(id);
        connectionSource.close();
        return eventCreator1;
    }

    @RequestMapping(value = "/resources/eventcreator", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "name", defaultValue = "!~NO_NAME") String name,
                       @RequestParam(value = "email", defaultValue = "!~NO_EMAIL") String email,
                       @RequestParam(value = "telegramusername", defaultValue = "!~NO_TELEGRAM_USERNAME") String telegram_username) throws SQLException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        if (name.equals("!~NO_NAME") && email.equals("!~NO_EMAIL") && telegram_username.equals("!~NO_TELEGRAM_USERNAME")) {
            // Deleting an event creator
            System.out.println("Received POST request: delete event creator with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.eventCreatorDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such event creator.\n";
            } else {
                String errorMessage = checkIfEventCreatorCanBeDeleted(id);
                if (errorMessage.equals("")) {
                    a.eventCreatorDao.deleteById(id);
                    connectionSource.close();
                    return "0. Event creator with id=" + id + " was successfully deleted.\n";
                } else {
                    connectionSource.close();
                    return "-1. " + errorMessage;
                }
            }
        } else {
            if (id == -1) {
                // Creating an event creator
                if (email.equals("!~NO_EMAIL") || name.equals("!~NO_NAME")) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                } else {
                    System.out.println("Received POST request: create new event creator");
                    telegram_username = checkEventCreatorsTelegramUsername(telegram_username);
                    a.eventCreatorDao.create(new EventCreator(id, name, email, telegram_username, new Date()));
                    QueryBuilder<EventCreator, Integer> qBuilder = a.eventCreatorDao.queryBuilder();
                    qBuilder.orderBy("id", false); // false for descending order
                    qBuilder.limit(1);
                    EventCreator createdEventCreator = a.eventCreatorDao.queryForId(qBuilder.query().get(0).getId());
                    System.out.println(createdEventCreator.getId() + " | " + createdEventCreator.getName() + " | " +
                            createdEventCreator.getEmail() + " | " + createdEventCreator.getTelegram_username() + " | " +
                            createdEventCreator.getModified());
                    connectionSource.close();
                    return "0. Event creator with id=" + createdEventCreator.getId() + " was successfully created.\n";
                }
            } else {
                // Updating an event creator
                System.out.println("Received POST request: update event creator with id=" + id);
                if (!a.eventCreatorDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such event creator.\n";
                } else {
                    EventCreatorUpdateData updEventCreator = checkDataForUpdates(new EventCreatorUpdateData(name, email, telegram_username),
                            a.eventCreatorDao.queryForId(id));
                    a.eventCreatorDao.update(new EventCreator(id, updEventCreator.getName(), updEventCreator.getEmail(),
                            updEventCreator.getTelegram_username(), new Date()));
                    connectionSource.close();
                    return "0. Event creator with id=" + id + " was successfully updated.\n";
                }
            }
        }
    }

    private String checkEventCreatorsTelegramUsername(String telegram_username) {
        if (telegram_username.equals("!~NO_TELEGRAM_USERNAME"))
            telegram_username = "";

        return telegram_username;
    }

    private EventCreatorUpdateData checkDataForUpdates(EventCreatorUpdateData checkedEventCreatorData, EventCreator eventCreatorInDatabase) throws SQLException {
        if (checkedEventCreatorData.getName().equals("!~NO_NAME"))
            checkedEventCreatorData.setName(eventCreatorInDatabase.getName());

        if (checkedEventCreatorData.getEmail().equals("!~NO_EMAIL"))
            checkedEventCreatorData.setEmail(eventCreatorInDatabase.getEmail());

        if (checkedEventCreatorData.getTelegram_username().equals("!~NO_TELEGRAM_USERNAME"))
            checkedEventCreatorData.setTelegram_username(eventCreatorInDatabase.getTelegram_username());

        return checkedEventCreatorData;
    }

    private class EventCreatorUpdateData {
        private String name;
        private String email;
        private String telegram_username;

        public EventCreatorUpdateData(String name, String email, String telegram_username) {
            this.name = name;
            this.email = email;
            this.telegram_username = telegram_username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelegram_username() {
            return telegram_username;
        }

        public void setTelegram_username(String telegram_username) {
            this.telegram_username = telegram_username;
        }
    }

    private String checkIfEventCreatorCanBeDeleted(int creatorId) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        QueryBuilder<Event, Integer> qbEvent = a.eventDao.queryBuilder();
        qbEvent.where().eq("creator_id", creatorId);
        if (qbEvent.query().size() > 0)
            errorMessage += "Delete all events with event creator with id=" + creatorId + " first. ";

        connectionSource.close();
        return errorMessage;
    }
}

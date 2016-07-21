package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Event;
import db.EventCreator;
import db.EventSchedule;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.sync.EventsSync;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/12/16.
 */

@RestController
public class EventsSyncController {
    private Application a = new Application();

    private List<Integer> getIdsFromEventCreators(List<EventCreator> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (EventCreator aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromEvents(List<Event> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (Event aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromEventSchedules(List<EventSchedule> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (EventSchedule aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    @RequestMapping("/resources/sync/events")
    public EventsSync syncEvents(@RequestParam(value = "date", defaultValue = "2015-07-26 15:00:00.0") String date) throws SQLException, ParseException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        System.out.println(new Date() + "Received GET request: return events synchronization data");

        Date modifiedDate;
        String modified = "modified";
        String dateFormat = "yyyy-MM-dd HH:mm:ss.S";

        QueryBuilder<EventCreator, Integer> qbEventCreator = a.eventCreatorDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbEventCreator.where().ge(modified, modifiedDate);
        PreparedQuery<EventCreator> pcEventCreator = qbEventCreator.prepare();
        List<EventCreator> eventCreators = a.eventCreatorDao.query(pcEventCreator);

        QueryBuilder<Event, Integer> qbEvent = a.eventDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbEvent.where().ge(modified, modifiedDate);
        PreparedQuery<Event> pcEvent = qbEvent.prepare();
        List<Event> events = a.eventDao.query(pcEvent);

        QueryBuilder<EventSchedule, Integer> qbEventSchedule = a.eventScheduleDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbEventSchedule.where().ge(modified, modifiedDate);
        PreparedQuery<EventSchedule> pcEventSchedule = qbEventSchedule.prepare();
        List<EventSchedule> eventSchedules = a.eventScheduleDao.query(pcEventSchedule);

        connectionSource.close();
        return new EventsSync(getIdsFromEventCreators(eventCreators), getIdsFromEvents(events), getIdsFromEventSchedules(eventSchedules));
    }
}

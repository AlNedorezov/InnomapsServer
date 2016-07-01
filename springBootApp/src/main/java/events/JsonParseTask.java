package events;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import db.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rest.Application;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by alnedorezov on 28/06/16.
 * Transferred from the mobile applications database package.
 */

public class JsonParseTask {

    private String jsonHash = null;
    private SimpleDateFormat googleTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private SimpleDateFormat serverAPITimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    //shift the given Date by exactly 8 days.
    private void shiftDate(Date d) {
        long MILLISECONDS_PER_8DAY = 1000L * 60 * 60 * 24 * 8;
        long time = d.getTime();
        time -= MILLISECONDS_PER_8DAY;
        d.setTime(time);
    }

    private String doGetRequest(String urlString) {

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }

        HttpURLConnection urlConnection;
        BufferedReader reader;
        String result = "";

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            result = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getGoogleApi() {
        String GOOGLE_MAP_API_FIRST_PART = "https://www.googleapis.com/calendar/v3/calendars/hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com/events?timeMin=";
        String GOOGLE_MAP_API_SECOND_PART = "T10%3A00%3A00-07%3A00&orderby=updated&sortorder=descending&futureevents=true&alt=json&key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o";
        Date currentDate = new Date();
        shiftDate(currentDate);
        return doGetRequest(GOOGLE_MAP_API_FIRST_PART
                + dateFormat.format(currentDate)
                + GOOGLE_MAP_API_SECOND_PART);
    }

    /**
     * Checks whether the JSON file was updated or not
     *
     * @param hashKey - md5 hash
     * @return true in case the JSON was updated
     */
    private boolean jsonUpdated(String hashKey) {
        return (jsonHash == null || jsonHash.equals(hashKey));
    }

    public void updateDbIfNeeded() {
        String strJson = getGoogleApi();
        JSONObject dataJsonObj;
        String md5 = new String(Hex.encodeHex(DigestUtils.md5(strJson)));
        try {
            dataJsonObj = new JSONObject(strJson);
            if (jsonUpdated(md5)) {
                updateDB(dataJsonObj);
                jsonHash = md5;
            }
        } catch (JSONException | SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    private String cutStringUntilTheFirstSpaceIfItExists(String s) {
        if (s.contains(" "))
            s = s.substring(0, s.indexOf(" "));
        return s;
    }

    private String removeSubstringWithNewlineCharacterFromDescription(String description, String substringToRemove, int arrayLength, int substringsOrderInArray) {
        if (substringsOrderInArray == 0 && substringsOrderInArray == arrayLength - 1)
            description = description.replaceFirst(substringToRemove, "");
        else if (substringsOrderInArray > 0 && substringsOrderInArray == arrayLength - 1)
            description = description.replaceFirst("\n" + substringToRemove, "");
        else if ((substringsOrderInArray == 0 && substringsOrderInArray < arrayLength - 1) || (substringsOrderInArray > 0 && substringsOrderInArray < arrayLength - 1))
            description = description.replaceFirst(substringToRemove + "\n", "");

        return description;
    }

    public boolean isStringAnInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<Integer> createEventCreatorsIfNeeded(List<String> telegram_usernames) throws SQLException {
        Application a = new Application();
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        List<Integer> event_creators_ids = new ArrayList<>();

        if (!telegram_usernames.isEmpty()) {

            for(String telegram_username: telegram_usernames) {
                QueryBuilder<EventCreator, Integer> qbEventCreator = a.eventCreatorDao.queryBuilder();
                qbEventCreator.where().eq("telegram_username", telegram_username);
                if (qbEventCreator.query().size() > 0)
                    event_creators_ids.add(qbEventCreator.query().get(0).getId());
                else {
                    a.eventCreatorDao.create(new EventCreator(0, "", null, telegram_username, new Date()));
                    qbEventCreator.reset();
                    qbEventCreator.orderBy("id", false); // false for descending order
                    qbEventCreator.limit(1);
                    EventCreator createdEventCreator = a.eventCreatorDao.queryForId(qbEventCreator.query().get(0).getId());
                    event_creators_ids.add(createdEventCreator.getId());
                }
            }

        }

        connectionSource.close();

        return event_creators_ids;
    }

    private int addEvent(String gcals_event_id, String event_name, String description, String link) throws SQLException {
        Application a = new Application();
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        int event_id;

        QueryBuilder<Event, Integer> qbEvent = a.eventDao.queryBuilder();
        qbEvent.where().eq("gcals_event_id", gcals_event_id);
        if (qbEvent.query().size() > 0) {
            event_id = qbEvent.query().get(0).getId();
            a.eventDao.update(new Event(event_id, event_name, description, link, gcals_event_id, new Date()));
        } else {
            a.eventDao.create(new Event(0, event_name, description, link, gcals_event_id, new Date()));
            qbEvent.reset();
            qbEvent.orderBy("id", false); // false for descending order
            qbEvent.limit(1);
            Event createdEvent = a.eventDao.queryForId(qbEvent.query().get(0).getId());
            event_id = createdEvent.getId();
        }

        connectionSource.close();

        return event_id;
    }

    private void assignCreatorsForTheEvent(int event_id, List<Integer> event_creators_ids) throws SQLException {
        Application a = new Application();
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        // find ids of previously assigned event creators
        QueryBuilder<EventCreatorAppointment, Integer> qbECA = a.eventCreatorAppointmentDao.queryBuilder();
        qbECA.where().eq("event_id", event_id);
        List<EventCreatorAppointment> previous_assignments_for_the_event = qbECA.query();
        List<Integer> previously_assigned_event_creators = new ArrayList<>();
        for(EventCreatorAppointment previous_assignment_for_event: previous_assignments_for_the_event)
            previously_assigned_event_creators.add(previous_assignment_for_event.getEvent_creator_id());

        for(int event_creator_id: event_creators_ids) {
            // if creator is still assigned for the event, remove it from previously_assigned_event_creators list
            if(previously_assigned_event_creators.contains(event_creator_id))
                previously_assigned_event_creators.remove(Integer.valueOf(event_creator_id));

            // assign current creators for the event
            qbECA.reset();
            qbECA = a.eventCreatorAppointmentDao.queryBuilder();
            qbECA.where().eq("event_creator_id", event_creator_id).and().eq("event_id", event_id);
            if (qbECA.query().size() == 0)
                a.eventCreatorAppointmentDao.create(new EventCreatorAppointment(event_id, event_creator_id));
        }

        // unassign creators that were previously assigned for event but no longer assigned for it
        for(int id_of_event_creator_that_is_no_longer_assigned_for_event: previously_assigned_event_creators) {
            DeleteBuilder<EventCreatorAppointment, Integer> db = a.eventCreatorAppointmentDao.deleteBuilder();
            db.where().eq("event_id", event_id).and().eq("event_creator_id", id_of_event_creator_that_is_no_longer_assigned_for_event);
            PreparedDelete<EventCreatorAppointment> preparedDelete = db.prepare();
            a.eventCreatorAppointmentDao.delete(preparedDelete);
        }

        connectionSource.close();
    }

    private int updateDB(JSONObject dataJsonObj) throws JSONException, SQLException, ParseException {
        int eventsInserted = 0;
        JSONArray events = dataJsonObj.getJSONArray("items");

        Application a = new Application();
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        for (int i = 0; i < events.length(); i++) {
            JSONObject jsonEvent = events.getJSONObject(i);
            String event_name = "", link = "", start = "", end = "",
                    location = "", gcals_event_id = "", description = "",
                    recurrence = "";
            List<String> telegram_usernames = new ArrayList<>();
            List<Integer> event_creators_ids;
            int event_id;
            Iterator<String> iter = jsonEvent.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                switch (key) {
                    case "summary":
                        event_name = jsonEvent.getString("summary");
                        break;
                    case "start":
                        start = jsonEvent.getJSONObject("start").getString("dateTime");
                        break;
                    case "end":
                        end = jsonEvent.getJSONObject("end").getString("dateTime");
                        break;
                    case "location":
                        location = jsonEvent.getString("location");
                        break;
                    case "id":
                        gcals_event_id = jsonEvent.getString("id");
                        break;
                    case "description":
                        description = jsonEvent.getString("description");
                        break;
                    case "recurrence":
                        recurrence = jsonEvent.getJSONArray("recurrence").getString(0).replace("RRULE:", "");
                        break;
                }
            }

            String descriptionArray[] = description.split("\n");

            for (int j = 0; j < descriptionArray.length; j++) {
                if (telegram_usernames.isEmpty() && descriptionArray[j].startsWith("Contact: @")) {
                    String telegramUsernamesString = descriptionArray[j].substring("Contact: @".length());
                    String[] tegegramUsernamesList;
                    if(telegramUsernamesString.contains(" @")) {
                        tegegramUsernamesList = telegramUsernamesString.split(" @");
                        for (String s: tegegramUsernamesList) {
                            telegram_usernames.add(cutStringUntilTheFirstSpaceIfItExists(s));
                        }
                    }
                    else
                        telegram_usernames.add(cutStringUntilTheFirstSpaceIfItExists(telegramUsernamesString));
                    description = removeSubstringWithNewlineCharacterFromDescription(description, descriptionArray[j], descriptionArray.length, j);
                }
                if (link.equals("") && descriptionArray[j].startsWith("Group link: ")) {
                    link = descriptionArray[j].substring("Group link: ".length());
                    link = cutStringUntilTheFirstSpaceIfItExists(link);
                    description = removeSubstringWithNewlineCharacterFromDescription(description, descriptionArray[j], descriptionArray.length, j);
                }
            }

            event_creators_ids = createEventCreatorsIfNeeded(telegram_usernames);

            event_id = addEvent(gcals_event_id, event_name, description, link);

            assignCreatorsForTheEvent(event_id, event_creators_ids);

            DateTime startDate;
            DateTime endDate;
            Long durationTime;

            try {
                startDate = new DateTime(googleTimeFormat.parse(start).getTime());
                endDate = new DateTime(googleTimeFormat.parse(end).getTime());
                durationTime = TimeUnit.MILLISECONDS.toMinutes(endDate.getTimestamp() - startDate.getTimestamp());
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            if (recurrence != null && !recurrence.equals("")) {
                DateTime currentDate = new DateTime(new Date().getTime());
                RecurrenceRule rule;
                try {
                    rule = new RecurrenceRule(recurrence);
                } catch (InvalidRecurrenceRuleException e) {
                    e.printStackTrace();
                    continue;
                }

                RecurrenceRuleIterator it = rule.iterator(startDate);
                it.fastForward(currentDate);
                int maxInstances = 3; // limit instances for 3 times

                while (it.hasNext() && (maxInstances-- > 0)) {
                    DateTime nextInstance = it.nextDateTime();
                    if (nextInstance.after(currentDate)) {
                        String finalStartDate = serverAPITimeFormat.format(new Date(nextInstance.getTimestamp()));
                        String finalEndDate = serverAPITimeFormat.format(new Date(nextInstance.addDuration(new Duration(1, 0, 0, durationTime.intValue(), 0)).getTimestamp()));
                        eventsInserted += addEventSchedule(finalStartDate, finalEndDate, location, event_id);
                    }
                }
            } else {
                String finalStartDate = serverAPITimeFormat.format(new Date(startDate.getTimestamp()));
                String finalEndDate = serverAPITimeFormat.format(new Date(endDate.getTimestamp()));
                eventsInserted += addEventSchedule(finalStartDate, finalEndDate, location, event_id);
            }
        }
        connectionSource.close();
        return eventsInserted;
    }

    private int addEventSchedule(String finalStartDate, String finalEndDate, String location, int event_id) throws SQLException, ParseException {
        Application a = new Application();
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);

        int eventsSchedulesInserted = 0;

        Date start_datetime = serverAPITimeFormat.parse(finalStartDate);
        Date end_datetime = serverAPITimeFormat.parse(finalEndDate);
        String locationArray[] = location.split("/");

        if (locationArray[0].equals("university"))
            locationArray[0] = a.coordinateDao.queryForId(1).getName();

        Integer locationId = null;
        int buildingId = Integer.MIN_VALUE;
        QueryBuilder<Coordinate, Integer> qbCoordinate = a.coordinateDao.queryBuilder();
        qbCoordinate.where().eq("name", locationArray[0]);
        if (qbCoordinate.query().size() > 0)
            locationId = qbCoordinate.query().get(0).getId();

        if (locationId != null) {
            QueryBuilder<Building, Integer> qbBuilding = a.buildingDao.queryBuilder();
            qbBuilding.where().eq("coordinate_id", locationId);
            if (qbBuilding.query().size() > 0)
                buildingId = qbBuilding.query().get(0).getId();
            else
                locationId = null;

            if (buildingId != Integer.MIN_VALUE && locationArray.length == 3 && isStringAnInteger(locationArray[2])) {
                QueryBuilder<Room, Integer> qbRoom = a.roomDao.queryBuilder();
                qbRoom.where().eq("building_id", buildingId).and().eq("number", locationArray[2]);
                if (qbRoom.query().size() > 0)
                    locationId = qbRoom.query().get(0).getCoordinate_id();
            }
        }

        QueryBuilder<EventSchedule, Integer> qbEventSchedule = a.eventScheduleDao.queryBuilder();

        int event_schedule_id = Integer.MIN_VALUE;
        if (locationId != null) {
            qbEventSchedule.where().eq("start_datetime", start_datetime).and().eq("end_datetime", end_datetime).and().eq("location_id", locationId);
            if (qbEventSchedule.query().size() > 0) {
                event_schedule_id = qbEventSchedule.query().get(0).getId();
                String eSchComment = a.eventScheduleDao.queryForId(event_schedule_id).getComment();
                a.eventScheduleDao.update(new EventSchedule(event_schedule_id, finalStartDate, finalEndDate, locationId, eSchComment, event_id, new Date()));
            } else {
                a.eventScheduleDao.create(new EventSchedule(event_schedule_id, finalStartDate, finalEndDate, locationId, "", event_id, new Date()));
            }
        } else {
            qbEventSchedule.where().eq("start_datetime", start_datetime).and().eq("end_datetime", end_datetime);
            if (qbEventSchedule.query().size() > 0) {
                List<EventSchedule> concurrentEvents = qbEventSchedule.query();
                for (int k = 0; k < concurrentEvents.size(); k++) {
                    if (concurrentEvents.get(k).getComment().endsWith(location)) {
                        event_schedule_id = concurrentEvents.get(k).getId();
                        break;
                    }
                }
            }
            if (event_schedule_id != Integer.MIN_VALUE) {
                String eSchComment = a.eventScheduleDao.queryForId(event_schedule_id).getComment();
                a.eventScheduleDao.update(new EventSchedule(event_schedule_id, finalStartDate, finalEndDate, locationId, eSchComment, event_id, new Date()));
            } else {
                a.eventScheduleDao.create(new EventSchedule(0, finalStartDate, finalEndDate, null, location, event_id, new Date()));
                ++eventsSchedulesInserted;
            }
        }

        connectionSource.close();

        return eventsSchedulesInserted;
    }
}
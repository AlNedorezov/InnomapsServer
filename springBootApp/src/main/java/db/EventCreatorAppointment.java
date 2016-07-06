package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 7/1/16.
 */
@DatabaseTable(tableName = "Event_creators_appointments")
public class EventCreatorAppointment {
    @DatabaseField(uniqueCombo = true)
    private int event_id;
    @DatabaseField(uniqueCombo = true)
    private int event_creator_id;
    @DatabaseField
    private Date created = null;

    public EventCreatorAppointment(int event_id, int event_creator_id, String createdStr) throws ParseException {
        this.event_id = event_id;
        this.event_creator_id = event_creator_id;
        this.created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(createdStr);
    }

    public EventCreatorAppointment(int event_id, int event_creator_id, Date created) {
        this.event_id = event_id;
        this.event_creator_id = event_creator_id;
        this.created = created;
    }

    public EventCreatorAppointment() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getEvent_id() {
        return event_id;
    }

    public int getEvent_creator_id() {
        return event_creator_id;
    }

    public String getCreated() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(created);
    }
}

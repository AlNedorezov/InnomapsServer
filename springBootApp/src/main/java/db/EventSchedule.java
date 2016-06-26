package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Event_schedules")
public class EventSchedule {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private Date start_datetime = null;
    @DatabaseField(uniqueCombo = true)
    private Date end_datetime = null;
    @DatabaseField(uniqueCombo = true)
    private int location_id;
    @DatabaseField
    private String comment;
    @DatabaseField
    private int event_id;
    @DatabaseField
    private Date modified = null;

    public EventSchedule(int id, String start_datetime_Str, String end_datetime_Str,
                         int location_id, String comment, int event_id, String modifiedStr) throws ParseException {
        this.id = id;
        this.start_datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(start_datetime_Str);
        this.end_datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(end_datetime_Str);
        this.location_id = location_id;
        this.comment = comment;
        this.event_id = event_id;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public EventSchedule(int id, String start_datetime_Str, String end_datetime_Str,
                         int location_id, String comment, int event_id, Date modified) throws ParseException {
        this.id = id;
        this.start_datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(start_datetime_Str);
        this.end_datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(end_datetime_Str);
        this.location_id = location_id;
        this.comment = comment;
        this.event_id = event_id;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public EventSchedule() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getStart_datetime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(start_datetime);
    }

    public String getEnd_datetime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(end_datetime);
    }

    public int getLocation_id() {
        return location_id;
    }

    public String getComment() {
        return comment;
    }

    public int getEvent_id() {
        return event_id;
    }

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}

package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Events")
public class Event {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String description;
    @DatabaseField
    private String link;
    @DatabaseField(unique = true)
    private String gcals_event_id; // Google calendar's event id or null
    @DatabaseField
    private Date modified = null;

    public Event(int id, String name, String description, String link, String gcals_event_id, String modifiedStr) throws ParseException {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.gcals_event_id = gcals_event_id;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public Event(int id, String name, String description, String link, String gcals_event_id, Date modified) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.gcals_event_id = gcals_event_id;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public Event() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getGcals_event_id() {
        return gcals_event_id;
    }

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}

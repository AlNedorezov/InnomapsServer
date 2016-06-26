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
    private int creator_id;
    @DatabaseField
    private String link;
    @DatabaseField
    private Date modified = null;

    public Event(int id, String name, String description, int creator_id, String link, String modifiedStr) throws ParseException {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creator_id = creator_id;
        this.link = link;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public Event(int id, String name, String description, int creator_id, String link, Date modified) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creator_id = creator_id;
        this.link = link;
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

    public int getCreator_id() {
        return creator_id;
    }

    public String getLink() {
        return link;
    }

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}

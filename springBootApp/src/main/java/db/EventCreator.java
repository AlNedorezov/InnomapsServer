package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 6/19/16.
 */
@DatabaseTable(tableName = "Event_creators")
public class EventCreator {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField(unique = true)
    private String email;
    @DatabaseField
    private String telegram_username;
    @DatabaseField
    private Date modified = null;

    public EventCreator(int id, String name, String email, String telegram_username, String modifiedStr) throws ParseException {
        this.id = id;
        this.name = name;
        this.email = email;
        this.telegram_username = telegram_username;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public EventCreator(int id, String name, String email, String telegram_username, Date modified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.telegram_username = telegram_username;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public EventCreator() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTelegram_username() {
        return telegram_username;
    }

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}

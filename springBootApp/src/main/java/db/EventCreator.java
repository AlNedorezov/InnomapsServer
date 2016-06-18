package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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

    public EventCreator(int id, String name, String email, String telegram_username) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.telegram_username = telegram_username;
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
}

package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 7/1/16.
 */
@DatabaseTable(tableName = "Event_event_creators")
public class EventEventCreator {
    @DatabaseField(uniqueCombo = true)
    private int event_id;
    @DatabaseField(uniqueCombo = true)
    private int event_creator_id;

    public EventEventCreator(int event_id, int event_creator_id) {
        this.event_id = event_id;
        this.event_creator_id = event_creator_id;
    }

    public EventEventCreator() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getEvent_id() {
        return event_id;
    }

    public int getEvent_creator_id() {
        return event_creator_id;
    }
}

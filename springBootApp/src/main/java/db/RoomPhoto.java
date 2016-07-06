package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Room_photos")
public class RoomPhoto {
    @DatabaseField(uniqueCombo = true)
    private int room_id;
    @DatabaseField(uniqueCombo = true)
    private int photo_id;
    @DatabaseField
    private Date created = null;

    public RoomPhoto(int room_id, int photo_id, String createdStr) throws ParseException {
        this.room_id = room_id;
        this.photo_id = photo_id;
        this.created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(createdStr);
    }

    public RoomPhoto(int room_id, int photo_id, Date created) {
        this.room_id = room_id;
        this.photo_id = photo_id;
        this.created = created;
    }

    public RoomPhoto() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getRoom_id() {
        return room_id;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public String getCreated() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(created);
    }
}

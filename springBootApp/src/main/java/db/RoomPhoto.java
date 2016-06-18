package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Room_photos")
public class RoomPhoto {
    @DatabaseField(uniqueCombo = true)
    private int room_id;
    @DatabaseField(uniqueCombo = true)
    private int photo_id;

    public RoomPhoto(int room_id, int photo_id) {
        this.room_id = room_id;
        this.photo_id = photo_id;
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
}

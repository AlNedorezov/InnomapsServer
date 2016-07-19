package db.fingerprinting;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 7/19/16.
 */

@DatabaseTable(tableName = "Fingerprint_locations")
public class Location {
    @DatabaseField(generatedId = true, unique = true)
    private long id;
    @DatabaseField(uniqueCombo = true)
    private double latitude;
    @DatabaseField(uniqueCombo = true)
    private double longitude;
    @DatabaseField(uniqueCombo = true)
    private int floor;
    @DatabaseField
    private Date modified = null;

    public Location(long id, double latitude, double longitude, int floor, String modifiedStr) throws ParseException {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floor = floor;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public Location(long id, double latitude, double longitude, int floor, Date modified) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floor = floor;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public Location() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getFloor() {
        return floor;
    }

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}

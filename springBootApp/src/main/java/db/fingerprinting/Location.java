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

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_MODIFIED = "modified";

    @DatabaseField(generatedId = true, unique = true, columnName = COLUMN_ID)
    private long id;
    @DatabaseField(uniqueCombo = true, columnName = COLUMN_LATITUDE)
    private double latitude;
    @DatabaseField(uniqueCombo = true, columnName = COLUMN_LONGITUDE)
    private double longitude;
    @DatabaseField(uniqueCombo = true, columnName = COLUMN_FLOOR)
    private int floor;
    @DatabaseField (columnName = COLUMN_MODIFIED)
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

    public Location(double latitude, double longitude, int floor, Date modified) {
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

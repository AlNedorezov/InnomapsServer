package db.fingerprinting;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 7/19/16.
 */

@DatabaseTable(tableName = "Fingerprint_location_access_points")
public class LocationAccessPoint {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LOCATION_ID = "location_id";
    public static final String COLUMN_ACCESS_POINT_ID = "access_point_id";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_MODIFIED = "modified";

    @DatabaseField(generatedId = true, unique = true, columnName = COLUMN_ID)
    private long id;
    @DatabaseField(uniqueCombo = true, columnName = COLUMN_LOCATION_ID)
    private long location_id;
    @DatabaseField(uniqueCombo = true ,columnName = COLUMN_ACCESS_POINT_ID)
    private long access_point_id;
    @DatabaseField(columnName = COLUMN_LEVEL)
    private double level;
    @DatabaseField(columnName = COLUMN_MODIFIED)
    private Date modified = null;

    public LocationAccessPoint(long id, long location_id, long access_point_id, double level, String modifiedStr) throws ParseException {
        this.id = id;
        this.location_id = location_id;
        this.access_point_id = access_point_id;
        this.level = level;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public LocationAccessPoint(long id, long location_id, long access_point_id, double level, Date modified) {
        this.id = id;
        this.location_id = location_id;
        this.access_point_id = access_point_id;
        this.level = level;
        this.modified = modified;
    }

    public LocationAccessPoint(long location_id, long access_point_id, double level, Date modified) {
        this.location_id = location_id;
        this.access_point_id = access_point_id;
        this.level = level;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public LocationAccessPoint() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public long getId() {
        return id;
    }

    public long getLocation_id() {
        return location_id;
    }

    public long getAccess_point_id() {
        return access_point_id;
    }

    public double getLevel() {
        return level;
    }

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}
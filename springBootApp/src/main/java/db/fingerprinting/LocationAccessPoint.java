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
    @DatabaseField(generatedId = true, unique = true)
    private long id;
    @DatabaseField(uniqueCombo = true)
    private long location_id;
    @DatabaseField(uniqueCombo = true)
    private long access_point_id;
    @DatabaseField
    private double level;
    @DatabaseField
    private Date modified = null;

    public LocationAccessPoint(int id, int location_id, int access_point_id, double level, String modifiedStr) throws ParseException {
        this.id = id;
        this.location_id = location_id;
        this.access_point_id = access_point_id;
        this.level = level;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public LocationAccessPoint(int id, int location_id, int access_point_id, double level, Date modified) {
        this.id = id;
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
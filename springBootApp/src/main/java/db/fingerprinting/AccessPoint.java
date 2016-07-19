package db.fingerprinting;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 7/19/16.
 */

@DatabaseTable(tableName = "Fingerprint_access_points")
public class AccessPoint {
    @DatabaseField(generatedId = true, unique = true)
    private long id;
    @DatabaseField(unique = true)
    private String BSSID;
    @DatabaseField
    private Date modified = null;

    public AccessPoint(long id, String BSSID, String modifiedStr) throws ParseException {
        this.id = id;
        this.BSSID = BSSID;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public AccessPoint(long id, String BSSID, Date modified) {
        this.id = id;
        this.BSSID = BSSID;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public AccessPoint() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public long getId() {
        return id;
    }

    public String getBSSID() {
        return BSSID;
    }

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}
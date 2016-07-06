package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Photos")
public class Photo {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String url;
    @DatabaseField
    private Date modified = null;

    public Photo(int id, String url, String modifiedStr) throws ParseException {
        this.id = id;
        this.url = url;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public Photo(int id, String url, Date modified) {
        this.id = id;
        this.url = url;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public Photo() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}
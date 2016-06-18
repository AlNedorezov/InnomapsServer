package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Photos")
public class Photo {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String url;

    public Photo(int id, String url) {
        this.id = id;
        this.url = url;
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
}
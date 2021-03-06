package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Roles")
public class Role {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // For deserialization with Jackson
    public Role() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

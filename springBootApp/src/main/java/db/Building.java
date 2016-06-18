package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Buildings")
public class Building {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField
    private String number;
    @DatabaseField
    private Integer block;
    @DatabaseField
    private String description;
    @DatabaseField
    private int coordinate_id;
    @DatabaseField
    private int street_id;

    public Building(int id, String number, Integer block, String description, int coordinate_id, int street_id) {
        this.id = id;
        this.number = number;
        this.block = block;
        this.description = description;
        this.coordinate_id = coordinate_id;
        this.street_id = street_id;
    }

    // For deserialization with Jackson
    public Building() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Integer getBlock() {
        return block;
    }

    public String getDescription() {
        return description;
    }

    public int getCoordinate_id() {
        return coordinate_id;
    }

    public int getStreet_id() {
        return street_id;
    }
}

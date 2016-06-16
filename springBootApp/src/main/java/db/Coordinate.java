package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/9/16.
 */
@DatabaseTable(tableName = "Coordinates")
public class Coordinate {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private double latitude;
    @DatabaseField(uniqueCombo = true)
    private double longitude;
    @DatabaseField(uniqueCombo = true)
    private int floor;
    @DatabaseField
    private int type_id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String description;

    public Coordinate(int id, double latitude, double longitude, int floor, int type_id, String name, String description) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floor = floor;
        this.type_id = type_id;
        this.name = name;
        this.description = description;
    }

    // For deserialization with Jackson
    public Coordinate() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
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

    public int getType_id() {
        return type_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

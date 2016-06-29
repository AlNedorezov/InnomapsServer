package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 6/24/16.
 */

@DatabaseTable(tableName = "Building_floor_overlays")
public class BuildingFloorOverlay {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private int building_id;
    @DatabaseField
    private int photo_id;
    @DatabaseField(uniqueCombo = true)
    private int floor;
    @DatabaseField
    private double southWestLatitude;
    @DatabaseField
    private double southWestLongitude;
    @DatabaseField
    private double northEastLatitude;
    @DatabaseField
    private double northEastLongitude;
    @DatabaseField
    private Date modified = null;

    public BuildingFloorOverlay(int id, int building_id, int photo_id, int floor, double southWestLatitude, double southWestLongitude,
                                double northEastLatitude, double northEastLongitude, String modifiedStr) throws ParseException {
        this.id = id;
        this.building_id = building_id;
        this.photo_id = photo_id;
        this.floor = floor;
        this.southWestLatitude = southWestLatitude;
        this.southWestLongitude = southWestLongitude;
        this.northEastLatitude = northEastLatitude;
        this.northEastLongitude = northEastLongitude;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public BuildingFloorOverlay(int id, int building_id, int photo_id, int floor, double southWestLatitude, double southWestLongitude,
                                double northEastLatitude, double northEastLongitude, Date modified) {
        this.id = id;
        this.building_id = building_id;
        this.photo_id = photo_id;
        this.floor = floor;
        this.southWestLatitude = southWestLatitude;
        this.southWestLongitude = southWestLongitude;
        this.northEastLatitude = northEastLatitude;
        this.northEastLongitude = northEastLongitude;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public BuildingFloorOverlay() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public int getFloor() {
        return floor;
    }

    public double getSouthWestLatitude() {
        return southWestLatitude;
    }

    public double getSouthWestLongitude() {
        return southWestLongitude;
    }

    public double getNorthEastLatitude() {
        return northEastLatitude;
    }

    public double getNorthEastLongitude() {
        return northEastLongitude;
    }

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}

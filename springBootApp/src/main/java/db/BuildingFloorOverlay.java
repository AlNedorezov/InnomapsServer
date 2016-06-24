package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/24/16.
 */

@DatabaseTable(tableName = "Building_floor_overlays")
public class BuildingFloorOverlay {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private int building_id;
    @DatabaseField(uniqueCombo = true)
    private int photo_id;
    @DatabaseField(uniqueCombo = true)
    private int floor;

    public BuildingFloorOverlay(int id, int building_id, int photo_id, int floor) {
        this.id = id;
        this.building_id = building_id;
        this.photo_id = photo_id;
        this.floor = floor;
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
}

package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/16/16.
 */
@DatabaseTable(tableName = "Rooms")
public class Room {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField
    private Integer number;
    @DatabaseField
    private int building_id;
    @DatabaseField(unique = true)
    private int coordinate_id;
    @DatabaseField
    private int type_id;

    public Room(int id, Integer number, int building_id, int coordinate_id, int type_id) {
        this.id = id;
        this.number = number;
        this.building_id = building_id;
        this.coordinate_id = coordinate_id;
        this.type_id = type_id;
    }

    // For deserialization with Jackson
    public Room() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public int getCoordinate_id() {
        return coordinate_id;
    }

    public int getType_id() {
        return type_id;
    }
}

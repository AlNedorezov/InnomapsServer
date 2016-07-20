package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 7/20/16.
 */

@DatabaseTable(tableName = "Building_auxiliary_coordinates")
public class BuildingAuxiliaryCoordinate {
    @DatabaseField(uniqueCombo = true)
    private int building_id;
    @DatabaseField(uniqueCombo = true)
    private int coordinate_id;
    @DatabaseField
    private Date created = null;

    public BuildingAuxiliaryCoordinate(int building_id, int coordinate_id, String createdStr) throws ParseException {
        this.building_id = building_id;
        this.coordinate_id = coordinate_id;
        this.created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(createdStr);
    }

    public BuildingAuxiliaryCoordinate(int building_id, int coordinate_id, Date created) {
        this.building_id = building_id;
        this.coordinate_id = coordinate_id;
        this.created = created;
    }

    public BuildingAuxiliaryCoordinate() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getBuilding_id() {
        return building_id;
    }

    public int getCoordinate_id() {
        return coordinate_id;
    }

    public String getCreated() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(created);
    }
}
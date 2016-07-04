package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Building_photos")
public class BuildingPhoto {
    @DatabaseField(uniqueCombo = true)
    private int building_id;
    @DatabaseField(uniqueCombo = true)
    private int photo_id;
    @DatabaseField
    private Date created = null;

    public BuildingPhoto(int building_id, int photo_id, String createdStr) throws ParseException {
        this.building_id = building_id;
        this.photo_id = photo_id;
        this.created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(createdStr);
    }

    public BuildingPhoto(int building_id, int photo_id, Date created) {
        this.building_id = building_id;
        this.photo_id = photo_id;
        this.created = created;
    }

    public BuildingPhoto() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getBuilding_id() {
        return building_id;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public String getCreated() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(created);
    }
}

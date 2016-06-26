package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Buildings")
public class Building {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private String number;
    @DatabaseField(uniqueCombo = true)
    private Integer block;
    @DatabaseField
    private String description;
    @DatabaseField(unique = true) // there can be no two buildings on the same spot
    private int coordinate_id;
    @DatabaseField(uniqueCombo = true)
    private int street_id;
    @DatabaseField
    private Date modified = null;

    public Building(int id, String number, Integer block, String description, int coordinate_id, int street_id, String modifiedStr) throws ParseException {
        this.id = id;
        this.number = number;
        this.block = block;
        this.description = description;
        this.coordinate_id = coordinate_id;
        this.street_id = street_id;
        this.modified = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(modifiedStr);
    }

    public Building(int id, String number, Integer block, String description, int coordinate_id, int street_id, Date modified) {
        this.id = id;
        this.number = number;
        this.block = block;
        this.description = description;
        this.coordinate_id = coordinate_id;
        this.street_id = street_id;
        this.modified = modified;
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

    public String getModified() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(modified);
    }
}

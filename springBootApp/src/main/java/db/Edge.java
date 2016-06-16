package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/16/16.
 */
@DatabaseTable(tableName = "Edges")
public class Edge {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField
    private int type_id;
    @DatabaseField(uniqueCombo = true)
    private int source_id;
    @DatabaseField(uniqueCombo = true)
    private int target_id;

    public Edge(int id, int type_id, int source_id, int target_id) {
        this.id = id;
        this.type_id = type_id;
        this.source_id = source_id;
        this.target_id = target_id;
    }

    // For deserialization with Jackson
    public Edge() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public int getType_id() {
        return type_id;
    }

    public int getSource_id() {
        return source_id;
    }

    public int getTarget_id() {
        return target_id;
    }
}

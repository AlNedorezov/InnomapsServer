package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "User_roles")
public class UserRole {
    @DatabaseField(uniqueCombo = true)
    private int user_id;
    @DatabaseField(uniqueCombo = true)
    private int role_id;

    public UserRole(int user_id, int role_id) {
        this.user_id = user_id;
        this.role_id = role_id;
    }

    // For deserialization with Jackson
    public UserRole() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getUser_id() {
        return user_id;
    }

    public int getRole_id() {
        return role_id;
    }
}

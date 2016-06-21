package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "User_roles")
public class UserRole {
    // It is assumed that one user can only have one role
    // If later it will be decided that one use can have
    // multiple roles, then all that will be necessary to do
    // is to remove unique annotation on user_id field
    // and change userrole function in UserRolesController
    // for it to return a list of roles
    @DatabaseField(uniqueCombo = true, unique = true)
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

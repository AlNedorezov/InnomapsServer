package db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alnedorezov on 6/18/16.
 */
@DatabaseTable(tableName = "Users")
public class User {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String email;
    @DatabaseField
    private String name;
    @DatabaseField
    private String password;
    @DatabaseField
    private int activated;
    @DatabaseField
    private String activation_code;
    @DatabaseField
    private Date created = null;
    @DatabaseField
    private boolean deleted;

    public User(int id, String email, String name, String password, int activated, String activation_code, String createdStr, boolean deleted) throws ParseException {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.activated = activated;
        this.activation_code = activation_code;
        this.created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(createdStr);
        this.deleted = deleted;
    }

    // For deserialization with Jackson
    public User() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public int getActivated() {
        return activated;
    }

    public String getActivation_code() {
        return activation_code;
    }

    public String getCreated() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(created);
    }

    public boolean getDeleted() {
        return deleted;
    }
}

package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Role;
import db.UserRole;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.RolesObject;

/**
 * Created by alnedorezov on 6/20/16.
 */

@RestController
public class RolesController {
    Application a = new Application();

    @RequestMapping("/resources/roles")
    public RolesObject roles() throws Exception {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);
        RolesObject roles1 = new RolesObject(a.roleDao.queryForAll());
        connectionSource.close();
        return roles1;
    }

    @RequestMapping("/resources/role")
    public Role role(@RequestParam(value = "id", defaultValue = "-1") int id) throws Exception {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);
        Role role1 = a.roleDao.queryForId(id);
        connectionSource.close();
        return role1;
    }

    @RequestMapping(value = "/resources/role", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "name", defaultValue = "!~DELETE") String name) throws Exception {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);

        if (name.equals("!~DELETE")) {
            // Deleting a role
            System.out.println("Received POST request: delete role with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.roleDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such role.\n";
            } else {
                a.roleDao.deleteById(id);
                DeleteBuilder<UserRole, Integer> db = a.userRoleDao.deleteBuilder();
                db.where().eq("role_id", id);
                PreparedDelete<UserRole> preparedDelete = db.prepare();
                a.userRoleDao.delete(preparedDelete);
                connectionSource.close();
                return "0. role with id=" + id + " was successfully deleted.\n";
            }
        } else {
            name = "ROLE_" + name; // Apparently according to Spring Security convention role names should start with "ROLE_"
            if (id == -1) {
                // Creating a role
                System.out.println("Received POST request: create role with name=" + name);
                a.roleDao.create(new Role(id, name));
                QueryBuilder<Role, Integer> qBuilder = a.roleDao.queryBuilder();
                qBuilder.orderBy("id", false); // false for descending order
                qBuilder.limit(1);
                Role createdRole = a.roleDao.queryForId(qBuilder.query().get(0).getId());
                System.out.println(createdRole.getId() + " | " + createdRole.getName());
                connectionSource.close();
                return "0. Role with id=" + createdRole.getId() + " was successfully created.\n";
            } else {
                // Updating a role
                System.out.println("Received POST request: update role with id=" + id);
                if (!a.roleDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such role.\n";
                } else {
                    Role role1 = a.roleDao.queryForId(id);
                    String updName;
                    if (!name.equals("!~DELETE"))
                        updName = name;
                    else
                        updName = role1.getName();
                    a.roleDao.update(new Role(id, updName));
                    connectionSource.close();
                    return "0. Role with id=" + id + " was successfully updated.\n";
                }
            }
        }
    }
}
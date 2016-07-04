package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import db.UserRole;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.UserRolesObject;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by alnedorezov on 6/20/16.
 */

@RestController
public class UserRolesController {
    Application a = new Application();

    @RequestMapping("/resources/userroles")
    public UserRolesObject userRoles() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        UserRolesObject userRolesObject1 = new UserRolesObject(a.userRoleDao.queryForAll());
        connectionSource.close();
        return userRolesObject1;
    }

    @RequestMapping("/resources/userrole")
    public UserRole userrole(@RequestParam(value = "userid", defaultValue = "-1") int userid) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        QueryBuilder<UserRole, Integer> qb = a.userRoleDao.queryBuilder();
        qb.where().eq("user_id", userid);
        UserRole userRole1 = qb.query().get(0);
        connectionSource.close();
        return userRole1;
    }

    @RequestMapping(value = "/resources/userrole", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "userid", defaultValue = "-1") int userid,
                       @RequestParam(value = "roleid", defaultValue = "-1") int roleid) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        QueryBuilder<UserRole, Integer> qb = a.userRoleDao.queryBuilder();
        qb.where().eq("user_id", userid).and().eq("role_id", roleid);
        PreparedQuery<UserRole> pc = qb.prepare();
        List<UserRole> userRoles = a.userRoleDao.query(pc);
        String action;
        if (userRoles.size() > 0)
            action = "delete";
        else
            action = "add";

        if (roleid == -1 || userid == -1) {
            connectionSource.close();
            return "-1. Wrong parameters.";
        } else {
            if (!a.roleDao.idExists(roleid)) {
                connectionSource.close();
                return "-1. There is no such role.\n";
            } else if (!a.userDao.idExists(userid)) {
                connectionSource.close();
                return "-1. There is no such user.\n";
            } else if (action.equals("add")) {
                System.out.println("Received POST request: user with id=" + userid + " was assigned to role with id=" + roleid);
                a.userRoleDao.create(new UserRole(userid, roleid));
                connectionSource.close();
                return "0. User with id=" + userid + " was assigned to role with id=" + roleid + "\n";
            } else { // if action = delete
                System.out.println("Received POST request: user with id=" + userid + " was unassigned from role with id=" + roleid);
                DeleteBuilder<UserRole, Integer> db = a.userRoleDao.deleteBuilder();
                db.where().eq("user_id", userid).and().eq("role_id", roleid);
                PreparedDelete<UserRole> preparedDelete = db.prepare();
                a.userRoleDao.delete(preparedDelete);
                connectionSource.close();
                return "0. User with id=" + userid + " was unassigned from role with id=" + roleid + "\n";
            }
        }
    }
}

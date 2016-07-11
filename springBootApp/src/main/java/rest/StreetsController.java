package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Building;
import db.Street;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.StreetsObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/23/16.
 */

@RestController
public class StreetsController {
    Application a = new Application();

    @RequestMapping("/resources/streets")
    public StreetsObject streets() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        StreetsObject streets1 = new StreetsObject(a.streetDao.queryForAll());
        connectionSource.close();
        return streets1;
    }

    @RequestMapping("/resources/street")
    public Street street(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        Street street1 = a.streetDao.queryForId(id);
        connectionSource.close();
        return street1;
    }

    @RequestMapping(value = "/resources/street", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "name", defaultValue = "!~DELETE") String name) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        if (name.equals("!~DELETE")) {
            // Deleting a street
            System.out.println("Received POST request: delete street with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.streetDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such street.\n";
            } else {
                QueryBuilder<Building, Integer> qb = a.buildingDao.queryBuilder();
                qb.where().eq("street_id", id);
                if (qb.query().size() > 0) {
                    connectionSource.close();
                    return "-1. Delete all buildings on " + a.streetDao.queryForId(id).getName() + " street first.\n";
                } else {
                    a.streetDao.deleteById(id);
                    connectionSource.close();
                    return "0. Street with id=" + id + " was successfully deleted.\n";
                }
            }
        } else {
            if (id == -1) {
                // Creating a street
                System.out.println("Received POST request: create street with name=" + name);
                a.streetDao.create(new Street(id, name, new Date()));
                QueryBuilder<Street, Integer> qBuilder = a.streetDao.queryBuilder();
                qBuilder.orderBy("id", false); // false for descending order
                qBuilder.limit(1);
                Street createdStreet = a.streetDao.queryForId(qBuilder.query().get(0).getId());
                System.out.println(createdStreet.getId() + " | " + createdStreet.getName() + " | " +
                        createdStreet.getModified());
                connectionSource.close();
                return "0. Street with id=" + createdStreet.getId() + " was successfully created.\n";
            } else {
                // Updating a street
                System.out.println("Received POST request: update street with id=" + id);
                if (!a.streetDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such street.\n";
                } else {
                    Street street1 = a.streetDao.queryForId(id);
                    String updName;
                    if (!name.equals("!~DELETE"))
                        updName = name;
                    else
                        updName = street1.getName();
                    a.streetDao.update(new Street(id, updName, new Date()));
                    connectionSource.close();
                    return "0. Street with id=" + id + " was successfully updated.\n";
                }
            }
        }
    }
}
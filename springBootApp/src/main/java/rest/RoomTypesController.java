package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Room;
import db.RoomType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.RoomTypesObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/23/16.
 */

@RestController
public class RoomTypesController {
    Application a = new Application();

    @RequestMapping("/resources/roomtypes")
    public RoomTypesObject roomTypes() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        RoomTypesObject roomTypes1 = new RoomTypesObject(a.roomTypeDao.queryForAll());
        connectionSource.close();
        return roomTypes1;
    }

    @RequestMapping("/resources/roomtype")
    public RoomType roomType(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        RoomType roomType1 = a.roomTypeDao.queryForId(id);
        connectionSource.close();
        return roomType1;
    }

    @RequestMapping(value = "/resources/roomtype", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id,
                       @RequestParam(value = "name", defaultValue = "!~DELETE") String name) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        if (name.equals("!~DELETE")) {
            // Deleting a room type
            System.out.println("Received POST request: delete room type with id=" + id);
            if (id == -1)
                return "-1. Wrong parameters.\n";
            else if (!a.roomTypeDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such room type.\n";
            } else {
                QueryBuilder<Room, Integer> qb = a.roomDao.queryBuilder();
                qb.where().eq("type_id", id);
                if (qb.query().size() > 0) {
                    connectionSource.close();
                    return "-1. Delete all rooms with type " + a.roomTypeDao.queryForId(id).getName() + " first.\n";
                } else {
                    a.roomTypeDao.deleteById(id);
                    connectionSource.close();
                    return "0. Room type with id=" + id + " was successfully deleted.\n";
                }
            }
        } else {
            if (id == -1) {
                // Creating a room type
                System.out.println("Received POST request: create room type with name=" + name);
                a.roomTypeDao.create(new RoomType(id, name));
                QueryBuilder<RoomType, Integer> qBuilder = a.roomTypeDao.queryBuilder();
                qBuilder.orderBy("id", false); // false for descending order
                qBuilder.limit(1);
                RoomType createdRoomType = a.roomTypeDao.queryForId(qBuilder.query().get(0).getId());
                System.out.println(createdRoomType.getId() + " | " + createdRoomType.getName());
                connectionSource.close();
                return "0. Room type with id=" + createdRoomType.getId() + " was successfully created.\n";
            } else {
                // Updating a room type
                System.out.println("Received POST request: update room type with id=" + id);
                if (!a.roomTypeDao.idExists(id)) {
                    connectionSource.close();
                    return "-1. There is no such room type.\n";
                } else {
                    RoomType roomType1 = a.roomTypeDao.queryForId(id);
                    String updName;
                    if (!name.equals("!~DELETE"))
                        updName = name;
                    else
                        updName = roomType1.getName();
                    a.roomTypeDao.update(new RoomType(id, updName));
                    connectionSource.close();
                    return "0. Room type with id=" + id + " was successfully updated.\n";
                }
            }
        }
    }
}
package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.Coordinate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.CoordinatesObject;

/**
 * Created by alnedorezov on 6/21/16.
 */

@RestController
public class CoordinatesController {
    Application a = new Application();

    @RequestMapping("/resources/coordinates")
    public CoordinatesObject roles() throws Exception {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);
        CoordinatesObject coordinates1 = new CoordinatesObject(a.coordinateDao.queryForAll());
        connectionSource.close();
        return coordinates1;
    }

    @RequestMapping("/resources/coordinate")
    public Coordinate coordinate(@RequestParam(value = "id", defaultValue = "-1") int id) throws Exception {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);
        Coordinate coordinate1 = a.coordinateDao.queryForId(id);
        connectionSource.close();
        return coordinate1;
    }
}
package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.QueryBuilder;
import db.BuildingFloorOverlay;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.BuildingFloorOverlaysObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/27/16.
 */

@RestController
public class BuildingFloorOverlaysController {
    Application a = new Application();

    @RequestMapping("/resources/buildingflooroverlays")
    public BuildingFloorOverlaysObject buildings() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        BuildingFloorOverlaysObject buildings1 = new BuildingFloorOverlaysObject(a.buildingFloorOverlayDao.queryForAll());
        connectionSource.close();
        return buildings1;
    }

    @RequestMapping("/resources/buildingflooroverlay")
    public BuildingFloorOverlay building(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        BuildingFloorOverlay buildingFloorOverlay1 = a.buildingFloorOverlayDao.queryForId(id);
        connectionSource.close();
        return buildingFloorOverlay1;
    }
}

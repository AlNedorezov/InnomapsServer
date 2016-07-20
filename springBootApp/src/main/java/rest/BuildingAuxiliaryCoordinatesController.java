package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import db.BuildingAuxiliaryCoordinate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.BuildingAuxiliaryCoordinatesObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/20/16.
 */

@RestController
public class BuildingAuxiliaryCoordinatesController {
    private Application a = new Application();

    @RequestMapping("/resources/buildingauxiliarycoordinates")
    public BuildingAuxiliaryCoordinatesObject buildingAuxiliaryCoordinates(@RequestParam(value = "createdAfterDate", defaultValue = "-1") String date) throws SQLException, ParseException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        BuildingAuxiliaryCoordinatesObject buildingAuxiliaryCoordinates1;

        if ("-1".equals(date))
            // if created after date is not specified, return all building coordinates
            buildingAuxiliaryCoordinates1 = new BuildingAuxiliaryCoordinatesObject(a.buildingAuxiliaryCoordinateDao.queryForAll());
        else {
            // if created after date is specified, return all building coordinates that were
            // created after on on specified date
            Date createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(date);
            QueryBuilder<BuildingAuxiliaryCoordinate, Integer> qb = a.buildingAuxiliaryCoordinateDao.queryBuilder();
            qb.where().ge("created", createdDate);
            PreparedQuery<BuildingAuxiliaryCoordinate> pc = qb.prepare();
            buildingAuxiliaryCoordinates1 = new BuildingAuxiliaryCoordinatesObject(a.buildingAuxiliaryCoordinateDao.query(pc));
        }
        connectionSource.close();
        return buildingAuxiliaryCoordinates1;
    }

    @RequestMapping("/resources/buildingauxiliarycoordinate")
    public BuildingAuxiliaryCoordinatesObject building(@RequestParam(value = "buildingid", defaultValue = "-1") int building_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        QueryBuilder<BuildingAuxiliaryCoordinate, Integer> qb = a.buildingAuxiliaryCoordinateDao.queryBuilder();
        qb.where().eq("building_id", building_id);
        BuildingAuxiliaryCoordinatesObject buildingAuxiliaryCoordinates1 = new BuildingAuxiliaryCoordinatesObject(qb.query());
        connectionSource.close();
        return buildingAuxiliaryCoordinates1;
    }

    @RequestMapping(value = "/resources/buildingauxiliarycoordinate", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "buildingid", defaultValue = "-1") int building_id,
                       @RequestParam(value = "coordinateid", defaultValue = "-2") int coordinate_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        QueryBuilder<BuildingAuxiliaryCoordinate, Integer> qb = a.buildingAuxiliaryCoordinateDao.queryBuilder();
        qb.where().eq("building_id", building_id).and().eq("coordinate_id", coordinate_id);
        PreparedQuery<BuildingAuxiliaryCoordinate> pc = qb.prepare();
        List<BuildingAuxiliaryCoordinate> buildingAuxiliaryCoordinates = a.buildingAuxiliaryCoordinateDao.query(pc);
        String action;
        if (buildingAuxiliaryCoordinates.size() > 0)
            action = "delete";
        else
            action = "add";

        if (building_id == -1 || coordinate_id == -2) {
            connectionSource.close();
            return "-1. Wrong parameters.";
        } else {
            String errorMessageOnAdd = checkIfBuildingAuxiliaryCoordinateCanBeAdded(building_id, coordinate_id);
            if (!errorMessageOnAdd.equals("")) {
                connectionSource.close();
                return "-1. " + errorMessageOnAdd;
            } else if (action.equals("add")) {
                System.out.println("Received POST request: coordinate with id=" + coordinate_id + " was assigned to building with id=" + building_id);
                a.buildingAuxiliaryCoordinateDao.create(new BuildingAuxiliaryCoordinate(building_id, coordinate_id, new Date()));
                connectionSource.close();
                return "0. Coordinate with id=" + coordinate_id + " was assigned to building with id=" + building_id + "\n";
            } else { // if action = delete
                System.out.println("Received POST request: coordinate with id=" + coordinate_id + " was unassigned from building with id=" + building_id);
                DeleteBuilder<BuildingAuxiliaryCoordinate, Integer> db = a.buildingAuxiliaryCoordinateDao.deleteBuilder();
                db.where().eq("building_id", building_id).and().eq("coordinate_id", coordinate_id);
                PreparedDelete<BuildingAuxiliaryCoordinate> preparedDelete = db.prepare();
                a.buildingAuxiliaryCoordinateDao.delete(preparedDelete);
                connectionSource.close();
                return "0. Coordinate with id=" + coordinate_id + " was unassigned from building with id=" + building_id + "\n";
            }
        }
    }

    private String checkIfBuildingAuxiliaryCoordinateCanBeAdded(int building_id, int coordinate_id) throws SQLException {
        String errorMessage = "";

        errorMessage += CommonFunctions.checkIfBuildingExist(building_id);
        errorMessage += CommonFunctions.checkIfCoordinateExist(coordinate_id);

        return errorMessage;
    }
}
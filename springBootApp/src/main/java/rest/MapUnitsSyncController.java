package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import db.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.sync.MapUnitsSync;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/12/16.
 */

@RestController
public class MapUnitsSyncController {
    Application a = new Application();

    private List<Integer> getIdsFromCoordinates(List<Coordinate> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (Coordinate aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromEdges(List<Edge> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (Edge aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromStreets(List<Street> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (Street aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromBuildings(List<Building> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (Building aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromRooms(List<Room> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (Room aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromPhotos(List<Photo> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (Photo aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    private List<Integer> getIdsFromBuildingFloorOverlays(List<BuildingFloorOverlay> list) throws ParseException {
        List<Integer> ids = new ArrayList<>();
        for (BuildingFloorOverlay aList : list) {
            ids.add(aList.getId());
        }
        return ids;
    }

    @RequestMapping("/resources/sync/mapunits")
    public MapUnitsSync syncTypes(@RequestParam(value = "date", defaultValue = "2015-07-26 15:00:00.0") String date) throws SQLException, ParseException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);
        Date modifiedDate;
        String modified = "modified";
        String dateFormat = "yyyy-MM-dd HH:mm:ss.S";

        QueryBuilder<Coordinate, Integer> qbCoordinate = a.coordinateDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbCoordinate.where().ge(modified, modifiedDate);
        PreparedQuery<Coordinate> pcCoordinate = qbCoordinate.prepare();
        List<Coordinate> coordinates = a.coordinateDao.query(pcCoordinate);

        QueryBuilder<Edge, Integer> qbEdge = a.edgeDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbEdge.where().ge(modified, modifiedDate);
        PreparedQuery<Edge> pcEdge = qbEdge.prepare();
        List<Edge> edges = a.edgeDao.query(pcEdge);

        QueryBuilder<Street, Integer> qbStreet = a.streetDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbStreet.where().ge(modified, modifiedDate);
        PreparedQuery<Street> pcStreet = qbStreet.prepare();
        List<Street> streets = a.streetDao.query(pcStreet);

        QueryBuilder<Building, Integer> qbBuilding = a.buildingDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbBuilding.where().ge(modified, modifiedDate);
        PreparedQuery<Building> pcBuilding = qbBuilding.prepare();
        List<Building> buildings = a.buildingDao.query(pcBuilding);

        QueryBuilder<Room, Integer> qbRoom = a.roomDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbRoom.where().ge(modified, modifiedDate);
        PreparedQuery<Room> pcRoom = qbRoom.prepare();
        List<Room> rooms = a.roomDao.query(pcRoom);

        QueryBuilder<Photo, Integer> qbPhoto = a.photoDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbPhoto.where().ge(modified, modifiedDate);
        PreparedQuery<Photo> pcPhoto = qbPhoto.prepare();
        List<Photo> photos = a.photoDao.query(pcPhoto);

        QueryBuilder<BuildingFloorOverlay, Integer> qbBuildingFloorOverlay = a.buildingFloorOverlayDao.queryBuilder();
        modifiedDate = new SimpleDateFormat(dateFormat).parse(date);
        qbBuildingFloorOverlay.where().ge(modified, modifiedDate);
        PreparedQuery<BuildingFloorOverlay> pcBuildingFloorOverlay = qbBuildingFloorOverlay.prepare();
        List<BuildingFloorOverlay> buildingFloorOverlays = a.buildingFloorOverlayDao.query(pcBuildingFloorOverlay);

        connectionSource.close();
        return new MapUnitsSync(getIdsFromCoordinates(coordinates), getIdsFromEdges(edges), getIdsFromStreets(streets),
                getIdsFromBuildings(buildings), getIdsFromRooms(rooms), getIdsFromPhotos(photos),
                getIdsFromBuildingFloorOverlays(buildingFloorOverlays));
    }
}

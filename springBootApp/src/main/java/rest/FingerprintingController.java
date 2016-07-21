package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import db.fingerprinting.AccessPoint;
import db.fingerprinting.Location;
import db.fingerprinting.LocationAccessPoint;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.fingerprinting.jsonWrappers.request.WrapperAccessPoints;
import rest.clientservercommunicationclasses.fingerprinting.jsonWrappers.request.WrapperLocationAccessPoints;

import java.sql.Date;
import java.sql.SQLException;

/**
 * Created by alnedorezov on 7/19/16.
 */

@RestController
public class FingerprintingController {
    private Application a = new Application();

    @RequestMapping(value = "/resources/fingerprinting/save", method = RequestMethod.POST)
    public String save(@RequestBody WrapperLocationAccessPoints locationAccessPoints) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        // For log on the server
        System.out.println(new java.util.Date() + " Received POST request: save location with access points");

        Location location = getOrCreateLocation(locationAccessPoints.getLatitude(), locationAccessPoints.getLongitude(),
                                                locationAccessPoints.getFloor());
        for(WrapperAccessPoints accessPointWithLevel: locationAccessPoints.getAccessPoints()){
            AccessPoint accessPoint = getOrCreateAccessPoint(accessPointWithLevel);
            createOrUpdateLocationAccessPoint(location, accessPoint, accessPointWithLevel.getAvgLevel());
        }
        connectionSource.close();
        return "Location with access points are saved.";
    }

    private Location getOrCreateLocation(Double latitude, Double longitude, Integer floor) throws SQLException {
        QueryBuilder<Location, Long> queryBuilder = a.fingerprintLocationDao.queryBuilder();
        Where<Location, Long> where = queryBuilder.where();
        where.and(where.eq(Location.COLUMN_LATITUDE, latitude), where.eq(Location.COLUMN_LONGITUDE, longitude),
                    where.eq(Location.COLUMN_FLOOR, floor));
        Location location = queryBuilder.queryForFirst();
        if(location==null){
            location = new Location(latitude, longitude, floor, new java.util.Date());
            a.fingerprintLocationDao.create(location);
        }
        return location;
    }

    private AccessPoint getOrCreateAccessPoint(WrapperAccessPoints accessPointWithLevel) throws SQLException {
        QueryBuilder<AccessPoint, Long> queryBuilder = a.fingerprintAccessPointDao.queryBuilder();
        queryBuilder.where().eq(AccessPoint.COLUMN_BSSID, accessPointWithLevel.getBssid());
        AccessPoint accessPoint = queryBuilder.queryForFirst();
        if(accessPoint==null){
            accessPoint = new AccessPoint(accessPointWithLevel.getBssid(), new java.util.Date());
            a.fingerprintAccessPointDao.create(accessPoint);
        }
        return accessPoint;
    }

    private void createOrUpdateLocationAccessPoint(Location location, AccessPoint accessPoint, Double avgLevel) throws SQLException {
        UpdateBuilder<LocationAccessPoint, Long> updateBuilder = a.fingerprintLocationAccessPointDao.updateBuilder();
        Where<LocationAccessPoint, Long> where = updateBuilder.where();
        where.and(where.eq(LocationAccessPoint.COLUMN_LOCATION_ID, location.getId()),
                where.eq(LocationAccessPoint.COLUMN_ACCESS_POINT_ID, accessPoint.getId()));
        updateBuilder.updateColumnValue(LocationAccessPoint.COLUMN_LEVEL, avgLevel);
        updateBuilder.updateColumnValue(LocationAccessPoint.COLUMN_MODIFIED, new java.util.Date());
        int updatedCount = updateBuilder.update();
        if(updatedCount<=0){
            a.fingerprintLocationAccessPointDao.create(new LocationAccessPoint(location.getId(), accessPoint.getId(), avgLevel, new java.util.Date()));
        }
    }
}

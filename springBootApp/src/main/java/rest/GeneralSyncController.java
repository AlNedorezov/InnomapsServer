package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import db.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rest.clientservercommunicationclasses.sync.GeneralSync;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by alnedorezov on 7/14/16.
 */

@RestController
public class GeneralSyncController {
    private Application a = new Application();

    @RequestMapping("/resources/sync/general")
    public GeneralSync sync() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        GeneralSync.GeneralSyncBuilder generalSyncBuilder = new GeneralSync.GeneralSyncBuilder();

        List<CoordinateType> coordinateTypes = a.coordinateTypeDao.queryForAll();
        List<EdgeType> edgeTypes = a.edgeTypeDao.queryForAll();
        List<RoomType> roomTypes = a.roomTypeDao.queryForAll();
        List<Coordinate> coordinates = a.coordinateDao.queryForAll();
        List<Edge> edges = a.edgeDao.queryForAll();
        List<Street> streets = a.streetDao.queryForAll();
        List<Building> buildings = a.buildingDao.queryForAll();
        List<Room> rooms = a.roomDao.queryForAll();
        List<Photo> photos = a.photoDao.queryForAll();
        List<BuildingPhoto> buildingPhotos = a.buildingPhotoDao.queryForAll();
        List<RoomPhoto> roomPhotos = a.roomPhotoDao.queryForAll();
        List<BuildingFloorOverlay> buildingFloorOverlays = a.buildingFloorOverlayDao.queryForAll();
        List<EventCreator> eventCreators = a.eventCreatorDao.queryForAll();
        List<Event> events = a.eventDao.queryForAll();
        List<EventSchedule> eventSchedules = a.eventScheduleDao.queryForAll();
        List<EventCreatorAppointment> eventCreatorAppointments = a.eventCreatorAppointmentDao.queryForAll();
        List<BuildingAuxiliaryCoordinate> buildingAuxiliaryCoordinates = a.buildingAuxiliaryCoordinateDao.queryForAll();

        generalSyncBuilder.setTypes(coordinateTypes, edgeTypes, roomTypes);
        generalSyncBuilder.setMapUnits(coordinates, edges, streets, buildings, rooms, photos, buildingFloorOverlays);
        generalSyncBuilder.setEvents(eventCreators, events, eventSchedules);
        generalSyncBuilder.setAssignments(buildingPhotos, roomPhotos, eventCreatorAppointments, buildingAuxiliaryCoordinates);

        connectionSource.close();
        return generalSyncBuilder.build();
    }
}

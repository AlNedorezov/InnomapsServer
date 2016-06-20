package rest;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import db.*;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.xmlpull.v1.XmlPullParserException;
import pathfinding.JGraphTWrapper;
import pathfinding.LatLngGraphEdge;
import pathfinding.LatLngGraphVertex;
import xmlToDB.ExtendedJGraphTWrapper;
import xmlToDB.LatLngExtendedGraphVertex;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/7/16.
 */

@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class Application {

    protected final static String DATABASE_URL = "jdbc:h2:tcp://10.90.104.144:9092/test;IFEXISTS=TRUE";
    // change 'localhost' to 10.90.104.144 for debug and vice versa for deploy
    // 10.90.104.144 works only for devices connected to the IU network

    protected Dao<User, Integer> userDao;
    protected Dao<Role, Integer> roleDao;
    protected Dao<UserRole, Integer> userRoleDao;
    protected Dao<CoordinateType, Integer> coordinateTypeDao;
    protected Dao<EdgeType, Integer> edgeTypeDao;
    protected Dao<RoomType, Integer> roomTypeDao;
    protected Dao<Coordinate, Integer> coordinateDao;
    protected Dao<Edge, Integer> edgeDao;
    protected Dao<Street, Integer> streetDao;
    protected Dao<Building, Integer> buildingDao;
    protected Dao<Room, Integer> roomDao;
    protected Dao<Photo, Integer> photoDao;
    protected Dao<BuildingPhoto, Integer> buildingPhotoDao;
    protected Dao<RoomPhoto, Integer> roomPhotoDao;
    protected Dao<EventCreator, Integer> eventCreatorDao;
    protected Dao<Event, Integer> eventDao;
    protected Dao<EventSchedule, Integer> eventScheduleDao;

    @Autowired
    private MyBean myBean;

    public static void main(String[] args) throws SQLException {

        // Initial connect to the database // check that tables are created
        new Application().connectToDB();

        // Insert demo data into the database
        // new Application().insertDemoDataInTheDatabase();

        // Inserting coordinates and edges from xml to the database
        // new Application().writeCoordinatesAndEdgesFromXmlToDB();

        // Run Spring application
        SpringApplication.run(Application.class, args);
    }

    private void connectToDB() throws SQLException {
        JdbcConnectionSource connectionSource;
        // create our data source
        connectionSource = new JdbcConnectionSource(DATABASE_URL, "sa", "sa");
        // setup our database and DAOs
        setupDatabase(connectionSource, true);
    }

    /**
     * Setup our database and DAOs
     */
    public void setupDatabase(ConnectionSource connectionSource, boolean createTables) throws SQLException {

        userDao = DaoManager.createDao(connectionSource, User.class);
        roleDao = DaoManager.createDao(connectionSource, Role.class);
        userRoleDao = DaoManager.createDao(connectionSource, UserRole.class);
        coordinateTypeDao = DaoManager.createDao(connectionSource, CoordinateType.class);
        edgeTypeDao = DaoManager.createDao(connectionSource, EdgeType.class);
        roomTypeDao = DaoManager.createDao(connectionSource, RoomType.class);
        coordinateDao = DaoManager.createDao(connectionSource, Coordinate.class);
        edgeDao = DaoManager.createDao(connectionSource, Edge.class);
        streetDao = DaoManager.createDao(connectionSource, Street.class);
        buildingDao = DaoManager.createDao(connectionSource, Building.class);
        roomDao = DaoManager.createDao(connectionSource, Room.class);
        photoDao = DaoManager.createDao(connectionSource, Photo.class);
        buildingPhotoDao = DaoManager.createDao(connectionSource, BuildingPhoto.class);
        roomPhotoDao = DaoManager.createDao(connectionSource, RoomPhoto.class);
        eventCreatorDao = DaoManager.createDao(connectionSource, EventCreator.class);
        eventDao = DaoManager.createDao(connectionSource, Event.class);
        eventScheduleDao = DaoManager.createDao(connectionSource, EventSchedule.class);

        // if you need to create tables
        if (createTables) {
            // when tables are created and only then alter table fields that need to be altered
            boolean alterCoordinatesTable = false;
            boolean alterBuildingsTable = false;
            boolean alterPhotosTable = false;
            boolean alterEventsTable = false;
            boolean alterEventSchedulesTable = false;

            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, Role.class);
            TableUtils.createTableIfNotExists(connectionSource, UserRole.class);
            TableUtils.createTableIfNotExists(connectionSource, CoordinateType.class);
            TableUtils.createTableIfNotExists(connectionSource, EdgeType.class);
            TableUtils.createTableIfNotExists(connectionSource, RoomType.class);
            if(!coordinateDao.isTableExists())
                alterCoordinatesTable = true;
            TableUtils.createTableIfNotExists(connectionSource, Coordinate.class);
            if(alterCoordinatesTable)
                coordinateDao.updateRaw("ALTER TABLE COORDINATES ALTER COLUMN DESCRIPTION VARCHAR(2500)");
            TableUtils.createTableIfNotExists(connectionSource, Edge.class);
            TableUtils.createTableIfNotExists(connectionSource, Street.class);
            if(!buildingDao.isTableExists())
                alterBuildingsTable = true;
            TableUtils.createTableIfNotExists(connectionSource, Building.class);
            if(alterBuildingsTable)
                buildingDao.updateRaw("ALTER TABLE BUILDINGS ALTER COLUMN DESCRIPTION VARCHAR(2500)");
            TableUtils.createTableIfNotExists(connectionSource, Room.class);
            if(!photoDao.isTableExists())
                alterPhotosTable = true;
            TableUtils.createTableIfNotExists(connectionSource, Photo.class);
            if(alterPhotosTable)
                photoDao.updateRaw("ALTER TABLE PHOTOS ALTER COLUMN URL VARCHAR(500)");
            TableUtils.createTableIfNotExists(connectionSource, BuildingPhoto.class);
            TableUtils.createTableIfNotExists(connectionSource, RoomPhoto.class);
            TableUtils.createTableIfNotExists(connectionSource, EventCreator.class);
            if(!eventDao.isTableExists())
                alterEventsTable = true;
            TableUtils.createTableIfNotExists(connectionSource, Event.class);
            if(alterEventsTable)
                eventDao.updateRaw("ALTER TABLE EVENTS ALTER COLUMN DESCRIPTION VARCHAR(2500)");
            if(!eventScheduleDao.isTableExists())
                alterEventSchedulesTable = true;
            TableUtils.createTableIfNotExists(connectionSource, EventSchedule.class);
            if(alterEventSchedulesTable)
                eventScheduleDao.updateRaw("ALTER TABLE EVENT_SCHEDULES ALTER COLUMN COMMENT VARCHAR(2500)");
        }
    }

    /**
     * Inserting demo data to the database
     */
    private void insertDemoDataInTheDatabase() throws SQLException {
        // create our data source
        ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL, "sa", "sa");
        setupDatabase(connectionSource, false);

        int id;
        double latitude;
        double longitude;
        int floor;
        int type_id;
        String name;
        String description;

        id=0;
        latitude = 55.75310771911266;
        longitude = 48.743609078228474;
        floor = 1;
        type_id = 1;
        name = "Classroom 1";
        description = "";

        coordinateDao.create(new Coordinate(id, latitude, longitude, floor, type_id, name, description));
        connectionSource.close();
    }

    /**
     * Inserting coordinates from xml to the database
     */
    private void writeCoordinatesAndEdgesFromXmlToDB() throws SQLException {
        LatLngExtendedGraphVertex[] coordinatesList;
        LatLngGraphEdge[] edgesList;
        ExtendedJGraphTWrapper jGraphTWrapper = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("9.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            try {
                jGraphTWrapper = new ExtendedJGraphTWrapper();
                jGraphTWrapper.importGraphML(inputStream);
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            // create our data source
            ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL, "sa", "sa");
            setupDatabase(connectionSource, false);

            coordinatesList = jGraphTWrapper.getVertices();

            String IU_description = "Specializing in the field " +
                    "of modern information technologies, Innopolis University is not only one of Russia’s youngest universities," +
                    " but also the new city’s intellectual center.\n" +
                    "The teaching staff consists of leading Russian and foreign IT specialists and robotic science.\n" +
                    "Driven by the demands of both business and industry, the educational programs are committed to producing" +
                    " a high-quality stream of professionals for companies located in Innopolis.";
            // Coordinate for Innopolis University are taken from Google Maps
            // Coordinate_type=2 (DAFAULT)
            coordinateDao.create(new Coordinate(0, 55.7541793, 48.744085, 1, 2, "Innopolis University", IU_description));
            streetDao.create(new Street(1, "Universitetskaya"));
            buildingDao.create(new Building(1, String.valueOf(1), null, IU_description, 1, 1));

            roomTypeDao.create(new RoomType(1, "ROOM"));
            roomTypeDao.create(new RoomType(2, "FOOD"));
            roomTypeDao.create(new RoomType(3, "WC"));
            roomTypeDao.create(new RoomType(4, "CLINIC"));
            roomTypeDao.create(new RoomType(5, "READING"));
            roomTypeDao.create(new RoomType(6, "DOOR"));
            roomTypeDao.create(new RoomType(7, "LIBRARY"));

            for(int i=0; i<coordinatesList.length; i++) {
                if(coordinatesList[i] != null) {

                    int coordinate_type_id;
                    QueryBuilder<CoordinateType, Integer> qBuilder = coordinateTypeDao.queryBuilder();
                    PreparedQuery<CoordinateType> preparedQuery;

                    qBuilder.where().eq("name", coordinatesList[i].getGraphVertexType().toString());
                    preparedQuery = qBuilder.prepare();
                    qBuilder.reset();

                    if(qBuilder.countOf() > 0 && coordinateTypeDao.query(preparedQuery).size() > 0) {
                            coordinate_type_id = coordinateTypeDao.query(preparedQuery).get(0).getId();
                    }
                    else {
                        coordinateTypeDao.create(new CoordinateType(0, coordinatesList[i].getGraphVertexType().toString()));
                        qBuilder.reset();
                        qBuilder.orderBy("id", false); // false for descending order
                        qBuilder.limit(1);
                        coordinate_type_id = qBuilder.query().get(0).getId();
                    }

                    coordinateDao.create(new Coordinate(0, coordinatesList[i].getVertex().getLatitude(), coordinatesList[i].getVertex().getLongitude(),
                                         (int) Math.floor(coordinatesList[i].getVertexId() / 1000), coordinate_type_id,
                                            coordinatesList[i].getName(), coordinatesList[i].getDescription()));

                    // ROOM, FOOD, WC, CLINIC, READING, DOOR, LIBRARY and EASTER_EGG
                    if(coordinatesList[i].getGraphVertexType().toString().equals("ROOM") || coordinatesList[i].getGraphVertexType().toString().equals("FOOD") ||
                            coordinatesList[i].getGraphVertexType().toString().equals("WC") || coordinatesList[i].getGraphVertexType().toString().equals("CLINIC") ||
                            coordinatesList[i].getGraphVertexType().toString().equals("READING") || coordinatesList[i].getGraphVertexType().toString().equals("DOOR") ||
                            coordinatesList[i].getGraphVertexType().toString().equals("LIBRARY")) {
                        int coordinate_id;
                        QueryBuilder<Coordinate, Integer> coordinateQBuilder = coordinateDao.queryBuilder();
                        coordinateQBuilder.orderBy("id", false); // false for descending order
                        coordinateQBuilder.limit(1);
                        coordinate_id = coordinateQBuilder.query().get(0).getId();
                        switch (coordinatesList[i].getGraphVertexType().toString()) {
                            case "ROOM":
                                roomDao.create(new Room(0, coordinatesList[i].getNumber(), 1, coordinate_id, 1));
                                break;
                            case "FOOD":
                                roomDao.create(new Room(0, coordinatesList[i].getNumber(), 1, coordinate_id, 2));
                                break;
                            case "WC":
                                roomDao.create(new Room(0, coordinatesList[i].getNumber(), 1, coordinate_id, 3));
                                break;
                            case "CLINIC":
                                roomDao.create(new Room(0, coordinatesList[i].getNumber(), 1, coordinate_id, 4));
                                break;
                            case "READING":
                                roomDao.create(new Room(0, coordinatesList[i].getNumber(), 1, coordinate_id, 5));
                                break;
                            case "DOOR":
                                roomDao.create(new Room(0, coordinatesList[i].getNumber(), 1, coordinate_id, 6));
                                break;
                            case "LIBRARY":
                                roomDao.create(new Room(0, coordinatesList[i].getNumber(), 1, coordinate_id, 7));
                                break;
                        }
                    }
                }
            }

            edgesList = jGraphTWrapper.getEdges();

            edgeTypeDao.create(new EdgeType(1, "DEFAULT"));
            edgeTypeDao.create(new EdgeType(2, "STAIRS"));

            for(int i=0; i<edgesList.length; i++) {
                if(edgesList[i] != null) {
                    int source_id, target_id;
                    QueryBuilder<Coordinate, Integer> qBuilder = coordinateDao.queryBuilder();
                    qBuilder.where().eq("latitude", edgesList[i].getV1().getVertex().getLatitude()).and().eq("longitude", edgesList[i].getV1().getVertex().getLongitude());
                    source_id = qBuilder.query().get(0).getId();
                    qBuilder.reset();
                    qBuilder.where().eq("latitude", edgesList[i].getV2().getVertex().getLatitude()).and().eq("longitude", edgesList[i].getV2().getVertex().getLongitude());
                    target_id = qBuilder.query().get(0).getId();

                    switch (edgesList[i].getGraphEdgeType().toString()) {
                        case "DEFAULT":
                            edgeDao.create(new Edge(0, 1, source_id, target_id));
                            break;
                        case "STAIRS":
                            edgeDao.create(new Edge(0, 2, source_id, target_id));
                    }
                }
            }

            connectionSource.close();
        }
    }
}

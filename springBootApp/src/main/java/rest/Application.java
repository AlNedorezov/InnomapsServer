package rest;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import db.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by alnedorezov on 6/7/16.
 */

@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class Application {

    public final static String DATABASE_URL = "jdbc:h2:tcp://10.90.104.144:9092/test;IFEXISTS=TRUE";
    // change 'localhost' to 10.90.104.144 for debug and vice versa for deploy
    // 10.90.104.144 works only for devices connected to the IU network

    public Dao<Coordinate, Integer> coordinateDao;

    @Autowired
    private MyBean myBean;

    public static void main(String[] args) throws Exception {

        // Initial connect to the database // for demo values
        new Application().connectToDB();

        // Insert demo data into the database
        // new Application().insertDemoDataInTheDatabase();

        // Run Spring application
        SpringApplication.run(Application.class, args);
    }

    private void connectToDB() throws Exception {
        JdbcConnectionSource connectionSource = null;
        try {
            // create our data source
            connectionSource = new JdbcConnectionSource(DATABASE_URL, "sa", "sa");
            // setup our database and DAOs
            setupDatabase(connectionSource, true);
        } finally {
            // destroy the data source which should close underlying connections
            if (connectionSource != null) {
                //connectionSource.close();
            }
        }
    }

    /**
     * Setup our database and DAOs
     */
    public void setupDatabase(ConnectionSource connectionSource, boolean createTables) throws Exception {

        coordinateDao = DaoManager.createDao(connectionSource, Coordinate.class);

        // if you need to create tables
        if (createTables) {
            TableUtils.createTableIfNotExists(connectionSource, Coordinate.class);
        }
    }

    /**
     * Setup our database and DAOs
     */
    private void insertDemoDataInTheDatabase() throws Exception {
        // create our data source
        ConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL, "sa", "sa");
        setupDatabase(connectionSource, false);

        int id;
        double latitude;
        double longitude;
        int floor;
        String type;
        String name;
        String description;

        id=0;
        latitude = 55.75310771911266;
        longitude = 48.743609078228474;
        floor = 1;
        type = "room";
        name = "Classroom 1";
        description = "";

        coordinateDao.create(new Coordinate(id, latitude, longitude, floor, type, name, description));
        connectionSource.close();
    }
}

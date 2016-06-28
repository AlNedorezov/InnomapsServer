package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/23/16.
 */
public class CommonFunctions {
    protected static Application a = new Application();

    static String checkIfCoordinateExist(int coordinate_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (coordinate_id < 0 || !a.coordinateDao.idExists(coordinate_id))
            errorMessage += "Coordinate with id=" + coordinate_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }

    static String checkIfBuildingExist(int building_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (building_id < 0 || !a.buildingDao.idExists(building_id))
            errorMessage += "Building with id=" + building_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }

    static String checkIfPhotoExist(int photo_id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.DATABASE_URL,
                Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);
        a.setupDatabase(connectionSource, false);
        String errorMessage = "";

        if (photo_id < 0 || !a.photoDao.idExists(photo_id))
            errorMessage += "Photo with id=" + photo_id + " does not exist. ";

        connectionSource.close();
        return errorMessage;
    }
}

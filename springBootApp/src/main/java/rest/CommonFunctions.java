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

    public static class EventUpdateData {
        private String name;
        private String description;
        private Integer creator_id;
        private String link;
        private String errorMessage;

        public EventUpdateData(String name, String description, Integer creator_id, String link) {
            this.name = name;
            this.description = description;
            this.creator_id = creator_id;
            this.link = link;
            this.errorMessage = "";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getCreator_id() {
            return creator_id;
        }

        public void setCreator_id(Integer creator_id) {
            this.creator_id = creator_id;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}

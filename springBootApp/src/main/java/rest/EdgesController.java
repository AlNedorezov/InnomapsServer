package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import db.Edge;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rest.clientServerCommunicationClasses.EdgesObject;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/21/16.
 */

@RestController
public class EdgesController {
    Application a = new Application();

    @RequestMapping("/resources/edges")
    public EdgesObject edges() throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);
        EdgesObject edges1 = new EdgesObject(a.edgeDao.queryForAll());
        connectionSource.close();
        return edges1;
    }

    @RequestMapping("/resources/edge")
    public Edge edge(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);
        Edge edge1 = a.edgeDao.queryForId(id);
        connectionSource.close();
        return edge1;
    }
}
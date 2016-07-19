package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 7/19/16.
 */

@RestController
public class FingerprintingController {
    private Application a = new Application();

    @RequestMapping(value = "/resources/fingerprinting/a", method = RequestMethod.POST)
    public String logs() throws SQLException {

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Application.getDatabaseUrl(),
                Application.getDatabaseUsername(), Application.getDatabasePassword());
        a.setupDatabase(connectionSource, false);

        connectionSource.close();
        return "";
    }
}

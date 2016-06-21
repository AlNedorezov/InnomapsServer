package rest;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import db.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 6/20/16.
 */

@RestController
public class UsersController {
    Application a = new Application();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/resources/user")
    public User user(@RequestParam(value = "id", defaultValue = "-1") int id) throws SQLException {
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);
        User user1 = a.userDao.queryForId(id);
        connectionSource.close();
        return user1;
    }

    @RequestMapping(value = "/resources/user", method = RequestMethod.POST)
    public String logs(@RequestParam(value = "id", defaultValue = "-1") int id, @RequestParam(value = "email", defaultValue = "") String email,
                       @RequestParam(value = "name", defaultValue = "") String name, @RequestParam(value = "password", defaultValue = "") String password,
                       @RequestParam(value = "activated", defaultValue = "-2") int activated, @RequestParam(value = "activationcode", defaultValue = "") String activation_code,
                       @RequestParam(value = "created", defaultValue = "-3") String createdStr,
                       @RequestParam(value = "deleted", defaultValue = "null") String strDeleted) throws SQLException, java.text.ParseException {
        boolean deleted = false;
        if (strDeleted.equals("true"))
            deleted = true;

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(a.DATABASE_URL, "sa", "sa");
        a.setupDatabase(connectionSource, false);

        if (deleted) {
            // Deleting a user
            System.out.println("Received POST request: delete user with id=" + id);
            if (id == -1) {
                connectionSource.close();
                return "-1. Wrong parameters.\n";
            } else if (!a.userDao.idExists(id)) {
                connectionSource.close();
                return "-1. There is no such user.\n";
            } else {
                User user1 = a.userDao.queryForId(id);
                a.userDao.update(new User(id, user1.getEmail(), user1.getName(), user1.getPassword(), user1.getActivated(), user1.getActivation_code(), user1.getCreated(), true));
                //For permanent deletion use: a.userDao.deleteById(id);
                connectionSource.close();
                return "0. User with id=" + id + " was successfully deleted.\n";
            }
        } else {
            if (!a.userDao.idExists(id)) {
                // Creating a user
                if (createdStr.equals("-3") || password.equals("")) {
                    connectionSource.close();
                    return "-1. Wrong parameters.\n";
                } else {
                    System.out.println("Received POST request: create new user");
                    a.userDao.create(new User(id, email.toLowerCase(), name, passwordEncoder.encode(password), activated, activation_code, createdStr, deleted));
                    QueryBuilder<User, Integer> qBuilder = a.userDao.queryBuilder();
                    qBuilder.orderBy("id", false); // false for descending order
                    qBuilder.limit(1);
                    User createdUser = a.userDao.queryForId(qBuilder.query().get(0).getId());
                    System.out.println(createdUser.getId() + " | " + createdUser.getEmail() + " | " +
                            createdUser.getName() + " | " + createdUser.getPassword() + " | " +
                            createdUser.getActivated() + " | " + createdUser.getActivation_code() + " | " +
                            createdUser.getCreated() + " | " + createdUser.getDeleted());
                    connectionSource.close();
                    return "0. User with id=" + createdUser.getId() + " was successfully created.\n";
                }
            } else {
                // Updating a user
                System.out.println("Received POST request: update user with id=" + id);
                UserUpdate updUser = checkDataForUpdates(new UserUpdate(email, name, password, activated, activation_code, createdStr), a.userDao.queryForId(id));
                boolean updDeleted = checkStrDeletedValue(strDeleted, a.userDao.queryForId(id));
                a.userDao.update(new User(id, updUser.getEmail().toLowerCase(), updUser.getName(), updUser.getPassword(), updUser.getActivated(), updUser.getActivation_code(), updUser.getCreated(), updDeleted));
                connectionSource.close();
                return "0. User with id=" + id + " was successfully updated.\n";
            }
        }
    }

    private UserUpdate checkDataForUpdates(UserUpdate checkedUserData, User userInDatabase) {
        if (checkedUserData.getEmail().equals(""))
            checkedUserData.setEmail(userInDatabase.getEmail());

        if (checkedUserData.getName().equals(""))
            checkedUserData.setName(userInDatabase.getName());

        if (checkedUserData.getPassword().equals(""))
            checkedUserData.setPassword(userInDatabase.getPassword());
        else
            checkedUserData.setPassword(passwordEncoder.encode(checkedUserData.getPassword()));

        if (checkedUserData.getActivated() < 0)
            checkedUserData.setActivated(userInDatabase.getActivated());

        if (checkedUserData.getActivation_code().equals(""))
            checkedUserData.setActivation_code(userInDatabase.getActivation_code());

        if (checkedUserData.getCreated().equals("-3"))
            checkedUserData.setCreated(userInDatabase.getCreated());

        return checkedUserData;
    }

    private boolean checkStrDeletedValue(String strDeleted, User userInDatabase) {
        if (strDeleted.equals("null"))
            return userInDatabase.getDeleted();
        else if (strDeleted.equals("true"))
            return true;
        else
            return false;
    }

    private class UserUpdate {
        private String email;
        private String name;
        private String password;
        private int activated;
        private String activation_code;
        private String created;

        public UserUpdate(String email, String name, String password, int activated, String activation_code, String created) {
            this.email = email;
            this.name = name;
            this.password = password;
            this.activated = activated;
            this.activation_code = activation_code;
            this.created = created;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getActivated() {
            return activated;
        }

        public void setActivated(int activated) {
            this.activated = activated;
        }

        public String getActivation_code() {
            return activation_code;
        }

        public void setActivation_code(String activation_code) {
            this.activation_code = activation_code;
        }

        public String getCreated() {
            return created;
        }

        public void setCreated(String created) {
            this.created = created;
        }
    }
}

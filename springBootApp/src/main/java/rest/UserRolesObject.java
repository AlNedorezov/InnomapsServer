package rest;

import db.UserRole;
import java.util.List;

/**
 * Created by alnedorezov on 6/20/16.
 */
public class UserRolesObject {
    private List<UserRole> userRoles;

    public UserRolesObject(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
    }

    public void setUserRole(int index, UserRole userRole) {
        this.userRoles.set(index, userRole);
    }

    public void getUserRole(int index) {
        this.userRoles.get(index);
    }

    public void removeUserRole(int index) {
        this.userRoles.remove(index);
    }

    public List<UserRole> getUserroles() {
        return userRoles;
    }
}
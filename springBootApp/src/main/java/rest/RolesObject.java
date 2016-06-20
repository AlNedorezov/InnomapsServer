package rest;

import db.Role;
import java.util.List;

/**
 * Created by alnedorezov on 6/20/16.
 */
public class RolesObject {
    private List<Role> roles;

    public RolesObject(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void setRole(int index, Role role) {
        this.roles.set(index, role);
    }

    public void getRole(int index) {
        this.roles.get(index);
    }

    public void removeRole(int index) {
        this.roles.remove(index);
    }

    public List<Role> getRoles() {
        return roles;
    }
}
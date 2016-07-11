package rest.clientservercommunicationclasses;

import db.Street;

import java.util.List;

/**
 * Created by alnedorezov on 6/23/16.
 */
public class StreetsObject {
    private List<Street> streets;

    public StreetsObject(List<Street> streets) {
        this.streets = streets;
    }

    public void addStreet(Street street) {
        this.streets.add(street);
    }

    public void setStreet(int index, Street street) {
        this.streets.set(index, street);
    }

    public void getStreet(int index) {
        this.streets.get(index);
    }

    public void removeStreet(int index) {
        this.streets.remove(index);
    }

    public List<Street> getStreets() {
        return streets;
    }
}
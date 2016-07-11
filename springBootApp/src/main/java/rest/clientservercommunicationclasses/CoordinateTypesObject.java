package rest.clientservercommunicationclasses;

import db.CoordinateType;

import java.util.List;

/**
 * Created by alnedorezov on 6/21/16.
 */

public class CoordinateTypesObject {
    private List<CoordinateType> coordinateTypes;

    public CoordinateTypesObject(List<CoordinateType> coordinateTypes) {
        this.coordinateTypes = coordinateTypes;
    }

    public void addCoordinateType(CoordinateType coordinateType) {
        this.coordinateTypes.add(coordinateType);
    }

    public void setCoordinateType(int index, CoordinateType coordinateType) {
        this.coordinateTypes.set(index, coordinateType);
    }

    public void getCoordinateType(int index) {
        this.coordinateTypes.get(index);
    }

    public void removeCoordinateType(int index) {
        this.coordinateTypes.remove(index);
    }

    public List<CoordinateType> getCoordinatetypes() {
        return coordinateTypes;
    }
}
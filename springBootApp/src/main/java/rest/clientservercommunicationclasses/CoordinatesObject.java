package rest.clientservercommunicationclasses;

import db.Coordinate;

import java.util.List;

/**
 * Created by alnedorezov on 6/21/16.
 */
public class CoordinatesObject {
    private List<Coordinate> coordinates;

    public CoordinatesObject(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public void addCoordinate(Coordinate coordinate) {
        this.coordinates.add(coordinate);
    }

    public void setCoordinate(int index, Coordinate coordinate) {
        this.coordinates.set(index, coordinate);
    }

    public void getCoordinate(int index) {
        this.coordinates.get(index);
    }

    public void removeCoordinate(int index) {
        this.coordinates.remove(index);
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }
}
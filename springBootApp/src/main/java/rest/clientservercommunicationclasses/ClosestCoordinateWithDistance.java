package rest.clientservercommunicationclasses;

import pathfinding.LatLngFlr;

/**
 * Created by alnedorezov on 6/15/16.
 */
public class ClosestCoordinateWithDistance {
    private LatLngFlr coordinate;
    private double distance;

    public ClosestCoordinateWithDistance(LatLngFlr coordinate, double distance) {
        this.coordinate = coordinate;
        this.distance = distance;
    }

    // For deserialization with Jackson
    public ClosestCoordinateWithDistance() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public LatLngFlr getCoordinate() {
        return coordinate;
    }

    public double getDistance() {
        return distance;
    }
}

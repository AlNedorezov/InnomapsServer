package pathfinding;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by alnedorezov on 6/12/16.
 */
public class LatLngFlr extends LatLng {

    private int floor;

    public LatLngFlr(double latitude, double longitude, int floor) {
        super(latitude, longitude);
        this.floor = floor;
    }

    // For deserialization with Jackson
    public LatLngFlr() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getFloor() {
        return floor;
    }

    @JsonIgnore
    public LatLng getLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + floor;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof LatLngFlr)) {
            return false;
        } else {
            LatLngFlr var2 = (LatLngFlr) o;
            return Double.doubleToLongBits(getLatitude()) == Double.doubleToLongBits(var2.getLatitude()) &&
                    Double.doubleToLongBits(getLongitude()) == Double.doubleToLongBits(var2.getLongitude()) &&
                    getFloor() == var2.getFloor();
        }
    }

    @Override
    public String toString() {
        return "coordinates: (" + getLatitude() + "," + getLongitude() + "," + getFloor() + ")";
    }
}

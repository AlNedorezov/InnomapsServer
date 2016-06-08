package pathfinding;

/**
 * Created by alnedorezov on 6/7/16.
 */
public class LatLng {

    public final double latitude;
    public final double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int hashCode() {
        boolean var1 = true;
        byte var2 = 1;
        long var3 = Double.doubleToLongBits(this.latitude);
        int var5 = 31 * var2 + (int) (var3 ^ var3 >>> 32);
        var3 = Double.doubleToLongBits(this.longitude);
        var5 = 31 * var5 + (int) (var3 ^ var3 >>> 32);
        return var5;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof LatLng)) {
            return false;
        } else {
            LatLng var2 = (LatLng) o;
            return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(var2.latitude) && Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(var2.longitude);
        }
    }

    public String toString() {
        return "coordinates: (" + this.latitude + "," + this.longitude + ")";
    }
}

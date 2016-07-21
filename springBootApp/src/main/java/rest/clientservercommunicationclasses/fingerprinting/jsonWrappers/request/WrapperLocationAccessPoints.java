package rest.clientservercommunicationclasses.fingerprinting.jsonWrappers.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by Ziyoiddin on 20-Jul-16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WrapperLocationAccessPoints {

    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("accessPoints")
    private ArrayList<WrapperAccessPoints> accessPoints;
    @JsonProperty("floor")
    private Integer floor;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<WrapperAccessPoints> getAccessPoints() {
        return accessPoints;
    }

    public void setAccessPoints(ArrayList<WrapperAccessPoints> accessPoints) {
        this.accessPoints = accessPoints;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }
}

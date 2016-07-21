package rest.clientservercommunicationclasses.fingerprinting.jsonWrappers.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Ziyoiddin on 20-Jul-16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WrapperAccessPoints {

    @JsonProperty("BSSID")
    private String bssid;
    @JsonProperty("avgLevel")
    private Double avgLevel;

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public Double getAvgLevel() {
        return avgLevel;
    }

    public void setAvgLevel(Double avgLevel) {
        this.avgLevel = avgLevel;
    }
}

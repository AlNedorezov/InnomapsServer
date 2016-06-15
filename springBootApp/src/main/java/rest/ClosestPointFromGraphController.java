package rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xmlpull.v1.XmlPullParserException;
import pathfinding.JGraphTWrapper;
import pathfinding.LatLngFlr;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/15/16.
 */

@RestController
public class ClosestPointFromGraphController {

    @RequestMapping("/resources/closestPointFromGraph")
    public LatLngFlr findClosestPointFromGraph(@RequestParam(value = "latitude", defaultValue = "0") double latitude,
                                               @RequestParam(value = "longitude", defaultValue = "0") double longitude,
                                               @RequestParam(value = "floor", defaultValue = "1") int floor) throws IOException {

        // This data will be written in the log on the server
        Date currentDate = new Date();
        System.out.println("Received POST request for finding closest point from graph to the received one on " + currentDate);
        System.out.println("Coordinates of the received point:");
        System.out.println("latitude:   " + latitude);
        System.out.println("longitude:  " + longitude);
        System.out.println("floor:  " + floor);
        LatLngFlr receivedCoordinates = new LatLngFlr(latitude, longitude, floor);
        JGraphTWrapper jGraphTWrapper;

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("9.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (inputStream == null) {
            return null;
        }
        try {
            jGraphTWrapper = new JGraphTWrapper();
            jGraphTWrapper.importGraphML(inputStream);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            return null;
        }

        if (jGraphTWrapper.graphContainsVertexWithCoordinates(receivedCoordinates))
            return receivedCoordinates;
        else
            return jGraphTWrapper.findClosestCoordinateToGiven(receivedCoordinates);
    }
}

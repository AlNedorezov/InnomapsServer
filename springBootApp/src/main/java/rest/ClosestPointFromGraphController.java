package rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xmlpull.v1.XmlPullParserException;
import pathfinding.JGraphTWrapper;
import pathfinding.LatLngFlr;
import rest.clientServerCommunicationClasses.ClosestCoordinateWithDistance;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/15/16.
 */

@RestController
public class ClosestPointFromGraphController {

    @RequestMapping("/resources/closestPointFromGraph")
    public ClosestCoordinateWithDistance findClosestPointFromGraph(@RequestParam(value = "latitude", defaultValue = "0") double latitude,
                                                                   @RequestParam(value = "longitude", defaultValue = "0") double longitude,
                                                                   @RequestParam(value = "floor", defaultValue = "1") int floor) throws SQLException {

        // This data will be written in the log on the server
        Date currentDate = new Date();
        System.out.println("Received POST request for finding closest point from graph to the received one on " + currentDate);
        System.out.println("Coordinates of the received point:");
        System.out.println("latitude:   " + latitude);
        System.out.println("longitude:  " + longitude);
        System.out.println("floor:  " + floor);
        LatLngFlr receivedCoordinates = new LatLngFlr(latitude, longitude, floor);

        JGraphTWrapper jGraphTWrapper = new JGraphTWrapper();
        jGraphTWrapper.importGraphFromDB(Application.DATABASE_URL, Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);

        if (jGraphTWrapper.graphContainsVertexWithCoordinates(receivedCoordinates))
            return new ClosestCoordinateWithDistance(receivedCoordinates, 0);
        else
            return jGraphTWrapper.findClosestCoordinateToGiven(receivedCoordinates);
    }
}

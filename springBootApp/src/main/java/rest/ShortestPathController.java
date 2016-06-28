package rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pathfinding.JGraphTWrapper;
import pathfinding.LatLngFlr;
import rest.clientServerCommunicationClasses.VerticesListObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/7/16.
 */

@RestController
public class ShortestPathController {

    @RequestMapping(value = "/resources/shortestPath", method = RequestMethod.POST)
    public VerticesListObject findShortestPath(@RequestParam(value = "vertexOneLatitude", defaultValue = "0") double vertexOneLatitude,
                                               @RequestParam(value = "vertexOneLongitude", defaultValue = "0") double vertexOneLongitude,
                                               @RequestParam(value = "vertexOneFloor", defaultValue = "1") int vertexOneFloor,
                                               @RequestParam(value = "vertexTwoLatitude", defaultValue = "0") double vertexTwoLatitude,
                                               @RequestParam(value = "vertexTwoLongitude", defaultValue = "0") double vertexTwoLongitude,
                                               @RequestParam(value = "vertexTwoFloor", defaultValue = "1") int vertexTwoFloor) throws SQLException {
        // This data will be written in the log on the server
        Date currentDate = new Date();
        System.out.println("Received POST request for building a shortest path on " + currentDate);
        System.out.println("with the following start and finish coordinates:");
        System.out.println("start point latitude:   " + vertexOneLatitude);
        System.out.println("start point longitude:  " + vertexOneLongitude);
        System.out.println("start point floor:  " + vertexOneFloor);
        System.out.println("finish point latitude:  " + vertexTwoLatitude);
        System.out.println("finish point longitude: " + vertexTwoLongitude);
        System.out.println("finish point floor:  " + vertexTwoFloor);
        LatLngFlr vertexOne = new LatLngFlr(vertexOneLatitude, vertexOneLongitude, vertexOneFloor);
        LatLngFlr vertexTwo = new LatLngFlr(vertexTwoLatitude, vertexTwoLongitude, vertexTwoFloor);

        JGraphTWrapper jGraphTWrapper = new JGraphTWrapper();
        jGraphTWrapper.importGraphFromDB(Application.DATABASE_URL, Application.DATABASE_USERNAME, Application.DATABASE_PASSWORD);

        return new VerticesListObject(jGraphTWrapper.shortestPath(vertexOne, vertexTwo));
    }
}

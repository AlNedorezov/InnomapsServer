package rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xmlpull.v1.XmlPullParserException;
import pathfinding.JGraphTWrapper;
import pathfinding.LatLng;
import pathfinding.LatLngFlr;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/7/16.
 */

@RestController
public class shortestPathController {

    @RequestMapping(value = "/resources/shortestPath", method = RequestMethod.POST)
    public VerticesListObject findShortestPath(@RequestParam(value = "vertexOneLatitude", defaultValue = "0") double vertexOneLatitude,
                                               @RequestParam(value = "vertexOneLongitude", defaultValue = "0") double vertexOneLongitude,
                                               @RequestParam(value = "vertexOneFloor", defaultValue = "1") int vertexOneFloor,
                                               @RequestParam(value = "vertexTwoLatitude", defaultValue = "0") double vertexTwoLatitude,
                                               @RequestParam(value = "vertexTwoLongitude", defaultValue = "0") double vertexTwoLongitude) throws SQLException {
        // This data will be written in the log on the server
        Date currentDate = new Date();
        System.out.println("Received POST request for building a shortest path on " + currentDate );
        System.out.println("with the following start and finish coordinates:");
        System.out.println("start point latitude:   " + vertexOneLatitude);
        System.out.println("start point longitude:  " + vertexOneLongitude);
        System.out.println("start point floor:  " + vertexOneFloor);
        System.out.println("finish point latitude:  " + vertexTwoLatitude);
        System.out.println("finish point longitude: " + vertexTwoLongitude);
        LatLngFlr vertexOne = new LatLngFlr(vertexOneLatitude, vertexOneLongitude, vertexOneFloor);
        LatLng vertexTwo = new LatLng(vertexTwoLatitude, vertexTwoLongitude);
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

        return new VerticesListObject(jGraphTWrapper.shortestPath(vertexOne, vertexTwo));
    }
}

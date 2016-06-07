package rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xmlpull.v1.XmlPullParserException;
import pathfinding.LatLng;
import pathfinding.JGraphTWrapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by alnedorezov on 6/7/16.
 */

@RestController
public class shortestPathController {

    @RequestMapping(value = "/resources/shortestPath", method = RequestMethod.POST)
    public VerticesListObject findShortestPath(@RequestParam(value = "vertexOneLatitude", defaultValue = "0") double vertexOneLatitude,
                                               @RequestParam(value = "vertexOneLongitude", defaultValue = "0") double vertexOneLongitude,
                                               @RequestParam(value = "vertexTwoLatitude", defaultValue = "0") double vertexTwoLatitude,
                                               @RequestParam(value = "vertexTwoLongitude", defaultValue = "0") double vertexTwoLongitude) throws Exception {

        System.out.println("vertexOneLatitude:  " + vertexOneLatitude);
        System.out.println("vertexOneLongitude: " + vertexOneLongitude);
        System.out.println("vertexTwoLatitude:  " + vertexTwoLatitude);
        System.out.println("vertexTwoLongitude: " + vertexTwoLongitude);
        LatLng vertexOne = new LatLng(vertexOneLatitude, vertexOneLongitude);
        LatLng vertexTwo = new LatLng(vertexTwoLatitude, vertexTwoLongitude);
        JGraphTWrapper jGraphTWrapper;

        FileInputStream inputStream = null;
        try {
            inputStream =  new FileInputStream("9.xml");
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

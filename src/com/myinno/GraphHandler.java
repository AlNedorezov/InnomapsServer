package com.myinno;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * All request about graph related stuff are processing here.
 * Created by luckychess on 3/10/16.
 */
public class GraphHandler implements HttpHandler {

    public static final String baseGraphUrl = "/innomaps/graphml/";
    private GraphCache floorCache = new GraphCache();

    @Override
    public void handle(HttpExchange httpExchange) {
        System.out.println("Got request " + httpExchange.getRequestURI().toString());
        String path = httpExchange.getRequestURI().getPath();
        String command = path.substring(path.indexOf(baseGraphUrl) + baseGraphUrl.length());

        String result = "";
        if (command.equals("md5")) {
            result = processMD5(httpExchange.getRequestURI().getQuery());
        }
        else if (command.equals("loadmap")) {
            result = processLoadMap(httpExchange.getRequestURI().getQuery());
        }
        response(httpExchange, result);
    }

    private void response(HttpExchange httpExchange, String text) {
        try {
            httpExchange.sendResponseHeaders(200, 0);
            try (BufferedOutputStream out = new BufferedOutputStream(httpExchange.getResponseBody())) {
                out.write(text.getBytes());
                out.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String processMD5(String toProcess) {
        if (!checkFloorRequestString(toProcess)) {
            return "";
        }
        String[] pair = toProcess.split("=");
        int floor = Integer.parseInt(pair[1]);
        MessageDigest md5Digest;
        try {
            md5Digest = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        byte[] md5 = floorCache.getMd5FloorData(floor);
        if (md5 != null) {
            System.out.println("MD5 cache hit for floor " + floor);
            return Arrays.toString(md5);
        }

        System.out.println("MD5 cache miss for floor " + floor);
        byte[] data;
        try {
            data = readBytesFromFile("res/floor/" + floor + ".xml");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (data.length == 0) {
            return "";
        }

        md5 = md5Digest.digest(data);
        floorCache.addNewMd5Floor(floor, md5);
        return Arrays.toString(md5);
    }

    private String processLoadMap(String toProcess) {
        if (!checkFloorRequestString(toProcess)) {
            return "";
        }
        String[] pair = toProcess.split("=");
        int floor = Integer.parseInt(pair[1]);
        String result = floorCache.getFloorData(floor);
        if (result != null) {
            System.out.println("Cache hit for floor " + floor);
            return result;
        }
        System.out.println("Cache miss for floor " + floor);
        List<String> lines;
        try {
            lines = readLinesFromFile("res/floor/" + floor + ".xml");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (lines.isEmpty()) {
            return "";
        }
        StringBuilder buildResult = new StringBuilder();
        lines.forEach(buildResult::append);
        result = buildResult.toString();
        floorCache.addNewFloor(floor, result);
        return result;
    }

    private boolean checkFloorRequestString(String toCheck) {
        String pair[] = toCheck.split("=");
        return pair.length == 2 && pair[0].equals("floor") && pair[1].matches("^[0-9]{1,9}$");
    }

    private synchronized List<String> readLinesFromFile(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename), Charset.defaultCharset());
    }

    private synchronized byte[] readBytesFromFile(String filename) throws IOException {
        return Files.readAllBytes(Paths.get(filename));
    }
}

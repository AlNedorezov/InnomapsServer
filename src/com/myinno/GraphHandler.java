package com.myinno;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * All request about graph related stuff are processing here.
 * Created by luckychess on 3/10/16.
 */
public class GraphHandler implements HttpHandler {

    public static final String baseGraphUrl = "/innomaps/graphml/";

    @Override
    public void handle(HttpExchange httpExchange) {
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
            httpExchange.sendResponseHeaders(200, text.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(text.getBytes());
            os.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String processMD5(String toProcess) {
        if (toProcess == null) {
            return "";
        }
        return toProcess;
    }

    private String processLoadMap(String toProcess) {
        if (!checkFloorRequestString(toProcess)) {
            return "";
        }
        String[] pair = toProcess.split("=");
        int floor = Integer.parseInt(pair[1]);
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("res/floor/" + floor + ".xml"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (lines.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        lines.forEach(result::append);
        System.out.println(result.toString());
        return result.toString();
    }

    private boolean checkFloorRequestString(String toCheck) {
        String pair[] = toCheck.split("=");
        return pair.length == 2 && pair[0].equals("floor") && pair[1].matches("^[0-9]{1,9}$");
    }
}

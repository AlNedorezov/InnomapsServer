package com.myinno;

import java.util.HashMap;

/**
 * Cache for all graph-related stuff.
 * Created by luckychess on 3/13/16.
 */
public class GraphCache {
    private static HashMap<Integer, String> floorCache;

    public GraphCache() {
        if (floorCache == null) {
            floorCache = new HashMap<>();
        }
    }

    public synchronized void addNewFloor(int floor, String data) {
        floorCache.put(floor, data);
    }

    public String getFloorData(int floor) {
        return floorCache.get(floor);
    }
}

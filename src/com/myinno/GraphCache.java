package com.myinno;

import java.io.IOException;
import java.util.HashMap;

/**
 * Cache for all graph-related stuff.
 * Created by luckychess on 3/13/16.
 */
public class GraphCache {
    private static HashMap<Integer, String> floorCache;
    private static HashMap<Integer, byte[]> md5FloorCache;

    public GraphCache() {
        if (floorCache == null) {
            floorCache = new HashMap<>();
        }
        if (md5FloorCache == null) {
            md5FloorCache = new HashMap<>();
        }
    }

    public synchronized void addNewFloor(int floor, String data) {
        floorCache.put(floor, data);
    }

    public synchronized void addNewMd5Floor(int floor, byte[] md5) {
        md5FloorCache.put(floor, md5);
    }

    public String getFloorData(int floor) {
        return floorCache.get(floor);
    }

    public byte[] getMd5FloorData(int floor) {return md5FloorCache.get(floor); }

    public static synchronized void clearCache(int floor) {
        System.out.println("Clearing cache for floor " + floor);
        floorCache.remove(floor);
        md5FloorCache.remove(floor);
    }
}

package com.myinno;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.List;

/**
 * Listening for the given directory xml files were changed.
 * Created by saian on 05.04.16.
 */
public class Listener {
    public void listenForChanges(String way) throws IOException {
        File file = new File(way);
        Path path = file.toPath();
        WatchService ws = path.getFileSystem().newWatchService();
        path.register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        WatchKey watch = null;
        while (true) {
            System.out.println("Event: " + file.getPath());
            try {
                watch = ws.take();

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (watch == null) continue;

            List<WatchEvent<?>> events = watch.pollEvents();
            watch.reset();
            for (WatchEvent<?> event : events) {
                WatchEvent.Kind<Path> kind = (WatchEvent.Kind<Path>) event.kind();
                Path context = (Path) event.context();

                if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE) ||
                        kind.equals(StandardWatchEventKinds.ENTRY_DELETE) ||
                        kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    String namefile = context.getFileName().toString();
                    String[] parts = namefile.split("\\.");
                    System.out.println("Modified file: " + context.getFileName());
                    if (parts.length == 2) {
                        //check what it is a file
                        if (parts[1].equals("xml")) {
                            //check what it is number
                            if (checkString(parts[0])) {
                                // clearing caches - both md5 and file itself
                                GraphCache.clearCache(Integer.parseInt(parts[0]));
                                try {
                                    Process p = Runtime.getRuntime().exec("python " + System.getProperty("user.dir")
                                            + File.separator + "res" + File.separator + "floor"
                                            + File.separator + "merge_floors.py",
                                            null,
                                            new File(System.getProperty("user.dir")
                                                    + File.separator + "res" + File.separator
                                                    + "floor"));
                                    p.waitFor();

                                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                                    String s;
                                    while ((s = stdError.readLine()) != null) {
                                        System.out.println(s);
                                    }
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //check that it is integer number
    private boolean checkString(String string) {
        if (string == null || string.length() == 0) return false;
        if (string.equals("9")) return false;   // to prevent endless loop

        int i = 0;
        if (string.charAt(0) == '-') {
            if (string.length() == 1) {
                return false;
            }
            i = 1;
        }

        char c;
        for (; i < string.length(); i++) {
            c = string.charAt(i);
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }
}



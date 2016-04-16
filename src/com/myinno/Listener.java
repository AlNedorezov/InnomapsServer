package com.myinno;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

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
        System.out.println("Monitoring path: " + file.getPath());
        while (true) {
            try {
                watch = ws.take();

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (watch == null) continue;

            List<WatchEvent<?>> events = watch.pollEvents();
            watch.reset();
            for (WatchEvent<?> event : events) {
                WatchEvent.Kind<?> kind = event.kind();
                Path context = (Path) event.context();

                if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE) ||
                        kind.equals(StandardWatchEventKinds.ENTRY_DELETE) ||
                        kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    String namefile = context.getFileName().toString();
                    String[] parts = namefile.split("\\.");
                    System.out.println("Modified file: " + context.getFileName());
                    //check does file have an "xml" extension and its name is a number
                    if (parts.length == 2 && parts[0].matches("^[0-9]{1,9}$") && parts[1].equals("xml")) {
                        try {
                            // wait a bit until modification process is not over
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // clearing caches - both md5 and file itself
                        GraphCache.clearCache(Integer.parseInt(parts[0]));
                        if (!Objects.equals(parts[0], "9")) {
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



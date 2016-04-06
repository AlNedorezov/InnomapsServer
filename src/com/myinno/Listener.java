package com.myinno;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Created by saian on 05.04.16.
 */
public class Listener {


        public  void listenForChanges(String way) throws IOException {
            File file = new File(way);
            Path path = file.toPath();
            WatchService ws = path.getFileSystem().newWatchService();
            path.register(ws, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey watch = null;
            while (true) {
                System.out.print("Event: " + file.getPath());
                try {
                    watch = ws.take();

                } catch (InterruptedException ex) {
                    System.err.println("Interrupted");
                }
                List<WatchEvent<?>> events = watch.pollEvents();
                watch.reset();
                for (WatchEvent<?> event : events) {
                    WatchEvent.Kind<Path> kind = (WatchEvent.Kind<Path>) event.kind();
                    Path context = (Path) event.context();

                        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE) ||
                                kind.equals(StandardWatchEventKinds.ENTRY_DELETE) ||
                                kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                            String namefile = context.getFileName().toString();
                            String[] parts = namefile.split(".");
                            System.out.println("Modified file: " + context.getFileName());
                            if (parts.length==2) {
                            //check what it is a file
                            if (parts.length == 2 && parts[1].equals("xml")) {
                                //check what it is number
                                if (checkString(parts[0])) {
                                    //call Kostya's method
                                    GraphCache.clearCache(Integer.getInteger( parts[0]));
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



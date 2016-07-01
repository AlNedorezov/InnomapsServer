package events;

/**
 * Created by alnedorezov on 6/29/16.
 */
public class CalendarSyncThread extends Thread {
    @Override
    public void run() {
        while (true) {

            // Add updated events from google calendar to the database
            new JsonParseTask().updateDbIfNeeded();

            try {
                long sleepTime = 1000 * 60 * 60;
                sleep(sleepTime); // sleep for 1 hour
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
package rest.clientServerCommunicationClasses;

import db.EventSchedule;

import java.util.List;

/**
 * Created by alnedorezov on 6/24/16.
 */
public class EventSchedulesObject {
    private List<EventSchedule> eventSchedules;

    public EventSchedulesObject(List<EventSchedule> eventSchedules) {
        this.eventSchedules = eventSchedules;
    }

    public void addEventSchedule(EventSchedule eventSchedule) {
        this.eventSchedules.add(eventSchedule);
    }

    public void setEventSchedule(int index, EventSchedule eventSchedule) {
        this.eventSchedules.set(index, eventSchedule);
    }

    public void getEventSchedule(int index) {
        this.eventSchedules.get(index);
    }

    public void removeEventSchedule(int index) {
        this.eventSchedules.remove(index);
    }

    public List<EventSchedule> getEventschedules() {
        return eventSchedules;
    }
}
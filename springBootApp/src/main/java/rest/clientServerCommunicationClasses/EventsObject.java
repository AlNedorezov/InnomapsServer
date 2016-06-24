package rest.clientServerCommunicationClasses;

import db.Event;

import java.util.List;

/**
 * Created by alnedorezov on 6/24/16.
 */
public class EventsObject {
    private List<Event> events;

    public EventsObject(List<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void setEvent(int index, Event event) {
        this.events.set(index, event);
    }

    public void getEvent(int index) {
        this.events.get(index);
    }

    public void removeEvent(int index) {
        this.events.remove(index);
    }

    public List<Event> getEvents() {
        return events;
    }
}

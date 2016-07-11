package rest.clientservercommunicationclasses;

import db.EventCreator;

import java.util.List;

/**
 * Created by alnedorezov on 6/24/16.
 */
public class EventCreatorsObject {
    private List<EventCreator> eventCreators;

    public EventCreatorsObject(List<EventCreator> eventCreators) {
        this.eventCreators = eventCreators;
    }

    public void addEventCreator(EventCreator eventCreator) {
        this.eventCreators.add(eventCreator);
    }

    public void setEventCreator(int index, EventCreator eventCreator) {
        this.eventCreators.set(index, eventCreator);
    }

    public void getEventCreator(int index) {
        this.eventCreators.get(index);
    }

    public void removeEventCreator(int index) {
        this.eventCreators.remove(index);
    }

    public List<EventCreator> getEventcreators() {
        return eventCreators;
    }
}
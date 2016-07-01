package rest.clientServerCommunicationClasses;

import db.EventCreatorAppointment;

import java.util.List;

/**
 * Created by alnedorezov on 7/1/16.
 */
public class EventCreatorAppointmentsObject {
    private List<EventCreatorAppointment> eventCreatorAppointments;

    public EventCreatorAppointmentsObject(List<EventCreatorAppointment> eventCreatorAppointments) {
        this.eventCreatorAppointments = eventCreatorAppointments;
    }

    public void addEventCreatorAppointment(EventCreatorAppointment eventCreatorAppointment) {
        this.eventCreatorAppointments.add(eventCreatorAppointment);
    }

    public void setEventCreatorAppointment(int index, EventCreatorAppointment eventCreatorAppointment) {
        this.eventCreatorAppointments.set(index, eventCreatorAppointment);
    }

    public void getEventCreatorAppointment(int index) {
        this.eventCreatorAppointments.get(index);
    }

    public void removeEventCreatorAppointment(int index) {
        this.eventCreatorAppointments.remove(index);
    }

    public List<EventCreatorAppointment> getEventCreatorAppointments() {
        return eventCreatorAppointments;
    }
}

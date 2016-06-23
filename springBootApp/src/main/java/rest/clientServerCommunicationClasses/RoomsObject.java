package rest.clientServerCommunicationClasses;

import db.Room;

import java.util.List;

/**
 * Created by alnedorezov on 6/23/16.
 */
public class RoomsObject {
    private List<Room> rooms;

    public RoomsObject(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    public void setRoom(int index, Room room) {
        this.rooms.set(index, room);
    }

    public void getRoom(int index) {
        this.rooms.get(index);
    }

    public void removeRoom(int index) {
        this.rooms.remove(index);
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
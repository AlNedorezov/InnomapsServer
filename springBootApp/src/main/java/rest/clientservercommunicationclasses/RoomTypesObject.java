package rest.clientservercommunicationclasses;

import db.RoomType;

import java.util.List;

/**
 * Created by alnedorezov on 6/23/16.
 */
public class RoomTypesObject {
    private List<RoomType> roomTypes;

    public RoomTypesObject(List<RoomType> roomTypes) {
        this.roomTypes = roomTypes;
    }

    public void addRoomType(RoomType roomType) {
        this.roomTypes.add(roomType);
    }

    public void setRoomType(int index, RoomType roomType) {
        this.roomTypes.set(index, roomType);
    }

    public void getRoomType(int index) {
        this.roomTypes.get(index);
    }

    public void removeRoomType(int index) {
        this.roomTypes.remove(index);
    }

    public List<RoomType> getRoomtypes() {
        return roomTypes;
    }
}
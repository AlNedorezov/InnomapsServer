package rest.clientServerCommunicationClasses;

import db.RoomPhoto;

import java.util.List;

/**
 * Created by alnedorezov on 6/23/16.
 */
public class RoomPhotosObject {
    private List<RoomPhoto> roomPhotos;

    public RoomPhotosObject(List<RoomPhoto> roomPhotos) {
        this.roomPhotos = roomPhotos;
    }

    public void addRoomPhoto(RoomPhoto roomPhoto) {
        this.roomPhotos.add(roomPhoto);
    }

    public void setRoomPhoto(int index, RoomPhoto roomPhoto) {
        this.roomPhotos.set(index, roomPhoto);
    }

    public void getRoomPhoto(int index) {
        this.roomPhotos.get(index);
    }

    public void removeRoomPhoto(int index) {
        this.roomPhotos.remove(index);
    }

    public List<RoomPhoto> getRoomphotos() {
        return roomPhotos;
    }
}
package rest.clientservercommunicationclasses;

import db.Photo;

import java.util.List;

/**
 * Created by alnedorezov on 6/23/16.
 */
public class PhotosObject {
    private List<Photo> photos;

    public PhotosObject(List<Photo> photos) {
        this.photos = photos;
    }

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    public void setPhoto(int index, Photo photo) {
        this.photos.set(index, photo);
    }

    public void getPhoto(int index) {
        this.photos.get(index);
    }

    public void removePhoto(int index) {
        this.photos.remove(index);
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
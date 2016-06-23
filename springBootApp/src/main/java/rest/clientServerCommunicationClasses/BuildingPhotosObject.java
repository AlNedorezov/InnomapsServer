package rest.clientServerCommunicationClasses;

import db.BuildingPhoto;

import java.util.List;

/**
 * Created by alnedorezov on 6/23/16.
 */
public class BuildingPhotosObject {
    private List<BuildingPhoto> buildingPhotos;

    public BuildingPhotosObject(List<BuildingPhoto> buildingPhotos) {
        this.buildingPhotos = buildingPhotos;
    }

    public void addBuildingPhoto(BuildingPhoto buildingPhoto) {
        this.buildingPhotos.add(buildingPhoto);
    }

    public void setBuildingPhoto(int index, BuildingPhoto buildingPhoto) {
        this.buildingPhotos.set(index, buildingPhoto);
    }

    public void getBuildingPhoto(int index) {
        this.buildingPhotos.get(index);
    }

    public void removeBuildingPhoto(int index) {
        this.buildingPhotos.remove(index);
    }

    public List<BuildingPhoto> getBuildingphotos() {
        return buildingPhotos;
    }
}
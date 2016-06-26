package rest.clientServerCommunicationClasses;

import db.BuildingFloorOverlay;

import java.util.List;

/**
 * Created by alnedorezov on 6/27/16.
 */
public class BuildingFloorOverlaysObject {
    private List<BuildingFloorOverlay> buildingFloorOverlays;

    public BuildingFloorOverlaysObject(List<BuildingFloorOverlay> buildingFloorOverlays) {
        this.buildingFloorOverlays = buildingFloorOverlays;
    }

    public void addBuildingFloorOverlay(BuildingFloorOverlay buildingFloorOverlay) {
        this.buildingFloorOverlays.add(buildingFloorOverlay);
    }

    public void setBuildingFloorOverlay(int index, BuildingFloorOverlay buildingFloorOverlay) {
        this.buildingFloorOverlays.set(index, buildingFloorOverlay);
    }

    public void getBuildingFloorOverlay(int index) {
        this.buildingFloorOverlays.get(index);
    }

    public void removeBuildingFloorOverlay(int index) {
        this.buildingFloorOverlays.remove(index);
    }

    public List<BuildingFloorOverlay> getBuildingflooroverlays() {
        return buildingFloorOverlays;
    }
}
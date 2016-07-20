package rest.clientservercommunicationclasses;

import db.BuildingAuxiliaryCoordinate;

import java.util.List;

/**
 * Created by alnedorezov on 7/20/16.
 */

public class BuildingAuxiliaryCoordinatesObject {
    private List<BuildingAuxiliaryCoordinate> buildingAuxiliaryCoordinates;

    public BuildingAuxiliaryCoordinatesObject(List<BuildingAuxiliaryCoordinate> buildingAuxiliaryCoordinates) {
        this.buildingAuxiliaryCoordinates = buildingAuxiliaryCoordinates;
    }

    public void addBuildingAuxiliaryCoordinate(BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinate) {
        this.buildingAuxiliaryCoordinates.add(buildingAuxiliaryCoordinate);
    }

    public void setBuildingAuxiliaryCoordinate(int index, BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinate) {
        this.buildingAuxiliaryCoordinates.set(index, buildingAuxiliaryCoordinate);
    }

    public void getBuildingAuxiliaryCoordinate(int index) {
        this.buildingAuxiliaryCoordinates.get(index);
    }

    public void removeBuildingAuxiliaryCoordinate(int index) {
        this.buildingAuxiliaryCoordinates.remove(index);
    }

    public List<BuildingAuxiliaryCoordinate> getBuildingauxiliarycoordinates() {
        return buildingAuxiliaryCoordinates;
    }
}
package rest.clientservercommunicationclasses;

import db.Building;

import java.util.List;

/**
 * Created by alnedorezov on 6/23/16.
 */
public class BuildingsObject {
    private List<Building> buildings;

    public BuildingsObject(List<Building> buildings) {
        this.buildings = buildings;
    }

    public void addBuilding(Building building) {
        this.buildings.add(building);
    }

    public void setBuilding(int index, Building building) {
        this.buildings.set(index, building);
    }

    public void getBuilding(int index) {
        this.buildings.get(index);
    }

    public void removeBuilding(int index) {
        this.buildings.remove(index);
    }

    public List<Building> getBuildings() {
        return buildings;
    }
}
package rest.clientservercommunicationclasses.sync;

import db.*;

import java.util.List;

/**
 * Created by alnedorezov on 7/14/16.
 */
public class GeneralSync {
    private List<CoordinateType> coordinateTypes;
    private List<EdgeType> edgeTypes;
    private List<RoomType> roomTypes;
    private List<Coordinate> coordinates;
    private List<Edge> edges;
    private List<Street> streets;
    private List<Building> buildings;
    private List<Room> rooms;
    private List<Photo> photos;
    private List<BuildingPhoto> buildingPhotos;
    private List<RoomPhoto> roomPhotos;
    private List<BuildingFloorOverlay> buildingFloorOverlays;
    private List<EventCreator> eventCreators;
    private List<Event> events;
    private List<EventSchedule> eventSchedules;
    private List<EventCreatorAppointment> eventCreatorAppointments;

    private GeneralSync(List<CoordinateType> coordinateTypes, List<EdgeType> edgeTypes, List<RoomType> roomTypes,
                        List<Coordinate> coordinates, List<Edge> edges, List<Street> streets, List<Building> buildings,
                        List<Room> rooms, List<Photo> photos, List<BuildingPhoto> buildingPhotos, List<RoomPhoto> roomPhotos,
                        List<BuildingFloorOverlay> buildingFloorOverlays, List<EventCreator> eventCreators, List<Event> events,
                        List<EventSchedule> eventSchedules, List<EventCreatorAppointment> eventCreatorAppointments) {
        this.coordinateTypes = coordinateTypes;
        this.edgeTypes = edgeTypes;
        this.roomTypes = roomTypes;
        this.coordinates = coordinates;
        this.edges = edges;
        this.streets = streets;
        this.buildings = buildings;
        this.rooms = rooms;
        this.photos = photos;
        this.buildingPhotos = buildingPhotos;
        this.roomPhotos = roomPhotos;
        this.buildingFloorOverlays = buildingFloorOverlays;
        this.eventCreators = eventCreators;
        this.events = events;
        this.eventSchedules = eventSchedules;
        this.eventCreatorAppointments = eventCreatorAppointments;
    }

    // For deserialization with Jackson
    public GeneralSync() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public static class GeneralSyncBuilder {
        private List<CoordinateType> coordinateTypes;
        private List<EdgeType> edgeTypes;
        private List<RoomType> roomTypes;
        private List<Coordinate> coordinates;
        private List<Edge> edges;
        private List<Street> streets;
        private List<Building> buildings;
        private List<Room> rooms;
        private List<Photo> photos;
        private List<BuildingPhoto> buildingPhotos;
        private List<RoomPhoto> roomPhotos;
        private List<BuildingFloorOverlay> buildingFloorOverlays;
        private List<EventCreator> eventCreators;
        private List<Event> events;
        private List<EventSchedule> eventSchedules;
        private List<EventCreatorAppointment> eventCreatorAppointments;

        public GeneralSyncBuilder() {
            // Empty constructor for builder class
        }

        public GeneralSyncBuilder setTypes(List<CoordinateType> coordinateTypes, List<EdgeType> edgeTypes, List<RoomType> roomTypes) {
            this.coordinateTypes = coordinateTypes;
            this.edgeTypes = edgeTypes;
            this.roomTypes = roomTypes;
            return this;
        }

        public GeneralSyncBuilder setMapUnits(List<Coordinate> coordinates, List<Edge> edges, List<Street> streets, List<Building> buildings,
                                              List<Room> rooms, List<Photo> photos, List<BuildingFloorOverlay> buildingFloorOverlays) {
            this.coordinates = coordinates;
            this.edges = edges;
            this.streets = streets;
            this.buildings = buildings;
            this.rooms = rooms;
            this.photos = photos;
            this.buildingFloorOverlays = buildingFloorOverlays;
            return this;
        }

        public GeneralSyncBuilder setEvents(List<EventCreator> eventCreators, List<Event> events, List<EventSchedule> eventSchedules) {
            this.eventCreators = eventCreators;
            this.events = events;
            this.eventSchedules = eventSchedules;
            return this;
        }

        public GeneralSyncBuilder setAssignments(List<BuildingPhoto> buildingPhotos, List<RoomPhoto> roomPhotos, List<EventCreatorAppointment> eventCreatorAppointments) {
            this.buildingPhotos = buildingPhotos;
            this.roomPhotos = roomPhotos;
            this.eventCreatorAppointments = eventCreatorAppointments;
            return this;
        }

        public GeneralSync build() {
            return new GeneralSync(coordinateTypes, edgeTypes, roomTypes, coordinates, edges, streets, buildings, rooms, photos,
                    buildingPhotos, roomPhotos, buildingFloorOverlays, eventCreators, events, eventSchedules, eventCreatorAppointments);
        }
    }

    public void addCoordinateType(CoordinateType coordinateType) {
        this.coordinateTypes.add(coordinateType);
    }

    public void setCoordinateType(int index, CoordinateType coordinateType) {
        this.coordinateTypes.set(index, coordinateType);
    }

    public void getCoordinateType(int index) {
        this.coordinateTypes.get(index);
    }

    public void removeCoordinateType(int index) {
        this.coordinateTypes.remove(index);
    }

    public List<CoordinateType> getCoordinatetypes() {
        return coordinateTypes;
    }


    public void addEdgeType(EdgeType edgeType) {
        this.edgeTypes.add(edgeType);
    }

    public void setEdgeType(int index, EdgeType edgeType) {
        this.edgeTypes.set(index, edgeType);
    }

    public void getEdgeType(int index) {
        this.edgeTypes.get(index);
    }

    public void removeEdgeType(int index) {
        this.edgeTypes.remove(index);
    }

    public List<EdgeType> getEdgetypes() {
        return edgeTypes;
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


    public void addCoordinate(Coordinate coordinate) {
        this.coordinates.add(coordinate);
    }

    public void setCoordinate(int index, Coordinate coordinate) {
        this.coordinates.set(index, coordinate);
    }

    public void getCoordinate(int index) {
        this.coordinates.get(index);
    }

    public void removeCoordinate(int index) {
        this.coordinates.remove(index);
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }


    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    public void setEdge(int index, Edge edge) {
        this.edges.set(index, edge);
    }

    public void getEdge(int index) {
        this.edges.get(index);
    }

    public void removeEdge(int index) {
        this.edges.remove(index);
    }

    public List<Edge> getEdges() {
        return edges;
    }


    public void addStreet(Street street) {
        this.streets.add(street);
    }

    public void setStreet(int index, Street street) {
        this.streets.set(index, street);
    }

    public void getStreet(int index) {
        this.streets.get(index);
    }

    public void removeStreet(int index) {
        this.streets.remove(index);
    }

    public List<Street> getStreets() {
        return streets;
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


    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void setEvent(int index, Event event) {
        this.events.set(index, event);
    }

    public void getEvent(int index) {
        this.events.get(index);
    }

    public void removeEvent(int index) {
        this.events.remove(index);
    }

    public List<Event> getEvents() {
        return events;
    }


    public void addEventSchedule(EventSchedule eventSchedule) {
        this.eventSchedules.add(eventSchedule);
    }

    public void setEventSchedule(int index, EventSchedule eventSchedule) {
        this.eventSchedules.set(index, eventSchedule);
    }

    public void getEventSchedule(int index) {
        this.eventSchedules.get(index);
    }

    public void removeEventSchedule(int index) {
        this.eventSchedules.remove(index);
    }

    public List<EventSchedule> getEventschedules() {
        return eventSchedules;
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

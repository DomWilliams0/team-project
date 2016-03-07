package com.b3.world;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A class used to query the world to find different objects of different types from the world (buildings, buildings by type and random building)
 */
public class WorldQueryService {

    private final World world;

    /**
     * Constructs the query service using the current world.
     * @param world the world that this query service will be fundamentally linked to
     */
    public WorldQueryService(World world) {
        this.world = world;
    }

    /**
     * Gets all the buildings in the world
     * @return all of the buildings in the world as a List
     */
    public List<Building> getAllBuildings() {
        return world.getBuildings();
    }

    /**
     * Gets all the buildings in the world which are a 'house' and have no events
     * @return all of the buildings (which are a 'house' and have no events) in the world as a List
     */
    public List<Building> getFreeBuildings() {
        List<Building> buildings = world.getBuildings();
        return buildings
                .stream()
                .filter(building -> building.getType() == BuildingType.HOUSE && building.getEvent() == null)
                .collect(Collectors.toList());
    }

    /**
     * Gets all the buildings in the world which are of the type inputted
     * @param type the predicate to limit the returning of the buildings
     * @return a list of buildings on the map which have the correct type.
     */
    public List<Building> getBuildingsByType(BuildingType type) {
        List<Building> buildings = world.getBuildings();
        return buildings
                .stream()
                .filter(building -> building.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Gets a random buildings in the world which are of the type inputted
     * @param type the predicate to limit the returning of the buildings
     * @return a buildings on the map which have the correct type.
     */
    public Building getRandomBuildingByType(BuildingType type) {
        List<Building> buildings = getBuildingsByType(type);
        Random rn = new Random();

        return buildings.get(rn.nextInt(buildings.size()));
    }

}

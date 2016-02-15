package com.b3.world;

import com.badlogic.ashley.core.Engine;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldQueryService {

    private World world;
    private Engine engine;

    public WorldQueryService(World world) {
        this.world = world;
        this.engine = world.getEngine();
    }

    public List<Building> getAllBuildings() {
        return world.getBuildings();
    }

    public List<Building> getFreeBuildings() {
        List<Building> buildings = world.getBuildings();
        return buildings
                .stream()
                .filter(building -> building.getType() == BuildingType.HOUSE && building.getEvent() == null)
                .collect(Collectors.toList());
    }

    public List<Building> getBuildingsByType(BuildingType type) {
        List<Building> buildings = world.getBuildings();
        return buildings
                .stream()
                .filter(building -> building.getType() == type)
                .collect(Collectors.toList());
    }

    public Building getRandomBuildingByType(BuildingType type) {
        List<Building> buildings = getBuildingsByType(type);
        Random rn = new Random();

        return buildings.get(rn.nextInt(buildings.size()));
    }

}

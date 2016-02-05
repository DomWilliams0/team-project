package com.b3.world;

import com.badlogic.ashley.core.Engine;

import java.util.List;

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
        return world.getBuildings();
    }

}

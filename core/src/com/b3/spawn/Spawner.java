package com.b3.spawn;

import com.b3.world.Building;
import com.b3.world.World;

public class Spawner {

    private World world;

    public Spawner(World world) {
        this.world = world;
    }

    public void agentFromBuilding(Building sourceBuilding) {
        world.spawnAgent(sourceBuilding.getTilePosition());
        // ...
    }

}

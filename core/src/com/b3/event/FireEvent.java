package com.b3.event;

import com.b3.world.Building;
import com.b3.world.World;
import com.b3.world.WorldQueryService;

import java.util.List;
import java.util.Random;

public class FireEvent extends WorldEvent {

    private World world;

    public FireEvent(World world) {
        super();
        this.world = world;
    }

    @Override
    public Building trigger() {
        super.trigger();

        WorldQueryService wqs = new WorldQueryService(world);
        List<Building> freeBuildings = wqs.getFreeBuildings();
        Random rn = new Random();
        int i = rn.nextInt(freeBuildings.size());

        return freeBuildings.get(i);
    }
}

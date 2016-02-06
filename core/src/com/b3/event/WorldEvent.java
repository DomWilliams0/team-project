package com.b3.event;

import com.b3.world.Building;
import com.b3.world.World;

import java.util.List;
import java.util.Random;

public class WorldEvent {

    private World world;
    private EventType eventType;

    public WorldEvent(World world, EventType eventType) {
        this.world = world;
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Building trigger() {
        List<Building> freeBuildings = world.getQueryService().getFreeBuildings();
        Random rn = new Random();
        int i = rn.nextInt(freeBuildings.size());

        return freeBuildings.get(i);
    }

}

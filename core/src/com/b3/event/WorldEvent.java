package com.b3.event;

import com.b3.world.Building;
import com.b3.world.World;

import java.util.List;
import java.util.Random;

/**
 * Describes an event happening in the world
 */
public class WorldEvent {

    private World world;
    private EventType eventType;

    /**
     * Initialises a WorldEvent instance
     * @param world         The world to work on
     * @param eventType     The type of the event
     */
    public WorldEvent(World world, EventType eventType) {
        this.world = world;
        this.eventType = eventType;
    }

    /**
     * @return Gets the event type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Triggers the event on a building
     * @return The building affected by the event
     */
    public Building trigger() {
        List<Building> freeBuildings = world.getQueryService().getFreeBuildings();
        Random rn = new Random();
        int i = rn.nextInt(freeBuildings.size());

        return freeBuildings.get(i);
    }

}

package com.b3.world;

import com.b3.event.EventMessage;
import com.b3.event.EventType;

import java.util.Observable;
import java.util.Observer;

public class WorldObserver implements Observer {

    private World world;
    public WorldObserver(World world) {
        this.world = world;
    }

    private BuildingType getBuildingTypeFromEvent(EventType eventType) {
        switch (eventType) {
            case FIRE:      return BuildingType.FIRE_STATION;
            case ROBBERY:   return BuildingType.POLICE_STATION;
            case DELIVERY:  return BuildingType.RESTAURANT;
        }

        return BuildingType.HOUSE;
    }

    @Override
    public void update(Observable o, Object arg) {
        EventMessage evt = (EventMessage)arg;
        EventType eventType = evt.getEventType();

        // Get target and source buildings
        Building targetBuilding = (Building)evt.getMessage();
        targetBuilding.setEvent(eventType);
        Building sourceBuilding = world.getQueryService().getRandomBuildingByType(getBuildingTypeFromEvent(eventType));

        // Perform search

        world.spawnAgent(sourceBuilding.getTilePosition());
    }
}

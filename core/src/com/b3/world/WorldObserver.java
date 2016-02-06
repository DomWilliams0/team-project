package com.b3.world;

import com.b3.event.EventMessage;
import com.b3.event.EventType;
import com.b3.spawn.Spawner;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector2;

import java.util.Observable;
import java.util.Observer;

public class WorldObserver implements Observer {

    private World world;
    private Spawner spawner;

    public WorldObserver(World world) {
        this.world = world;
        this.spawner = new Spawner(world);
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

        switch (eventType) {
            case FIRE:
                // Get target and source buildings
                Building targetBuilding = (Building)evt.getMessage();
                targetBuilding.setEvent(eventType);
                Building sourceBuilding = world.getQueryService().getRandomBuildingByType(getBuildingTypeFromEvent(eventType));

                //building.getEntryPoint();
                spawner.agentFromBuilding(sourceBuilding);

                // Perform search

                break;

            case ROBBERY:

                break;

            case DELIVERY:

                break;
        }
    }
}

package com.b3.world;

import com.b3.event.EventMessage;
import com.badlogic.gdx.math.Vector2;

import java.util.Observable;
import java.util.Observer;

public class WorldObserver implements Observer {

    private World world;

    public WorldObserver(World world) {
        this.world = world;
    }

    @Override
    public void update(Observable o, Object arg) {
        EventMessage evt = (EventMessage)arg;

        // Spawn entity + start search

        switch (evt.getEventType()) {
            case FIRE:
                Building building = (Building)evt.getMessage();
                //building.getEntryPoint();
                world.addAgent(building.getTilePosition());
                // ...
                break;

            case ROBBERY:

                break;

            case DELIVERY:

                break;
        }
    }
}

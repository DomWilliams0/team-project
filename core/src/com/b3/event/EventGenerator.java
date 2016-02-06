package com.b3.event;

import com.b3.world.World;
import com.badlogic.gdx.Gdx;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EventGenerator extends Observable implements Runnable {

    private int timeForNextEvent;
    private int minTime;
    private int maxTime;
    private World world;

    public EventGenerator(World world) {
        this.world = world;
        this.minTime = 3;
        this.maxTime = 10;
        //width = Gdx.graphics.getWidth();
        //height = Gdx.graphics.getHeight();
        generateTimeForNextEvent();
    }

    private void generateTimeForNextEvent() {
        timeForNextEvent = ThreadLocalRandom.current().nextInt(minTime, maxTime + 1);
    }

    // This is just dummy (it actually highlights a node)
    /*private void triggerFire() {
        Array<Actor> actors = proto.getGraphStage().getActors();
        Random rn = new Random();
        int i = rn.nextInt(actors.size);

        NodeActor selectedActor = (NodeActor)actors.get(i);
        selectedActor.setSelected(true);

        setChanged();
        notifyObservers(selectedActor.getPoint());
    }*/

    /**
     * Triggers a random event in the world
     */
    private void triggerRandomEvent() {
        List<EventType> events = Collections.unmodifiableList(Arrays.asList(EventType.values()));
        int numEvents = events.size();
        Random rn = new Random();

        EventType eventType = EventType.FIRE; //events.get(rn.nextInt(numEvents));
        EventMessage evtMessage = new EventMessage(eventType);
        WorldEvent evt;

        switch (eventType) {
            case ROBBERY:
                evt = new RobberyEvent();
                break;
            case DELIVERY:
                evt = new DeliveryEvent();
                break;
            case FIRE:
            default:
                evt = new FireEvent(world);
                break;
        }

        Object data = evt.trigger();
        evtMessage.setMessage(data);

        setChanged();
        notifyObservers(evtMessage);
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(timeForNextEvent * 1000);

                // Post to the main thread
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        //triggerFire();
                        triggerRandomEvent();
                    }
                });

                generateTimeForNextEvent();
            }
            catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

    }
}

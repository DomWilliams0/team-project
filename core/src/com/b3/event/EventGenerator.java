package com.b3.event;

import com.b3.world.World;
import com.badlogic.gdx.Gdx;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Describes the event system
 */
public class EventGenerator extends Observable implements Runnable {

    protected int timeForNextEvent;
    protected int minTime;
    protected int maxTime;
    protected World world;

    /**
     * Initialises an EventGenerator instance
     * @param world The world to work on
     */
    public EventGenerator(World world, int minTime, int maxTime) {
        this.world = world;
        this.minTime = minTime;
        this.maxTime = maxTime;
        generateTimeForNextEvent();
    }

    /**
     * Gets the time to wait for the next event
     */
    protected void generateTimeForNextEvent() {
        timeForNextEvent = ThreadLocalRandom.current().nextInt(minTime, maxTime + 1);
    }

    /**
     * Triggers a random event in the world
     */
    protected void triggerRandomEvent() {
        List<EventType> events = Collections.unmodifiableList(Arrays.asList(EventType.values()));
        int numEvents = events.size();
        Random rn = new Random();

        EventType eventType = events.get(rn.nextInt(numEvents));
        triggerEvent(eventType);
    }

    /**
     * Triggers a specific event
     * @param eventType The event type (FIRE, ROBBERY, DELIVERY)
     */
    public void triggerEvent(EventType eventType) {
        EventMessage evtMessage = new EventMessage(eventType);
        WorldEvent evt = new WorldEvent(world, eventType);

        Object data = evt.trigger();
        evtMessage.setMessage(data);

        setChanged();
        notifyObservers(evtMessage);
    }

    /**
     * Triggers an event
     * @param eventMessage The event message to pass to the observer
     */
    public void triggerEvent(EventMessage eventMessage) {
        setChanged();
        notifyObservers(eventMessage);
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(timeForNextEvent * 1000);

                // Post to the main thread
                /*Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        triggerRandomEvent();
                    }
                });*/

                triggerRandomEvent();
                generateTimeForNextEvent();
            }
            catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

    }
}

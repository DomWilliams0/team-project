package com.b3.event;

import com.b3.world.Building;
import com.b3.world.World;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EventGeneratorTest {

    private EventGenerator eventGenerator;

    @Before
    public void setUp() {
        World world = mock(World.class);
        eventGenerator = new DummyEventGenerator(world, 1, 3);
    }

    @Test(timeout = 4000)
    public void testGenerateTimeForNextEvent() throws InterruptedException {
        new Thread(eventGenerator).start();

        while (true) {
            if (eventGenerator.hasChanged()) {
                assertTrue("Event generator respects timeout and changes state", true);
                break;
            }
        }
    }

    class DummyEventGenerator extends EventGenerator {

        public DummyEventGenerator(World world, int minTime, int maxTime) {
            super(world, minTime, maxTime);
        }

        @Override
        protected void triggerRandomEvent() {
            List<EventType> events = Collections.unmodifiableList(Arrays.asList(EventType.values()));
            int numEvents = events.size();
            Random rn = new Random();

            EventType eventType = events.get(rn.nextInt(numEvents));
            triggerEvent(new EventMessage(eventType, mock(Building.class)));
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(timeForNextEvent * 1000);

                    triggerRandomEvent();
                    generateTimeForNextEvent();
                }
                catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}

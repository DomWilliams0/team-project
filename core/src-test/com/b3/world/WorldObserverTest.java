package com.b3.world;

import com.b3.event.EventGenerator;
import com.b3.event.EventMessage;
import com.b3.event.EventType;
import org.junit.Before;
import org.junit.Test;

import java.util.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class WorldObserverTest {

    private EventGenerator eventGenerator;
    private WorldObserver worldObserver;

    @Before
    public void setUp() {
        World world = mock(World.class, RETURNS_DEEP_STUBS);
        when(world.getQueryService()).thenCallRealMethod();

        worldObserver = new DummyWorldObserver(world);
        eventGenerator = new EventGenerator(world);
    }

    @Test
    public void testListener() {

        eventGenerator.addObserver(worldObserver);
        assertEquals("There is one observer", 1, eventGenerator.countObservers());

        // Trigger event
        EventMessage evtMessage = new EventMessage();
        Object data = mock(Building.class);
        evtMessage.setMessage(data);
        evtMessage.setEventType(EventType.FIRE);

        eventGenerator.triggerEvent(evtMessage);

        assertEquals("Event gets caught", true, ((DummyWorldObserver)worldObserver).isUpdated());

    }

    class DummyWorldObserver extends WorldObserver {

        private boolean updated;

        public DummyWorldObserver(World world) {
            super(world);
            this.updated = false;
        }

        public boolean isUpdated() {
            return updated;
        }

        public void setUpdated(boolean updated) {
            this.updated = updated;
        }

        @Override
        public void update(Observable o, Object arg) {
            setUpdated(true);
        }
    }

}

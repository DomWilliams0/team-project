package com.b3.event;

public class EventMessage {

    private EventType eventType;
    private Object message;

    public EventMessage() {}

    public EventMessage(EventType eventType) {
        this.eventType = eventType;
    }

    public EventMessage(EventType eventType, Object message) {
        this.eventType = eventType;
        this.message = message;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

}

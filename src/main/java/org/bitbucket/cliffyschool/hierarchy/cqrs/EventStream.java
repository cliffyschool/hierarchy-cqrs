package org.bitbucket.cliffyschool.hierarchy.cqrs;

import org.bitbucket.cliffyschool.hierarchy.event.Event;

import java.util.List;

public class EventStream {
    private List<Event> events;

    public EventStream(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}

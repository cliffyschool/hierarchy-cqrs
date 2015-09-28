package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.event.Event;

import java.util.List;

public class EventStream {
    private List<Event> events;

    EventStream(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public EventStream append(Event... events) {
        if (events == null)
            return this;

        this.events.addAll(Lists.newArrayList(events));
        return this;
    }

    public EventStream append(EventStream eventStream){
        if (eventStream == null || eventStream.getEvents() == null)
            return this;

        this.events.addAll(eventStream.getEvents());
        return this;
    }

    public void clear() {
        events.clear();
    }

    public static EventStream from(List<Event> events){
        if (events == null || events.isEmpty())
            return new EventStream(Lists.newArrayList());

        return new EventStream(events);
    }

    public static EventStream from(Event... events){
        if (events == null || events.length == 0)
            return new EventStream(Lists.newArrayList());

        return new EventStream(Lists.newArrayList(events));
    }
}


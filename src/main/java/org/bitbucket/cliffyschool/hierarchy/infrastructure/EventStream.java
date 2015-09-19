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

    public static EventStream from(List<Event> events){
        if (events == null || events.isEmpty())
            return new EventStream(Lists.newArrayList());

        long versionId = events.get(0).getVersionId();
        if (!events.stream().allMatch(e -> e.getVersionId() == versionId))
            throw new IllegalArgumentException("All events in a given event stream must share the same version id.");

        return new EventStream(events);
    }
}


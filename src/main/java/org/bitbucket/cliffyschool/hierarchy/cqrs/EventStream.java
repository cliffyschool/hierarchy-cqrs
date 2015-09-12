package org.bitbucket.cliffyschool.hierarchy.cqrs;

import org.bitbucket.cliffyschool.hierarchy.event.Event;

import java.util.List;

public class EventStream {
    private long versionId;
    private List<Event> events;

    public EventStream(long versionId, List<Event> events) {

        this.versionId = versionId;
        this.events = events;
    }

    public long getVersionId() {
        return versionId;
    }

    public List<Event> getEvents() {
        return events;
    }
}

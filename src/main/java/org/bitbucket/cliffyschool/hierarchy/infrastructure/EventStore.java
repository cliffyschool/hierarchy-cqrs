package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import org.bitbucket.cliffyschool.hierarchy.event.Event;

import java.util.List;
import java.util.UUID;

public interface EventStore {

    void store(UUID aggregateId, EventStream stream, long versionBeingModified);

    List<Event> getEvents(UUID aggregateId);


}

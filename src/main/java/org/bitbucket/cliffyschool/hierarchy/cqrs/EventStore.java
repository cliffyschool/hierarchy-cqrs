package org.bitbucket.cliffyschool.hierarchy.cqrs;

import org.bitbucket.cliffyschool.hierarchy.event.Event;

import java.util.List;

public class EventStore {

    public EventStream loadEvents(String aggregateId){
        return null;
    }

    public void store(String aggregateId, long versionId, List<Event> events) {

    }

}

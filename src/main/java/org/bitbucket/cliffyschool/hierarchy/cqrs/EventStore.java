package org.bitbucket.cliffyschool.hierarchy.cqrs;

import org.bitbucket.cliffyschool.hierarchy.event.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventStore<T> {

    Optional<T> findById(UUID aggregateId);

    void store(UUID aggregateId, EventStream stream);

}

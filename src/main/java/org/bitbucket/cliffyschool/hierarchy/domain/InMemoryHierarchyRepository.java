package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.InMemoryEventStore;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryHierarchyRepository extends InMemoryEventStore implements Repository<Hierarchy> {

    @Override
    public Optional<Hierarchy> findById(UUID aggregateId) {
        List<Event> events = getEvents(aggregateId);
        return build(events);
    }

    private static Optional<Hierarchy> build(List<Event> eventList){
        if (eventList == null || eventList.size() < 1)
            return Optional.empty();

        Hierarchy hierarchy = Hierarchy.apply((HierarchyCreated)eventList.get(0));
        List<Event> hierEvents = eventList.subList(1, eventList.size());
        hierEvents.forEach(hierarchy::apply);

        return Optional.of(hierarchy);
    }
}

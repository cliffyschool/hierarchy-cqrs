package org.bitbucket.cliffyschool.hierarchy.domain;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import javaslang.Tuple2;
import org.bitbucket.cliffyschool.hierarchy.cqrs.EventStore;
import org.bitbucket.cliffyschool.hierarchy.cqrs.EventStream;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InMemoryHierarchyRepository implements EventStore<Hierarchy> {

    private ListMultimap<UUID,Event> events = ArrayListMultimap.create();

    @Override
    public Optional<Hierarchy> findById(UUID aggregateId) {
        return
        Optional.ofNullable(events.get(aggregateId))
                .filter(e -> e.size() > 0)
                .flatMap(InMemoryHierarchyRepository::build);
    }

    private static Optional<Hierarchy> build(List<Event> eventList){
        if (eventList == null || eventList.size() < 1)
            return Optional.empty();

        Hierarchy hierarchy = Hierarchy.apply((HierarchyCreated)eventList.get(0));
        List<Event> hierEvents = eventList.subList(1, eventList.size());
        hierEvents.forEach(hierarchy::apply);

        return Optional.of(hierarchy);
    }

    @Override
    public void store(UUID aggregateId, EventStream stream) {
        if (stream == null || stream.getEvents().isEmpty())
            return;

        synchronized (events){
            long lastEventVersionId = Optional.ofNullable(this.events.get(aggregateId))
                    .filter(e -> e.size() > 0)
                    .map(Iterables::getLast)
                    .map(Event::getVersionId)
                    .orElse(0L);

            long newStreamVersionId = Iterables.getLast(stream.getEvents()).getVersionId();

            if (lastEventVersionId > 0 && lastEventVersionId >  newStreamVersionId)
                throw new RuntimeException(String.format("Aggregate with id '%s' has been updated since last retrieval.", aggregateId));

            if (lastEventVersionId > 0 && lastEventVersionId < newStreamVersionId)
                throw new RuntimeException(String.format("Invalid version id specified. Are you trying to skip versions?"));

            List<Event> descriptors =
            IntStream.range(0, stream.getEvents().size())
                    .mapToObj(i -> new Tuple2<>(lastEventVersionId + i + 1, stream.getEvents().get(i)))
                    .map(tup -> tup._2.copy(tup._1))
                    .collect(Collectors.toList());

            this.events.putAll(aggregateId, descriptors);
        }
    }
}

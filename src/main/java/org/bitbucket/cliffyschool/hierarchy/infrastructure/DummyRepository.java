package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

public class DummyRepository<K,T extends AggregateRoot>  implements Repository<K,T> {

    private Map<K, T> map = Maps.newHashMap();

    @Override
    public Optional<T> findById(K aggregateId) {

        Optional<T> aggregate = Optional.ofNullable(map.get(aggregateId));
        aggregate.ifPresent(agg -> agg.changeEvents.clear());
        return Optional.ofNullable(map.get(aggregateId));
    }

    @Override
    public void store(K aggregateId, T item, long versionBeingModified) {
        long versionId = item.versionId;
        if (versionId > 0 && versionId > versionBeingModified)
            throw new RuntimeException(String.format("Aggregate with id '%s' has been updated since last retrieval.", aggregateId));

        if (versionId > 0 && versionId < versionBeingModified)
            throw new RuntimeException(String.format("Invalid version id specified. Are you trying to skip versions?"));

        item.versionId = item.versionId + 1;
        map.put(aggregateId, item);
    }
}

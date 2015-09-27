package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import java.util.Optional;
import java.util.UUID;

public interface Repository <K,T extends AggregateRoot> {
    Optional<T> findById(K aggregateId);
    void store(K aggregateId, T item, long versionBeingModified);
}

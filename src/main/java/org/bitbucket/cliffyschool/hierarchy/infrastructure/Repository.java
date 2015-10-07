package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import java.util.Optional;

public interface Repository <K,T extends IAggregateRoot> {
    Optional<T> findById(K aggregateId);
    void store(K aggregateId, T item, long versionBeingModified);
}

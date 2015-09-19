package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import java.util.Optional;
import java.util.UUID;

public interface Repository <T> {
    Optional<T> findById(UUID aggregateId);
}

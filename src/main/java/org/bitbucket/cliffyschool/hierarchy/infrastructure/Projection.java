package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import java.util.Optional;
import java.util.UUID;

public interface Projection<T> {
    void write(UUID id, T viewDto);

    Optional<T> find(UUID id);
}

package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import java.util.Optional;
import java.util.UUID;

public interface Projection<K,T> {
    void write(K id, T viewDto);

    Optional<T> find(K id);
}

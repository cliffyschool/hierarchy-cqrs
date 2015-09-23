package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DummyProjectionStore <K,T> {

    private Map<K, T> objectsById = Maps.newHashMap();

    public void write(K id, T viewDto){
        objectsById.put(id, viewDto);
    }

    public Optional<T> find(K id) {
        return Optional.ofNullable(objectsById.get(id));
    }
}

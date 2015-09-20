package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DummyProjectionStore <T> {

    private Map<UUID, T> objectsById = Maps.newHashMap();

    public void write(UUID id, T viewDto){
        objectsById.put(id, viewDto);
    }

    public Optional<T> find(UUID id) {
        return Optional.ofNullable(objectsById.get(id));
    }
}

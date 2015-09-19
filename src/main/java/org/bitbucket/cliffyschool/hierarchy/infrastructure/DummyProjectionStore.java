package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DummyProjectionStore <T> {

    private Map<UUID, T> viewsById = Maps.newHashMap();

    public void write(UUID id, T viewDto){
        viewsById.put(id, viewDto);
    }

    public Optional<T> find(UUID id) {
        return Optional.ofNullable(viewsById.get(id));
    }
}

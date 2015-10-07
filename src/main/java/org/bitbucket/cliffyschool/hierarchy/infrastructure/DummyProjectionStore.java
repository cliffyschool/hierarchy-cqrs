package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;

public class DummyProjectionStore <K,T> {

    protected Map<K, T> objectsById = Maps.newHashMap();

    public void write(K id, T viewDto){
        objectsById.put(id, viewDto);
    }

    public Optional<T> find(K id) {
        return Optional.ofNullable(objectsById.get(id));
    }
}

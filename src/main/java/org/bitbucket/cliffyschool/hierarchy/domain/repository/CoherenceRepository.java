package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import org.bitbucket.cliffyschool.hierarchy.application.exception.ConcurrencyException;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.AggregateRoot;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.Repository;

import java.util.Optional;

public class CoherenceRepository<K,T extends AggregateRoot> implements Repository<K, T> {
    protected NamedCache cache;

    public CoherenceRepository(String cacheName, Class<T> clazz) {
        cache = CacheFactory.getCache(cacheName,clazz.getClassLoader());
    }

    public Optional<T> findById(K key) {
        T item = null;
        cache.lock(key, -1);
        try {
            item = (T)cache.get(key);
        }
        finally {
            cache.unlock(key);
        }
        return Optional.ofNullable(item);
    }

    public void store(K key, T item, long versionBeingModified) {
        cache.lock(key, -1);
        T hierarchy = null;
        try {
            hierarchy = (T)cache.get(key);
            if (hierarchy != null && hierarchy.getVersionId() > versionBeingModified)
                throw new ConcurrencyException("Stale write!");

            long newVersionId = Optional.ofNullable(hierarchy).map(h -> h.getVersionId() + 1).orElse(1L);

            cache.put(key, item.withVersionId(newVersionId));
        }
        finally {
            cache.unlock(key);
        }
    }
}

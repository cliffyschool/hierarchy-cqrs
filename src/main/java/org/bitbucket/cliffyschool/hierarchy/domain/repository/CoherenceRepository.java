package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import org.bitbucket.cliffyschool.hierarchy.application.exception.ConcurrencyException;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.IAggregateRoot;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.Repository;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.SupportsPof;

import java.util.Optional;

public abstract class CoherenceRepository<K,T extends IAggregateRoot> implements Repository<K, T> {
    protected NamedCache cache;

    public CoherenceRepository(String cacheName, Class<? extends T> clazz) {
        cache = CacheFactory.getCache(cacheName,clazz.getClassLoader());
    }

    abstract void put(K key, T value);
    abstract T get(K key);

    public Optional<T> findById(K key) {
        T item = null;
        cache.lock(key, -1);
        try {
            item = get(key);
        }
        finally {
            cache.unlock(key);
        }
        return Optional.ofNullable(item);
    }

    public void store(K key, T item, long versionBeingModified) {
        cache.lock(key, -1);
        T existing = null;
        try {
            existing = get(key);
            if (existing != null && existing.getVersionId() > versionBeingModified)
                throw new ConcurrencyException("Stale write!");

            long newVersionId = Optional.ofNullable(existing).map(h -> h.getVersionId() + 1).orElse(1L);
            T itemEntry = (T)item.withVersionId(newVersionId);
            put(key, itemEntry);
        }
        finally {
            cache.unlock(key);
        }
    }
}

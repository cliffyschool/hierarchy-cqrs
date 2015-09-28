package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;

import java.util.Optional;
import java.util.UUID;

public class CoherenceHierarchyRepository implements HierarchyRepository {
    private NamedCache cache;

    public CoherenceHierarchyRepository(){
        cache = CacheFactory.getCache("hierarchyCache", Hierarchy.class.getClassLoader());
    }


    @Override
    public Optional<Hierarchy> findById(UUID aggregateId) {
        Hierarchy hierarchy = (Hierarchy)cache.get(aggregateId.toString());
        return Optional.ofNullable(hierarchy);
    }

    @Override
    public void store(UUID aggregateId, Hierarchy item, long versionBeingModified) {
        cache.put(aggregateId.toString(), item);
    }

}

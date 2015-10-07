package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyImpl;
import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.PofHierarchy;

import java.util.UUID;

public class CoherenceHierarchyRepository extends CoherenceRepository<UUID,Hierarchy> implements HierarchyRepository {

    public CoherenceHierarchyRepository(){
        super("hierarchyCache", HierarchyImpl.class);
    }

    @Override
    void put(UUID key, Hierarchy value) {
        PofHierarchy dto = (PofHierarchy)((HierarchyImpl) value).asPof();
        cache.put(key, dto);
    }

    @Override
    Hierarchy get(UUID key) {
        PofHierarchy dto = (PofHierarchy)cache.get(key);
        return dto == null ? null : dto.asHierarchy();
    }
}

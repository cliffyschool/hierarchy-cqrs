package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import org.bitbucket.cliffyschool.hierarchy.application.exception.ConcurrencyException;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;

import java.util.Optional;
import java.util.UUID;

public class CoherenceHierarchyRepository extends CoherenceRepository<UUID,Hierarchy> implements HierarchyRepository {

    public CoherenceHierarchyRepository(){
        super("hierarchyCache", Hierarchy.class);
    }
}

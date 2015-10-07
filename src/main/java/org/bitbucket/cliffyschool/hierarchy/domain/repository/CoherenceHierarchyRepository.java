package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyImpl;
import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;

import java.util.UUID;

public class CoherenceHierarchyRepository extends CoherenceRepository<UUID,Hierarchy> implements HierarchyRepository {

    public CoherenceHierarchyRepository(){
        super("hierarchyCache", HierarchyImpl.class);
    }
}

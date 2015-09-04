package org.bitbucket.cliffyschool.hierarchy.domain;

import java.util.Optional;
import java.util.UUID;

public interface HierarchyRepository {
    void save(Hierarchy hierarchy);
    Optional<Hierarchy> findById(UUID id);
}

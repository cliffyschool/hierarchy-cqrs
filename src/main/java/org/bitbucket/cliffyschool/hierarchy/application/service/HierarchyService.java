package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;

import java.util.Optional;
import java.util.UUID;

public interface HierarchyService {

    void saveHierarchy(Hierarchy hierarchy);

    void createNewNode(UUID hierarchyId, String nodeName);

    Optional<HierarchyAsGrid> getHierarchyAsGrid(UUID id);
}

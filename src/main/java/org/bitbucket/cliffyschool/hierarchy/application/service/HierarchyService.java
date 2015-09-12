package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;

import java.util.Optional;
import java.util.UUID;

public interface HierarchyService {

    void saveHierarchy(Hierarchy hierarchy);

    void createNewNode(UUID hierarchyId, CreateNodeCommand createNodeCommand);

    Optional<HierarchyAsGrid> getHierarchyAsGrid(UUID id);
}

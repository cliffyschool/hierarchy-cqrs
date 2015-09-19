package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.event.CreateHierarchyCommand;

import java.util.Optional;
import java.util.UUID;

public interface HierarchyService {

    void createNewHierarchy(CreateHierarchyCommand createHierarchyCommand);
    void createNewNode(UUID hierarchyId, CreateNodeCommand createNodeCommand);
    void changeNodeName(UUID hierarchyId, ChangeNodeNameCommand changeNodeNameCommand);

    Optional<HierarchyAsGrid> getHierarchyAsGrid(UUID id);
}

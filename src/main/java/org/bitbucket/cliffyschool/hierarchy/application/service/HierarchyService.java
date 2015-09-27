package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildList;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateHierarchyCommand;

import java.util.Optional;
import java.util.UUID;

public interface HierarchyService {

    void createNewHierarchy(CreateHierarchyCommand createHierarchyCommand);
    void createNewNode(CreateNodeCommand createNodeCommand);
    void changeNodeName(ChangeNodeNameCommand changeNodeNameCommand);

    Optional<HierarchyAsGrid> getHierarchyAsGrid(UUID id);

    Optional<ChildList> getChildList(UUID hierarchyId, Optional<UUID> parentNodeId);
}

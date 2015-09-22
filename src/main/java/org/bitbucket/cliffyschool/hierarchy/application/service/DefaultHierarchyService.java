package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjectionKey;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildList;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjection;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.InMemoryHierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.command.CreateHierarchyCommand;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.FakeBus;

import java.util.Optional;
import java.util.UUID;

public class DefaultHierarchyService implements HierarchyService {

    private final HierarchyAsGridProjection gridProjection;
    private ChildListProjection childListProjection;
    private InMemoryHierarchyRepository hierarchyRepository;
    private FakeBus fakeBus;

    public DefaultHierarchyService(InMemoryHierarchyRepository hierarchyRepository,
                                   ChildListProjection childListProjection,
                                   HierarchyAsGridProjection gridProjection,
                                   FakeBus fakeBus)
    {
        this.hierarchyRepository = hierarchyRepository;
        this.childListProjection = childListProjection;
        this.gridProjection = gridProjection;
        this.fakeBus = fakeBus;
    }


    @Override
    public void createNewHierarchy(CreateHierarchyCommand createHierarchyCommand) {
        EventStream stream = Hierarchy.createNewHierarchy(createHierarchyCommand);
        this.hierarchyRepository.store(createHierarchyCommand.getHierarchyId(), stream, 0L);

        fakeBus.publish(stream);
    }

    @Override
    public void createNewNode(UUID hierarchyId, CreateNodeCommand createNodeCommand) {
        Hierarchy hierarchy = hierarchyRepository.findById(hierarchyId)
                .orElseThrow(() -> new ObjectNotFoundException("ChildList", hierarchyId));

        EventStream stream = hierarchy.createNode(createNodeCommand);
        hierarchyRepository.store(hierarchyId, stream, createNodeCommand.getBaseVersionId());

        fakeBus.publish(stream);
    }

    @Override
    public void changeNodeName(UUID hierarchyId, ChangeNodeNameCommand changeNodeNameCommand) {
        Hierarchy hier = hierarchyRepository.findById(hierarchyId)
                .orElseThrow(() -> new ObjectNotFoundException("ChildList", hierarchyId));

        EventStream stream = hier.changeNodeName(changeNodeNameCommand);
        hierarchyRepository.store(hierarchyId, stream, changeNodeNameCommand.getBaseVersionId());

        fakeBus.publish(stream);
    }

    @Override
    public Optional<HierarchyAsGrid> getHierarchyAsGrid(UUID hierarchyId) {
        return gridProjection.find(hierarchyId);
    }

    @Override
    public Optional<ChildList> getChildList(UUID hierarchyId, Optional<UUID> parentNodeId) {
        return childListProjection.find(new ChildListProjectionKey(hierarchyId, parentNodeId));
    }
}

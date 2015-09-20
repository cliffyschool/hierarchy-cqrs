package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.hierarchy.HierarchyProjection;
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
    private HierarchyProjection hierarchyProjection;
    private InMemoryHierarchyRepository hierarchyRepository;
    private FakeBus fakeBus;

    public DefaultHierarchyService(InMemoryHierarchyRepository hierarchyRepository,
                                   HierarchyProjection hierarchyProjection,
                                   HierarchyAsGridProjection gridProjection,
                                   FakeBus fakeBus)
    {
        this.hierarchyRepository = hierarchyRepository;
        this.hierarchyProjection = hierarchyProjection;
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
        Hierarchy hier = hierarchyRepository.findById(hierarchyId)
                .orElseThrow(() -> new RuntimeException(String.format("Hierarchy %s not found.", hierarchyId)));

        EventStream stream = hier.createNode(createNodeCommand);
        hierarchyRepository.store(hierarchyId, stream, createNodeCommand.getBaseVersionId());

        fakeBus.publish(stream);
    }

    @Override
    public void changeNodeName(UUID hierarchyId, ChangeNodeNameCommand changeNodeNameCommand) {
        Hierarchy hier = hierarchyRepository.findById(hierarchyId)
                .orElseThrow(() -> new RuntimeException(String.format("Hierarchy %s not found.", hierarchyId)));

        EventStream stream = hier.changeNodeName(changeNodeNameCommand);
        hierarchyRepository.store(hierarchyId, stream, changeNodeNameCommand.getBaseVersionId());

        fakeBus.publish(stream);
    }

    @Override
    public Optional<HierarchyAsGrid> getHierarchyAsGrid(UUID hierarchyId) {
        return gridProjection.find(hierarchyId);
    }

    @Override
    public Optional<org.bitbucket.cliffyschool.hierarchy.application.projection.hierarchy.Hierarchy> getHierarchy(UUID hierarchyId) {
        return hierarchyProjection.find(hierarchyId);
    }
}

package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGridProjection;
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
    private InMemoryHierarchyRepository hierarchyRepository;
    private FakeBus fakeBus;

    public DefaultHierarchyService(InMemoryHierarchyRepository hierarchyRepository,
                                   HierarchyAsGridProjection gridProjection,
                                   FakeBus fakeBus)
    {
        this.hierarchyRepository = hierarchyRepository;
        this.gridProjection = gridProjection;
        this.fakeBus = fakeBus;
    }


    @Override
    public void createNewHierarchy(CreateHierarchyCommand createHierarchyCommand) {
        EventStream stream = Hierarchy.createNewHierarchy(createHierarchyCommand);
        this.hierarchyRepository.store(createHierarchyCommand.getHierarchyId(), stream);

        fakeBus.publish(stream);
    }

    @Override
    public void createNewNode(UUID hierarchyId, CreateNodeCommand createNodeCommand) {
        Hierarchy hier = hierarchyRepository.findById(hierarchyId)
                .orElseThrow(() -> new RuntimeException(String.format("Hierarchy %s not found.", hierarchyId)));

        EventStream stream = hier.createNode(createNodeCommand);
        hierarchyRepository.store(hierarchyId, stream);

        fakeBus.publish(stream);
    }

    @Override
    public void changeNodeName(UUID hierarchyId, ChangeNodeNameCommand changeNodeNameCommand) {
        Hierarchy hier = hierarchyRepository.findById(hierarchyId)
                .orElseThrow(() -> new RuntimeException(String.format("Hierarchy %s not found.", hierarchyId)));

        EventStream stream = hier.changeNodeName(changeNodeNameCommand);
        hierarchyRepository.store(hierarchyId, stream);

        fakeBus.publish(stream);
    }

    @Override
    public Optional<HierarchyAsGrid> getHierarchyAsGrid(UUID hierarchyId) {
        return gridProjection.find(hierarchyId);
    }
}

package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjectionKey;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildList;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjection;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.Node;
import org.bitbucket.cliffyschool.hierarchy.domain.NodeRepository;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.command.CreateHierarchyCommand;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.FakeBus;

import java.util.Optional;
import java.util.UUID;

public class DefaultHierarchyService implements HierarchyService {

    private final HierarchyAsGridProjection gridProjection;
    private ChildListProjection childListProjection;
    private HierarchyRepository hierarchyRepository;
    private NodeRepository nodeRepository;
    private FakeBus fakeBus;

    public DefaultHierarchyService(HierarchyRepository hierarchyRepository,
                                   NodeRepository nodeRepository,
                                   ChildListProjection childListProjection,
                                   HierarchyAsGridProjection gridProjection,
                                   FakeBus fakeBus)
    {
        this.hierarchyRepository = hierarchyRepository;
        this.nodeRepository = nodeRepository;
        this.childListProjection = childListProjection;
        this.gridProjection = gridProjection;
        this.fakeBus = fakeBus;
    }


    @Override
    public void createNewHierarchy(CreateHierarchyCommand createHierarchyCommand) {
        Hierarchy hierarchy = Hierarchy.createHierarchy(createHierarchyCommand.getHierarchyId());
        this.hierarchyRepository.store(createHierarchyCommand.getHierarchyId(), hierarchy, 0);

        fakeBus.publish(hierarchy.getChangeEvents());
    }

    @Override
    public void createNewNode(CreateNodeCommand createNodeCommand) {
        Hierarchy hierarchy = hierarchyRepository.findById(createNodeCommand.getHierarchyId())
                .orElseThrow(() -> new ObjectNotFoundException("ChildList", createNodeCommand.getHierarchyId()));

        Node node = Node.createNode(createNodeCommand);
        hierarchy.addNode(createNodeCommand.getParentNodeId(), node);

        hierarchyRepository.store(hierarchy.getId(), hierarchy, hierarchy.getVersionId());
        nodeRepository.store(node.getId(), node, node.getVersionId());

        EventStream eventStream = EventStream.from(node.getChangeEvents().getEvents());
        eventStream.append(hierarchy.getChangeEvents());
        fakeBus.publish(eventStream);
    }

    @Override
    public void changeNodeName(ChangeNodeNameCommand changeNodeNameCommand) {
        Node node = nodeRepository.findById(changeNodeNameCommand.getNodeId())
                .orElseThrow(() -> new ObjectNotFoundException("Node", changeNodeNameCommand.getNodeId()));
        Hierarchy hierarchy = hierarchyRepository.findById(changeNodeNameCommand.getHierarchyId())
                .orElseThrow(() -> new ObjectNotFoundException("Hierarchy", changeNodeNameCommand.getHierarchyId()));

        hierarchy.changeNodeName(changeNodeNameCommand, node);
        nodeRepository.store(changeNodeNameCommand.getNodeId(), node, node.getVersionId());


        fakeBus.publish(hierarchy.getChangeEvents().append(node.getChangeEvents()));
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

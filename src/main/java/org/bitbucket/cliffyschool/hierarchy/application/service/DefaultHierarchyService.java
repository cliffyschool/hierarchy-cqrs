package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.HierarchyService;
import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildList;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjectionKey;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.command.*;
import org.bitbucket.cliffyschool.hierarchy.domain.*;
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
        HierarchyImpl hierarchy = HierarchyImpl.createHierarchy(createHierarchyCommand.getHierarchyId());
        this.hierarchyRepository.store(createHierarchyCommand.getHierarchyId(), hierarchy, 0);

        fakeBus.publish(hierarchy.getChangeEvents());
    }

    @Override
    public void createNewNode(CreateNodeCommand createNodeCommand) {
        Hierarchy hierarchy = hierarchyRepository.findById(createNodeCommand.getHierarchyId())
                .orElseThrow(() -> new ObjectNotFoundException("Hierarchy", createNodeCommand.getHierarchyId()));
        Optional<Node> parentNode = createNodeCommand.getParentNodeId()
                .map(nodeRepository::findById).orElse(Optional.empty());
        if (createNodeCommand.getParentNodeId().isPresent() && !parentNode.isPresent())
            throw new ObjectNotFoundException("Node", createNodeCommand.getParentNodeId().get());

        Node node = Node.createNode(createNodeCommand);
        hierarchy.insertNode(parentNode, node);

        hierarchyRepository.store(hierarchy.getId(), hierarchy, createNodeCommand.getLastHierarchyVersionLoaded());
        nodeRepository.store(node.getId(), node, 0);

        fakeBus.publish(node.getChangeEvents().append(hierarchy.getChangeEvents()));
    }

    @Override
    public void insertNode(InsertNodeCommand insertNodeCommand) {
        Hierarchy hierarchy = hierarchyRepository.findById(insertNodeCommand.getHierarchyId())
                .orElseThrow(() -> new ObjectNotFoundException("Hierarchy", insertNodeCommand.getHierarchyId()));
        Node parentNode = nodeRepository.findById(insertNodeCommand.getParentId())
                .orElseThrow(() -> new ObjectNotFoundException("Node", insertNodeCommand.getParentId()));
        Node childNode = nodeRepository.findById(insertNodeCommand.getNodeId())
                .orElseThrow(() -> new ObjectNotFoundException("Node", insertNodeCommand.getNodeId()));

        hierarchy.insertNode(Optional.of(parentNode), childNode);
        hierarchyRepository.store(hierarchy.getId(), hierarchy, insertNodeCommand.getLastHierarchyVersionLoaded());

        fakeBus.publish(hierarchy.getChangeEvents());
    }

    @Override
    public void changeNodeName(ChangeNodeNameCommand changeNodeNameCommand) {
        Node node = nodeRepository.findById(changeNodeNameCommand.getNodeId())
                .orElseThrow(() -> new ObjectNotFoundException("Node", changeNodeNameCommand.getNodeId()));
        Hierarchy hierarchy = hierarchyRepository.findById(changeNodeNameCommand.getHierarchyId())
                .orElseThrow(() -> new ObjectNotFoundException("Hierarchy", changeNodeNameCommand.getHierarchyId()));

        hierarchy.changeNodeName(changeNodeNameCommand, node);
        hierarchyRepository.store(hierarchy.getId(), hierarchy, changeNodeNameCommand.getLastHierarchyVersionLoaded());
        nodeRepository.store(changeNodeNameCommand.getNodeId(), node, node.getVersionId());


        fakeBus.publish(hierarchy.getChangeEvents().append(node.getChangeEvents()));
    }

    @Override
    public void changeNodePropertyValue(ChangeNodePropertyCommand changeNodePropertyCommand) {
        Node node = nodeRepository.findById(changeNodePropertyCommand.getNodeId())
                .orElseThrow(() -> new ObjectNotFoundException("Node", changeNodePropertyCommand.getNodeId()));

        node.changePropertyValue(changeNodePropertyCommand.getPropertyName(), changeNodePropertyCommand.getPropertyValue());
        nodeRepository.store(node.getId(), node, changeNodePropertyCommand.getLastNodeVersionLoaded());

        fakeBus.publish(node.getChangeEvents());
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

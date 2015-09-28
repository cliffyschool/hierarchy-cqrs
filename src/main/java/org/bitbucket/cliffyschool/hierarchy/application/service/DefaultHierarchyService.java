package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjectionKey;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildList;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjection;
import org.bitbucket.cliffyschool.hierarchy.command.InsertNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.Node;
import org.bitbucket.cliffyschool.hierarchy.domain.NodeRepository;
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
                .orElseThrow(() -> new ObjectNotFoundException("Hierarchy", createNodeCommand.getHierarchyId()));

        Node node = Node.createNode(createNodeCommand);
        Optional<Node> parentNode = createNodeCommand.getParentNodeId()
                .map(nodeRepository::findById).orElse(Optional.empty());
        hierarchy.insertNode(parentNode, node);

        hierarchyRepository.store(hierarchy.getId(), hierarchy, createNodeCommand.getLastHierarchyVersionLoaded());
        nodeRepository.store(node.getId(), node, 0);
        parentNode.ifPresent(pNode -> {
            nodeRepository.store(pNode.getId(), pNode, pNode.getVersionId());
        });

        EventStream eventStream = EventStream.from(node.getChangeEvents().getEvents());
        eventStream.append(hierarchy.getChangeEvents());
        fakeBus.publish(eventStream);
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
        nodeRepository.store(parentNode.getId(), parentNode, parentNode.getVersionId());

        EventStream eventStream = hierarchy.getChangeEvents()
                .append(parentNode.getChangeEvents())
                .append(childNode.getChangeEvents());

        fakeBus.publish(eventStream);
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
    public Optional<HierarchyAsGrid> getHierarchyAsGrid(UUID hierarchyId) {
        return gridProjection.find(hierarchyId);
    }

    @Override
    public Optional<ChildList> getChildList(UUID hierarchyId, Optional<UUID> parentNodeId) {
        return childListProjection.find(new ChildListProjectionKey(hierarchyId, parentNodeId));
    }
}

package org.bitbucket.cliffyschool.hierarchy.domain;

import com.google.common.collect.*;
import org.bitbucket.cliffyschool.hierarchy.application.exception.NameAlreadyUsedException;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.AggregateRoot;
import org.bitbucket.cliffyschool.hierarchy.event.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Hierarchy extends AggregateRoot {

    private Map<UUID, UUID> nodesById = Maps.newHashMap();
    private Map<String, UUID> nodesByName = Maps.newHashMap();
    private ListMultimap<UUID, UUID> childrenByParentId = ArrayListMultimap.create();

    public Hierarchy(UUID id) {
        super(id);
    }

    public static Hierarchy createHierarchy(UUID id) {
        Hierarchy hierarchy = new Hierarchy(id);
        hierarchy.changeEvents.append(new HierarchyCreated(id));
        return hierarchy;
    }

    public boolean containsNode(UUID nodeId) {
        return nodesById.containsKey(nodeId);
    }

    public void insertNode(Optional<Node> parentNode, Node node) {
        if (node == null)
            throw new NullPointerException("node");
        if (nodesByName.containsKey(node.getName()))
            throw new NameAlreadyUsedException("Node", node.getName());

        nodesById.put(node.getId(), node.getId());
        nodesByName.put(node.getName(), node.getId());
        parentNode.ifPresent(pNode -> {
            childrenByParentId.put(pNode.getId(), node.getId());
            pNode.changeChildCount(pNode.getChildCount() + 1);
            changeEvents.append(pNode.getChangeEvents());
        });

        changeEvents.append(new NodeInserted(id, parentNode.map(AggregateRoot::getId), node.getId()));
    }

    public Optional<UUID> getParentId(UUID nodeId) {
        // TODO: replace with parent map
        return childrenByParentId.entries().stream()
                .filter(entry -> entry.getValue().equals(nodeId))
                .findFirst()
                .map(Map.Entry::getKey);
    }

    public void changeNodeName(ChangeNodeNameCommand changeNodeNameCmd, Node node) {
        if (nodesByName.containsKey(changeNodeNameCmd.getNewName()))
            throw new NameAlreadyUsedException("Node", changeNodeNameCmd.getNewName());

        String oldName= node.getName();
        node.changeNodeName(changeNodeNameCmd);
        nodesByName.remove(oldName);
        nodesByName.put(node.getName(), node.getId());

        Optional<UUID> parentId = getParentId(node.getId());
        changeEvents.append(new NodeNameChanged(id, node.getId(), parentId, changeNodeNameCmd.getNewName()));
    }
}

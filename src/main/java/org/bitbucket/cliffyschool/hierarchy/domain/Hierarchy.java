package org.bitbucket.cliffyschool.hierarchy.domain;

import com.google.common.collect.*;
import javaslang.Tuple2;
import org.bitbucket.cliffyschool.hierarchy.application.exception.NameAlreadyUsedException;
import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.AggregateRoot;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.event.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Hierarchy extends AggregateRoot {

    private Map<UUID, Node> nodesById = Maps.newHashMap();
    private Map<String, Node> nodesByName = Maps.newHashMap();
    private ListMultimap<UUID, UUID> childrenByParentId = ArrayListMultimap.create();

    public Hierarchy(UUID id) {
        super(id);
    }

    public static Hierarchy createHierarchy(UUID id) {
        Hierarchy hierarchy = new Hierarchy(id);
        hierarchy.changeEvents.append(new HierarchyCreated(id));
        return hierarchy;
    }

    public Node nodeById(UUID nodeId) {
        return nodesById.get(nodeId);
    }

    public void addNode(Optional<UUID> parentId, Node node) {
        if (node == null)
            throw new NullPointerException("node");
        if (nodesByName.containsKey(node.getName()))
            throw new NameAlreadyUsedException("Node", node.getName());

        Optional<Node> parentNode = parentId.map(nodesById::get);

        nodesById.put(node.getId(), node);
        nodesByName.put(node.getName(), node);
        parentNode.ifPresent(pNode -> {
            childrenByParentId.put(pNode.getId(), node.getId());
            pNode.changeChildCount(pNode.getChildCount() + 1);
            changeEvents.append(pNode.getChangeEvents());
        });

        changeEvents.append(new NodeAddedToHierarchy(id, parentId, node.getId()));
    }

    void changeNodeName(String oldName, String newName){
        Node node = nodesByName.remove(oldName);
        nodesByName.put(newName, node);
    }

    public Optional<UUID> getParentId(UUID nodeId) {
        // TODO: replace with parent map
        return childrenByParentId.entries().stream()
                .filter(entry -> entry.getValue().equals(nodeId))
                .findFirst()
                .map(Map.Entry::getKey);
    }
}

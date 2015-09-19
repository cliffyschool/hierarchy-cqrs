package org.bitbucket.cliffyschool.hierarchy.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateHierarchyCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.event.*;

import java.util.Map;
import java.util.UUID;

public class Hierarchy {

    private Map<String, Node> nodesByName = Maps.newHashMap();
    private Map<UUID,Node> nodesById = Maps.newHashMap();
    private UUID id;
    private long versionId;

    public Hierarchy(UUID id) {
        this.id = id;
        versionId = 1L;
    }

    public void apply(Iterable<Event> events) {
        events.forEach(this::apply);
    }

    public void apply(Event event){
        if (event instanceof NodeCreated)
            apply((NodeCreated)event);
        else if (event instanceof NodeNameChanged)
            apply((NodeNameChanged)event);
    }

    public void apply(NodeCreated event) {
        Node node = new Node(event.getNodeId(), event.getNodeName());
        nodesByName.put(node.getName(), node);
        nodesById.put(node.getId(), node);
        versionId = event.getVersionId();
    }

    public void apply(NodeNameChanged event)
    {
        Node existing = nodesById.get(event.getNodeId());
        if (existing != null)
        {
            Node changedNode = new Node(existing.getId(), event.getNewName());
            nodesById.put(changedNode.getId(), changedNode);
            nodesByName.put(changedNode.getName(), changedNode);
            nodesByName.remove(existing.getName());
            versionId = event.getVersionId();
        }
    }

    public static Hierarchy apply(HierarchyCreated event)
    {
        return new Hierarchy(event.getHierarchyId());
    }

    public static EventStream createNewHierarchy(CreateHierarchyCommand command){
        return EventStream.from(Lists.newArrayList(new HierarchyCreated(command.getHierarchyId())));
    }

    public EventStream createNode(CreateNodeCommand command) {
        if (nodesByName.containsKey(command.getNodeName()))
            throw new RuntimeException(String.format("Node with name '%s' already exists.", command.getNodeName()));
        return EventStream.from(Lists.newArrayList(new NodeCreated(id, versionId, command.getNodeId(), command.getNodeName(), "blue", "circle")));
    }

    public EventStream changeNodeName(ChangeNodeNameCommand command) {

        if (nodesByName.containsKey(command.getNewName()))
            throw new RuntimeException(String.format("Node with name '%s' already exists.", command.getNewName()));
        if (!nodesById.containsKey(command.getNodeId()))
            throw new RuntimeException(String.format("Node '%s' not found.", command.getNodeId()));

        return EventStream.from(Lists.newArrayList(new NodeNameChanged(id, versionId, command.getNodeId(), command.getNewName())));
    }
    public Node nodeById(UUID nodeId) {
        return nodesById.get(nodeId);
    }

    public long getVersionId() {
        return versionId;
    }

    public UUID getId() {
        return id;
    }
}

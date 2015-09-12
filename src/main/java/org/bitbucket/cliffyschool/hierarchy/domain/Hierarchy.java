package org.bitbucket.cliffyschool.hierarchy.domain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.Command;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class Hierarchy {

    private Map<String, Node> nodesByName = Maps.newHashMap();
    private Map<UUID,Node> nodesById = Maps.newHashMap();
    private UUID id;

    public Hierarchy(UUID id) {
        this.id = id;
    }

    public List<Event> apply(Command command) {
        return handleCommand(command);
    }

    private List<Event> handleCommand(Command command){
        // TODO: make a function map
        if (command == null)
            return Lists.newArrayList();
        if (command instanceof CreateNodeCommand)
            return createNode((CreateNodeCommand)command);
        if (command instanceof ChangeNodeNameCommand)
            return changeNodeName((ChangeNodeNameCommand)command);
        return Lists.newArrayList();
    }

    private List<Event> createNode(CreateNodeCommand command) {
        Node node = new Node(UUID.randomUUID(), command.getNodeName());
        nodesByName.put(node.getName(), node);
        nodesById.put(node.getId(), node);
        return Lists.newArrayList(new NodeCreated(id, node.getId(), command.getNodeName(), "blue", "circle"));
    }

    private List<Event> changeNodeName(ChangeNodeNameCommand command) {
        List<Event> events = Lists.newArrayList();

        Node existing = nodesById.get(command.getNodeId());
        if (existing != null)
        {
            Node changedNode = new Node(existing.getId(), command.getNewName());
            nodesById.put(changedNode.getId(), changedNode);
            nodesByName.put(changedNode.getName(), changedNode);
            events.add(new NodeCreated(id, changedNode.getId(), changedNode.getName(), "blue", "circle"));
        }
        return events;
    }
    public Node nodeById(UUID nodeId) {
        return nodesById.get(nodeId);
    }

    public UUID getId() {
        return id;
    }
}

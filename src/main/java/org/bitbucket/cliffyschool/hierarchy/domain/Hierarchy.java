package org.bitbucket.cliffyschool.hierarchy.domain;

import com.google.common.collect.*;
import javaslang.Tuple2;
import org.bitbucket.cliffyschool.hierarchy.application.exception.NameAlreadyUsedException;
import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateHierarchyCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.hierarchy.Node;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.event.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Hierarchy {

    private Map<String, Node> nodesByName = Maps.newHashMap();
    private Map<UUID, Node> nodesById = Maps.newHashMap();
    private ListMultimap<UUID, UUID> childrenByParentId = ArrayListMultimap.create();
    private UUID id;

    public Hierarchy(UUID id) {
        this.id = id;
    }

    public Node nodeById(UUID nodeId) {
        return nodesById.get(nodeId);
    }

    public UUID getId() {
        return id;
    }

    public static EventStream createNewHierarchy(CreateHierarchyCommand command) {
        return EventStream.from(Lists.newArrayList(new HierarchyCreated(command.getHierarchyId())));
    }

    public EventStream createNode(CreateNodeCommand command) {
        if (nodesByName.containsKey(command.getNodeName()))
            throw new NameAlreadyUsedException("Node", command.getNodeName());

        EventStream eventStream = EventStream.from(new NodeCreated( id, command.getNodeId(), command.getNodeName(),
                                                                    command.getColor(), command.getParentNodeId()));

        Optional<Node> parentNode = command.getParentNodeId().map(nodesById::get);
        parentNode.ifPresent(parent -> {
            eventStream.append(new NodePropertyValueChanged<>(  id, command.getParentNodeId().get(),"ChildCount",
                                                                parent.getChildCount() + 1, parent.getChildCount()));
        });

        return eventStream;
    }

    public EventStream changeNodeName(ChangeNodeNameCommand command) {
        if (nodesByName.containsKey(command.getNewName()))
            throw new NameAlreadyUsedException("Node", command.getNewName());
        if (!nodesById.containsKey(command.getNodeId()))
            throw new ObjectNotFoundException("Node", command.getNodeId());

        // TODO: replace with parentsByChildIds map
        Optional<UUID> parentId = childrenByParentId.entries().stream()
                .filter(e -> e.getValue().equals(command.getNodeId())).findFirst()
                .map(Map.Entry::getKey);

        return EventStream.from(Lists.newArrayList(new NodeNameChanged(id, command.getNodeId(), parentId, command.getNewName())));
    }

    public void apply(Event event) {
        if (event instanceof NodeCreated)
            apply((NodeCreated) event);
        else if (event instanceof NodeNameChanged)
            apply((NodeNameChanged) event);
        else if (event instanceof NodePropertyValueChanged)
            apply((NodePropertyValueChanged<?>) event);
    }

    public void apply(NodeCreated event) {
        Node node = new Node(event.getNodeId(), event.getNodeName(), event.getNodeColor());
        nodesByName.put(node.getName(), node);
        nodesById.put(node.getId(), node);
        event.getParentNodeId().ifPresent(pId -> {
            childrenByParentId.put(pId, node.getId());
        });
    }

    public void apply(NodePropertyValueChanged<?> event) {
        Node node = Optional.ofNullable(nodesById.get(event.getNodeId()))
                .orElseThrow(() -> new ObjectNotFoundException("Node", event.getNodeId()));
        node.applyPropertyValueChange(event.getPropertyName(), event.getNewValue());
    }

    public void apply(NodeNameChanged event) {
        Optional.ofNullable(nodesById.get(event.getNodeId()))
                .map(n -> new Tuple2<>(n.getName(), new Node(n.getId(), event.getNewName(), n.getColor())))
                .ifPresent(n -> {
                    nodesById.put(n._2.getId(), n._2);
                    nodesByName.put(n._2.getName(), n._2);
                    nodesByName.remove(n._1);
                });
    }

    public static Hierarchy apply(HierarchyCreated event) {
        return new Hierarchy(event.getHierarchyId());
    }

    public void apply(Iterable<Event> events) {
        events.forEach(this::apply);
    }
}

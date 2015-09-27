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

    @Override
    public Event creationEvent() {
        return new HierarchyCreated(id);
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
        parentId.ifPresent(pId -> childrenByParentId.put(pId, node.getId()));

        EventStream eventStream = EventStream.from(Lists.newArrayList());
        eventStream.append(new NodeAddedToHierarchy(id, parentId, node.getId()));
        parentNode.ifPresent(parent -> {
            eventStream.append(new NodePropertyValueChanged<>(  id, parentId.get(),"ChildCount",
                                                                parent.getChildCount() + 1, parent.getChildCount()));
        });

        apply(eventStream.getEvents());

        changeEvents.append(eventStream);
    }


    private void apply(Event event) {
        if (event instanceof NodeCreated)
            apply((NodeCreated) event);
        else if (event instanceof NodeNameChanged)
            apply((NodeNameChanged) event);
        else if (event instanceof NodePropertyValueChanged)
            apply((NodePropertyValueChanged<?>) event);
    }


    private void apply(NodePropertyValueChanged<?> event) {
        Node node = Optional.ofNullable(nodesById.get(event.getNodeId()))
                .orElseThrow(() -> new ObjectNotFoundException("Node", event.getNodeId()));
        node.applyPropertyValueChange(event.getPropertyName(), event.getNewValue());
    }

    private void apply(Iterable<Event> events) {
        events.forEach(this::apply);
    }

    public Optional<UUID> getParentId(UUID nodeId) {
        // TODO: replace with parent map
        return childrenByParentId.entries().stream()
                .filter(entry -> entry.getValue().equals(nodeId))
                .findFirst()
                .map(Map.Entry::getKey);
    }
}

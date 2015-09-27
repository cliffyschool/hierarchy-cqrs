package org.bitbucket.cliffyschool.hierarchy.application.projection.childlist;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.event.*;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.ProjectionHandler;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class HierarchyProjectionUpdater implements ProjectionHandler {

    private Map<Class<? extends Event>,Consumer<? super Event>> handlers =
            new ImmutableMap.Builder<Class<? extends Event>,Consumer<? super Event>>()
                    .put(HierarchyCreated.class, e -> handle((HierarchyCreated) e))
                    .put(NodeCreated.class, e -> handle((NodeCreated) e))
                    .put(NodeAddedToHierarchy.class, e -> handle((NodeAddedToHierarchy)e))
                    .put(NodeNameChanged.class, e -> handle((NodeNameChanged) e))
            .build();

    private ChildListProjection childListProjection;

    public HierarchyProjectionUpdater(ChildListProjection childListProjection) {
        this.childListProjection = childListProjection;
    }

    @Override
    public void write(EventStream eventStream) {
        if (eventStream == null)
            return;

        eventStream.getEvents().stream()
                .forEach(event -> Optional.ofNullable(handlers.get(event.getClass()))
                        .ifPresent(handler -> handler.accept(event)));
    }

    private void handle(HierarchyCreated e){
        ChildListProjectionKey key = new ChildListProjectionKey(e.getHierarchyId(), Optional.empty());
        childListProjection.write(key, new ChildList(e.getHierarchyId(), Lists.newArrayList()));
    }

    private void handle(NodeCreated e) {
        ChildListProjectionKey key = new ChildListProjectionKey(e.getHierarchyId(), Optional.empty());
        ChildList childList = childListProjection.find(key)
                .orElse(new ChildList(null, Lists.newArrayList()));

        Node newNode = new Node(e.getNodeId(), e.getNodeName(), e.getNodeColor());
        childList.getNodes().add(newNode);

        childListProjection.write(key, childList);
    }

    private void handle(NodeAddedToHierarchy e){

        ChildListProjectionKey key = new ChildListProjectionKey(e.getHierarchyId(), e.getParentNodeId());
        ChildList childList = childListProjection.find(key)
                .orElse(new ChildList(e.getParentNodeId().orElse(null), Lists.newArrayList()));

        ChildList rootChildList = childListProjection.find(new ChildListProjectionKey(e.getHierarchyId(), Optional.empty()))
                .orElseThrow(() -> new ObjectNotFoundException("ChildList", e.getHierarchyId()));

        Node node = rootChildList.getNodes().stream()
                .filter(n -> n.getNodeId().equals(e.getChildNodeId()))
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("Node", e.getChildNodeId()));

        childList.getNodes().add(node);
        rootChildList.getNodes().remove(node);

        childListProjection.write(key, childList);
    }

    private void handle(NodeNameChanged e){
        ChildListProjectionKey key = new ChildListProjectionKey(e.getHierarchyId(), e.getParentNodeId());
        ChildList childList = childListProjection.find(key)
                .orElseThrow(() -> new ObjectNotFoundException("ChildList", e.getHierarchyId()));

        childList.getNodes().stream().filter(n -> n.getNodeId().equals(e.getNodeId())).findFirst()
                        .ifPresent(n -> n.setName(e.getNewName()));
        childListProjection.write(key, childList);
    }
}

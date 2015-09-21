package org.bitbucket.cliffyschool.hierarchy.application.projection.hierarchy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.event.*;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.ProjectionHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;

public class HierarchyProjectionUpdater implements ProjectionHandler {

    private Map<Class<? extends Event>,Consumer<? super Event>> handlers =
            new ImmutableMap.Builder<Class<? extends Event>,Consumer<? super Event>>()
                    .put(HierarchyCreated.class, e -> handle((HierarchyCreated) e))
                    .put(NodeCreated.class, e -> handle((NodeCreated) e))
                    .put(NodeNameChanged.class, e -> handle((NodeNameChanged) e))
            .build();

    private HierarchyProjection hierarchyProjection;

    public HierarchyProjectionUpdater(HierarchyProjection hierarchyProjection) {
        this.hierarchyProjection = hierarchyProjection;
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
        HierarchyProjectionKey key = new HierarchyProjectionKey(e.getHierarchyId(), e.g)
        hierarchyProjection.write(e.getHierarchyId(), new Hierarchy(e.getHierarchyId(), Lists.newArrayList()));
    }

    private void handle(NodeCreated e){
        Hierarchy hierarchy = hierarchyProjection.find(e.getHierarchyId())
                .orElseThrow(() -> new ObjectNotFoundException("Hierarchy", e.getHierarchyId()));

        Node newNode = new Node(e.getNodeId(), e.getNodeName(), e.getNodeColor());
        if (e.getParentNodeId().isPresent()) {
            Stack<Node> nodeStack = new Stack<>();
            hierarchy.getNodes().stream().forEach(nodeStack::push);
            List<Node> nodeList = Lists.newArrayList();
            collectNodes(nodeStack, nodeList);

            nodeList.stream()
                    .filter(n -> n.getNodeId().equals(e.getParentNodeId().get()))
                    .findFirst()
                    .ifPresent(parent -> parent.getChildren().add(newNode));
        }
        else {
            hierarchy.getNodes().add(newNode);
        }

        hierarchyProjection.write(hierarchy.getId(), hierarchy);
    }

    private void handle(NodeNameChanged e){
        Hierarchy hierarchy = hierarchyProjection.find(e.getHierarchyId())
                .orElseThrow(() -> new ObjectNotFoundException("Hierarchy", e.getHierarchyId()));

        hierarchy.getNodes().stream().filter(n -> n.getNodeId().equals(e.getNodeId())).findFirst()
                        .ifPresent(n -> n.setName(e.getNewName()));
        hierarchyProjection.write(hierarchy.getId(), hierarchy);
    }

    private static void collectNodes(Stack<Node> nodeStack, List<Node> nodeList){
        if (nodeStack.empty())
            return;

        Node node = nodeStack.pop();

        nodeList.add(node);
        node.getChildren().stream().forEach(nodeStack::push);

        collectNodes(nodeStack, nodeList);
    }
}

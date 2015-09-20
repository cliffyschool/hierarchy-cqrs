package org.bitbucket.cliffyschool.hierarchy.application.projection.hierarchy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.event.*;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.ProjectionHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiConsumer;

public class HierarchyProjectionUpdater implements ProjectionHandler{

    private static Map<Class<? extends Event>,BiConsumer<? super Event,HierarchyProjection>> handlers =
            new ImmutableMap.Builder<Class<? extends Event>,BiConsumer<? super Event, HierarchyProjection>>()
                    .put(HierarchyCreated.class, (e, p) -> writeHierarchyCreated((HierarchyCreated) e, p))
                    .put(NodeCreated.class, (e, p) -> writeNodeCreated((NodeCreated) e, p))
                    .put(NodeNameChanged.class, (e, p) -> writeNodeNameChanged((NodeNameChanged) e, p))
            .build();

    private HierarchyProjection hierarchyProjection;

    public HierarchyProjectionUpdater(HierarchyProjection hierarchyProjection) {
        this.hierarchyProjection = hierarchyProjection;
    }

    @Override
    public void write(EventStream stream) {
        if (stream == null)
            return;

        stream.getEvents().stream()
                .forEach(event -> Optional.ofNullable(handlers.get(event.getClass()))
                        .ifPresent(writer -> writer.accept(event, hierarchyProjection)));
    }

    private static void writeHierarchyCreated(HierarchyCreated e, HierarchyProjection projection){
        projection.write(e.getHierarchyId(), new Hierarchy(e.getHierarchyId(), Lists.newArrayList()));
    }

    private static void writeNodeCreated(NodeCreated e, HierarchyProjection projection){
        Hierarchy hierarchy = projection.find(e.getHierarchyId())
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

        projection.write(hierarchy.getId(), hierarchy);
    }

    private static void writeNodeNameChanged(NodeNameChanged e, HierarchyProjection projection){
        Hierarchy hierarchy = projection.find(e.getHierarchyId())
                .orElseThrow(() -> new ObjectNotFoundException("Hierarchy", e.getHierarchyId()));

        hierarchy.getNodes().stream().filter(n -> n.getNodeId().equals(e.getNodeId())).findFirst()
                        .ifPresent(n -> n.setName(e.getNewName()));
        projection.write(hierarchy.getId(), hierarchy);
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

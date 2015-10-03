package org.bitbucket.cliffyschool.hierarchy.application.projection.grid;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.event.*;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.ProjectionHandler;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class HierarchyAsGridProjectionUpdater implements ProjectionHandler {

    private Map<Class<? extends Event>,Consumer<? super Event>> handlers =
            new ImmutableMap.Builder<Class<? extends Event>,Consumer<? super Event>>()
                    .put(HierarchyCreated.class, e -> handle((HierarchyCreated) e))
                    .put(NodeCreated.class, e -> handle((NodeCreated) e))
                    .put(NodeNameChanged.class, e -> handle((NodeNameChanged) e))
                    .put(NodePathChanged.class, e -> handle((NodePathChanged) e))
            .build();

    private HierarchyAsGridProjection gridProjection;

    public HierarchyAsGridProjectionUpdater(HierarchyAsGridProjection gridProjection) {
        this.gridProjection = gridProjection;
    }

    public void write(EventStream stream){
        if (stream == null)
            return;

        stream.getEvents().stream()
                .forEach(event -> Optional.ofNullable(handlers.get(event.getClass()))
                        .ifPresent(writer -> writer.accept(event)));
    }

    private void handle(HierarchyCreated hierarchyCreated){
         gridProjection.write(hierarchyCreated.getHierarchyId(), new HierarchyAsGrid(hierarchyCreated.getHierarchyId(), Lists.newArrayList()));
    }

    private void handle(NodeCreated nodeCreated){
        HierarchyAsGrid grid = gridProjection.find(nodeCreated.getHierarchyId())
                .orElseThrow(() -> new RuntimeException("Can't find hierarchy."));

        grid.getRows().add(new NodeAsRow(nodeCreated.getNodeId(), nodeCreated.getNodeName(), nodeCreated.getNodeColor()));

        gridProjection.write(grid.getId(), grid);
    }

     private void handle(NodeNameChanged nodeNameChanged){
        HierarchyAsGrid grid = gridProjection.find(nodeNameChanged.getHierarchyId())
                .orElseThrow(() -> new RuntimeException("Can't find hierarchy."));
         grid.getRows().stream()
                 .filter(n -> n.getNodeId().equals(nodeNameChanged.getNodeId())).findAny()
                 .ifPresent(n -> n.setName(nodeNameChanged.getNewName()));
         gridProjection.write(grid.getId(), grid);
    }

    private void handle(NodePathChanged nodePathChanged) {
        HierarchyAsGrid grid = gridProjection.find(nodePathChanged.getHierarchyId())
                .orElseThrow(() -> new RuntimeException("Can't find hierarchy."));
        grid.getRows().stream()
                 .filter(n -> n.getNodeId().equals(nodePathChanged.getNodeId())).findAny()
                 .ifPresent(n -> n.setNodePath(nodePathChanged.getNodePath()));
        gridProjection.write(grid.getId(), grid);
    }
}

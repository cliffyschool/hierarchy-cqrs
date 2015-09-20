package org.bitbucket.cliffyschool.hierarchy.application.projection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class HierarchyAsGridProjectionUpdater {

    private static Map<Class<? extends Event>,BiConsumer<? super Event,HierarchyAsGridProjection>> handlers =
            new ImmutableMap.Builder<Class<? extends Event>,BiConsumer<? super Event, HierarchyAsGridProjection>>()
                    .put(HierarchyCreated.class, (e, p) -> writeHierarchyCreated((HierarchyCreated) e, p))
                    .put(NodeCreated.class, (e, p) -> writeNodeCreated((NodeCreated) e, p))
                    .put(NodeNameChanged.class, (e, p) -> writeNodeNameChanged((NodeNameChanged) e, p))
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
                        .ifPresent(writer -> writer.accept(event, gridProjection)));
    }

    private static void writeHierarchyCreated(HierarchyCreated hierarchyCreated, HierarchyAsGridProjection gridProjection){
         gridProjection.write(hierarchyCreated.getHierarchyId(), new HierarchyAsGrid(hierarchyCreated.getHierarchyId(), Lists.newArrayList()));
    }

    private static void writeNodeCreated(NodeCreated nodeCreated, HierarchyAsGridProjection gridProjection){
        gridProjection.find(nodeCreated.getHierarchyId())
        .ifPresent(grid -> {
            grid.getNodes().removeIf(n -> StringUtils.equals(nodeCreated.getNodeName(), n.getName()));
            grid.getNodes().add(new FlatNode(nodeCreated.getNodeId(), nodeCreated.getNodeName(), nodeCreated.getNodeColor()));
        });
    }

     private static void writeNodeNameChanged(NodeNameChanged nodeNameChanged, HierarchyAsGridProjection gridProjection){
        gridProjection.find(nodeNameChanged.getHierarchyId())
        .ifPresent(grid -> {
            Optional<FlatNode> existing = grid.getNodes().stream()
                    .filter(n -> n.getNodeId().equals(nodeNameChanged.getNodeId())).findAny();
            existing.ifPresent(n -> n.setName(nodeNameChanged.getNewName()));
        });
    }
}

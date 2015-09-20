package org.bitbucket.cliffyschool.hierarchy.application.projection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.ProjectionHandler;

import java.util.Map;
import java.util.Optional;
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
                .orElseThrow(() -> new RuntimeException("Can't find hierarchy"));

        hierarchy.getNodes().removeIf(n -> n.getNodeId().equals(e.getHierarchyId()));
        hierarchy.getNodes().add(new Node(e.getNodeId(), e.getNodeName(), e.getNodeColor()));
        projection.write(hierarchy.getId(), hierarchy);
    }

    private static void writeNodeNameChanged(NodeNameChanged e, HierarchyProjection projection){
        Hierarchy hierarchy = projection.find(e.getHierarchyId())
                .orElseThrow(() -> new RuntimeException("Can't find hierarchy"));

        hierarchy.getNodes().stream().filter(n -> n.getNodeId().equals(e.getNodeId())).findFirst()
                        .ifPresent(n -> n.setName(e.getNewName()));
        projection.write(hierarchy.getId(), hierarchy);
    }
}

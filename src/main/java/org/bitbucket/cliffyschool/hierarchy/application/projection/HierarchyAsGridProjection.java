package org.bitbucket.cliffyschool.hierarchy.application.projection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cliffyschool.hierarchy.cqrs.EventStream;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNamedChanged;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HierarchyAsGridProjection {

    private Map<UUID,HierarchyAsGrid> grids = Maps.newHashMap();

    public void write(EventStream stream){
        if (stream == null)
            return;

        stream.getEvents().forEach(this::writeEvent);
    }

    public Optional<HierarchyAsGrid> findHierarchyAsGrid(UUID hierarchyId){
        return Optional.ofNullable(grids.get(hierarchyId));
    }

    private void writeEvent(Event event)
    {
        if (event instanceof NodeCreated)
        {
            NodeCreated nc = (NodeCreated)event;
            Optional.ofNullable(grids.get(event.getHierarchyId()))
                    .ifPresent(grid -> {
                                grid.getNodes().removeIf(n -> StringUtils.equals(nc.getNodeName(), n.getName()));
                                grid.getNodes().add(new NodeDto(nc.getNodeId(), nc.getNodeName(), nc.getNodeColor(), nc.getNodeShape()));
                    });
        }
        else if (event instanceof HierarchyCreated)
        {
            HierarchyCreated hc = (HierarchyCreated)event;
            grids.put(hc.getHierarchyId(), new HierarchyAsGrid(hc.getHierarchyId(), Lists.newArrayList()));
        }
        else if (event instanceof NodeNamedChanged)
        {
            NodeNamedChanged nc = (NodeNamedChanged)event;
            Optional.ofNullable(grids.get(event.getHierarchyId()))
                    .ifPresent(grid -> {
                        Optional<NodeDto> existing = grid.getNodes().stream()
                                .filter(n -> n.getNodeId().equals(nc.getNodeId())).findAny();
                        existing.ifPresent(n -> n.setName(nc.getNewName()));
                    });
        }
    }
}

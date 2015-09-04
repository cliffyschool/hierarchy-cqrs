package org.bitbucket.cliffyschool.hierarchy.application.projection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreatedEvent;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HierarchyAsGridProjection {

    private Map<UUID,HierarchyAsGrid> grids = Maps.newHashMap();

    public void write(List<Event> events){
        if (events == null)
            return;

        events.forEach(this::writeEvent);

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
                                grid.getNodes().add(new NodeDto(nc.getNodeName(), nc.getNodeColor(), nc.getNodeShape()));
                    });
        }
        else if (event instanceof HierarchyCreatedEvent)
        {
            HierarchyCreatedEvent hc = (HierarchyCreatedEvent)event;
            grids.put(hc.getHierarchyId(), new HierarchyAsGrid(hc.getHierarchyId(), Lists.newArrayList()));
        }
    }
}

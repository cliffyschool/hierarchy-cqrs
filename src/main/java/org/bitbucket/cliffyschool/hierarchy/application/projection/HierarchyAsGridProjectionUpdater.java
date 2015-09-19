package org.bitbucket.cliffyschool.hierarchy.application.projection;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;

import java.util.Optional;

public class HierarchyAsGridProjectionUpdater {

    private HierarchyAsGridProjection gridProjection;

    public HierarchyAsGridProjectionUpdater(HierarchyAsGridProjection projection)
    {
        this.gridProjection = projection;
    }

    public void write(EventStream stream){
        if (stream == null)
            return;

        stream.getEvents().forEach(this::writeEvent);
    }

    private void writeEvent(Event event)
    {
        if (event instanceof NodeCreated)
        {
            NodeCreated nc = (NodeCreated)event;
            gridProjection.find(event.getHierarchyId())
                    .ifPresent(grid -> {
                        grid.getNodes().removeIf(n -> StringUtils.equals(nc.getNodeName(), n.getName()));
                        grid.getNodes().add(new FlatNode(nc.getNodeId(), nc.getNodeName(), nc.getNodeColor()));
                    });
        }
        else if (event instanceof HierarchyCreated)
        {
            HierarchyCreated hc = (HierarchyCreated)event;
            gridProjection.write(hc.getHierarchyId(), new HierarchyAsGrid(hc.getHierarchyId(), Lists.newArrayList()));
        }
        else if (event instanceof NodeNameChanged)
        {
            NodeNameChanged nc = (NodeNameChanged)event;
            gridProjection.find(event.getHierarchyId())
                    .ifPresent(grid -> {
                        Optional<FlatNode> existing = grid.getNodes().stream()
                                .filter(n -> n.getNodeId().equals(nc.getNodeId())).findAny();
                        existing.ifPresent(n -> n.setName(nc.getNewName()));
                    });
        }
    }
}

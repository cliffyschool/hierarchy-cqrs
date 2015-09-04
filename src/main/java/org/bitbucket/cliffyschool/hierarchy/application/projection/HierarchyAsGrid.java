package org.bitbucket.cliffyschool.hierarchy.application.projection;

import com.google.common.collect.Multimap;

import java.util.List;
import java.util.UUID;

public class HierarchyAsGrid {
    private UUID id;
    private List<NodeDto> nodes;

    public HierarchyAsGrid(UUID id, List<NodeDto> nodes)
    {
        this.id = id;
        this.nodes = nodes;
    }

    public UUID getId() {
        return id;
    }

    public List<NodeDto> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDto> nodes) {
        this.nodes = nodes;
    }
}

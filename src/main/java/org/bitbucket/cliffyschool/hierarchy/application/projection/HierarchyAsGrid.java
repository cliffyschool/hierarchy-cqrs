package org.bitbucket.cliffyschool.hierarchy.application.projection;

import java.util.List;
import java.util.UUID;

public class HierarchyAsGrid {
    private UUID id;
    private List<FlatNode> nodes;

    public HierarchyAsGrid(UUID id, List<FlatNode> nodes)
    {
        this.id = id;
        this.nodes = nodes;
    }

    public UUID getId() {
        return id;
    }

    public List<FlatNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<FlatNode> nodes) {
        this.nodes = nodes;
    }
}

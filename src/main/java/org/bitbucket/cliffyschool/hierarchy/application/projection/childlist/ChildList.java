package org.bitbucket.cliffyschool.hierarchy.application.projection.childlist;

import java.util.List;
import java.util.UUID;

public class ChildList {
    private UUID id;
    private List<Node> nodes;
    private String nodePath;

    public ChildList(UUID id, List<Node> nodes)
    {
        this.id = id;
        this.nodes = nodes;
    }

    public UUID getId() {
        return id;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }
}

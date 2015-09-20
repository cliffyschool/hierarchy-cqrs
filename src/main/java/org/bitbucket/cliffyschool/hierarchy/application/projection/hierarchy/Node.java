package org.bitbucket.cliffyschool.hierarchy.application.projection.hierarchy;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

public class Node  {
   private UUID nodeId;
    private String name;
    private String color;
    private List<Node> children;

    public Node(UUID nodeId, String name, String color) {
        this.nodeId = nodeId;
        this.name = name;
        this.color = color;
        children = Lists.newArrayList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public void setNodeId(UUID nodeId) {
        this.nodeId = nodeId;
    }

    public List<Node> getChildren(){
        return children;
    }
}

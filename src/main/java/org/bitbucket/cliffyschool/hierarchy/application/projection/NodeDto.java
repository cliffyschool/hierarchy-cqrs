package org.bitbucket.cliffyschool.hierarchy.application.projection;

import java.util.UUID;

public class NodeDto {
    private UUID nodeId;
    private String name;
    private String color;

    public NodeDto(UUID nodeId, String name, String color) {
        this.nodeId = nodeId;
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
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

}

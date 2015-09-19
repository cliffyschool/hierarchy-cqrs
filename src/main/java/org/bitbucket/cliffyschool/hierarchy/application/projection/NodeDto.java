package org.bitbucket.cliffyschool.hierarchy.application.projection;

import java.util.UUID;

public class NodeDto {
    private UUID nodeId;
    private String name;
    private String color;
    private String shape;

    public NodeDto(UUID nodeId, String name, String color, String shape) {
        this.nodeId = nodeId;
        this.name = name;
        this.color = color;
        this.shape = shape;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getShape() {
        return shape;
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

    public void setShape(String shape) {
        this.shape = shape;
    }
}

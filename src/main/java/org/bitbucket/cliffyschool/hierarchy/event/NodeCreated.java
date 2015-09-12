package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class NodeCreated extends Event {
    private String nodeName;
    private String nodeColor;
    private String nodeShape;
    private UUID nodeId;

    public NodeCreated(UUID hierarchyId, UUID nodeId, String nodeName, String nodeColor, String nodeShape) {
        super(hierarchyId);
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeColor = nodeColor;
        this.nodeShape = nodeShape;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeColor() {
        return nodeColor;
    }

    public String getNodeShape() {
        return nodeShape;
    }

    public UUID getNodeId() {
        return nodeId;
    }
}

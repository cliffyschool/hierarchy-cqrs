package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class NodeCreated extends Event {
    private String nodeName;
    private String nodeColor;
    private UUID nodeId;

    public NodeCreated(UUID hierarchyId, long versionId, UUID nodeId, String nodeName, String nodeColor) {
        super(hierarchyId, versionId);
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeColor = nodeColor;
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

    public UUID getNodeId() {
        return nodeId;
    }

    @Override
    public Event copy(long newVersionId) {
        return new NodeCreated(getHierarchyId(), newVersionId, nodeId, nodeName, nodeColor);
    }
}

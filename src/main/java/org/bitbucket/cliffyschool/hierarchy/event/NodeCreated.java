package org.bitbucket.cliffyschool.hierarchy.event;


import java.util.Optional;
import java.util.UUID;

public class NodeCreated extends Event {
    private String nodeName;
    private String nodeColor;
    private UUID nodeId;

    public NodeCreated(UUID hierarchyId, UUID nodeId, String nodeName, String nodeColor) {
        super(hierarchyId);
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeColor = nodeColor;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeColor() {
        return nodeColor;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    @Override
    public Event withVersionId(long newVersionId) {
        NodeCreated nc = new NodeCreated(getHierarchyId(), nodeId, nodeName, nodeColor);
        nc.versionId = newVersionId;
        return nc;
    }
}

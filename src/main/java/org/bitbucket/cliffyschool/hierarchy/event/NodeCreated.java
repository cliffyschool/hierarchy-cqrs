package org.bitbucket.cliffyschool.hierarchy.event;

import org.bitbucket.cliffyschool.hierarchy.application.projection.Node;

import java.util.Optional;
import java.util.UUID;

public class NodeCreated extends Event {
    private String nodeName;
    private String nodeColor;
    private UUID nodeId;
    private Optional<UUID> parentNodeId;

    public NodeCreated(UUID hierarchyId, UUID nodeId, String nodeName, String nodeColor, Optional<UUID> parentNodeId) {
        super(hierarchyId);
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeColor = nodeColor;
        this.parentNodeId = parentNodeId;
    }

    public NodeCreated(UUID hierarchyId, UUID nodeId, String nodeName, String nodeColor) {
       this(hierarchyId,nodeId,nodeName,nodeColor, Optional.empty());
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
    public Event withVersionId(long newVersionId) {
        NodeCreated nc = new NodeCreated(getHierarchyId(), nodeId, nodeName, nodeColor, parentNodeId);
        nc.versionId = newVersionId;
        return nc;
    }

    public Optional<UUID> getParentNodeId() {
        return parentNodeId;
    }
}

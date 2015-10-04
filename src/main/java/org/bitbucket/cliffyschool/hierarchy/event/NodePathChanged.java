package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.Optional;
import java.util.UUID;

public class NodePathChanged extends Event{
    private final Optional<UUID> parentNodeId;
    private final UUID nodeId;
    private final String nodePath;

    public NodePathChanged(UUID hierarchyId, Optional<UUID> parentNodeId, UUID nodeId, String nodePath) {
        super(hierarchyId);
        this.parentNodeId = parentNodeId;
        this.nodeId = nodeId;
        this.nodePath = nodePath;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public String getNodePath() {
        return nodePath;
    }

    public Optional<UUID> getParentNodeId() {
        return parentNodeId;
    }

    @Override
    public Event withVersionId(long newVersionId) {
        NodePathChanged event = new NodePathChanged(getHierarchyId(), parentNodeId, nodeId, nodePath);
        event.versionId = newVersionId;
        return event;
    }
}

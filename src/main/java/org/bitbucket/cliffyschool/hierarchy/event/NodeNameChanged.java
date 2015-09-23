package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.Optional;
import java.util.UUID;

public class NodeNameChanged extends Event {
    private final String newName;
    private final UUID nodeId;
    private Optional<UUID> parentNodeId;

    public NodeNameChanged(UUID hierarchyId, UUID nodeId, Optional<UUID> parentNodeId, String newName) {
        super(hierarchyId);
        this.nodeId = nodeId;
        this.newName = newName;
        this.parentNodeId = parentNodeId;
    }

    public String getNewName() {
        return newName;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    @Override
    public Event withVersionId(long newVersionId) {
        NodeNameChanged nodeNameChanged = new NodeNameChanged(getHierarchyId(), nodeId, parentNodeId, newName);
        nodeNameChanged.versionId = newVersionId;
        return nodeNameChanged;
    }

    public Optional<UUID> getParentNodeId() {
        return parentNodeId;
    }
}

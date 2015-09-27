package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.Optional;
import java.util.UUID;

public class NodeAddedToHierarchy extends Event {
    private Optional<UUID> parentNodeId;
    private UUID childNodeId;

    public NodeAddedToHierarchy(UUID hierarchyId, Optional<UUID> parentNodeId, UUID childNodeId) {
        super(hierarchyId);
        this.parentNodeId = parentNodeId;
        this.childNodeId = childNodeId;
    }

    public Optional<UUID> getParentNodeId() {
        return parentNodeId;
    }

    public UUID getChildNodeId() {
        return childNodeId;
    }

    @Override
    public Event withVersionId(long newVersionId) {
        return null;
    }
}

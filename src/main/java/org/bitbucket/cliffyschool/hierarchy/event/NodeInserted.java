package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.Optional;
import java.util.UUID;

public class NodeInserted extends Event {
    private Optional<UUID> parentNodeId;
    private UUID childNodeId;

    public NodeInserted(UUID hierarchyId, Optional<UUID> parentNodeId, UUID childNodeId) {
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

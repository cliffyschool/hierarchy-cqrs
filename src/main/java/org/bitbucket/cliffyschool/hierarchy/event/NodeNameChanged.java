package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class NodeNameChanged extends Event {
    private final String newName;
    private final UUID nodeId;

    public NodeNameChanged(UUID hierarchyId, long versionId, UUID nodeId, String newName) {
        super(hierarchyId, versionId);
        this.nodeId = nodeId;
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    @Override
    public Event copy(long newVersionId) {
        return new NodeNameChanged(getHierarchyId(), newVersionId, nodeId, newName);
    }
}

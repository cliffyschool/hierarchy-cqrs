package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class NodeNamedChanged extends Event {
    private final String newName;
    private final UUID nodeId;

    public NodeNamedChanged(UUID hierarchyId, long versionId, UUID nodeId, String newName) {
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
        return new NodeNamedChanged(getHierarchyId(), newVersionId, nodeId, newName);
    }
}

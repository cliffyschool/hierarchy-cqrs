package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class NodeNameChanged extends Event {
    private final String newName;
    private final UUID nodeId;

    public NodeNameChanged(UUID hierarchyId, UUID nodeId, String newName) {
        super(hierarchyId);
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
    public Event withVersionId(long newVersionId) {
        NodeNameChanged nodeNameChanged = new NodeNameChanged(getHierarchyId(), nodeId, newName);
        nodeNameChanged.versionId = newVersionId;
        return nodeNameChanged;
    }
}

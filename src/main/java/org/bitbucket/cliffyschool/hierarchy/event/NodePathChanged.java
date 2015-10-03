package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class NodePathChanged extends Event{
    private final UUID nodeId;
    private final String nodePath;

    public NodePathChanged(UUID hierarchyId, UUID nodeId, String nodePath) {
        super(hierarchyId);
        this.nodeId = nodeId;
        this.nodePath = nodePath;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public String getNodePath() {
        return nodePath;
    }

    @Override
    public Event withVersionId(long newVersionId) {
        NodePathChanged event = new NodePathChanged(getHierarchyId(), nodeId, nodePath);
        event.versionId = newVersionId;
        return event;
    }
}

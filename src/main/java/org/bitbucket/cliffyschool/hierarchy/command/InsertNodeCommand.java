package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.UUID;

public class InsertNodeCommand extends HierarchyCommand {
    private UUID hierarchyId;
    private UUID parentId;
    private UUID nodeId;

    public InsertNodeCommand(UUID hierarchyId, long hierarchyVersionLastLoaded, UUID parentId, UUID nodeId) {
        super(hierarchyVersionLastLoaded);
        this.hierarchyId = hierarchyId;
        this.parentId = parentId;
        this.nodeId = nodeId;
    }

    public UUID getHierarchyId() {
        return hierarchyId;
    }

    public UUID getParentId() {
        return parentId;
    }

    public UUID getNodeId() {
        return nodeId;
    }
}

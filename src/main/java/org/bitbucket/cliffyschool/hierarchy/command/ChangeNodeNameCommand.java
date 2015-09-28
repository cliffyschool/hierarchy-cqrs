package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.UUID;

public class ChangeNodeNameCommand extends HierarchyCommand {
    private UUID hierarchyId;
    private UUID nodeId;
    private String newName;

    public UUID getNodeId() {
        return nodeId;
    }

    public String getNewName() {
        return newName;
    }

    public ChangeNodeNameCommand(UUID hierarchyId, long hierarchyVersionLastLoaded, UUID nodeId, String newName) {
        super(hierarchyVersionLastLoaded);
        this.hierarchyId = hierarchyId;
        this.nodeId = nodeId;
        this.newName = newName;
    }

    public UUID getHierarchyId() {
        return hierarchyId;
    }
}

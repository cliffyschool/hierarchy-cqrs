package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.UUID;

public class ChangeNodeNameCommand extends Command {
    private UUID nodeId;
    private String newName;

    public UUID getNodeId() {
        return nodeId;
    }

    public String getNewName() {
        return newName;
    }

    public ChangeNodeNameCommand(UUID nodeId, String newName, long originalVersionId) {
        super(originalVersionId);
        this.nodeId = nodeId;
        this.newName = newName;
    }
}

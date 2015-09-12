package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.UUID;

public class ChangeNodeNameCommand implements Command {
    private UUID nodeId;
    private String newName;

    public UUID getNodeId() {
        return nodeId;
    }

    public String getNewName() {
        return newName;
    }

    public ChangeNodeNameCommand(UUID nodeId, String newName) {

        this.nodeId = nodeId;
        this.newName = newName;
    }
}

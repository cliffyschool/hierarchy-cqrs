package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.UUID;

public class ChangeNodePropertyCommand extends NodeCommand {
    private final UUID nodeId;
    private final String propertyName;
    private final String propertyValue;

    public ChangeNodePropertyCommand(UUID nodeId, long lastNodeVersionLoaded, String propertyName, String propertyValue) {
        super(lastNodeVersionLoaded);
        this.nodeId = nodeId;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
}

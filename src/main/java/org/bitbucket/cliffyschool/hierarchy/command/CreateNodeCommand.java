package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.Optional;
import java.util.UUID;

public class CreateNodeCommand extends Command {
    private final String color;
    private final UUID nodeId;
    private String nodeName;
    private Optional<UUID> parentNodeId = Optional.empty();
    private long baseVersionId;

    public CreateNodeCommand(UUID nodeId, long baseVersionId, String nodeName, String color, Optional<UUID> parentNodeId) {
        this.nodeId = nodeId;
        this.baseVersionId = baseVersionId;
        this.nodeName = nodeName;
        this.color = color;
        this.parentNodeId = parentNodeId;
    }

    public CreateNodeCommand(UUID nodeId, long baseVersionId, String nodeName, String color) {
       this(nodeId, baseVersionId, nodeName, color, Optional.empty());
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public String getColor() {
        return color;
    }

    public String getNodeName() {
        return nodeName;
    }

    public Optional<UUID> getParentNodeId() {
        return parentNodeId;
    }

    public long getBaseVersionId() {
        return baseVersionId;
    }
}

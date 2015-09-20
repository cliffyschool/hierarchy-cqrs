package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.Optional;
import java.util.UUID;

public class CreateNodeCommand extends Command {
    private final String color;
    private final UUID nodeId;
    private String nodeName;
    private Optional<UUID> parentNodeId = Optional.empty();

    public CreateNodeCommand(UUID nodeId, long versionId, String nodeName, String color, Optional<UUID> parentNodeId) {
        super(versionId);
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.color = color;
        this.parentNodeId = parentNodeId;
    }

    public CreateNodeCommand(UUID nodeId, long versionId, String nodeName, String color) {
       this(nodeId, versionId, nodeName, color, Optional.empty());
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
}

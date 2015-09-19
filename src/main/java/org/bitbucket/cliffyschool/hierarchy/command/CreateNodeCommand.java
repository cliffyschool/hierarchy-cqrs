package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.UUID;

public class CreateNodeCommand extends Command {
    private final String shape;
    private final String color;
    private final UUID nodeId;
    private String nodeName;

    public CreateNodeCommand(UUID nodeId, long versionId, String color, String shape, String nodeName) {
        super(versionId);
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.color = color;
        this.shape = shape;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public String getColor() {
        return color;
    }

    public String getShape() {
        return shape;
    }

    public String getNodeName() {
        return nodeName;
    }
}

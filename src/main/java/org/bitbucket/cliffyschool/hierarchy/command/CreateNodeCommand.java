package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.Optional;
import java.util.UUID;

public class CreateNodeCommand extends HierarchyCommand {
    private UUID hierarchyId;
    private final String color;
    private final UUID nodeId;
    private String nodeName;
    private Optional<UUID> parentNodeId = Optional.empty();

    public CreateNodeCommand(UUID hierarchyId, long lastHierarchyVersionLoaded, UUID nodeId, String nodeName, String color,
                             Optional<UUID> parentNodeId) {
        super(lastHierarchyVersionLoaded);
        this.hierarchyId = hierarchyId;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.color = color;
        this.parentNodeId = parentNodeId;
    }

    public CreateNodeCommand(UUID hierarchyId, UUID nodeId, long lastHierarchyVersionLoaded, String nodeName, String color) {
       this(hierarchyId, lastHierarchyVersionLoaded, nodeId, nodeName, color, Optional.empty());
    }

    public UUID getHierarchyId() {
        return hierarchyId;
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

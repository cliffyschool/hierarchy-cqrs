package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodePropertyValueChanged;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.AggregateRoot;

import java.io.Serializable;
import java.util.UUID;

public class Node extends AggregateRoot implements Serializable {
    private UUID hierarchyId;
    private String name;
    private String color;

    public Node(UUID id, UUID hierarchyId, String name, String color) {
        super(id);
        this.name = name;
        this.color = color;
        this.hierarchyId = hierarchyId;
    }

    public static Node createNode(CreateNodeCommand createNodeCommand) {
        Node node = new Node(createNodeCommand.getNodeId(),createNodeCommand.getHierarchyId(),
                                createNodeCommand.getNodeName(), createNodeCommand.getColor());

        node.changeEvents.append(new NodeCreated(node.getHierarchyId(), node.getId(), node.getName(), node.getColor()));
        return node;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public UUID getHierarchyId() { return hierarchyId; }

    void changeNodeName(ChangeNodeNameCommand changeNodeNameCommand) {
        name = changeNodeNameCommand.getNewName();
    }

    @Override
    public AggregateRoot withVersionId(long newVersionId) {
        Node ret = new Node(id, hierarchyId, name, color);
        ret.versionId = newVersionId;
        return ret;
    }
}

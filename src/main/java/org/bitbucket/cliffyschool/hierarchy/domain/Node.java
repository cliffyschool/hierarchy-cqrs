package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;
import org.bitbucket.cliffyschool.hierarchy.event.NodePropertyValueChanged;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.AggregateRoot;

import java.util.Optional;
import java.util.UUID;

public class Node extends AggregateRoot{
    private UUID hierarchyId;
    private String name;
    private String color;
    private int childCount;

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

    public int getChildCount() {
        return childCount;
    }

    public UUID getHierarchyId() { return hierarchyId; }

    public <T> void changeChildCount(int childCount) {
        int previousChildCount = this.childCount;
        this.childCount = childCount;
        changeEvents.append(new NodePropertyValueChanged<Integer>(hierarchyId, id, "ChildCount",previousChildCount, childCount));
    }

    void changeNodeName(ChangeNodeNameCommand changeNodeNameCommand) {
        name = changeNodeNameCommand.getNewName();
    }
}

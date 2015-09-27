package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;
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

    @Override
    public Event creationEvent() {
        return new NodeCreated(hierarchyId, id, name, color);
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

    public <T> void applyPropertyValueChange(String propertyName, T newValue) {
       if ("childcount".equalsIgnoreCase(propertyName))
           childCount = (Integer)newValue;
    }

    public void changeNodeName(ChangeNodeNameCommand changeNodeNameCommand, Hierarchy hierarchy) {
        name = changeNodeNameCommand.getNewName();
        Optional<UUID> parentId = hierarchy.getParentId(changeNodeNameCommand.getNodeId());
        changeEvents.append(new NodeNameChanged(hierarchyId, id, parentId, name));
    }
}

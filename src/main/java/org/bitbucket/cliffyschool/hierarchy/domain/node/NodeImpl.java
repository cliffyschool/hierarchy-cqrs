package org.bitbucket.cliffyschool.hierarchy.domain.node;

import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.Node;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodePropertyValueChanged;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.BaseAggregateRoot;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.SupportsPof;

import java.util.UUID;

public class NodeImpl extends BaseAggregateRoot implements Node, SupportsPof {
    private UUID hierarchyId;
    private String name;
    private String color;

    public NodeImpl(UUID id, UUID hierarchyId, String name, String color) {
        super(id);
        this.name = name;
        this.color = color;
        this.hierarchyId = hierarchyId;
    }

    public static Node createNode(CreateNodeCommand createNodeCommand) {
        NodeImpl node = new NodeImpl(createNodeCommand.getNodeId(),createNodeCommand.getHierarchyId(),
                                createNodeCommand.getNodeName(), createNodeCommand.getColor());

        node.changeEvents.append(new NodeCreated(node.getHierarchyId(), node.getId(), node.getName(), node.getColor()));
        return node;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public UUID getHierarchyId() { return hierarchyId; }

    @Override
    public void changePropertyValue(String propertyName, String value) {
        if ("color".equalsIgnoreCase(propertyName)){
            String previousColor = color;
            color = value;
            changeEvents.append(new NodePropertyValueChanged<>(hierarchyId, id, "color", color, previousColor));
        }
    }

    public void changeNodeName(ChangeNodeNameCommand changeNodeNameCommand) {
        name = changeNodeNameCommand.getNewName();
    }

    @Override
    public Node withVersionId(long newVersionId) {
        NodeImpl ret = new NodeImpl(id, hierarchyId, name, color);
        ret.versionId = newVersionId;
        return ret;
    }

    @Override
    public Object asPof() {
        return new PofNode(id, versionId, hierarchyId, name, color);
    }
}

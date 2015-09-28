package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeNodeName {

    private UUID nodeId;
    private UUID hierarchyId;
    private Hierarchy hierarchy;
    private Node node;

    @Before
    public void setUp(){
        nodeId = UUID.randomUUID();
        hierarchyId = UUID.randomUUID();
        hierarchy = new Hierarchy(hierarchyId);
        node = new Node(nodeId, hierarchyId, "name", "blue");
       hierarchy.insertNode(Optional.empty(), node);
    }

    @Test
    public void whenNodeNameChangedThenNewNameIsSet(){
        hierarchy.changeNodeName(new ChangeNodeNameCommand(hierarchyId, 0, nodeId, "newName"), node);

        assertThat(node.getName()).isEqualTo("newName");
    }

    @Test
    public void whenNodeNameChangedThenOldNameCanBeUsedAgain() {
        String oldName = node.getName();
        hierarchy.changeNodeName(new ChangeNodeNameCommand(hierarchyId, 0, nodeId, "newName"), node);

        UUID newNodeId = UUID.randomUUID();
        hierarchy.insertNode(Optional.empty(), new Node(newNodeId, hierarchyId, oldName, "blue"));

        assertThat(hierarchy.containsNode(newNodeId)).isNotNull();
    }
}
